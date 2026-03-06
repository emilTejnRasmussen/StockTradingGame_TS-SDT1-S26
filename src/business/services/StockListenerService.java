package business.services;

import business.dto.StockDTO;
import entities.Stock;
import entities.StockPriceHistory;
import persistence.fileImplementation.FileStockDao;
import persistence.fileImplementation.FileStockPriceHistoryDao;
import persistence.fileImplementation.FileUnitOfWork;
import shared.logging.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class StockListenerService implements PropertyChangeListener
{
    private final Logger logger;
    private final FileStockDao stockDao;
    private final FileStockPriceHistoryDao stockPriceHistoryDao;
    private final FileUnitOfWork uow;

    private final PropertyChangeSupport support;

    public StockListenerService(FileUnitOfWork uow, FileStockDao stockDao,
                                FileStockPriceHistoryDao stockPriceHistoryDao)
    {
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
            updatePrice(stockDTO);
            uow.commit();
            support.firePropertyChange(stockDTO.symbol(), null, stockDTO);
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

    private void updatePrice(StockDTO stockDTO)
    {
        Stock stock = stockDao.getBySymbol(stockDTO.symbol())
                .orElseThrow(() -> new IllegalArgumentException("Stock not found: " + stockDTO.symbol()));

        stock.setCurrentPrice(stockDTO.currentPrice());
        stock.setCurrentState(stockDTO.currentState());
        stockDao.update(stock);

        StockPriceHistory stockPriceHistory = StockPriceHistory.create(stockDTO.symbol(), stockDTO.currentPrice());
        stockPriceHistoryDao.create(stockPriceHistory);
    }
}
