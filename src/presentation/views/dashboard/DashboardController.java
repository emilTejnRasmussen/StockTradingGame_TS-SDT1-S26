package presentation.views.dashboard;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import presentation.core.ViewManager;
import presentation.core.Views;

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
        symbolColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        sharesColumn.setCellValueFactory(new PropertyValueFactory<>("shares"));
        avgPriceColumn.setCellValueFactory(new PropertyValueFactory<>("avgPrice"));
        currentPriceColumn.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        plColumn.setCellValueFactory(new PropertyValueFactory<>("pl"));

        holdingsTableView.getSelectionModel().setCellSelectionEnabled(false);

        holdingsTableView.setItems(dashboardViewModel.getHoldings());

        holdingsTableView.setRowFactory(tv -> {
            TableRow<HoldingRowViewModel> row = new TableRow<>();
            row.setOnMousePressed(event -> {
                if (!row.isEmpty()) {
                    holdingsTableView.getSelectionModel().clearSelection();
                    holdingsTableView.getFocusModel().focus(-1);
                }
            });
            return row;
        });
    }

    private void setupTransactionsTable()
    {
        transactionTimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        transactionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        transactionSymbolColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        transactionSharesColumn.setCellValueFactory(new PropertyValueFactory<>("shares"));
        transactionPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        transactionTotalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        transactionsTableView.getSelectionModel().setCellSelectionEnabled(false);

        transactionsTableView.setItems(dashboardViewModel.getTransactions());

        transactionsTableView.setRowFactory(tv -> {
            TableRow<TransactionRowViewModel> row = new TableRow<>();
            row.setOnMousePressed(event -> {
                if (!row.isEmpty()) {
                    transactionsTableView.getSelectionModel().clearSelection();
                    transactionsTableView.getFocusModel().focus(-1);
                }
            });
            return row;
        });
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