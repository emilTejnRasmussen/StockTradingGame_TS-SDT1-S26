package presentation.core;

import business.services.GameService;
import business.stockmarket.MarketTicker;
import javafx.util.Callback;
import persistence.fileImplementation.*;
import persistence.interfaces.*;
import presentation.views.leftmenu.MainLeftMenuController;
import presentation.views.leftmenu.MainLeftMenuViewModel;
import presentation.views.mainmenu.MainMenuController;
import presentation.views.mainmenu.MainMenuViewModel;
import presentation.views.portfolio.PortfolioController;
import presentation.views.portfolio.PortfolioViewModel;
import presentation.views.stockmarket.StockMarketController;
import presentation.views.stockmarket.StockMarketViewModel;


public class ControllerFactory implements Callback<Class<?>, Object>
{
    private GameService gameService;

    @Override
    public Object call(Class<?> controllerType)
    {
        if (controllerType == MainMenuController.class) {
            UnitOfWork uow = createUOW();
            GameService gameService = createGameService(uow);

            MainMenuViewModel mainMenuViewModel = new MainMenuViewModel(gameService);

            return new MainMenuController(mainMenuViewModel);
        }

        if (controllerType == MainLeftMenuController.class) {
            UnitOfWork uow = createUOW();
            GameService gameService = createGameService(uow);
            MainLeftMenuViewModel mainLeftMenuViewModel = new MainLeftMenuViewModel(gameService);
            return new MainLeftMenuController(mainLeftMenuViewModel);
        }

        if (controllerType == PortfolioController.class) {
            PortfolioViewModel portfolioViewModel = new PortfolioViewModel();
            return new PortfolioController(portfolioViewModel);
        }

        if (controllerType == StockMarketController.class)
        {
            StockMarketViewModel stockMarketViewModel = new StockMarketViewModel(gameService.getStockListenerService());
            return new StockMarketController(stockMarketViewModel);
        }

        throw new RuntimeException("Controller of type '" + controllerType.getSimpleName() + "' is not supported!");
    }

    private GameService createGameService(UnitOfWork uow)
    {
        if (gameService == null) {
            gameService = new GameService(
                    uow,
                    createPortfolioDAO(uow),
                    createStockDAO(uow),
                    createStockPriceHistoryDAO(uow),
                    createOwnedStockDAO(uow)
            );
        }

        return gameService;
    }

    private OwnedStockDao createOwnedStockDAO(UnitOfWork uow)
    {
        return new FileOwnedStockDao((FileUnitOfWork) uow);
    }

    private StockPriceHistoryDao createStockPriceHistoryDAO(UnitOfWork uow)
    {
        return new FileStockPriceHistoryDao((FileUnitOfWork) uow);
    }

    private StockDao createStockDAO(UnitOfWork uow)
    {
        return new FileStockDao((FileUnitOfWork) uow);
    }

    private PortfolioDao createPortfolioDAO(UnitOfWork uow)
    {
        return new FilePortfolioDao((FileUnitOfWork) uow);
    }

    private UnitOfWork createUOW()
    {
        return new FileUnitOfWork("data/");
    }
}