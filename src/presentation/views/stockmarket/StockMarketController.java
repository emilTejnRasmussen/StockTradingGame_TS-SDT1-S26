package presentation.views.stockmarket;

import business.dto.StockDTO;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
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
    private TableColumn<StockDTO, Void> actionCol;

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