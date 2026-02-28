package business.stockmarket;

import business.stockmarket.simulation.LiveStock;
import entities.Stock;
import shared.logging.Logger;

import java.util.List;

public class StockMarket
{
    private List<LiveStock> liveStocks;
    private Logger logger;
    private static StockMarket instance;

    private StockMarket() {

    }

    public void addLiveStock(String stockSymbol) {
        liveStocks.add(new LiveStock(stockSymbol));
        logger.info("New live stock added: " + stockSymbol);
    }

    public void addExistingStock(Stock stock) {
        liveStocks.add(new LiveStock(stock.getSymbol(), stock.getCurrentState(), stock.getCurrentPrice()));
        logger.info("Existing stock added: " + stock.getSymbol());
    }

    public void updateAllStocks() {
        for (LiveStock liveStock : liveStocks){
            liveStock.updatePrice();
            logger.info(liveStock.getSymbol() + " | " + liveStock.getCurrentPrice() + " | " + liveStock.getStateName());
        }
    }

    public static StockMarket getInstance() {
        if (instance == null){
            instance = new StockMarket();
        }
        return instance;
    }
}
