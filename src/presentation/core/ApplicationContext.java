package presentation.core;

import business.services.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import presentation.views.dashboard.DashboardViewModel;
import presentation.views.dashboard.HoldingRowViewModel;
import presentation.views.dashboard.TransactionRowViewModel;
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
    private final StockService stockService;

    private final StockMarketViewModel stockMarketViewModel;
    private final MainMenuViewModel mainMenuViewModel;
    private final MainLeftMenuViewModel mainLeftMenuViewModel;
    private final DashboardViewModel dashboardViewModel;

    private final ObjectProperty<UUID> activePortfolioId = new SimpleObjectProperty<>();

    private ApplicationContext()
    {
        uow = new FileUnitOfWork("data/");

        portfolioDao = new FilePortfolioDao(uow);
        stockDao = new FileStockDao(uow);
        stockPriceHistoryDao = new FileStockPriceHistoryDao(uow);
        ownedStockDao = new FileOwnedStockDao(uow);
        transactionDao = new FileTransactionDao(uow);

        portfolioService = createPortfolioService();
        tradingService = createTradingService();
        stockHistoryService = createStockHistoryService();
        gameService = createGameService();
        stockService = createStockService();

        stockMarketViewModel = createStockMarketViewModel();
        dashboardViewModel = createDashboardViewModel();

        mainMenuViewModel = createMainMenuViewModel();
        mainLeftMenuViewModel = createMainLeftMenuViewModel();
    }

    private StockService createStockService()
    {
        return new StockService(stockDao);
    }

    private MainLeftMenuViewModel createMainLeftMenuViewModel()
    {
        return new MainLeftMenuViewModel(gameService);
    }

    private MainMenuViewModel createMainMenuViewModel()
    {
        return new MainMenuViewModel(gameService, portfolioService);
    }

    private DashboardViewModel createDashboardViewModel()
    {
        return new DashboardViewModel(
                this,
                portfolioService,
                stockService,
                gameService.getStockListenerService()
        );
    }

    private StockMarketViewModel createStockMarketViewModel()
    {
        return new StockMarketViewModel(
                this,
                gameService.getStockListenerService(),
                stockHistoryService,
                portfolioService,
                tradingService
        );
    }

    private GameService createGameService()
    {
        return new GameService(
                uow,
                portfolioDao,
                stockDao,
                stockPriceHistoryDao,
                ownedStockDao
        );
    }

    private StockHistoryService createStockHistoryService()
    {
        return new StockHistoryService(stockPriceHistoryDao);
    }

    private TradingService createTradingService()
    {
        return new TradingService(
                uow,
                stockDao,
                portfolioDao,
                transactionDao,
                ownedStockDao,
                Logger.getInstance()
        );
    }

    private PortfolioService createPortfolioService()
    {
        return new PortfolioService(
                portfolioDao,
                ownedStockDao,
                stockDao,
                transactionDao
        );
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

    public DashboardViewModel getDashboardViewModel()
    {
        return dashboardViewModel;
    }

    public UUID getActivePortfolioId()
    {
        return activePortfolioId.get();
    }

    public void setActivePortfolioId(UUID portfolioId)
    {
        activePortfolioId.set(portfolioId);
    }

    public ObjectProperty<UUID> activePortfolioIdProperty()
    {
        return activePortfolioId;
    }
}