package presentation.views.stockmarket;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import presentation.views.utility.SetupViewUtil;

import java.util.HashMap;
import java.util.Map;

public class StockMarketController
{
    @FXML
    private Label totalPLLbl;
    @FXML
    private Label holdingsValueLbl;
    @FXML
    private Label cashBalanceLbl;
    @FXML
    private Label netWorthLbl;
    @FXML
    private Label selectedStockLbl;

    @FXML
    private TableView<StockRowViewModel> stockTableView;
    @FXML
    private TableColumn<StockRowViewModel, String> symbolCol;
    @FXML
    private TableColumn<StockRowViewModel, String> priceCol;
    @FXML
    private TableColumn<StockRowViewModel, String> ownedCol;

    @FXML
    private Spinner<Integer> quantitySpinner;
    @FXML
    private Button buyBtn;
    @FXML
    private Button sellBtn;

    @FXML
    private LineChart<Number, Number> stockMarketChart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private final StockMarketViewModel stockMarketViewModel;

    @FXML
    public void initialize()
    {
        bindViewModel();
        setupStockTable();
        setupLineChart();
        setupSpinner();
        setupButtons();
        stockMarketViewModel.load();
    }

    public StockMarketController(StockMarketViewModel stockMarketViewModel)
    {
        this.stockMarketViewModel = stockMarketViewModel;
    }

    @FXML
    public void handleBuyStocksPressed()
    {
        stockMarketViewModel.buy(quantitySpinner.getValue());
    }

    @FXML
    public void handleSellStocksPressed()
    {
        stockMarketViewModel.sell(quantitySpinner.getValue());
    }

    private void bindViewModel()
    {
        totalPLLbl.textProperty().bind(stockMarketViewModel.totalPLProperty());
        holdingsValueLbl.textProperty().bind(stockMarketViewModel.holdingsValueProperty());
        cashBalanceLbl.textProperty().bind(stockMarketViewModel.cashBalanceProperty());
        netWorthLbl.textProperty().bind(stockMarketViewModel.netWorthProperty());
        selectedStockLbl.textProperty().bind(
                Bindings.createStringBinding(
                        () -> {
                            String symbol = stockMarketViewModel.getSelectedStockSymbol();
                            return symbol == null || symbol.isBlank() ? "No stock selected" : "Selected: " + symbol;
                        },
                        stockMarketViewModel.selectedStockSymbolProperty()
                )
        );
    }

    private void setupSpinner()
    {
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1);

        quantitySpinner.setValueFactory(valueFactory);
        quantitySpinner.setEditable(true);

        TextFormatter<Integer> textFormatter = new TextFormatter<>(
                new StringConverter<>()
                {
                    @Override
                    public String toString(Integer value)
                    {
                        return value == null ? "1" : value.toString();
                    }

                    @Override
                    public Integer fromString(String text)
                    {
                        if (text == null || text.isBlank()) return 1;
                        return Integer.parseInt(text);
                    }
                },
                1,
                change -> {
                    String newText = change.getControlNewText();

                    if (newText.isEmpty()) return change;
                    if (newText.matches("[1-9][0-9]*")) return change;

                    return null;
                }
        );

        quantitySpinner.getEditor().setTextFormatter(textFormatter);

        valueFactory.valueProperty().bindBidirectional(textFormatter.valueProperty());
    }

    private void setupLineChart()
    {
        stockMarketChart.setAnimated(false);
        stockMarketChart.setCreateSymbols(false);
        stockMarketChart.setLegendVisible(true);

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(45);
        xAxis.setTickUnit(5);
        xAxis.setLabel("Last 30 updates");

        yAxis.setAutoRanging(true);
        yAxis.setForceZeroInRange(false);
        yAxis.setLabel("Price");

        stockMarketChart.setData(stockMarketViewModel.getChartSeries());
    }

    private void setupButtons()
    {
        quantitySpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null)
            {
                stockMarketViewModel.setSelectedQuantity(newValue);
            }
        });

        buyBtn.disableProperty().bind(stockMarketViewModel.buyDisabledProperty());
        sellBtn.disableProperty().bind(stockMarketViewModel.sellDisabledProperty());
    }

    private void setupStockTable()
    {
        Map<TableColumn<StockRowViewModel, ?>, String> columnMappings = new HashMap<>(Map.of(
                symbolCol, "symbol",
                priceCol, "price",
                ownedCol, "owned"
        ));

        SetupViewUtil.setupTableView(stockTableView, columnMappings);
        stockTableView.setItems(stockMarketViewModel.getStocks());

        stockTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null)
            {
                stockMarketViewModel.setSelectedStockSymbol("");
            } else
            {
                stockMarketViewModel.setSelectedStockSymbol(newValue.getSymbol());
            }
        });
    }


}