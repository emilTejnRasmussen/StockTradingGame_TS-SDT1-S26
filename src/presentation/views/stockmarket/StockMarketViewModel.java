package presentation.views.stockmarket;

import business.dto.StockDTO;
import business.dto.transaction.BuyStockRequestDTO;
import business.dto.transaction.SellStockRequestDTO;
import business.services.PortfolioService;
import business.services.StockHistoryService;
import business.services.StockService;
import business.services.TradingService;
import business.services.listener.StockListenerService;
import entities.StockPriceHistory;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import presentation.views.utility.Parser;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.*;

public class StockMarketViewModel implements PropertyChangeListener
{
    private static final int DEFAULT_HISTORY_SIZE = 30;

    private final StockHistoryService stockHistoryService;
    private final StockService stockService;
    private final PortfolioService portfolioService;
    private final TradingService tradingService;


    private final StringProperty totalPL = new SimpleStringProperty("");
    private final StringProperty holdingsValue = new SimpleStringProperty("");
    private final StringProperty cashBalance = new SimpleStringProperty("");
    private final StringProperty netWorth = new SimpleStringProperty("");
    private final StringProperty selectedStockSymbol = new SimpleStringProperty("");
    private final IntegerProperty selectedQuantity = new SimpleIntegerProperty(1);
    private final BooleanProperty buyDisabled = new SimpleBooleanProperty(true);
    private final BooleanProperty sellDisabled = new SimpleBooleanProperty(true);

    private final ObservableList<StockRowViewModel> stocks = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Series<Number, Number>> chartSeries = FXCollections.observableArrayList();

    private final Map<String, XYChart.Series<Number, Number>> seriesBySymbol = new HashMap<>();

    private UUID portfolioId;



    public StockMarketViewModel(StockListenerService stockListenerService, StockHistoryService stockHistoryService, StockService stockService, PortfolioService portfolioService, TradingService tradingService, ObservableValue<UUID> activePortfolioId)
    {
        this.stockHistoryService = stockHistoryService;
        this.stockService = stockService;
        this.portfolioService = portfolioService;
        this.tradingService = tradingService;
        this.portfolioId = activePortfolioId.getValue();

        activePortfolioId.addListener((_, _, newId) -> {
            this.portfolioId = newId;
            load();
        });

        stockListenerService.addListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (evt.getNewValue() != null)
        {
            Platform.runLater(() -> refreshValues((StockDTO) evt.getNewValue()));
        }
    }

    public void load()
    {
        stocks.clear();
        selectedStockSymbol.set(null);

        loadLineChart();
        loadTable();
        refreshPortfolioValues();
    }

    public void buy(int amount) {
        tradingService.buyStock(new BuyStockRequestDTO(
                selectedStockSymbol.get(),
                portfolioId,
                amount
        ));
    }

    public void sell(int amount) {
        tradingService.sellStock(new SellStockRequestDTO(
                selectedStockSymbol.get(),
                portfolioId,
                amount
        ));
    }

    private void refreshValues(StockDTO stockDTO)
    {

        refreshTable(stockDTO);
        refreshLineChart(stockDTO);
        refreshPortfolioValues();
    }

    private void refreshPortfolioValues()
    {
        if (portfolioId == null)
        {
            totalPL.set("¤ 0.00");
            holdingsValue.set("¤ 0.00");
            cashBalance.set("¤ 0.00");
            netWorth.set("¤ 0.00");

            updateButtonStates();
            return;
        }

        String totalPL = formatPrice(portfolioService.getTotalProfitLoss(portfolioId));
        String holdingsValue = formatPrice(portfolioService.getHoldingsValue(portfolioId));
        String cashBalance = formatPrice(portfolioService.getPortfolioBalance(portfolioId));
        String netWorth = formatPrice(portfolioService.getPortfolioNetWorth(portfolioId));

        this.totalPL.set(totalPL);
        this.holdingsValue.set(holdingsValue);
        this.cashBalance.set(cashBalance);
        this.netWorth.set(netWorth);

        updateButtonStates();
    }

    private void refreshTable(StockDTO stockDTO)
    {
        for (StockRowViewModel stockRowViewModel : stocks)
        {
            if (!stockRowViewModel.getSymbol().equals(stockDTO.symbol())) continue;

            stockRowViewModel.setSymbol(stockDTO.symbol());
            stockRowViewModel.setPrice(formatPrice(stockDTO.currentPrice()));
            try
            {
                stockRowViewModel.setOwned(portfolioService.getNumberOfSharesOwned(portfolioId, stockDTO.symbol()) + "");
            } catch (Exception e)
            {
                stockRowViewModel.setOwned("0");
            }
        }

        updateButtonStates();
    }

    private void refreshLineChart(StockDTO stockDTO)
    {
        XYChart.Series<Number, Number> series = seriesBySymbol.get(stockDTO.symbol());
        if (series == null) return;

        ObservableList<XYChart.Data<Number, Number>> data = series.getData();

        if (data.size() >= DEFAULT_HISTORY_SIZE)
        {
            data.removeFirst();
        }

        for (int i = 0; i < data.size(); i++)
        {
            data.get(i).setXValue(i);
        }

        data.add(new XYChart.Data<>(
                data.size(),
                stockDTO.currentPrice().doubleValue()
        ));
    }

    private void loadTable()
    {
        if (portfolioId == null) return;

        List<StockDTO> stockDTOs = stockService.getAll();

        for (StockDTO stockDTO : stockDTOs)
        {
            String owned = "0";

            if (portfolioId != null)
            {
                try {
                    owned = String.valueOf(
                            portfolioService.getNumberOfSharesOwned(
                                    portfolioId,
                                    stockDTO.symbol()
                            )
                    );
                } catch (Exception ignored) {
                    owned = "0";
                }
            }
            stocks.add(new StockRowViewModel(
                    stockDTO.symbol(),
                    formatPrice(stockDTO.currentPrice()),
                    owned
            ));
        }
    }

    private void loadLineChart()
    {
        Map<String, List<StockPriceHistory>> stockHistory =
                stockHistoryService.getLatestStockUpdatesForAll(DEFAULT_HISTORY_SIZE);

        chartSeries.clear();
        seriesBySymbol.clear();

        for (Map.Entry<String, List<StockPriceHistory>> entry : stockHistory.entrySet())
        {
            String symbol = entry.getKey();
            List<StockPriceHistory> history = entry.getValue();

            if (history == null || history.isEmpty()) continue;

            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(symbol);

            int startIndex = Math.max(0, history.size() - DEFAULT_HISTORY_SIZE);

            for (int i = startIndex; i < history.size(); i++)
            {
                StockPriceHistory point = history.get(i);

                series.getData().add(new XYChart.Data<>(
                        i - startIndex,
                        point.price().doubleValue()
                ));
            }

            chartSeries.add(series);
            seriesBySymbol.put(symbol, series);
        }
    }

    private void updateButtonStates()
    {
        String selectedSymbol = selectedStockSymbol.get();
        int quantity = selectedQuantity.get();

        boolean disableBuy = true;
        boolean disableSell = true;

        if (selectedSymbol != null && !selectedSymbol.isBlank() && quantity > 0)
        {
            StockRowViewModel selectedStock = findSelectedStock();

            if (selectedStock != null)
            {
                BigDecimal cash = Parser.parseMoney(cashBalance.get());
                BigDecimal price = Parser.parseMoney(selectedStock.getPrice());
                int owned = Parser.parseOwned(selectedStock.getOwned());

                BigDecimal totalCost = price.multiply(BigDecimal.valueOf(quantity));

                disableBuy = cash.compareTo(totalCost) < 0;
                disableSell = quantity > owned;
            }
        }

        buyDisabled.set(disableBuy);
        sellDisabled.set(disableSell);
    }

    private StockRowViewModel findSelectedStock()
    {
        String selectedSymbol = selectedStockSymbol.get();
        if (selectedSymbol == null || selectedSymbol.isBlank()) return null;

        for (StockRowViewModel stock : stocks)
        {
            if (selectedSymbol.equals(stock.getSymbol()))
            {
                return stock;
            }
        }

        return null;
    }

    private String formatPrice(BigDecimal bigDecimal)
    {
        return String.format("¤ %.2f", bigDecimal);
    }

    public StringProperty totalPLProperty()
    {
        return totalPL;
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

    public BooleanProperty buyDisabledProperty()
    {
        return buyDisabled;
    }

    public void setSelectedQuantity(int quantity)
    {
        selectedQuantity.set(quantity);
        updateButtonStates();
    }

    public BooleanProperty sellDisabledProperty()
    {
        return sellDisabled;
    }

    public ObservableList<StockRowViewModel> getStocks()
    {
        return stocks;
    }

    public ObservableList<XYChart.Series<Number, Number>> getChartSeries()
    {
        return chartSeries;
    }

    public StringProperty selectedStockSymbolProperty()
    {
        return selectedStockSymbol;
    }

    public String getSelectedStockSymbol()
    {
        return selectedStockSymbol.get();
    }

    public void setSelectedStockSymbol(String symbol)
    {
        this.selectedStockSymbol.set(symbol);
    }
}