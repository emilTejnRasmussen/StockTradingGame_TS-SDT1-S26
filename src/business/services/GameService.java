package business.services;

import business.services.listener.StockAlertService;
import business.services.listener.StockBankruptService;
import business.services.listener.StockListenerService;
import business.stockmarket.MarketTicker;
import business.stockmarket.StockMarket;
import entities.Portfolio;
import entities.Stock;
import persistence.interfaces.*;

public class GameService
{
    private final UnitOfWork uow;

    private final PortfolioDao portfolioDao;
    private final StockDao stockDao;
    private final StockPriceHistoryDao stockPriceHistoryDao;
    private final OwnedStockDao ownedStockDao;
    private final MarketTicker marketTicker;

    public GameService(UnitOfWork uow, PortfolioDao portfolioDao, StockDao stockDao, StockPriceHistoryDao stockPriceHistoryDao, OwnedStockDao ownedStockDao)
    {
        this.uow = uow;
        this.portfolioDao = portfolioDao;
        this.stockDao = stockDao;
        this.stockPriceHistoryDao = stockPriceHistoryDao;
        this.ownedStockDao = ownedStockDao;
        this.marketTicker = new MarketTicker();
    }

    public void startGame() {
        uow.begin();

        Portfolio portfolio = new Portfolio();
        portfolioDao.create(portfolio);

        StockMarket stockMarket = StockMarket.getInstance();

        StockListenerService listenerService = new StockListenerService(uow, stockDao, stockPriceHistoryDao);
        StockAlertService alertService = new StockAlertService();
        StockBankruptService stockBankruptService = new StockBankruptService(uow, ownedStockDao);

        stockMarket.addListener(listenerService);
        stockMarket.addListener(alertService);
        stockMarket.addListener(stockBankruptService);

        for (Stock stock : stockDao.getAll()){
            stockMarket.addExistingStock(stock);
        }

        marketTicker.startLiveStockUpdates();

        uow.commit();
    }
    public void resetGame() {
        uow.begin();


    }
    public void loadGame() {

    }

    public void stopGame() {
        marketTicker.stopLiveStockUpdates();
    }
}
