import business.stockmarket.MarketTickerThread;
import business.stockmarket.StockMarket;
import entities.Stock;
import persistence.fileImplementation.FileUnitOfWork;
import shared.configuration.AppConfig;

void main() throws InterruptedException
{
    StockMarket stockMarket = StockMarket.getInstance();
    FileUnitOfWork uow = new FileUnitOfWork("data/");
    List<Stock> stocks = uow.getStocks();

//    for (Stock stock : stocks) {
//        stockMarket.addExistingStock(stock);
//    }

    Stock stock = new Stock("AAPL", "Apple", AppConfig.getInstance().getStockResetValue());
    stockMarket.addExistingStock(stock);

    MarketTickerThread ticker = new MarketTickerThread();
    Thread tickerThread = new Thread(ticker, "MarketTicker");

    tickerThread.start();

    System.out.println("Ticker running for 30 seconds...");

    Thread.sleep(30_000);

    System.out.println("Interrupting ticker...");
    tickerThread.interrupt();

    tickerThread.join();

    System.out.println("Ticker stopped.");
}