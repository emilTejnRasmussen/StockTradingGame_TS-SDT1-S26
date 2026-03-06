package business.stockmarket;

import business.dto.StockDTO;
import business.stockmarket.simulation.LiveStock;
import entities.Stock;
import shared.logging.Logger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class StockMarket
{
    private List<LiveStock> liveStocks;
    private final Logger logger;
    private static StockMarket instance;
    private final PropertyChangeSupport support;

    private StockMarket()
    {
        this.liveStocks = new ArrayList<>();
        this.logger = Logger.getInstance();
        this.support = new PropertyChangeSupport(this);
    }

    public void addListener(PropertyChangeListener listener)
    {
        support.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener)
    {
        support.removePropertyChangeListener(listener);
    }

    public void addLiveStock(String stockSymbol)
    {
        LiveStock liveStock = new LiveStock(stockSymbol);
        liveStocks.add(liveStock);
        logger.info("New live stock added: " + stockSymbol);
    }

    public void addExistingStock(Stock stock)
    {
        LiveStock liveStock = new LiveStock(stock.getSymbol(), stock.getCurrentState(), stock.getCurrentPrice());
        liveStocks.add(liveStock);
        logger.info("Existing stock added: " + stock.getSymbol());
    }

    public void updateStock(LiveStock liveStock)
    {
        BigDecimal priceBeforeUpdate = liveStock.getCurrentPrice().setScale(4, RoundingMode.HALF_UP);

        liveStock.updatePrice();

        BigDecimal priceAfterUpdate = liveStock.getCurrentPrice().setScale(4, RoundingMode.HALF_UP);
        logger.info(liveStock.getSymbol() +
                " | " + priceAfterUpdate +
                " | " + liveStock.getStateName() +
                " | Price change: " + priceAfterUpdate.subtract(priceBeforeUpdate));

        StockDTO stockDTO = new StockDTO(liveStock.getSymbol(), priceAfterUpdate, liveStock.getStateName());
        support.firePropertyChange(liveStock.getSymbol(), null, stockDTO);
    }

    public static StockMarket getInstance()
    {
        if (instance == null)
        {
            instance = new StockMarket();
        }
        return instance;
    }

    public List<LiveStock> getLiveStocks()
    {
        return List.copyOf(liveStocks);
    }
}
