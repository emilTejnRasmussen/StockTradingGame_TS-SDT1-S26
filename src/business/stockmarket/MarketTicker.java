package business.stockmarket;

import business.stockmarket.simulation.LiveStock;
import business.stockmarket.simulation.LiveStockUpdater;
import shared.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class MarketTicker
{
    private final StockMarket stockMarket;
    private final Logger logger;
    private List<Thread> liveStockThreads = new ArrayList<>();


    public MarketTicker()
    {
        this.stockMarket = StockMarket.getInstance();
        this.logger = Logger.getInstance();
    }

    public void startLiveStockUpdates()
    {
        if (!liveStockThreads.isEmpty()) {
            logger.warning("Live stock updates already started.");
            return;
        }

        logger.info("Starting live stock updates. amount=" + stockMarket.getLiveStocks().size());

        for (LiveStock liveStock : stockMarket.getLiveStocks())
        {
            Thread liveStockThread = new Thread(
                    new LiveStockUpdater(stockMarket, liveStock),
                    "LiveStockUpdater-" + liveStock.getSymbol());

            liveStockThread.start();
            liveStockThreads.add(liveStockThread);
        }
    }

    public void stopLiveStockUpdates() {
        logger.warning("Stopping live stock updates");

        for (Thread thread : liveStockThreads) {
            thread.interrupt();
        }
        liveStockThreads.clear();
    }
}
