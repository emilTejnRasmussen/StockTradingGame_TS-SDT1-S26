package presentation.core;

import business.services.GameService;
import business.services.PortfolioService;
import business.services.StockHistoryService;
import business.services.TradingService;
import persistence.fileImplementation.FileOwnedStockDao;
import persistence.fileImplementation.FilePortfolioDao;
import persistence.fileImplementation.FileStockDao;
import persistence.fileImplementation.FileStockPriceHistoryDao;
import persistence.fileImplementation.FileTransactionDao;
import persistence.fileImplementation.FileUnitOfWork;
import persistence.interfaces.OwnedStockDao;
import persistence.interfaces.PortfolioDao;
import persistence.interfaces.StockDao;
import persistence.interfaces.StockPriceHistoryDao;
import persistence.interfaces.TransactionDao;
import presentation.views.leftmenu.MainLeftMenuViewModel;
import presentation.views.mainmenu.MainMenuViewModel;
import presentation.views.portfolio.PortfolioViewModel;
import presentation.views.stockmarket.StockMarketViewModel;
import shared.logging.Logger;

import java.util.UUID;

public class ApplicationContext
{
    private static ApplicationContext instance;

    private final FileUnitOfWork uow;

    private final PortfolioDao portfolioDao;
    private final StockDao stockDao;
    private final StockPriceHistoryDao stockPriceHistoryDao;
    private final OwnedStockDao ownedStockDao;
    private final TransactionDao transactionDao;

    private final GameService gameService;
    private final PortfolioService portfolioService;
    private final TradingService tradingService;
    private final StockHistoryService stockHistoryService;

    private final StockMarketViewModel stockMarketViewModel;
    private final MainMenuViewModel mainMenuViewModel;
    private final MainLeftMenuViewModel mainLeftMenuViewModel;

    private ApplicationContext()
    {
        uow = new FileUnitOfWork("data/");

        portfolioDao = new FilePortfolioDao(uow);
        stockDao = new FileStockDao(uow);
        stockPriceHistoryDao = new FileStockPriceHistoryDao(uow);
        ownedStockDao = new FileOwnedStockDao(uow);
        transactionDao = new FileTransactionDao(uow);

        portfolioService = new PortfolioService(
                portfolioDao,
                ownedStockDao,
                stockDao,
                transactionDao
        );

        tradingService = new TradingService(
                uow,
                stockDao,
                portfolioDao,
                transactionDao,
                ownedStockDao,
                Logger.getInstance()
        );

        stockHistoryService = new StockHistoryService(stockPriceHistoryDao);

        gameService = new GameService(
                uow,
                portfolioDao,
                stockDao,
                stockPriceHistoryDao,
                ownedStockDao
        );

        stockMarketViewModel = new StockMarketViewModel(
                gameService.getStockListenerService(),
                stockHistoryService,
                portfolioService,
                tradingService
        );

        mainMenuViewModel = new MainMenuViewModel(gameService, portfolioService);
        mainLeftMenuViewModel = new MainLeftMenuViewModel(gameService);
    }

    public static ApplicationContext getInstance()
    {
        ApplicationContext result = instance;
        if (result == null)
        {
            synchronized (ApplicationContext.class)
            {
                result = instance;
                if (result == null)
                {
                    instance = result = new ApplicationContext();
                }
            }
        }
        return result;
    }

    public MainMenuViewModel getMainMenuViewModel()
    {
        return mainMenuViewModel;
    }

    public MainLeftMenuViewModel getMainLeftMenuViewModel()
    {
        return mainLeftMenuViewModel;
    }

    public StockMarketViewModel getStockMarketViewModel()
    {
        return stockMarketViewModel;
    }

    public PortfolioViewModel getPortfolioViewModel()
    {
        return new PortfolioViewModel();
    }
}