import business.services.PortfolioService;
import business.services.StockBankruptService;
import business.services.StockListenerService;
import business.stockmarket.MarketTickerThread;
import business.stockmarket.StockMarket;
import entities.Stock;
import persistence.fileImplementation.FileOwnedStockDao;
import persistence.fileImplementation.FilePortfolioDao;
import persistence.fileImplementation.FileStockDao;
import persistence.fileImplementation.FileStockPriceHistoryDao;
import persistence.fileImplementation.FileUnitOfWork;

import java.beans.PropertyChangeEvent;
import java.util.List;

public class StockMarketListenerDemo
{
    public static void main(String[] args) throws InterruptedException
    {
        StockMarket stockMarket = StockMarket.getInstance();

        FileUnitOfWork uow = new FileUnitOfWork("data/");
        StockListenerService listenerService = createStockListenerService(uow);

        stockMarket.addListener(listenerService);
        listenerService.addListener(StockMarketListenerDemo::printUpdate);

        addAppleStockToMarket(stockMarket, uow.getStocks());

        Thread tickerThread = new Thread(new MarketTickerThread(), "MarketTicker");
        tickerThread.start();

        System.out.println("Ticker running for 10 seconds...");
        Thread.sleep(10_000);

        System.out.println("Interrupting ticker...");
        tickerThread.interrupt();
        tickerThread.join();

        System.out.println("Ticker stopped.");
    }

    private static void printUpdate(PropertyChangeEvent event)
    {
        System.out.println("Received update: " + event);
    }

    private static void addAppleStockToMarket(StockMarket stockMarket, List<Stock> stocks)
    {
        for (Stock stock : stocks)
        {
            if ("AAPL".equals(stock.getSymbol()))
            {
                stockMarket.addExistingStock(stock);
            }
        }
    }

    private static StockListenerService createStockListenerService(FileUnitOfWork uow)
    {
        FileStockDao stockDao = new FileStockDao(uow);
        FileStockPriceHistoryDao stockPriceHistoryDao = new FileStockPriceHistoryDao(uow);
        FileOwnedStockDao ownedStockDao = new FileOwnedStockDao(uow);
        FilePortfolioDao portfolioDao = new FilePortfolioDao(uow);

        PortfolioService portfolioService = new PortfolioService(stockDao, ownedStockDao, portfolioDao);
        StockBankruptService bankruptService = new StockBankruptService(ownedStockDao, portfolioService);

        return new StockListenerService(uow, stockDao, stockPriceHistoryDao, bankruptService);
    }
}