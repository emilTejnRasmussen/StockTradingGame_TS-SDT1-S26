package presentation.views.transactions;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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


    public void initialize() {
        bindViewModel();
        setupTransactionsTable();
        transactionsViewModel.load();
    }

    public TransactionsController(TransactionsViewModel transactionsViewModel)
    {
        this.transactionsViewModel = transactionsViewModel;
    }

    @FXML
    public void handleRefresh()
    {
        transactionsViewModel.refresh();
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
    }

    private void setupTransactionsTable()
    {
    }
}
