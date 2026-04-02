package presentation.core;

import business.services.GameService;
import business.services.StockHistoryService;
import persistence.fileImplementation.*;
import persistence.interfaces.*;
import presentation.views.leftmenu.MainLeftMenuViewModel;
import presentation.views.mainmenu.MainMenuViewModel;
import presentation.views.portfolio.PortfolioViewModel;
import presentation.views.stockmarket.StockMarketViewModel;

public class ApplicationContext
{
    private static volatile ApplicationContext instance;

    private final GameService gameService;

   private ApplicationContext() {
       UnitOfWork uow = createUow();

       PortfolioDao portfolioDao = createPortfolioDao(uow);
       StockDao stockDao = createStockDao(uow);
       StockPriceHistoryDao stockPriceHistoryDao = createStockPriceHistoryDao(uow);
       OwnedStockDao ownedStockDao = createOwnedStockDao(uow);

       this.gameService = new GameService(
               uow,
               portfolioDao,
               stockDao,
               stockPriceHistoryDao,
               ownedStockDao
       );
   }


    public static ApplicationContext getInstance() {
        ApplicationContext result = instance;
        if (result == null) {
            synchronized (ApplicationContext.class) {
                result = instance;
                if (result == null) {
                    instance = result = new ApplicationContext();
                }
            }
        }

        return result;
    }

    private OwnedStockDao createOwnedStockDao(UnitOfWork uow)
    {
        return new FileOwnedStockDao((FileUnitOfWork) uow);
    }

    private StockPriceHistoryDao createStockPriceHistoryDao(UnitOfWork uow)
    {
        return new FileStockPriceHistoryDao((FileUnitOfWork) uow);
    }

    private StockDao createStockDao(UnitOfWork uow)
    {
        return new FileStockDao((FileUnitOfWork) uow);
    }

    private PortfolioDao createPortfolioDao(UnitOfWork uow)
    {
        return new FilePortfolioDao((FileUnitOfWork) uow);
    }

    private UnitOfWork createUow() {
       return new FileUnitOfWork("data/");
    }

    private StockPriceHistoryDao createStockPriceHistoryDao() {
       return new FileStockPriceHistoryDao((FileUnitOfWork) createUow());
    }

    private StockHistoryService createStockHistoryService() {
       return new StockHistoryService(createStockPriceHistoryDao());
    }

    public StockMarketViewModel getStockMarketViewModel()
    {
        return new StockMarketViewModel(gameService.getStockListenerService(), createStockHistoryService());
    }

    public MainLeftMenuViewModel getMainLeftMenuViewModel()
    {
        return new MainLeftMenuViewModel(gameService);
    }

    public PortfolioViewModel getPortfolioViewModel()
    {
        return new PortfolioViewModel();
    }

    public MainMenuViewModel getMainMenuViewModel()
    {
        return new MainMenuViewModel(gameService);
    }
}
