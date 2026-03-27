package presentation.views.stockmarket;

import business.dto.StockDTO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.util.Duration;

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
    private Timeline axisTimeline;

    public StockMarketController(StockMarketViewModel viewModel)
    {
        this.viewModel = viewModel;
    }

    @FXML
    public void initialize()
    {
        setupLineChart();
        setupTableView();

    }

    private void setupTableView()
    {
        stockTableView = new TableView<>();
        symbolCol = new TableColumn<>("Symbol");
        symbolCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().symbol()));

        priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().currentPrice()));

        actionCol = new TableColumn<>("");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button buyButton = new Button("Buy");
            {
                buyButton.setOnAction(event -> {
                    StockDTO stock = getTableView().getItems().get(getIndex());
                    System.out.println("Buying: " + stock.symbol());
                });
            }
        });
    }

    private void setupLineChart()
    {
        xAxis.setLabel("Time (seconds)");
        yAxis.setLabel("Price");

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(29);
        xAxis.setTickUnit(5);

        stockMarketChart.setTitle("Stock Prices");
        stockMarketChart.setAnimated(false);
        stockMarketChart.setData(viewModel.getChartSeries());

        axisTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> updateXAxis())
        );
        axisTimeline.setCycleCount(Timeline.INDEFINITE);
        axisTimeline.play();
    }

    private void updateXAxis()
    {
        int currentSecond = viewModel.getCurrentSecond();

        xAxis.setLowerBound(Math.max(0, currentSecond - 29));
        xAxis.setUpperBound(Math.max(29, currentSecond));
    }
}