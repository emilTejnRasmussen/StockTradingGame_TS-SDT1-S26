package presentation.core;

import javafx.util.Callback;
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

    @Override
    public Object call(Class<?> controllerType)
    {
        if (controllerType == MainMenuController.class) {
            MainMenuViewModel mainMenuViewModel = new MainMenuViewModel();

            return new MainMenuController(mainMenuViewModel);
        }

        if (controllerType == MainLeftMenuController.class) {
            MainLeftMenuViewModel mainLeftMenuViewModel = new MainLeftMenuViewModel();
            return new MainLeftMenuController(mainLeftMenuViewModel);
        }

        if (controllerType == PortfolioController.class) {
            PortfolioViewModel portfolioViewModel = new PortfolioViewModel();
            return new PortfolioController(portfolioViewModel);
        }

        if (controllerType == StockMarketController.class)
        {
            StockMarketViewModel stockMarketViewModel = new StockMarketViewModel();
            return new StockMarketController(stockMarketViewModel);
        }

        throw new RuntimeException("Controller of type '" + controllerType.getSimpleName() + "' is not supported!");
    }
}