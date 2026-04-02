package presentation.views.stockmarket;

import business.dto.StockDTO;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.math.BigDecimal;

public class StockMarketController
{
    @FXML
    private TableView<StockDTO> stockTableView;
    @FXML
    private TableColumn<StockDTO, String> symbolCol;
    @FXML
    private TableColumn<StockDTO, BigDecimal> priceCol;
    @FXML
    private TableColumn<StockDTO, Integer> ownedCol;
    @FXML
    private TableColumn<StockDTO, Void> buyCol;
    @FXML
    private TableColumn<StockDTO, Void> sellCol;

    @FXML
    private LineChart<Number, Number> stockMarketChart;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private final StockMarketViewModel viewModel;

    public StockMarketController(StockMarketViewModel viewModel)
    {
        this.viewModel = viewModel;
    }

    @FXML
    public void initialize()
    {
        setupTable();
        setupChart();

        stockTableView.setItems(viewModel.getStocks());
        stockMarketChart.setData(viewModel.getChartSeries());

        viewModel.loadInitialData();
    }

    private void setupTable()
    {
        symbolCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().symbol()));

        priceCol.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().currentPrice()));

        ownedCol.setCellValueFactory(cellData ->
                viewModel.ownedQuantityProperty(cellData.getValue().symbol()).asObject());

        buyCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(null));
        sellCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(null));

        setupBuyColumn();
        setupSellColumn();
    }

    private void setupSellColumn()
    {
        sellCol.setCellFactory(col -> new TableCell<>() {
            private final Button sellButton = new Button("Sell");
            private String currentSymbol;

            {
                sellButton.setOnAction(event -> {
                    StockDTO stock = getTableRow().getItem();
                    if (stock != null) {
                        viewModel.sell(stock);
                    }
                });

                tableRowProperty().addListener((obs, oldRow, newRow) -> {
                    if (oldRow != null) {
                        oldRow.itemProperty().removeListener((o, oldItem, newItem) -> {});
                    }

                    if (newRow != null) {
                        newRow.itemProperty().addListener((o, oldItem, newItem) -> rebindButton(newItem));
                        rebindButton(newRow.getItem());
                    }
                });
            }

            private void rebindButton(StockDTO stock) {
                sellButton.disableProperty().unbind();

                if (stock == null) {
                    currentSymbol = null;
                    sellButton.setDisable(true);
                    return;
                }

                currentSymbol = stock.symbol();
                sellButton.disableProperty().bind(
                        viewModel.ownedQuantityProperty(currentSymbol).lessThanOrEqualTo(0)
                );
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : sellButton);
            }
        });
    }

    private void setupBuyColumn()
    {
        buyCol.setCellFactory(col -> new TableCell<>() {
            private final Button buyButton = new Button("Buy");

            {
                buyButton.setOnAction(event -> {
                    StockDTO stock = getTableRow().getItem();
                    if (stock != null) {
                        viewModel.buy(stock);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buyButton);
            }
        });
    }

    private void setupChart()
    {
        xAxis.setLabel("Last 30 updates");
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(1);
        xAxis.setUpperBound(30);
        xAxis.setTickUnit(1);

        yAxis.setLabel("Price");
        yAxis.setAutoRanging(true);

        stockMarketChart.setAnimated(false);
        stockMarketChart.setCreateSymbols(false);
        stockMarketChart.setLegendVisible(true);
    }

    public void dispose()
    {
        viewModel.dispose();
    }
}