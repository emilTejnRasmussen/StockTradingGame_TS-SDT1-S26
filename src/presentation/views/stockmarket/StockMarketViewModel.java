package presentation.views.stockmarket;

import business.dto.StockDTO;
import business.services.listener.StockListenerService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class StockMarketViewModel implements PropertyChangeListener
{
    private final ObservableList<XYChart.Series<Number, Number>> chartSeries;
    private final Map<String, XYChart.Series<Number, Number>> seriesMap;
    private final Map<String, BigDecimal> latestPrices;

    private int second = 0;
    private final Timeline chartTimeline;

    public StockMarketViewModel(StockListenerService stockListenerService)
    {
        this.chartSeries = FXCollections.observableArrayList();
        this.seriesMap = new HashMap<>();
        this.latestPrices = new HashMap<>();

        stockListenerService.addListener(this);

        this.chartTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> addSnapshotToChart())
        );
        this.chartTimeline.setCycleCount(Timeline.INDEFINITE);
        this.chartTimeline.play();
    }

    public ObservableList<XYChart.Series<Number, Number>> getChartSeries()
    {
        return chartSeries;
    }

    public int getCurrentSecond()
    {
        return second;
    }

    private XYChart.Series<Number, Number> getOrCreateSeries(String stockSymbol)
    {
        XYChart.Series<Number, Number> series = seriesMap.get(stockSymbol);

        if (series != null)
        {
            return series;
        }

        XYChart.Series<Number, Number> newSeries = new XYChart.Series<>();
        newSeries.setName(stockSymbol);

        seriesMap.put(stockSymbol, newSeries);
        chartSeries.add(newSeries);

        return newSeries;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (!"stockUpdated".equals(evt.getPropertyName()))
        {
            return;
        }

        StockDTO stockDTO = (StockDTO) evt.getNewValue();

        Platform.runLater(() ->
        {
            String symbol = stockDTO.symbol();
            BigDecimal price = stockDTO.currentPrice();

            getOrCreateSeries(symbol);
            latestPrices.put(symbol, price);
        });
    }

    private void addSnapshotToChart()
    {
        for (Map.Entry<String, BigDecimal> entry : latestPrices.entrySet())
        {
            String symbol = entry.getKey();
            BigDecimal price = entry.getValue();

            XYChart.Series<Number, Number> series = getOrCreateSeries(symbol);
            series.getData().add(new XYChart.Data<>(second, price));

            if (series.getData().size() > 30)
            {
                series.getData().removeFirst();
            }
        }

        second++;
    }
}