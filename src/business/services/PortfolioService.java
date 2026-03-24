package business.services;

import business.dto.PageResult;
import business.dto.PortfolioHistoryDTO;
import entities.OwnedStock;
import entities.Transaction;
import persistence.interfaces.OwnedStockDao;
import persistence.interfaces.PortfolioDao;
import persistence.interfaces.StockDao;
import persistence.interfaces.TransactionDao;
import shared.configuration.AppConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class PortfolioService
{
    private static final int PAGINATION_MAX = 50;
    private final PortfolioDao portfolioDao;
    private final OwnedStockDao ownedStockDao;
    private final StockDao stockDao;
    private final TransactionDao transactionDao;

    public PortfolioService(PortfolioDao portfolioDao, OwnedStockDao ownedStockDao, StockDao stockDao, TransactionDao transactionDao)
    {
        this.portfolioDao = portfolioDao;
        this.ownedStockDao = ownedStockDao;
        this.stockDao = stockDao;
        this.transactionDao = transactionDao;
    }

    public List<OwnedStock> getOwnedStocks(UUID portfolioId) {
        return ownedStockDao.getAllByPortfolioId(portfolioId);
    }

    public int getNumberOfSharesOwned(UUID portfolioId, String stockSymbol) {
        return ownedStockDao.getByPortfolioIdAndStockSymbol(portfolioId, stockSymbol)
                .orElseThrow(() -> new IllegalArgumentException("No owned stock=" + stockSymbol + " found in portfolio"))
                .getNumberOfShares();
    }

    public BigDecimal getPortfolioBalance(UUID portfolioId) {
        return portfolioDao.getById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("No portfolio with id=" + portfolioId + " found"))
                .getCurrentBalance();
    }

    public BigDecimal getTotalPortfolioValue(UUID portfolioId) {
        BigDecimal total = getPortfolioBalance(portfolioId);

        List<OwnedStock> ownedStocks = getOwnedStocks(portfolioId);

        for (OwnedStock ownedStock : ownedStocks){
            BigDecimal stockPrice = stockDao.getBySymbol(ownedStock.getStockSymbol())
                    .orElseThrow(() -> new IllegalArgumentException("No stock with symbol=" + ownedStock.getStockSymbol() + " found"))
                    .getCurrentPrice();

            BigDecimal shares = BigDecimal.valueOf(ownedStock.getNumberOfShares());

            BigDecimal value = stockPrice.multiply(shares);
            total = total.add(value);
        }

        return total.setScale(4, RoundingMode.HALF_UP);
    }

    public PageResult<Transaction> getTransactionHistory(UUID portfolioId, int page, int pageSize) {
        validatePagination(page, pageSize);

        List<Transaction> results = transactionDao.findTransactionsByPortfolioIdPaginated(portfolioId, page, pageSize);

        int totalItems = transactionDao.countTransactionsByPortfolioId(portfolioId);

        return toPageResult(results, page, pageSize, totalItems);
    }

    public PageResult<PortfolioHistoryDTO> getPortfolioHistory(UUID portfolioId, int page, int pageSize) {
        validatePagination(page, pageSize);

        List<Transaction> transactions = getTransactionsSortedByOldestFirst(portfolioId);

        List<PortfolioHistoryDTO> history = new ArrayList<>();

        BigDecimal runningBalance = AppConfig.getInstance().getStartingBalance();

        for (Transaction transaction : transactions) {
            switch (transaction.type()){
                case BUY -> runningBalance = runningBalance.subtract(transaction.getTotalPriceWithFee());
                case SELL -> runningBalance = runningBalance.add(transaction.getTotalPriceFeeSubtracted());
            }
            history.add(new PortfolioHistoryDTO(transaction.timeStamp(), runningBalance));
        }

        Collections.reverse(history);
        return paginateList(history, page, pageSize);
    }

    public BigDecimal getTotalProfitLoss(UUID portfolioId) {
        List<Transaction> transactions = transactionDao.findTransactionsByPortfolioId(portfolioId);

        BigDecimal spent = BigDecimal.ZERO;
        BigDecimal earned = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            switch (transaction.type()){
                case BUY -> spent = spent.add(transaction.getTotalPriceWithFee());
                case SELL -> earned = earned.add(transaction.getTotalPriceFeeSubtracted());
            }
        }

        return earned.subtract(spent).setScale(4, RoundingMode.HALF_UP);
    }

    private <T> PageResult<T> paginateList(List<T> listToPaginate, int page, int pageSize) {
        List<T> results = listToPaginate.stream()
                .skip((long) page * pageSize)
                .limit(pageSize)
                .toList();

        int totalItems = listToPaginate.size();

        return toPageResult(results, page, pageSize, totalItems);
    }

    private <T> PageResult<T> toPageResult(List<T> results, int page, int pageSize, int totalItems){
        int totalPages = (totalItems + pageSize - 1) / pageSize;

        return new PageResult<>(
                results,
                page,
                pageSize,
                totalItems,
                totalPages
        );
    }

    private List<Transaction> getTransactionsSortedByOldestFirst(UUID portfolioId)
    {
        return transactionDao.findTransactionsByPortfolioId(portfolioId).stream()
                .sorted(Comparator.comparing(Transaction::timeStamp))
                .toList();
    }

    private void validatePagination(int page, int pageSize) {
        if (page < 0 || pageSize <= 0 || pageSize > PAGINATION_MAX) {
            throw new IllegalArgumentException("Invalid pagination values");
        }
    }
}
