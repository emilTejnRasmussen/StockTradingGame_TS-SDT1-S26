package business.services;

import business.dto.BuyStockDTO;
import business.dto.SellStockDTO;
import entities.Portfolio;
import entities.Stock;
import entities.Transaction;
import exception.BusinessRuleException;
import persistence.interfaces.PortfolioDao;
import persistence.interfaces.StockDao;
import persistence.interfaces.UnitOfWork;
import shared.configuration.AppConfig;
import shared.logging.Logger;

import java.math.BigDecimal;

public class TradingService
{
    private final UnitOfWork uow;
    private final Logger logger;

    private final StockDao stockDao;
    private final PortfolioDao portfolioDao;

    private Portfolio portfolio;
    private Stock stock;

    public TradingService(UnitOfWork uow, StockDao stockDao, PortfolioDao portfolioDao)
    {
        this.uow = uow;
        this.stockDao = stockDao;
        this.portfolioDao = portfolioDao;
        this.logger = Logger.getInstance();
    }

    public void buyStock(BuyStockDTO buyStockDTO) {
        try
        {
            uow.begin();
            validateBuyStockDTO(buyStockDTO);
            Transaction transaction = createBuyTransactionFromDTO(buyStockDTO);

            uow.commit();
            logger.info("");
        } catch (Exception e)
        {
            uow.rollback();
            logger.warning("Transaction 'buyStock' failed: " + e.getMessage());
        } finally
        {
            reset();
        }
    }

    public void sellStock(SellStockDTO sellStockDTO)
    {

    }

    private Transaction createBuyTransactionFromDTO(BuyStockDTO buyStockDTO)
    {
        Transaction transaction = Transaction.create(
                portfolio.getId(),
                stock.getSymbol(),
                Transaction.Type.BUY,
                buyStockDTO.quantity(),
                stock.getCurrentPrice(),
                AppConfig.getInstance().getTransactionFee());

        BigDecimal total = transaction.getTotalPriceWithFee();

        if (buyStockDTO.playerBalance().compareTo(total) < 0)
        {
            throw new IllegalArgumentException("Insufficient player balance to complete transaction");
        }

        return transaction;
    }

    private void validateBuyStockDTO(BuyStockDTO buyStockDTO)
    {
        portfolio = portfolioDao.getById(buyStockDTO.portfolioID())
                .orElseThrow(() -> new IllegalArgumentException("No portfolio with id=" + buyStockDTO.portfolioID() + " found"));

        stock = stockDao.getBySymbol(buyStockDTO.stockSymbol())
                .orElseThrow(() -> new IllegalArgumentException("No stock with symbol=" + buyStockDTO.stockSymbol() + " found"));

        if (stock.getCurrentState() == Stock.State.BANKRUPT){
            throw new BusinessRuleException("Cannot buy stock in state BANKRUPT");
        }
        if (stock.getCurrentState() == Stock.State.RESET){
            throw new BusinessRuleException("Cannot buy stock in state RESET");
        }

        if (buyStockDTO.quantity() < 1) throw new IllegalArgumentException("Quantity must be positive");
    }

    private void reset()
    {
        portfolio = null;
        stock = null;
    }
}
