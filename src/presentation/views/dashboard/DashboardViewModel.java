package presentation.views.dashboard;

import business.services.PortfolioService;
import business.services.StockService;
import business.services.listener.StockListenerService;
import entities.OwnedStock;
import entities.Transaction;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import presentation.core.ApplicationContext;
import shared.configuration.AppConfig;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class DashboardViewModel implements PropertyChangeListener
{
    private static final String CURRENCY_ZERO = "¤ 0.00";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private final ObservableList<HoldingRowViewModel> holdings = FXCollections.observableArrayList();
    private final ObservableList<TransactionRowViewModel> transactions = FXCollections.observableArrayList();
    private final ObservableList<PieChart.Data> shareDistribution = FXCollections.observableArrayList();

    private final ReadOnlyStringWrapper netWorth = new ReadOnlyStringWrapper(CURRENCY_ZERO);

    private final ReadOnlyStringWrapper cashBalance = new ReadOnlyStringWrapper(CURRENCY_ZERO);
    private final ReadOnlyStringWrapper cashStatus = new ReadOnlyStringWrapper("Available cash");

    private final ReadOnlyStringWrapper totalStockValue = new ReadOnlyStringWrapper(CURRENCY_ZERO);
    private final ReadOnlyStringWrapper ownedStocksCount = new ReadOnlyStringWrapper("0 owned stocks");

    private final ReadOnlyStringWrapper totalPL = new ReadOnlyStringWrapper(CURRENCY_ZERO);
    private final ReadOnlyStringWrapper totalPLPercent = new ReadOnlyStringWrapper("+0.00%");

    private final ReadOnlyStringWrapper holdingsUpdatedText = new ReadOnlyStringWrapper("Not updated yet");
    private final ReadOnlyStringWrapper transactionsCountText = new ReadOnlyStringWrapper("0 transactions");

    private final ReadOnlyStringWrapper bestPerformer = new ReadOnlyStringWrapper("N/A");
    private final ReadOnlyStringWrapper worstPerformer = new ReadOnlyStringWrapper("N/A");
    private final ReadOnlyStringWrapper totalShares = new ReadOnlyStringWrapper("0");
    private final ReadOnlyStringWrapper ownedStocks = new ReadOnlyStringWrapper("0");

    private final PortfolioService portfolioService;
    private final StockService stockService;

    private UUID portfolioId;

    public DashboardViewModel(PortfolioService portfolioService, StockService stockService, StockListenerService stockListenerService, ObservableValue<UUID> activePortfolioId)
    {
        this.portfolioService = portfolioService;
        this.stockService = stockService;
        this.portfolioId = activePortfolioId.getValue();

        activePortfolioId.addListener((observable, oldId, newId) -> {
            this.portfolioId = newId;
            load();
        });

        stockListenerService.addListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        Platform.runLater(this::refreshValues);
    }

    public void load()
    {
        holdings.clear();
        transactions.clear();
        shareDistribution.clear();

        loadHoldings();
        loadTransactions();
        recalculateSummary();
        holdingsUpdatedText.set("Updated " + TIME_FORMATTER.format(LocalDateTime.now()));
    }

    public ObservableList<PieChart.Data> buildShareDistribution()
    {
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();

        if (portfolioId == null) {
            return chartData;
        }

        List<OwnedStock> ownedStocks = portfolioService.getOwnedStocks(portfolioId);

        for (OwnedStock ownedStock : ownedStocks) {
            int numberOfShares = ownedStock.getNumberOfShares();
            if (numberOfShares > 0) {
                chartData.add(new PieChart.Data(ownedStock.getStockSymbol(), numberOfShares));
            }
        }

        return chartData;
    }

    private void refreshValues()
    {
        if (portfolioId == null) return;

        List<OwnedStock> ownedStocksList = portfolioService.getOwnedStocks(portfolioId);

        for (OwnedStock ownedStock : ownedStocksList)
        {
            String stockSymbol = ownedStock.getStockSymbol();
            HoldingRowViewModel row = findHoldingRow(stockSymbol);

            if (row == null) {
                continue;
            }

            int numberOfShares = ownedStock.getNumberOfShares();
            BigDecimal currentPrice = stockService.getCurrentPrice(stockSymbol);
            BigDecimal value = currentPrice.multiply(BigDecimal.valueOf(numberOfShares));
            BigDecimal avgPrice = portfolioService.getAvgStockBuyPrice(stockSymbol, portfolioId);
            BigDecimal pl = currentPrice
                    .subtract(avgPrice)
                    .multiply(BigDecimal.valueOf(numberOfShares));

            row.setShares(numberOfShares);
            row.setAvgPrice(formatCurrency(avgPrice));
            row.setCurrentPrice(formatCurrency(currentPrice));
            row.setValue(formatCurrency(value));
            row.setPl(formatCurrency(pl));
        }

        shareDistribution.setAll(buildShareDistribution());
        recalculateSummary();
        holdingsUpdatedText.set("Updated " + TIME_FORMATTER.format(LocalDateTime.now()));
    }

    private HoldingRowViewModel findHoldingRow(String symbol)
    {
        for (HoldingRowViewModel row : holdings)
        {
            if (row.getSymbol().equals(symbol)) {
                return row;
            }
        }
        return null;
    }

    private void loadHoldings()
    {
        if (portfolioId == null) return;

        List<OwnedStock> ownedStocks = portfolioService.getOwnedStocks(portfolioId);

        for (OwnedStock ownedStock : ownedStocks) {
            String stockSymbol = ownedStock.getStockSymbol();
            int numberOfShares = ownedStock.getNumberOfShares();
            BigDecimal currentPrice = stockService.getCurrentPrice(ownedStock.getStockSymbol());
            BigDecimal value = currentPrice.multiply(BigDecimal.valueOf(numberOfShares));
            BigDecimal totalPL = portfolioService.getTotalProfitLoss(portfolioId);
            BigDecimal avgPrice = portfolioService.getAvgStockBuyPrice(ownedStock.getStockSymbol(), portfolioId);


            holdings.add(new HoldingRowViewModel(
                    stockSymbol,
                    numberOfShares,
                    formatCurrency(avgPrice),
                    formatCurrency(currentPrice),
                    formatCurrency(value),
                    formatCurrency(totalPL)
            ));
        }
    }

    private void loadTransactions()
    {
        List<Transaction> latestTransactions = portfolioService.getTransactionHistory(portfolioId, 0, 4).items();
        for (Transaction transaction : latestTransactions) {
            String time = transaction.timeStamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Transaction.Type type = transaction.type();
            int numberOfShares = transaction.quantity();
            BigDecimal price = type == Transaction.Type.BUY ?
                    transaction.getTotalPriceWithFee() :
                    transaction.getTotalPriceFeeSubtracted();

            BigDecimal total = price.multiply(BigDecimal.valueOf(numberOfShares));

            transactions.add(new TransactionRowViewModel(
                    time,
                    type.toString(),
                    transaction.stockSymbol(),
                    transaction.quantity(),
                    formatCurrency(price),
                    formatCurrency(total)
            ));
        }
        transactionsCountText.set("latest " + transactions.size() + " transactions");
    }

    private void recalculateSummary()
    {
        BigDecimal cashBalanceNumber = portfolioService.getPortfolioBalance(portfolioId);
        BigDecimal totalStockValueNumber = portfolioService.getHoldingsValue(portfolioId);
        BigDecimal totalPLNumber = portfolioService.getTotalProfitLoss(portfolioId);
        int totalSharesNumber = portfolioService.getTotalNumberOfShares(portfolioId);

        String bestSymbol;
        String worstSymbol;

        try
        {
            bestSymbol = portfolioService.getBestSymbol(portfolioId);
            worstSymbol = portfolioService.getWorstSymbol(portfolioId);
        }
        catch (IllegalArgumentException e)
        {
            bestSymbol = "N/A";
            worstSymbol = "N/A";
        }

        BigDecimal netWorthNumber = cashBalanceNumber.add(totalStockValueNumber);
        BigDecimal startingBalance = AppConfig.getInstance().getStartingBalance();

        cashBalance.set(formatCurrency(cashBalanceNumber));
        totalStockValue.set(formatCurrency(totalStockValueNumber));
        totalPL.set(formatCurrency(totalPLNumber));
        netWorth.set(formatCurrency(netWorthNumber));

        ownedStocks.set(String.valueOf(holdings.size()));
        totalShares.set(String.valueOf(totalSharesNumber));
        ownedStocksCount.set(holdings.size() + " owned stocks");

        bestPerformer.set(bestSymbol);
        worstPerformer.set(worstSymbol);

        cashStatus.set(cashBalanceNumber.compareTo(BigDecimal.ZERO) > 0 ? "Available cash" : "No cash available");
        totalPLPercent.set(formatProfitLossPercent(totalPLNumber, startingBalance));
    }

    private String formatProfitLossPercent(BigDecimal profitLoss, BigDecimal baseValue)
    {
        if (baseValue == null || baseValue.compareTo(BigDecimal.ZERO) == 0)
        {
            return "0.00%";
        }

        BigDecimal percent = profitLoss
                .divide(baseValue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        return (percent.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "")
                + percent.setScale(2, RoundingMode.HALF_UP)
                + "%";
    }

    private String formatCurrency(BigDecimal value)
    {
        return String.format("¤ %.2f", value);
    }

    public ObservableList<HoldingRowViewModel> getHoldings()
    {
        return holdings;
    }

    public ObservableList<TransactionRowViewModel> getTransactions()
    {
        return transactions;
    }

    public ObservableList<PieChart.Data> getShareDistribution()
    {
        return shareDistribution;
    }

    public ReadOnlyStringWrapper netWorthProperty()
    {
        return netWorth;
    }

    public ReadOnlyStringWrapper cashBalanceProperty()
    {
        return cashBalance;
    }

    public ReadOnlyStringWrapper cashStatusProperty()
    {
        return cashStatus;
    }

    public ReadOnlyStringWrapper totalStockValueProperty()
    {
        return totalStockValue;
    }

    public ReadOnlyStringWrapper ownedStocksCountProperty()
    {
        return ownedStocksCount;
    }

    public ReadOnlyStringWrapper totalPLProperty()
    {
        return totalPL;
    }

    public ReadOnlyStringWrapper totalPLPercentProperty()
    {
        return totalPLPercent;
    }

    public ReadOnlyStringWrapper holdingsUpdatedTextProperty()
    {
        return holdingsUpdatedText;
    }

    public ReadOnlyStringWrapper transactionsCountTextProperty()
    {
        return transactionsCountText;
    }

    public ReadOnlyStringWrapper bestPerformerProperty()
    {
        return bestPerformer;
    }

    public ReadOnlyStringWrapper worstPerformerProperty()
    {
        return worstPerformer;
    }

    public ReadOnlyStringWrapper totalSharesProperty()
    {
        return totalShares;
    }

    public ReadOnlyStringWrapper ownedStocksProperty()
    {
        return ownedStocks;
    }
}