package business.stockmarket;

import business.stockmarket.simulation.LiveStock;
import entities.Stock;
import shared.logging.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class StockMarket
{
    private List<LiveStock> liveStocks;
    private final Logger logger;
    private static StockMarket instance;

    private StockMarket() {
        this.liveStocks = new ArrayList<>();
        this.logger = Logger.getInstance();
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
            BigDecimal priceBeforeUpdate = liveStock.getCurrentPrice().setScale(4, RoundingMode.HALF_UP);
            liveStock.updatePrice();
            BigDecimal priceAfterUpdate = liveStock.getCurrentPrice().setScale(4, RoundingMode.HALF_UP);
            logger.info(liveStock.getSymbol() + " | " + priceAfterUpdate + " | " + liveStock.getStateName() +
                    " | Price change: " + priceAfterUpdate.subtract(priceBeforeUpdate));
        }
    }

    public static StockMarket getInstance() {
        if (instance == null){
            instance = new StockMarket();
        }
        return instance;
    }
}
