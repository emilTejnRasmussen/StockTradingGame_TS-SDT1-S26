import business.stockmarket.MarketTickerThread;
import business.stockmarket.StockMarket;
import entities.Stock;
import persistence.fileImplementation.FileStockDao;
import persistence.fileImplementation.FileUnitOfWork;

void main() {
    // ---- Get stock market singleton ----
    StockMarket stockMarket = StockMarket.getInstance();

    FileUnitOfWork uow = new FileUnitOfWork("data/");
    FileStockDao fileStockDao = new FileStockDao(uow);
    List<Stock> stocks = fileStockDao.getAll();

    for (Stock stock : stocks) {
        stockMarket.addExistingStock(stock);
    }


    // ---- Add test stocks ----
//    stockMarket.addLiveStock("AAPL");
//    stockMarket.addLiveStock("GOOGL");
//    stockMarket.addLiveStock("TSLA");
//    stockMarket.addLiveStock("MSFT");
//    stockMarket.addLiveStock("NVDA");

    System.out.println("Stocks added. Starting market ticker...\n");

    // ---- Start market ticker thread ----
    Thread tickerThread = new Thread(new MarketTickerThread(), "MarketTickerThread");
    tickerThread.start();

    // ---- Keep program alive (demo purpose) ----
    try {
        Thread.sleep(20_000); // run simulation for 30 seconds
    } catch (InterruptedException ignored) {}

    System.out.println("\nTest finished.");

    tickerThread.interrupt();

    try
    {
        tickerThread.join(2_000);
    } catch (InterruptedException _)
    {
    }
}