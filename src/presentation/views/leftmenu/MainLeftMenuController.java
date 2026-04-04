package presentation.views.leftmenu;

import business.services.listener.StockAlertService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import presentation.core.notification.NotificationService;

public class MainLeftMenuController
{
    @FXML
    private VBox notificationContainer;
    @FXML
    private Button exitBtn;
    @FXML
    private Label exitLabel;
    @FXML
    private Label menuTitleLabel;
    @FXML
    private Label stockMarketLabel;
    @FXML
    private Button stockMarketBtn;
    @FXML
    private Button portfolioBtn;
    @FXML
    private Label portfolioLabel;
    @FXML
    private VBox menu;

    private final MainLeftMenuViewModel viewModel;
    private final StockAlertService stockAlertService;
    private NotificationService notificationService;

    private Button[] buttons;

    public MainLeftMenuController(MainLeftMenuViewModel viewModel, StockAlertService stockAlertService)
    {
        this.viewModel = viewModel;
        this.stockAlertService = stockAlertService;
    }

    public void initialize()
    {
        Label[] labels = {portfolioLabel, stockMarketLabel, menuTitleLabel, exitLabel};
        buttons = new Button[]{portfolioBtn, stockMarketBtn};
        viewModel.setupMenu(menu, labels);

        viewModel.setButtonIcons(portfolioBtn, "/icons/portfolio.png", "/icons/portfolio-active.png");
        viewModel.setButtonIcons(stockMarketBtn, "/icons/stockmarket.png", "/icons/stockmarket-active.png");

        setupNotifications();
    }

    private void setupNotifications()
    {
        notificationService = new NotificationService(notificationContainer, stockAlertService);

        // TEST notifications


        notificationService.showNotification("bankrupt", "AMZN went bankrupt!", "bankrupt");
        notificationService.showNotification("Goal reached","AAPL exceeded price 200!", "goal");
        notificationService.showNotification("Stock reset", "MSFT reset and is tradable again!", "reset");

    }

    @FXML
    public void handlePortfolioClicked()
    {
        viewModel.portfolioClicked();
        viewModel.setActiveBtn(buttons, portfolioBtn);
    }

    @FXML
    public void handleStockMarketClicked()
    {
        viewModel.stockMarketClicked();
        viewModel.setActiveBtn(buttons, stockMarketBtn);
    }

    public void handleLogoClicked()
    {
        viewModel.logoClicked();
        viewModel.setActiveBtn(buttons, null);
    }

    public void handleExitClicked()
    {
        viewModel.exitClicked();
    }
}
