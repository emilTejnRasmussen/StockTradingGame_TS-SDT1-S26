package business.services;

import business.dto.PortfolioHistoryDTO;
import entities.OwnedStock;
import entities.Portfolio;
import entities.Transaction;
import persistence.interfaces.OwnedStockDao;
import persistence.interfaces.PortfolioDao;
import persistence.interfaces.StockDao;
import persistence.interfaces.TransactionDao;
import shared.configuration.AppConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class PortfolioService
{
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

    public List<Transaction> getTransactionHistory(UUID portfolioId) {
        return transactionDao.getAllFromPortfolioId(portfolioId).stream()
                .sorted(Comparator.comparing(Transaction::timeStamp))
                .toList();
    }

    public List<PortfolioHistoryDTO> getPortfolioHistory(UUID portfolioId) {
        List<Transaction> transactions = getTransactionHistory(portfolioId);

        List<PortfolioHistoryDTO> history = new ArrayList<>();

        BigDecimal runningBalance = AppConfig.getInstance().getStartingBalance();

        for (Transaction transaction : transactions) {
            switch (transaction.type()){
                case BUY -> runningBalance = runningBalance.subtract(transaction.getTotalPriceWithFee());
                case SELL -> runningBalance = runningBalance.add(transaction.getTotalPriceFeeSubtracted());
            }
            history.add(new PortfolioHistoryDTO(transaction.timeStamp(), runningBalance));
        }

        return history;
    }

    public BigDecimal getTotalProfitLoss(UUID portfolioId) {
        List<Transaction> transactions = getTransactionHistory(portfolioId);

        BigDecimal spent = BigDecimal.ZERO;
        BigDecimal earned = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            switch (transaction.type()){
                case BUY -> spent = spent.add(transaction.getTotalPriceWithFee());
                case SELL -> spent = earned.add(transaction.getTotalPriceFeeSubtracted());
            }
        }

        return earned.subtract(spent).setScale(4, RoundingMode.HALF_UP);
    }


}
