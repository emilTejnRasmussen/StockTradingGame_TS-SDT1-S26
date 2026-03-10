import business.services.StockListenerService;
import business.stockmarket.MarketTickerThread;
import business.stockmarket.StockMarket;
import business.stockmarket.simulation.LiveStock;
import entities.Stock;
import persistence.fileImplementation.FileStockDao;
import persistence.fileImplementation.FileStockPriceHistoryDao;
import persistence.fileImplementation.FileUnitOfWork;

void main() throws InterruptedException
{
    StockMarket stockMarket = StockMarket.getInstance();

    FileUnitOfWork uow = new FileUnitOfWork("data/");
    FileStockDao stockDao = new FileStockDao(uow);
    FileStockPriceHistoryDao stockPriceHistoryDao = new FileStockPriceHistoryDao(uow);

    StockListenerService listenerService =
            new StockListenerService(uow, stockDao, stockPriceHistoryDao);

    stockMarket.addListener(listenerService);

    List<Stock> stocks = uow.getStocks();

    for (Stock stock : stocks)
    {
        if (stock.getSymbol().equals("AAPL")){

            stockMarket.addExistingStock(stock);
        }
    }

    MarketTickerThread ticker = new MarketTickerThread();
    Thread tickerThread = new Thread(ticker, "MarketTicker");

    tickerThread.start();

    System.out.println("Ticker running for 10 seconds...");

    Thread.sleep(10_000);

    System.out.println("Interrupting ticker...");

    tickerThread.interrupt();

    tickerThread.join();

    System.out.println("Ticker stopped.");
}