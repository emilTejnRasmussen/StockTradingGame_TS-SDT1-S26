package business.services;

import business.dto.StockDTO;
import business.stockmarket.simulation.LiveStock;
import entities.Stock;
import entities.StockPriceHistory;
import persistence.fileImplementation.FileStockDao;
import persistence.fileImplementation.FileStockPriceHistoryDao;
import persistence.fileImplementation.FileUnitOfWork;
import shared.logging.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Optional;

public class StockListenerService implements PropertyChangeListener
{
    private final Logger logger;
    private final FileStockDao stockDao;
    private final FileStockPriceHistoryDao stockPriceHistoryDao;
    private final FileUnitOfWork uow;

    public StockListenerService(FileUnitOfWork uow, FileStockDao stockDao, FileStockPriceHistoryDao stockPriceHistoryDao)
    {
        this.logger = Logger.getInstance();
        this.uow = uow;
        this.stockDao = stockDao;
        this.stockPriceHistoryDao = stockPriceHistoryDao;
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
        } catch (Exception e)
        {
            uow.rollback();
            logger.error("Failed to persist stock update: " + e.getMessage());
        }
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
