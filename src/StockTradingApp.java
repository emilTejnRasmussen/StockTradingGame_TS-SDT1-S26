import javafx.application.Application;
import javafx.stage.Stage;
import presentation.core.ViewManager;
import presentation.core.Views;

public class StockTradingApp extends Application
{
    @Override
    public void start(Stage primaryStage)
    {
        ViewManager.setStage(primaryStage);
        ViewManager.showScene(Views.MAIN_MENU);

        primaryStage.show();
    }
}
