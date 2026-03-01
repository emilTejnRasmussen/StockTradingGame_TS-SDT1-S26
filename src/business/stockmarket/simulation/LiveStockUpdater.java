package business.stockmarket.simulation;

import business.stockmarket.StockMarket;
import shared.configuration.AppConfig;
import shared.logging.Logger;

import java.util.concurrent.ThreadLocalRandom;

public class LiveStockUpdater implements Runnable
{
    private final StockMarket stockMarket;
    private final LiveStock liveStock;

    public LiveStockUpdater(StockMarket stockMarket, LiveStock liveStock)
    {
        this.stockMarket = stockMarket;
        this.liveStock = liveStock;
    }

    @Override
    public void run()
    {
        while (!Thread.currentThread().isInterrupted())
        {
            try
            {
                stockMarket.updateStock(liveStock);

                int base = AppConfig.getInstance().getUpdateFrequencyInMs();
                int variance = AppConfig.getInstance().getUpdateMaxVarianceInMs();

                int randomFrequency = base + ThreadLocalRandom.current().nextInt(-variance, variance + 1);

                Thread.sleep(randomFrequency);

            } catch (InterruptedException e)
            {
                Logger.getInstance().warning("LiveStockUpdater for '" + liveStock.getSymbol() + "' interrupted - stopping");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e)
            {
                Logger.getInstance().error("LiveStockUpdater error for '" + liveStock.getSymbol() + "': " + e.getMessage());
            }

        }
    }
}
