package business.services;

import business.dto.StockDTO;
import entities.Stock;
import entities.StockPriceHistory;
import persistence.interfaces.StockDao;
import persistence.interfaces.StockPriceHistoryDao;
import persistence.interfaces.UnitOfWork;
import shared.logging.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class StockListenerService implements PropertyChangeListener
{
    private final UnitOfWork uow;
    private final Logger logger;
    private final StockDao stockDao;
    private final StockPriceHistoryDao stockPriceHistoryDao;
    private final StockBankruptService bankruptService;

    private final PropertyChangeSupport support;

    public StockListenerService(UnitOfWork uow, StockDao stockDao,
                                StockPriceHistoryDao stockPriceHistoryDao, StockBankruptService bankruptService)
    {
        this.bankruptService = bankruptService;
        this.logger = Logger.getInstance();
        this.uow = uow;
        this.stockDao = stockDao;
        this.stockPriceHistoryDao = stockPriceHistoryDao;
        this.support = new PropertyChangeSupport(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        StockDTO stockDTO = (StockDTO) evt.getNewValue();
        try
        {
            uow.begin();

            handleStockUpdate(stockDTO);

            uow.commit();
            support.firePropertyChange("stockUpdated", null, stockDTO);
        } catch (Exception e)
        {
            uow.rollback();
            logger.error("Failed to persist stock update: " + e.getMessage());
        }
    }

    public void addListener(PropertyChangeListener listener)
    {
        support.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener)
    {
        support.removePropertyChangeListener(listener);
    }

    private void handleStockUpdate(StockDTO stockDTO)
    {
        Stock stock = stockDao.getBySymbol(stockDTO.symbol())
                .orElseThrow(() -> new IllegalArgumentException("Stock not found: " + stockDTO.symbol()));

        Stock.State oldState = stock.getCurrentState();

        stock.setCurrentPrice(stockDTO.currentPrice());
        stock.setCurrentState(stockDTO.currentState());
        stockDao.update(stock);

        StockPriceHistory stockPriceHistory = StockPriceHistory.create(stockDTO.symbol(), stockDTO.currentPrice());
        stockPriceHistoryDao.create(stockPriceHistory);

        if (becameBankrupt(oldState, stock.getCurrentState())){
            bankruptService.handleBankruptStock(stock.getSymbol());
        }
    }

    private boolean becameBankrupt(Stock.State oldState, Stock.State newState)
    {
        boolean oldStateNotBankrupt = oldState != Stock.State.BANKRUPT;
        boolean newStateIsBankrupt = newState == Stock.State.BANKRUPT;

        return oldStateNotBankrupt && newStateIsBankrupt;
    }
}
