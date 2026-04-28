package presentation.views.portfolio;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import presentation.views.utility.SetupViewUtil;

import java.util.HashMap;
import java.util.Map;

public class PortfolioController
{
    @FXML
    private Label activePortfolioNameLabel;
    @FXML
    private Label activeCashBalanceLabel;
    @FXML
    private Label activeNetWorthLabel;
    @FXML
    private Label activeOwnedStocksLabel;
    @FXML
    private Label activeTotalSharesLabel;

    @FXML
    private TextField portfolioNameField;
    @FXML
    private TextField startingBalanceField;

    @FXML
    private Label portfolioCountLabel;

    @FXML
    private TableView<PortfolioTableRow> portfolioTableView;
    @FXML
    private TableColumn<PortfolioTableRow, String> nameColumn;
    @FXML
    private TableColumn<PortfolioTableRow, String> cashColumn;
    @FXML
    private TableColumn<PortfolioTableRow, String> netWorthColumn;
    @FXML
    private TableColumn<PortfolioTableRow, String> ownedStocksColumn;
    @FXML
    private TableColumn<PortfolioTableRow, String> totalSharesColumn;
    @FXML
    private TableColumn<PortfolioTableRow, String> activeColumn;
    @FXML
    private TableColumn<PortfolioTableRow, Void> actionsColumn;

    private final PortfolioViewModel portfolioViewModel;

    @FXML
    public void initialize()
    {
        setupPortfolioTable();
        bindViewModel();
        setupActionsColumn();
        setupInputValidation();
        portfolioViewModel.load();
    }

    public PortfolioController(PortfolioViewModel portfolioViewModel)
    {
        this.portfolioViewModel = portfolioViewModel;
    }

    private void bindViewModel()
    {
        activePortfolioNameLabel.textProperty().bind(portfolioViewModel.activePortfolioNameProperty());
        activeCashBalanceLabel.textProperty().bind(portfolioViewModel.activeCashBalanceProperty());
        activeNetWorthLabel.textProperty().bind(portfolioViewModel.activeNetWorthProperty());
        activeOwnedStocksLabel.textProperty().bind(portfolioViewModel.activeOwnedStocksProperty());
        activeTotalSharesLabel.textProperty().bind(portfolioViewModel.activeTotalSharesProperty());
        portfolioCountLabel.textProperty().bind(portfolioViewModel.portfolioCountProperty());
    }

    private void setupPortfolioTable()
    {
        Map<TableColumn<PortfolioTableRow, ?>, String> columnMappings = new HashMap<>(Map.of(
                nameColumn, "name",
                cashColumn, "cash",
                netWorthColumn, "netWorth",
                ownedStocksColumn, "ownedStocks",
                totalSharesColumn, "totalShares",
                activeColumn, "active"
        ));

        SetupViewUtil.setupTableView(portfolioTableView, columnMappings);
        portfolioTableView.setItems(portfolioViewModel.getPortfolios());
    }

    private void setupInputValidation()
    {
        TextFormatter<String> balanceFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            if (newText.isEmpty()) return change;
            if (newText.matches("\\d*([.,]\\d{0,2})?")) return change;

            return null;
        });

        startingBalanceField.setTextFormatter(balanceFormatter);
    }

    private void setupActionsColumn()
    {
        actionsColumn.setCellFactory(col -> new TableCell<>()
        {
            private final Button setActiveButton = new Button("Set Active");

            {
                setActiveButton.getStyleClass().add("portfolio-primary-button");
                setActiveButton.setFocusTraversable(false);

                setActiveButton.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, event -> {
                    if (getTableRow() != null)
                    {
                        getTableRow().requestFocus();
                        getTableView().requestFocus();
                        getTableView().getSelectionModel().select(getIndex());
                    }
                });

                setActiveButton.setOnAction(event -> {
                    PortfolioTableRow row = getTableView().getItems().get(getIndex());
                    portfolioViewModel.setActivePortfolio(row.getPortfolioId());
                    getTableView().requestFocus();
                    getTableView().getSelectionModel().select(getIndex());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty)
            {
                super.updateItem(item, empty);

                if (empty || getIndex() >= getTableView().getItems().size())
                {
                    setGraphic(null);
                    return;
                }

                PortfolioTableRow row = getTableView().getItems().get(getIndex());

                setActiveButton.setDisable("active".equals(row.getActive()));
                setGraphic(setActiveButton);
            }
        });
    }

    @FXML
    public void onCreatePortFolio()
    {
        portfolioViewModel.createNewPortfolio(portfolioNameField.getText(), startingBalanceField.getText());
        onClearInput();
    }

    @FXML
    public void onClearInput()
    {
        portfolioNameField.setText("");
        startingBalanceField.setText("");
    }
}