package presentation.views.leftmenu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class MainLeftMenuController
{
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

    private Button[] buttons;

    public MainLeftMenuController(MainLeftMenuViewModel viewModel)
    {
        this.viewModel = viewModel;
    }

    public void initialize() {
        Label[] labels = {portfolioLabel, stockMarketLabel, menuTitleLabel, exitLabel};
        buttons = new Button[]{portfolioBtn, stockMarketBtn};
        viewModel.setupMenu(menu, labels);

        viewModel.setButtonIcons(portfolioBtn, "/icons/portfolio.png", "/icons/portfolio-active.png");
        viewModel.setButtonIcons(stockMarketBtn, "/icons/stockmarket.png", "/icons/stockmarket-active.png");
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
