package presentation.views.transactions;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import presentation.views.utility.SetupViewUtil;

import java.util.HashMap;
import java.util.Map;

public class TransactionsController
{
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
    private TableView<TransactionTableRow> transactionsTableView;
    @FXML
    private TableColumn<TransactionTableRow, String> timeColumn;
    @FXML
    private TableColumn<TransactionTableRow, String> typeColumn;
    @FXML
    private TableColumn<TransactionTableRow, String> symbolColumn;
    @FXML
    private TableColumn<TransactionTableRow, Integer> sharesColumn;
    @FXML
    private TableColumn<TransactionTableRow, String> priceColumn;
    @FXML
    private TableColumn<TransactionTableRow, String> totalColumn;
    @FXML
    private Label resultInfoLabel;

    private final TransactionsViewModel transactionsViewModel;

    @FXML
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
        Map<TableColumn<TransactionTableRow, ?>, String> columnMappings = new HashMap<>(Map.of(
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
