package presentation.views.stockmarket;

import business.dto.StockDTO;
import business.services.StockHistoryService;
import business.services.listener.StockListenerService;
import entities.StockPriceHistory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
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

    public StockMarketViewModel(StockListenerService stockListenerService,
                                StockHistoryService stockHistoryService)
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

    public void loadInitialData()
    {
        Map<String, List<StockPriceHistory>> latestHistory =
                stockHistoryService.getLatestStockUpdatesForAll(DEFAULT_HISTORY_SIZE);

        List<StockDTO> loadedStocks = new ArrayList<>();
        List<XYChart.Series<Number, Number>> loadedSeries = new ArrayList<>();

        List<String> symbols = new ArrayList<>(latestHistory.keySet());
        symbols.sort(String::compareTo);

        for (String symbol : symbols)
        {
            List<StockPriceHistory> history = latestHistory.get(symbol);

            if (history == null || history.isEmpty())
            {
                continue;
            }

            StockPriceHistory latestPoint = history.getLast();
            loadedStocks.add(new StockDTO(symbol, latestPoint.price(), null));

            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(symbol);

            for (int i = 0; i < history.size(); i++)
            {
                StockPriceHistory point = history.get(i);
                series.getData().add(new XYChart.Data<>(i + 1, point.price().doubleValue()));
            }

            loadedSeries.add(series);
        }

        Platform.runLater(() -> {
            stocks.setAll(loadedStocks);
            chartSeries.setAll(loadedSeries);

            seriesBySymbol.clear();
            for (XYChart.Series<Number, Number> series : loadedSeries)
            {
                seriesBySymbol.put(series.getName(), series);
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (!"stockUpdated".equals(evt.getPropertyName()))
        {
            return;
        }

        StockDTO updatedStock = (StockDTO) evt.getNewValue();

        Platform.runLater(() -> {
            updateStockInTable(updatedStock);
            appendToSeries(updatedStock);
        });
    }

    private void updateStockInTable(StockDTO updatedStock)
    {
        for (int i = 0; i < stocks.size(); i++)
        {
            StockDTO existing = stocks.get(i);

            if (existing.symbol().equals(updatedStock.symbol()))
            {
                stocks.set(i, updatedStock);
                return;
            }
        }

        stocks.add(updatedStock);
        FXCollections.sort(stocks, Comparator.comparing(StockDTO::symbol));
    }

    private void appendToSeries(StockDTO updatedStock)
    {
        XYChart.Series<Number, Number> series = seriesBySymbol.get(updatedStock.symbol());

        if (series == null)
        {
            // New stock that was not present during initial load
            XYChart.Series<Number, Number> newSeries = new XYChart.Series<>();
            newSeries.setName(updatedStock.symbol());
            newSeries.getData().add(new XYChart.Data<>(1, updatedStock.currentPrice().doubleValue()));

            seriesBySymbol.put(updatedStock.symbol(), newSeries);
            chartSeries.add(newSeries);
            chartSeries.sort(Comparator.comparing(XYChart.Series::getName));
            return;
        }

        int nextX = series.getData().size() + 1;
        series.getData().add(new XYChart.Data<>(nextX, updatedStock.currentPrice().doubleValue()));

        if (series.getData().size() > DEFAULT_HISTORY_SIZE)
        {
            series.getData().removeFirst();

            // Re-number x values so axis stays 1..30
            for (int i = 0; i < series.getData().size(); i++)
            {
                XYChart.Data<Number, Number> dataPoint = series.getData().get(i);
                dataPoint.setXValue(i + 1);
            }
        }
    }

    public void dispose()
    {
        stockListenerService.removeListener(this);
    }
}