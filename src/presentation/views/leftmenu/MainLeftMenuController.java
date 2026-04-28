package presentation.views.leftmenu;

import business.services.listener.StockAlertService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import presentation.core.notification.NotificationImpl;

public class MainLeftMenuController
{
    @FXML
    private Button transactionBtn;
    @FXML
    private Label transactionLbl;
    @FXML
    private VBox notificationContainer;
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

    private Button[] buttons;

    @FXML
    public void initialize()
    {
        Label[] labels = {portfolioLabel, stockMarketLabel, transactionLbl, menuTitleLabel, exitLabel};
        buttons = new Button[]{portfolioBtn, stockMarketBtn, transactionBtn};
        viewModel.setupMenu(menu, labels);

        viewModel.setButtonIcons(portfolioBtn, "/icons/portfolio.png", "/icons/portfolio-active.png");
        viewModel.setButtonIcons(stockMarketBtn, "/icons/stockmarket.png", "/icons/stockmarket-active.png");
        viewModel.setButtonIcons(transactionBtn, "/icons/transaction.png", "/icons/transaction-active.png");

        setupNotifications();
    }

    public MainLeftMenuController(MainLeftMenuViewModel viewModel, StockAlertService stockAlertService)
    {
        this.viewModel = viewModel;
        this.stockAlertService = stockAlertService;
    }

    private void setupNotifications()
    {
        new NotificationImpl(notificationContainer, stockAlertService);
    }

    @FXML
    public void handlePortfolioClicked()
    {
        viewModel.portfolioClicked(buttons, portfolioBtn);
    }

    @FXML
    public void handleStockMarketClicked()
    {
        viewModel.stockMarketClicked(buttons, stockMarketBtn);
    }

    @FXML
    public void handleTransactionClicked()
    {
        viewModel.transactionsClicked(buttons, transactionBtn);
    }

    @FXML
    public void handleLogoClicked()
    {
        viewModel.logoClicked(buttons);
    }

    @FXML
    public void handleExitClicked()
    {
        viewModel.exitClicked();
    }
}
