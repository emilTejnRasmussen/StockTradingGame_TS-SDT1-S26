package business.stockmarket;

import shared.configuration.AppConfig;
import shared.logging.Logger;

public class MarketTickerThread implements Runnable
{
    private final StockMarket stockMarket;
    private final int updateFrequencyInMs;
    private Logger logger;


    public MarketTickerThread()
    {
        this.stockMarket = StockMarket.getInstance();
        this.updateFrequencyInMs = AppConfig.getInstance().getUpdateFrequencyInMs();
        this.logger = Logger.getInstance();
    }

    @Override
    public void run()
    {
        logger.info("Market ticker started. Frequency=" + updateFrequencyInMs);

        while (true) {
            try {
                stockMarket.updateAllStocks();
                Thread.sleep(updateFrequencyInMs);
            } catch (InterruptedException e)
            {
                logger.warning("Market ticker interrupted - stopping");
                break;
            } catch (Exception e)
            {
                logger.error("Market ticker error: " + e.getMessage());
            }
        }
    }
}
