package business.services;

import business.dto.StockDTO;
import entities.OwnedStock;
import entities.Stock;
import persistence.interfaces.OwnedStockDao;
import persistence.interfaces.UnitOfWork;
import shared.logging.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class StockBankruptService implements PropertyChangeListener
{
    private final UnitOfWork uow;
    private final OwnedStockDao ownedStockDao;
    private final PortfolioService portfolioService;
    private final Logger logger;

    public StockBankruptService(UnitOfWork uow, OwnedStockDao ownedStockDao, PortfolioService portfolioService)
    {
        this.uow = uow;
        this.ownedStockDao = ownedStockDao;
        this.portfolioService = portfolioService;
        this.logger = Logger.getInstance();
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        StockDTO oldStock = (StockDTO) evt.getOldValue();
        StockDTO newStock = (StockDTO) evt.getOldValue();
        if (!becameBankrupt(oldStock.currentState(), newStock.currentState())) return;

        try
        {
            uow.begin();
            handleBankruptStock(newStock.symbol());
            uow.commit();
        } catch (Exception e){
            uow.rollback();
            logger.warning("Error in bankruptService: " + e.getMessage());
        }
    }

    public void handleBankruptStock(String stockSymbol) {
        List<OwnedStock> ownedStocks = ownedStockDao.getAllByStockSymbol(stockSymbol);
        logger.info("Handling bankruptcy for " + stockSymbol + ", ownedStocks found " + ownedStocks.size());

        if (ownedStocks.isEmpty()) return;

        for (OwnedStock ownedStock : ownedStocks) {
            ownedStockDao.delete(ownedStock.getId());
        }
    }

    private boolean becameBankrupt(Stock.State oldState, Stock.State newState)
    {
        boolean oldStateNotBankrupt = oldState != Stock.State.BANKRUPT;
        boolean newStateIsBankrupt = newState == Stock.State.BANKRUPT;

        return oldStateNotBankrupt && newStateIsBankrupt;
    }
}
