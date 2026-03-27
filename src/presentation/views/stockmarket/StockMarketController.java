package presentation.views.stockmarket;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.util.Duration;

public class StockMarketController
{
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