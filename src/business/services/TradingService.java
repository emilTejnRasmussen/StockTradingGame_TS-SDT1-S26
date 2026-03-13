package business.services;

import business.dto.transaction.BuyStockRequestDTO;
import business.dto.transaction.SellStockRequestDTO;
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

    public void buyStock(BuyStockRequestDTO request)
    {
        try
        {
            uow.begin();

            int quantity = request.quantity();
            Transaction.Type type = Transaction.Type.BUY;

            Portfolio portfolio = getPortfolio(request.portfolioId());
            Stock stock = getStock(request.stockSymbol());

            validateRequest(request, stock, type);
            validateBuy(portfolio, stock, quantity);

            handlePayment(portfolio, stock, quantity, type);

            addSharesToOwnedStock(portfolio, stock, quantity);
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

    public void sellStock(SellStockRequestDTO request)
    {
        try
        {
            uow.begin();

            int quantity = request.quantity();
            Transaction.Type type = Transaction.Type.SELL;

            Portfolio portfolio = getPortfolio(request.portfolioId());
            Stock stock = getStock(request.stockSymbol());

            validateRequest(request, stock, type);
            validateSell(portfolio, stock, quantity);

            removeSharesFromOwnedStock(portfolio, stock, quantity);
            handlePayment(portfolio, stock, quantity, type);
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

    private void createTransaction(UUID portfolioId, Stock stock, int quantity, Transaction.Type type)
    {
        Transaction transaction = Transaction.create(portfolioId, stock.getSymbol(), type, quantity, stock.getCurrentPrice(), AppConfig.getInstance().getTransactionFee());

        transactionDao.create(transaction);
    }

    private void addSharesToOwnedStock(Portfolio portfolio, Stock stock, int numberOfShares)
    {
        Optional<OwnedStock> ownedStockOpt = ownedStockDao.getByPortfolioIdAndStockSymbol(portfolio.getId(), stock.getSymbol());

        if (ownedStockOpt.isEmpty())
        {
            createNewOwnedStock(portfolio, stock, numberOfShares);
            return;
        }
        OwnedStock ownedStock = ownedStockOpt.get();
        ownedStock.addShares(numberOfShares);
        ownedStockDao.update(ownedStock);

    }

    private void removeSharesFromOwnedStock(Portfolio portfolio, Stock stock, int sharesToSell)
    {
        OwnedStock ownedStock = ownedStockDao.getByPortfolioIdAndStockSymbol(portfolio.getId(), stock.getSymbol()).orElseThrow(() -> new IllegalArgumentException("No stock='" + stock.getSymbol() + "' owned"));

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

    private void handlePayment(Portfolio portfolio, Stock stock, int numberOfShares, Transaction.Type type)
    {
        BigDecimal fee = BigDecimal.valueOf(AppConfig.getInstance().getTransactionFee());
        BigDecimal amount = stock.getCurrentPrice().multiply(BigDecimal.valueOf(numberOfShares));

        if (type == Transaction.Type.BUY)
        {
            portfolio.pay(amount.add(fee));
        } else
        {
            BigDecimal proceeds = amount.subtract(fee);
            if (proceeds.compareTo(BigDecimal.ZERO) < 0)
            {
                throw new BusinessRuleException("Sell proceeds cannot be negative");
            }
            portfolio.earn(amount.subtract(fee));
        }

        portfolioDao.update(portfolio);
    }

    private void validateRequest(StockTransactionRequest request, Stock stock, Transaction.Type type)
    {
        String action = type.toString().toLowerCase();

        if (stock.getCurrentState() == Stock.State.BANKRUPT || stock.getCurrentState() == Stock.State.RESET)
        {
            throw new BusinessRuleException("Cannot " + action + " stock in " + stock.getCurrentState() + " state");
        }

        if (request.quantity() < 1)
            throw new IllegalArgumentException("Number of shares to " + action + " must be positive");
    }

    private void validateBuy(Portfolio portfolio, Stock stock, int quantity)
    {
        BigDecimal fee = BigDecimal.valueOf(AppConfig.getInstance().getTransactionFee());
        BigDecimal total = stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity)).add(fee);

        if (portfolio.getCurrentBalance().compareTo(total) < 0)
        {
            throw new BusinessRuleException("Insufficient player balance to complete transaction");
        }
    }

    private void validateSell(Portfolio portfolio, Stock stock, int quantity)
    {
        OwnedStock ownedStock = ownedStockDao.getByPortfolioIdAndStockSymbol(portfolio.getId(), stock.getSymbol()).orElseThrow(() -> new IllegalArgumentException("No owned '" + stock.getSymbol() + "' stocks in this portfolio"));

        if (ownedStock.getNumberOfShares() < quantity)
        {
            throw new BusinessRuleException("Cannot sell more shares than is owned");
        }
    }

    private void createNewOwnedStock(Portfolio portfolio, Stock stock, int numberOfShares)
    {
        OwnedStock ownedStock = new OwnedStock(portfolio.getId(), stock.getSymbol(), numberOfShares);
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
