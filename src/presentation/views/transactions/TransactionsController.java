package presentation.views.transactions;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import presentation.views.portfolio.PortfolioRowViewModel;
import presentation.views.utility.SetupViewUtil;

import java.util.HashMap;
import java.util.Map;

public class TransactionsController
{
    @FXML
    private Button refreshButton;
    @FXML
    private Label totalTransactionsLabel;
    @FXML
    private Label buyCountLabel;
    @FXML
    private Label sellCountLabel;
    @FXML
    private Label latestActivityLabel;
    @FXML
    private Label pageInfoLabel;
    @FXML
    private TableView<TransactionRowViewModel> transactionsTableView;
    @FXML
    private TableColumn<TransactionRowViewModel, String> timeColumn;
    @FXML
    private TableColumn<TransactionRowViewModel, String> typeColumn;
    @FXML
    private TableColumn<TransactionRowViewModel, String> symbolColumn;
    @FXML
    private TableColumn<TransactionRowViewModel, Integer> sharesColumn;
    @FXML
    private TableColumn<TransactionRowViewModel, String> priceColumn;
    @FXML
    private TableColumn<TransactionRowViewModel, String> totalColumn;
    @FXML
    private Label resultInfoLabel;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;

    private final TransactionsViewModel transactionsViewModel;


    public void initialize()
    {
        setupTransactionsTable();
        bindViewModel();
        transactionsViewModel.load();
    }

    public TransactionsController(TransactionsViewModel transactionsViewModel)
    {
        this.transactionsViewModel = transactionsViewModel;
    }

    @FXML
    public void handlePreviousPage()
    {
        transactionsViewModel.previousPage();
    }

    @FXML
    public void handleNextPage()
    {
        transactionsViewModel.nextPage();
    }

    private void bindViewModel()
    {
        totalTransactionsLabel.textProperty().bind(transactionsViewModel.totalTransactionsProperty());
        buyCountLabel.textProperty().bind(transactionsViewModel.buyCountProperty());
        sellCountLabel.textProperty().bind(transactionsViewModel.sellCountProperty());
        latestActivityLabel.textProperty().bind(transactionsViewModel.latestActivityProperty());
        pageInfoLabel.textProperty().bind(transactionsViewModel.pageInfoProperty());
        resultInfoLabel.textProperty().bind(transactionsViewModel.resultInfoProperty());
    }

    private void setupTransactionsTable()
    {
        Map<TableColumn<TransactionRowViewModel, ?>, String> columnMappings = new HashMap<>(Map.of(
                timeColumn, "time",
                typeColumn, "type",
                symbolColumn, "symbol",
                sharesColumn, "shares",
                priceColumn, "price",
                totalColumn, "total"
        ));
        SetupViewUtil.setupTableView(transactionsTableView, columnMappings);
        transactionsTableView.setItems(transactionsViewModel.getTransactions());
    }
}
