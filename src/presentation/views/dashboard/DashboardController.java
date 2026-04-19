package presentation.views.dashboard;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import presentation.core.ViewManager;
import presentation.core.Views;
import presentation.views.utility.SetupViewUtil;

import java.util.HashMap;
import java.util.Map;

public class DashboardController
{
    @FXML
    private Label netWorthLabel;
    @FXML
    private Label cashBalanceLabel;
    @FXML
    private Label cashStatusLabel;
    @FXML
    private Label totalStockValueLabel;
    @FXML
    private Label ownedStocksCountLabel;
    @FXML
    private Label totalPLLabel;
    @FXML
    private Label totalPLPercentLabel;
    @FXML
    private Label holdingsUpdatedLabel;

    @FXML
    private TableView<HoldingRowViewModel> holdingsTableView;
    @FXML
    private TableColumn<HoldingRowViewModel, String> symbolColumn;
    @FXML
    private TableColumn<HoldingRowViewModel, Integer> sharesColumn;
    @FXML
    private TableColumn<HoldingRowViewModel, String> avgPriceColumn;
    @FXML
    private TableColumn<HoldingRowViewModel, String> currentPriceColumn;
    @FXML
    private TableColumn<HoldingRowViewModel, String> valueColumn;
    @FXML
    private TableColumn<HoldingRowViewModel, String> plColumn;

    @FXML
    private PieChart sharesPieChart;

    @FXML
    private Label transactionsCountLabel;
    @FXML
    private TableView<TransactionRowViewModel> transactionsTableView;
    @FXML
    private TableColumn<TransactionRowViewModel, String> transactionTimeColumn;
    @FXML
    private TableColumn<TransactionRowViewModel, String> transactionTypeColumn;
    @FXML
    private TableColumn<TransactionRowViewModel, String> transactionSymbolColumn;
    @FXML
    private TableColumn<TransactionRowViewModel, Integer> transactionSharesColumn;
    @FXML
    private TableColumn<TransactionRowViewModel, String> transactionPriceColumn;
    @FXML
    private TableColumn<TransactionRowViewModel, String> transactionTotalColumn;

    @FXML
    private Label bestPerformerLabel;
    @FXML
    private Label worstPerformerLabel;
    @FXML
    private Label totalSharesLabel;
    @FXML
    private Label ownedStocksLabel;


    private final DashboardViewModel dashboardViewModel;

    public DashboardController(DashboardViewModel dashboardViewModel)
    {
        this.dashboardViewModel = dashboardViewModel;
    }

    @FXML
    public void initialize()
    {
        setupHoldingsTable();
        setupTransactionsTable();
        setupPieChart();
        bindViewModel();
        dashboardViewModel.load();
    }

    private void setupPieChart()
    {
        var data = dashboardViewModel.buildShareDistribution();

        javafx.application.Platform.runLater(() -> {
            sharesPieChart.setPrefSize(300, 300);
            sharesPieChart.setMinSize(200, 200);
            sharesPieChart.setLabelsVisible(true);
            sharesPieChart.setLegendVisible(true);
            sharesPieChart.setData(data);
        });
    }

    private void setupHoldingsTable()
    {
        Map<TableColumn<HoldingRowViewModel, ?>, String> columnMappings = new HashMap<>(Map.of(
                symbolColumn, "symbol",
                sharesColumn, "shares",
                avgPriceColumn, "avgPrice",
                currentPriceColumn, "currentPrice",
                valueColumn, "value",
                plColumn , "pl"
        ));

        SetupViewUtil.setupTableView(holdingsTableView, columnMappings);
        holdingsTableView.setItems(dashboardViewModel.getHoldings());
    }

    private void setupTransactionsTable()
    {
        Map<TableColumn<TransactionRowViewModel, ?>, String> columnMappings = new HashMap<>(Map.of(
                transactionTimeColumn, "time",
                transactionTypeColumn, "type",
                transactionSymbolColumn, "symbol",
                transactionSharesColumn, "shares",
                transactionPriceColumn, "price",
                transactionTotalColumn, "total"
        ));

        SetupViewUtil.setupTableView(transactionsTableView, columnMappings);
        transactionsTableView.setItems(dashboardViewModel.getTransactions());
    }

    private void bindViewModel()
    {
        netWorthLabel.textProperty().bind(dashboardViewModel.netWorthProperty());

        cashBalanceLabel.textProperty().bind(dashboardViewModel.cashBalanceProperty());
        cashStatusLabel.textProperty().bind(dashboardViewModel.cashStatusProperty());

        totalStockValueLabel.textProperty().bind(dashboardViewModel.totalStockValueProperty());
        ownedStocksCountLabel.textProperty().bind(dashboardViewModel.ownedStocksCountProperty());

        totalPLLabel.textProperty().bind(dashboardViewModel.totalPLProperty());
        totalPLPercentLabel.textProperty().bind(dashboardViewModel.totalPLPercentProperty());

        holdingsUpdatedLabel.textProperty().bind(dashboardViewModel.holdingsUpdatedTextProperty());
        transactionsCountLabel.textProperty().bind(dashboardViewModel.transactionsCountTextProperty());

        bestPerformerLabel.textProperty().bind(dashboardViewModel.bestPerformerProperty());
        worstPerformerLabel.textProperty().bind(dashboardViewModel.worstPerformerProperty());
        totalSharesLabel.textProperty().bind(dashboardViewModel.totalSharesProperty());
        ownedStocksLabel.textProperty().bind(dashboardViewModel.ownedStocksProperty());

        sharesPieChart.setData(dashboardViewModel.getShareDistribution());
    }

    @FXML
    public void handleOpenMarket()
    {
        ViewManager.setCenter(Views.STOCK_MARKET);
    }

    @FXML
    public void handleOpenPortfolio()
    {
        ViewManager.setCenter(Views.PORTFOLIO);
    }
}