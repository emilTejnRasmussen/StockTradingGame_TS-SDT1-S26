package business.stockmarket;

import business.stockmarket.simulation.LiveStock;
import business.stockmarket.simulation.LiveStockUpdater;
import shared.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class MarketTickerThread implements Runnable
{
    private final StockMarket stockMarket;
    private Logger logger;
    private List<Thread> liveStockThreads = new ArrayList<>();


    public MarketTickerThread()
    {
        this.stockMarket = StockMarket.getInstance();
        this.logger = Logger.getInstance();
    }

    @Override
    public void run()
    {
        logger.info("Market ticker started. Stocks=" + stockMarket.getLiveStocks().size());

        for (LiveStock liveStock : stockMarket.getLiveStocks())
        {
            Thread liveStockThread = new Thread(
                    new LiveStockUpdater(stockMarket, liveStock),
                    "LiveStockUpdater-" + liveStock.getSymbol());
            liveStockThread.start();
            liveStockThreads.add(liveStockThread);
        }


        try
        {
            while (!Thread.currentThread().isInterrupted())
            {
                Thread.sleep(1000); // Keep thread alive
            }
        } catch (InterruptedException e)
        {
            logger.warning("Market ticker interrupted.");
            Thread.currentThread().interrupt();
        } finally
        {
            logger.warning("Market ticker stopping - interrupting all LiveStockUpdater Threads");
            for (Thread thread : liveStockThreads)
            {
                thread.interrupt();
            }
        }
    }
}
