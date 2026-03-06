import business.dto.StockDTO;
import business.services.StockListenerService;
import business.stockmarket.MarketTickerThread;
import business.stockmarket.StockMarket;
import entities.Stock;
import entities.StockPriceHistory;
import persistence.fileImplementation.FileStockDao;
import persistence.fileImplementation.FileStockPriceHistoryDao;
import persistence.fileImplementation.FileUnitOfWork;
import persistence.interfaces.StockPriceHistoryDao;

void main() throws InterruptedException
{
    StockMarket stockMarket = StockMarket.getInstance();

    FileUnitOfWork uow = new FileUnitOfWork("data/");
    FileStockDao stockDao = new FileStockDao(uow);
    StockPriceHistoryDao stockPriceHistoryDao = new FileStockPriceHistoryDao(uow);

    StockListenerService stockListenerService =
            new StockListenerService(uow, stockDao, stockPriceHistoryDao);

    stockMarket.addListener(stockListenerService);

    stockListenerService.addListener(evt -> {
        StockDTO stockDTO = (StockDTO) evt.getNewValue();
        System.out.println("Received update: "
                + stockDTO.symbol()
                + " -> "
                + stockDTO.currentPrice()
                + " | "
                + stockDTO.currentState());
    });

    List<Stock> stocks = uow.getStocks();

    for (Stock stock : stocks)
    {
        stockMarket.addExistingStock(stock);
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
    System.out.println("Check stock and stock price history files for persisted updates.");
}
