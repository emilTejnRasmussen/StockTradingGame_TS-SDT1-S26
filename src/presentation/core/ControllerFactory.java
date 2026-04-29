package presentation.core;

import business.services.listener.StockAlertService;
import javafx.util.Callback;
import persistence.fileImplementation.*;
import persistence.interfaces.*;
import presentation.core.notification.CustomAlertBoxAdapter;
import presentation.core.notification.NotificationHandler;
import presentation.views.dashboard.DashboardController;
import presentation.views.leftmenu.MainLeftMenuController;
import presentation.views.mainmenu.MainMenuController;
import presentation.views.portfolio.PortfolioController;
import presentation.views.stockmarket.StockMarketController;
import presentation.views.transactions.TransactionsController;


public class ControllerFactory implements Callback<Class<?>, Object>
{
    private final ApplicationContext appContext = ApplicationContext.getInstance();

    @Override
    public Object call(Class<?> controllerType)
    {
        if (controllerType == MainMenuController.class) {
            return new MainMenuController(appContext.getMainMenuViewModel());
        }

        if (controllerType == MainLeftMenuController.class) {
            return new MainLeftMenuController(
                    appContext.getMainLeftMenuViewModel(),
                    appContext.getNotificationHandler()
            );
        }

        if (controllerType == PortfolioController.class) {
            return new PortfolioController(appContext.getPortfolioViewModel());
        }

        if (controllerType == StockMarketController.class)
        {
            return new StockMarketController(appContext.getStockMarketViewModel());
        }

        if (controllerType == DashboardController.class) {
            return new DashboardController(appContext.getDashboardViewModel());
        }

        if (controllerType == TransactionsController.class){
            return new TransactionsController(appContext.getTransactionsViewModel());
        }

        throw new RuntimeException("Controller of type '" + controllerType.getSimpleName() + "' is not supported!");
    }
}