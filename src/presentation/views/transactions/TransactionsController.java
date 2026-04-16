package presentation.views.transactions;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import presentation.views.portfolio.PortfolioRowViewModel;

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
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        symbolColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        sharesColumn.setCellValueFactory(new PropertyValueFactory<>("shares"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        transactionsTableView.getSelectionModel().setCellSelectionEnabled(false);
        transactionsTableView.setItems(transactionsViewModel.getTransactions());

        transactionsTableView.setRowFactory(_ -> {
            TableRow<TransactionRowViewModel> row = new TableRow<>();
            row.setOnMousePressed(_ -> {
                if (!row.isEmpty()) {
                    transactionsTableView.getSelectionModel().clearSelection();
                    transactionsTableView.getFocusModel().focus(-1);
                }
            });
            return row;
        });
    }
}
