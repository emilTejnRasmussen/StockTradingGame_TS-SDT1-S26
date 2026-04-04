package presentation.views.stockmarket;

import business.dto.StockDTO;
import business.dto.transaction.BuyStockRequestDTO;
import business.dto.transaction.SellStockRequestDTO;
import business.services.PortfolioService;
import business.services.StockHistoryService;
import business.services.TradingService;
import business.services.listener.StockListenerService;
import entities.OwnedStock;
import entities.StockPriceHistory;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import presentation.core.ApplicationContext;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

public class StockMarketViewModel implements PropertyChangeListener
{
    private static final int DEFAULT_HISTORY_SIZE = 30;

    private final StockHistoryService stockHistoryService;
    private final PortfolioService portfolioService;
    private final TradingService tradingService;

    private final StringProperty totalPL = new SimpleStringProperty("");
    private final StringProperty ownedStocks = new SimpleStringProperty("");
    private final StringProperty totalShares = new SimpleStringProperty("");
    private final StringProperty holdingsValue = new SimpleStringProperty("");
    private final StringProperty cashBalance = new SimpleStringProperty("");
    private final StringProperty netWorth = new SimpleStringProperty("");

    private final ObservableList<StockDTO> stocks = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Series<Number, Number>> chartSeries = FXCollections.observableArrayList();

    private UUID portfolioId;

    private final Map<String, XYChart.Series<Number, Number>> seriesBySymbol = new HashMap<>();
    private final Map<String, IntegerProperty> ownedBySymbol = new HashMap<>();


    public StockMarketViewModel(ApplicationContext appContext, StockListenerService stockListenerService,
                                StockHistoryService stockHistoryService,
                                PortfolioService portfolioService,
                                TradingService tradingService)
    {
        this.stockHistoryService = stockHistoryService;
        this.portfolioService = portfolioService;
        this.tradingService = tradingService;

        stockListenerService.addListener(this);

        ChangeListener<UUID> activePortfolioListener = (_, _, newId) -> this.portfolioId = newId;
        appContext.activePortfolioIdProperty().addListener(activePortfolioListener);
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
            updatePortfolioInfo();
        });
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

            refreshOwnedStocks();
            updatePortfolioInfo();
        });
    }

    private void updatePortfolioInfo()
    {
        if (portfolioId == null)
        {
            clearPortfolioInfo();
            return;
        }

        String newCashBalance = String.format("¤ %.2f", portfolioService.getPortfolioBalance(portfolioId));
        String newNetWorth = String.format("¤ %.2f", portfolioService.getPortfolioNetWorth(portfolioId));
        String newTotalPL = String.format("¤ %.2f", portfolioService.getTotalProfitLoss(portfolioId));
        String newOwnedStocks = String.format("%d", portfolioService.getOwnedStocks(portfolioId).size());
        String newTotalShares = String.format("%d", portfolioService.getTotalNumberOfShares(portfolioId));
        String newHoldingsValue = String.format("¤ %.2f", portfolioService.getHoldingsValue(portfolioId));

        cashBalance.set(newCashBalance);
        netWorth.set(newNetWorth);
        totalPL.set(newTotalPL);
        ownedStocks.set(newOwnedStocks);
        totalShares.set(newTotalShares);
        holdingsValue.set(newHoldingsValue);
    }

    private void clearPortfolioInfo()
    {
        cashBalance.set("¤ 0.00");
        netWorth.set("¤ 0.00");
        totalPL.set("¤ 0.00");
        ownedStocks.set("0");
        totalShares.set("0");
        holdingsValue.set("¤ 0.00");
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

            for (int i = 0; i < series.getData().size(); i++)
            {
                XYChart.Data<Number, Number> dataPoint = series.getData().get(i);
                dataPoint.setXValue(i + 1);
            }
        }
    }

    public IntegerProperty ownedQuantityProperty(String stockSymbol)
    {
        return ownedBySymbol.computeIfAbsent(stockSymbol, _ -> new SimpleIntegerProperty(0));
    }

    public void refreshOwnedStocks()
    {
        Map<String, Integer> latest = new HashMap<>();

        if (portfolioId != null)
        {
            for (OwnedStock os : portfolioService.getOwnedStocks(portfolioId))
            {
                latest.put(os.getStockSymbol(), os.getNumberOfShares());
            }
        }

        Runnable applyUpdate = () -> {
            for (String symbol : latest.keySet())
            {
                ownedQuantityProperty(symbol).set(latest.get(symbol));
            }

            for (String symbol : ownedBySymbol.keySet())
            {
                if (!latest.containsKey(symbol))
                {
                    ownedBySymbol.get(symbol).set(0);
                }
            }
        };

        if (Platform.isFxApplicationThread())
        {
            applyUpdate.run();
        } else
        {
            Platform.runLater(applyUpdate);
        }
    }

    public void sell(StockDTO stock)
    {
        if (portfolioId == null) return;

        SellStockRequestDTO request = new SellStockRequestDTO(stock.symbol(), portfolioId, 1);

        try
        {
            tradingService.sellStock(request);
            refreshOwnedStocks();
            updatePortfolioInfo();
        } catch (Exception ignored)
        {
            // TODO - show error popup
        }
    }


    public void buy(StockDTO stock)
    {
        if (portfolioId == null) return;

        BuyStockRequestDTO request = new BuyStockRequestDTO(stock.symbol(), portfolioId, 1);

        try
        {
            tradingService.buyStock(request);
            refreshOwnedStocks();
            updatePortfolioInfo();
        } catch (Exception ignored)
        {
            // TODO - show error popup
        }
    }

    public StringProperty totalPLProperty()
    {
        return totalPL;
    }

    public StringProperty ownedStocksProperty()
    {
        return ownedStocks;
    }

    public StringProperty totalSharesProperty()
    {
        return totalShares;
    }

    public StringProperty holdingsValueProperty()
    {
        return holdingsValue;
    }

    public StringProperty cashBalanceProperty()
    {
        return cashBalance;
    }

    public StringProperty netWorthProperty()
    {
        return netWorth;
    }
}