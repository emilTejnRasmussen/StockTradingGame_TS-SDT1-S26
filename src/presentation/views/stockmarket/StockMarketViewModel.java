package presentation.views.stockmarket;

import business.dto.StockDTO;
import business.services.StockHistoryService;
import business.services.listener.StockListenerService;
import entities.StockPriceHistory;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockMarketViewModel implements PropertyChangeListener
{
    private static final int DEFAULT_HISTORY_SIZE = 30;

    private final StockListenerService stockListenerService;
    private final StockHistoryService stockHistoryService;

    private final ObservableList<StockDTO> stocks = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Series<Number, Number>> chartSeries = FXCollections.observableArrayList();

    private final Map<String, XYChart.Series<Number, Number>> seriesBySymbol = new HashMap<>();

    private final ObjectProperty<StockDTO> selectedStock = new SimpleObjectProperty<>();

    public StockMarketViewModel(StockListenerService stockListenerService, StockHistoryService stockHistoryService)
    {
        this.stockListenerService = stockListenerService;
        this.stockHistoryService = stockHistoryService;

        this.stockListenerService.addListener(this);
    }

    public ObservableList<StockDTO> getStocks()
    {
        return stocks;
    }

    public ObservableList<XYChart.Series<Number, Number>> getChartSeries()
    {
        return chartSeries;
    }

    public ObjectProperty<StockDTO> selectedStockProperty()
    {
        return selectedStock;
    }

    public void loadInitialData()
    {
        Map<String, List<StockPriceHistory>> latestHistory =
                stockHistoryService.getLatestStockUpdatesForAll(DEFAULT_HISTORY_SIZE);

        Platform.runLater(() -> {
            chartSeries.clear();
            seriesBySymbol.clear();

            for (Map.Entry<String, List<StockPriceHistory>> entry : latestHistory.entrySet())
            {
                String symbol = entry.getKey();
                XYChart.Series<Number, Number> series = buildSeries(symbol, entry.getValue());

                chartSeries.add(series);
                seriesBySymbol.put(symbol, series);
            }
        });
    }

    public void refreshStockHistory(String stockSymbol)
    {
        List<StockPriceHistory> history =
                stockHistoryService.getLatestStockUpdatesForStock(stockSymbol, DEFAULT_HISTORY_SIZE);

        Platform.runLater(() -> {
            XYChart.Series<Number, Number> updatedSeries = buildSeries(stockSymbol, history);
            XYChart.Series<Number, Number> existingSeries = seriesBySymbol.get(stockSymbol);

            if (existingSeries != null)
            {
                int index = chartSeries.indexOf(existingSeries);
                chartSeries.set(index, updatedSeries);
            }
            else
            {
                chartSeries.add(updatedSeries);
            }

            seriesBySymbol.put(stockSymbol, updatedSeries);
        });
    }

    private XYChart.Series<Number, Number> buildSeries(String stockSymbol, List<StockPriceHistory> history)
    {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(stockSymbol);

        for (int i = 0; i < history.size(); i++)
        {
            StockPriceHistory point = history.get(i);

            // x = 0..29, y = stock price
            series.getData().add(new XYChart.Data<>(i + 1, point.price().doubleValue()));
        }

        return series;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (!"stockUpdated".equals(evt.getPropertyName()))
        {
            return;
        }

        // assuming evt.getNewValue() contains the symbol, e.g. "AAPL"
        StockDTO stock= (StockDTO) evt.getNewValue();
        refreshStockHistory(stock.symbol());
    }
}