package business.services;

import business.services.listener.StockAlertService;
import business.services.listener.StockBankruptService;
import business.services.listener.StockListenerService;
import business.stockmarket.MarketTicker;
import business.stockmarket.StockMarket;
import entities.Portfolio;
import entities.Stock;
import persistence.fileImplementation.FileUnitOfWork;
import persistence.interfaces.*;
import presentation.core.ApplicationContext;
import shared.configuration.AppConfig;
import shared.logging.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class GameService
{
    private final UnitOfWork uow;

    private final PortfolioDao portfolioDao;
    private final StockDao stockDao;
    private final StockPriceHistoryDao stockPriceHistoryDao;
    private final OwnedStockDao ownedStockDao;
    private final MarketTicker marketTicker;

    private final StockListenerService stockListenerService;

    public GameService(UnitOfWork uow, PortfolioDao portfolioDao, StockDao stockDao, StockPriceHistoryDao stockPriceHistoryDao, OwnedStockDao ownedStockDao)
    {
        this.uow = uow;
        this.portfolioDao = portfolioDao;
        this.stockDao = stockDao;
        this.stockPriceHistoryDao = stockPriceHistoryDao;
        this.ownedStockDao = ownedStockDao;
        this.marketTicker = new MarketTicker();

        this.stockListenerService = new StockListenerService(uow, stockDao, stockPriceHistoryDao);
    }

    public void startGame()
    {
        resetGame();

        uow.begin();

        Portfolio portfolio = new Portfolio();
        portfolioDao.create(portfolio);

        uow.commit();

        loadGame();
    }
    public void resetGame() {
        clearGameData();
        addDefaultStocks();
    }

    public void loadGame() {
        UUID portfolioId = portfolioDao.getAll().getFirst().getId();
        ApplicationContext.getInstance().setActivePortfolioId(portfolioId);

        StockMarket stockMarket = StockMarket.getInstance();

        StockAlertService alertService = new StockAlertService();
        StockBankruptService stockBankruptService = new StockBankruptService(uow, ownedStockDao);

        stockMarket.addListener(stockListenerService);
        stockMarket.addListener(alertService);
        stockMarket.addListener(stockBankruptService);

        for (Stock stock : stockDao.getAll()){
            stockMarket.addExistingStock(stock);
        }

        marketTicker.startLiveStockUpdates();

    }

    public void stopGame() {
        marketTicker.stopLiveStockUpdates();
    }

    private void addDefaultStocks()
    {
        uow.begin();
        Stock stock1 = new Stock("AAPL", "Apple Inc.", AppConfig.getInstance().getStockResetValue());
        Stock stock2 = new Stock("MSFT", "Microsoft Corporation", AppConfig.getInstance().getStockResetValue());
        Stock stock3 = new Stock("GOOGL", "Alphabet Inc. Class A", AppConfig.getInstance().getStockResetValue());
        Stock stock4 = new Stock("AMZN", "Amazon.com Inc.", AppConfig.getInstance().getStockResetValue());

        stockDao.create(stock1);
        stockDao.create(stock2);
        stockDao.create(stock3);
        stockDao.create(stock4);
        uow.commit();
    }

    private void clearGameData()
    {
        FileUnitOfWork fUow = (FileUnitOfWork) uow;
        String directoryPath = fUow.getDirectoryPath();
        
        clearFile(directoryPath + fUow.getOwnedStocksFilename());
        clearFile(directoryPath + fUow.getPortfoliosFilename());
        clearFile(directoryPath + fUow.getStockPriceHistoryFilename());
        clearFile(directoryPath + fUow.getStocksFilename());
        clearFile(directoryPath + fUow.getTransactionsFilename());
    }

    private void clearFile(String filepath)
    {
        try (FileWriter writer = new FileWriter(filepath, false)) {
            writer.write("");
        } catch (IOException e)
        {
            Logger.getInstance().error("Could not clear json files: " + e.getMessage());
        }
    }

    public Optional<UUID> getCurrentPortfolioId()
    {
        return portfolioDao.getAll().stream()
                .findFirst()
                .map(Portfolio::getId);
    }

    public StockListenerService getStockListenerService()
    {
        return stockListenerService;
    }
}
