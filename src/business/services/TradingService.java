package business.services;

import business.dto.transaction.StockTransactionRequest;
import entities.OwnedStock;
import entities.Portfolio;
import entities.Stock;
import entities.Transaction;
import exception.BusinessRuleException;
import exception.TransactionFailedException;
import persistence.interfaces.*;
import shared.configuration.AppConfig;
import shared.logging.Logger;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class TradingService
{
    private final UnitOfWork uow;
    private final Logger logger;

    private final StockDao stockDao;
    private final PortfolioDao portfolioDao;
    private final TransactionDao transactionDao;
    private final OwnedStockDao ownedStockDao;

    public TradingService(UnitOfWork uow, StockDao stockDao, PortfolioDao portfolioDao, TransactionDao transactionDao, OwnedStockDao ownedStockDao)
    {
        this.uow = uow;
        this.stockDao = stockDao;
        this.portfolioDao = portfolioDao;
        this.transactionDao = transactionDao;
        this.ownedStockDao = ownedStockDao;
        this.logger = Logger.getInstance();
    }

    public void buyStock(StockTransactionRequest request)
    {
        try
        {
            uow.begin();

            int quantity = request.quantity();
            Transaction.Type type = Transaction.Type.BUY;

            Portfolio portfolio = getPortfolio(request.portfolioId());
            Stock stock = getStock(request.stockSymbol());

            ensureStockIsNotInBankruptOrResetState(stock);
            ensureTradeShareCountLargerThanZero(request);

            BigDecimal fee = BigDecimal.valueOf(AppConfig.getInstance().getTransactionFee());
            BigDecimal totalPrice = stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity)).add(fee);

            ensureBalanceLargerThanTotalPrice(portfolio, totalPrice);

            portfolio.pay(totalPrice);
            portfolioDao.update(portfolio);

            addSharesToOwnedStock(portfolio.getId(), stock, quantity);
            createTransaction(portfolio.getId(), stock, quantity, type);

            uow.commit();
            logger.info("Transaction complete: Stock='" + stock.getSymbol() + "' quantity='" + request.quantity() + "' type='BUY'");
        } catch (Exception e)
        {
            uow.rollback();
            logger.warning("Transaction 'buyStock' failed: " + e.getMessage());
            throw new TransactionFailedException("buyStock failed", e);
        }
    }

    public void sellStock(StockTransactionRequest request)
    {
        try
        {
            uow.begin();

            int quantity = request.quantity();
            Transaction.Type type = Transaction.Type.SELL;

            Portfolio portfolio = getPortfolio(request.portfolioId());
            Stock stock = getStock(request.stockSymbol());

            ensureStockIsNotInBankruptOrResetState(stock);
            ensureTradeShareCountLargerThanZero(request);
            ensurePortfolioHasStockAndAmount(portfolio.getId(), stock.getSymbol(), quantity);

            removeSharesFromOwnedStock(portfolio.getId(), stock, quantity);

            BigDecimal fee = BigDecimal.valueOf(AppConfig.getInstance().getTransactionFee());
            BigDecimal proceeds = stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity)).subtract(fee);

            ensureProceedsIsPositive(proceeds);

            portfolio.earn(proceeds);
            portfolioDao.update(portfolio);

            createTransaction(portfolio.getId(), stock, quantity, type);

            uow.commit();
            logger.info("Transaction complete: Stock='" + stock.getSymbol() + "' quantity='" + request.quantity() + "' type='SELL'");
        } catch (Exception e)
        {
            uow.rollback();
            logger.warning("Transaction 'sellStock' failed: " + e.getMessage());
            throw new TransactionFailedException("sellStock failed", e);
        }
    }

    private void ensureBalanceLargerThanTotalPrice(Portfolio portfolio, BigDecimal totalPrice)
    {
        if (portfolio.getCurrentBalance().compareTo(totalPrice) < 0)
        {
            throw new BusinessRuleException("Insufficient player balance to complete transaction");
        }
    }

    private void ensureTradeShareCountLargerThanZero(StockTransactionRequest request)
    {
        if (request.quantity() < 1) {
            throw new BusinessRuleException("Shares to trade must be a positive number");
        }
    }

    private void ensureStockIsNotInBankruptOrResetState(Stock stock)
    {
        Stock.State currentState = stock.getCurrentState();
        if (currentState == Stock.State.BANKRUPT || currentState == Stock.State.RESET){
            throw new BusinessRuleException("Stock is in state=" + currentState + ", and is not tradeable");
        }
    }

    private void ensureProceedsIsPositive(BigDecimal proceeds)
    {
        if (proceeds.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new BusinessRuleException("Sell proceeds cannot be negative");
        }
    }

    private void ensurePortfolioHasStockAndAmount(UUID portfolioId, String stockSymbol, int quantity)
    {
        OwnedStock ownedStock = ownedStockDao.getByPortfolioIdAndStockSymbol(portfolioId, stockSymbol)
                .orElseThrow(() -> new IllegalArgumentException("No owned '" + stockSymbol + "' stocks in this portfolio"));

        if (ownedStock.getNumberOfShares() < quantity)
        {
            throw new BusinessRuleException("Cannot sell more shares than is owned");
        }
    }

    private void createTransaction(UUID portfolioId, Stock stock, int quantity, Transaction.Type type)
    {
        Transaction transaction = Transaction.create(portfolioId, stock.getSymbol(), type, quantity, stock.getCurrentPrice(), AppConfig.getInstance().getTransactionFee());

        transactionDao.create(transaction);
    }

    private void addSharesToOwnedStock(UUID portfolioId, Stock stock, int numberOfShares)
    {
        Optional<OwnedStock> ownedStockOpt = ownedStockDao.getByPortfolioIdAndStockSymbol(portfolioId, stock.getSymbol());

        if (ownedStockOpt.isEmpty())
        {
            createNewOwnedStock(portfolioId, stock, numberOfShares);
            return;
        }
        OwnedStock ownedStock = ownedStockOpt.get();
        ownedStock.addShares(numberOfShares);
        ownedStockDao.update(ownedStock);

    }

    private void removeSharesFromOwnedStock(UUID portfolioId, Stock stock, int sharesToSell)
    {
        OwnedStock ownedStock = ownedStockDao.getByPortfolioIdAndStockSymbol(portfolioId, stock.getSymbol()).orElseThrow(() -> new IllegalArgumentException("No stock='" + stock.getSymbol() + "' owned"));

        int sharesOwned = ownedStock.getNumberOfShares();

        if (sharesOwned == sharesToSell)
        {
            ownedStockDao.delete(ownedStock.getId());
            logger.info("All '" + stock.getSymbol() + "' shares sold");
        } else
        {
            ownedStock.removeShares(sharesToSell);
            ownedStockDao.update(ownedStock);
            logger.info(sharesToSell + " '" + stock.getSymbol() + "' shares sold - " + (sharesOwned - sharesToSell) + " remaining");
        }
    }


    private void createNewOwnedStock(UUID portfolioId, Stock stock, int numberOfShares)
    {
        OwnedStock ownedStock = new OwnedStock(portfolioId, stock.getSymbol(), numberOfShares);
        ownedStockDao.create(ownedStock);
    }

    private Stock getStock(String stockSymbol)
    {
        return stockDao.getBySymbol(stockSymbol).orElseThrow(() -> new IllegalArgumentException("No stock with symbol=" + stockSymbol + " found"));
    }

    private Portfolio getPortfolio(UUID portfolioId)
    {
        return portfolioDao.getById(portfolioId).orElseThrow(() -> new IllegalArgumentException("No portfolio with id=" + portfolioId + " found"));
    }
}
