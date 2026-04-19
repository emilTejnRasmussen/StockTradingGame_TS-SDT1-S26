package business.services;

import business.dto.PageResult;
import business.dto.PortfolioHistoryDTO;
import entities.OwnedStock;
import entities.Portfolio;
import entities.Transaction;
import persistence.interfaces.*;
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
    private final UnitOfWork uow;

    public PortfolioService(UnitOfWork uow, PortfolioDao portfolioDao, OwnedStockDao ownedStockDao, StockDao stockDao, TransactionDao transactionDao)
    {
        this.uow = uow;
        this.portfolioDao = portfolioDao;
        this.ownedStockDao = ownedStockDao;
        this.stockDao = stockDao;
        this.transactionDao = transactionDao;
    }

    public List<OwnedStock> getOwnedStocks(UUID portfolioId)
    {
        return ownedStockDao.getAllByPortfolioId(portfolioId);
    }

    public int getNumberOfSharesOwned(UUID portfolioId, String stockSymbol)
    {
        return ownedStockDao.getByPortfolioIdAndStockSymbol(portfolioId, stockSymbol)
                .orElseThrow(() -> new IllegalArgumentException("No owned stock=" + stockSymbol + " found in portfolio"))
                .getNumberOfShares();
    }

    public String getPortfolioName(UUID portfolioId) {
        return portfolioDao.getById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("No portfolio with id=" + portfolioId))
                .getName();
    }

    public List<Portfolio> getAllPortfolios() {
        return portfolioDao.getAll();
    }

    public BigDecimal getAvgStockBuyPrice(String stockSymbol, UUID portfolioId) {
        List<Transaction> transactions = transactionDao.findTransactionsByPortfolioId(portfolioId).stream()
                .filter(t -> t.stockSymbol().equals(stockSymbol) && t.type() == Transaction.Type.BUY)
                .toList();

        BigDecimal totalSpent = transactions.stream()
                .map(Transaction::getGrossAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalSharesBought = transactions.stream()
                .mapToInt(Transaction::quantity)
                .sum();

        if (totalSharesBought == 0)
        {
            return BigDecimal.ZERO;
        }

        return totalSpent.divide(BigDecimal.valueOf(totalSharesBought), 2, RoundingMode.HALF_UP);
    }

    public String getBestSymbol(UUID portfolioId)
    {
        return getOwnedStocks(portfolioId).stream()
                .max(Comparator.comparing(ownedStock -> getChangePercent(portfolioId, ownedStock.getStockSymbol())))
                .map(OwnedStock::getStockSymbol)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio has no owned stocks"));
    }

    public String getWorstSymbol(UUID portfolioId)
    {
        return getOwnedStocks(portfolioId).stream()
                .min(Comparator.comparing(ownedStock -> getChangePercent(portfolioId, ownedStock.getStockSymbol())))
                .map(OwnedStock::getStockSymbol)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio has no owned stocks"));
    }

    private BigDecimal getChangePercent(UUID portfolioId, String stockSymbol)
    {
        BigDecimal avgBuyPrice = getAvgStockBuyPrice(stockSymbol, portfolioId);

        if (avgBuyPrice.compareTo(BigDecimal.ZERO) == 0)
        {
            return BigDecimal.ZERO;
        }

        BigDecimal currentPrice = stockDao.getBySymbol(stockSymbol)
                .orElseThrow(() -> new IllegalArgumentException("No stock with symbol=" + stockSymbol + " found"))
                .getCurrentPrice();

        return currentPrice.subtract(avgBuyPrice)
                .divide(avgBuyPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public boolean hasCreatedPortfolio()
    {
        return !portfolioDao.getAll().isEmpty();
    }

    public BigDecimal getPortfolioBalance(UUID portfolioId)
    {
        return portfolioDao.getById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("No portfolio with id=" + portfolioId + " found"))
                .getCurrentBalance();
    }

    public int getTotalNumberOfShares(UUID portfolioId)
    {
        return ownedStockDao.getAllByPortfolioId(portfolioId).stream()
                .mapToInt(OwnedStock::getNumberOfShares)
                .sum();
    }

    public BigDecimal getPortfolioNetWorth(UUID portfolioId)
    {
        return getPortfolioBalance(portfolioId)
                .add(getHoldingsValue(portfolioId))
                .setScale(4, RoundingMode.HALF_UP);
    }

    public BigDecimal getHoldingsValue(UUID portfolioId)
    {
        BigDecimal total = BigDecimal.ZERO;

        for (OwnedStock ownedStock : getOwnedStocks(portfolioId))
        {
            BigDecimal stockPrice = stockDao.getBySymbol(ownedStock.getStockSymbol())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No stock with symbol=" + ownedStock.getStockSymbol() + " found"))
                    .getCurrentPrice();

            BigDecimal shares = BigDecimal.valueOf(ownedStock.getNumberOfShares());
            total = total.add(stockPrice.multiply(shares));
        }

        return total.setScale(4, RoundingMode.HALF_UP);
    }

    public PageResult<Transaction> getTransactionHistory(UUID portfolioId, int page, int pageSize)
    {
        validatePagination(page, pageSize);

        List<Transaction> results = transactionDao.findTransactionsByPortfolioIdPaginated(portfolioId, page, pageSize);

        int totalItems = getTotalTransactions(portfolioId);

        return toPageResult(results, page, pageSize, totalItems);
    }

    public int getTotalTransactions(UUID portfolioId) {
        return transactionDao.countTransactionsByPortfolioId(portfolioId);
    }

    public int getTransactionTotalBuyCount(UUID portfolioId) {
        return transactionDao.findTransactionsByPortfolioId(portfolioId).stream()
                .filter(t -> t.type() == Transaction.Type.BUY)
                .toList().size();
    }

    public int getTransactionTotalSellCount(UUID portfolioId) {
        return transactionDao.findTransactionsByPortfolioId(portfolioId).stream()
                .filter(t -> t.type() == Transaction.Type.SELL)
                .toList().size();
    }

    public Transaction.Type getLatestTransactionType(UUID portfolioId) {
        return transactionDao.getAll().stream()
                .filter(t -> t.portfolioId().equals(portfolioId))
                .max(Comparator.comparing(Transaction::timeStamp))
                .orElseThrow(() -> new IllegalArgumentException("No transactions yet"))
                .type();
    }

    public PageResult<PortfolioHistoryDTO> getPortfolioHistory(UUID portfolioId, int page, int pageSize)
    {
        validatePagination(page, pageSize);

        List<Transaction> transactions = getTransactionsSortedByOldestFirst(portfolioId);

        List<PortfolioHistoryDTO> history = new ArrayList<>();

        BigDecimal runningBalance = AppConfig.getInstance().getStartingBalance();

        for (Transaction transaction : transactions)
        {
            switch (transaction.type())
            {
                case BUY -> runningBalance = runningBalance.subtract(transaction.getTotalPriceWithFee());
                case SELL -> runningBalance = runningBalance.add(transaction.getTotalPriceFeeSubtracted());
            }
            history.add(new PortfolioHistoryDTO(transaction.timeStamp(), runningBalance));
        }

        Collections.reverse(history);
        return paginateList(history, page, pageSize);
    }

    public BigDecimal getTotalProfitLoss(UUID portfolioId)
    {
        return getPortfolioNetWorth(portfolioId)
                .subtract(AppConfig.getInstance().getStartingBalance())
                .setScale(4, RoundingMode.HALF_UP);
    }

    private <T> PageResult<T> paginateList(List<T> listToPaginate, int page, int pageSize)
    {
        List<T> results = listToPaginate.stream()
                .skip((long) page * pageSize)
                .limit(pageSize)
                .toList();

        int totalItems = listToPaginate.size();

        return toPageResult(results, page, pageSize, totalItems);
    }

    private <T> PageResult<T> toPageResult(List<T> results, int page, int pageSize, int totalItems)
    {
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

    private void validatePagination(int page, int pageSize)
    {
        if (page < 0 || pageSize <= 0 || pageSize > PAGINATION_MAX)
        {
            throw new IllegalArgumentException("Invalid pagination values");
        }
    }

    public void createNewPortfolio(String name, BigDecimal startingBalance)
    {

        Portfolio portfolio = new Portfolio(name);
        portfolio.setCurrentBalance(startingBalance);

        uow.begin();
        portfolioDao.create(portfolio);
        uow.commit();
    }
}
