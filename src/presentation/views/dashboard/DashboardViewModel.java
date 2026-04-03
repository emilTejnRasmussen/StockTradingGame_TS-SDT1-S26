package presentation.views.dashboard;

import business.services.PortfolioService;
import business.services.StockService;
import entities.OwnedStock;
import entities.Portfolio;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import presentation.core.ApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class DashboardViewModel
{
    private static final String CURRENCY_ZERO = "¤ 0.00";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private final ApplicationContext appContext;

    private final ObservableList<HoldingRowViewModel> holdings = FXCollections.observableArrayList();
    private final ObservableList<TransactionRowViewModel> transactions = FXCollections.observableArrayList();
    private final ObservableList<PieChart.Data> shareDistribution = FXCollections.observableArrayList();

    private final ReadOnlyStringWrapper netWorth = new ReadOnlyStringWrapper(CURRENCY_ZERO);
    private final ReadOnlyStringWrapper netWorthChange = new ReadOnlyStringWrapper("+0.00%");

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
    private final ChangeListener<UUID> activePortfolioListener;

    public DashboardViewModel(ApplicationContext appContext, PortfolioService portfolioService, StockService stockService)
    {
        this.appContext = appContext;
        this.portfolioService = portfolioService;
        this.stockService = stockService;

        this.activePortfolioListener = (_, _, newId) -> this.portfolioId = newId;
        appContext.activePortfolioIdProperty().addListener(activePortfolioListener);
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

    private void loadHoldings()
    {
        if (portfolioId == null) return;

        List<OwnedStock> ownedStocks = portfolioService.getOwnedStocks(portfolioId);



        for (OwnedStock ownedStock : ownedStocks) {
            String stockSymbol = ownedStock.getStockSymbol();
            int numberOfShares = ownedStock.getNumberOfShares();
            BigDecimal currentPrice = BigDecimal.TEN; // stockService.getCurrentPrice(ownedStock.getStockSymbol());
            BigDecimal value = currentPrice.multiply(BigDecimal.valueOf(numberOfShares));
            BigDecimal totalPL = portfolioService.getTotalProfitLoss(portfolioId);
            BigDecimal avgPrice = portfolioService.getAvgStockBuyPrice(ownedStock.getStockSymbol(), portfolioId);


            holdings.add(new HoldingRowViewModel(
                    stockSymbol,
                    numberOfShares,
                    formatCurrency(avgPrice.doubleValue()),
                    formatCurrency(currentPrice.doubleValue()),
                    formatCurrency(value.doubleValue()),
                    formatCurrency(totalPL.doubleValue())
            ));
        }
    }

    private void loadTransactions()
    {
        /*
        Replace this with your real transaction history, for example:
        var txs = appContext.getTransactionService().getRecentTransactions();
        */

        transactions.add(new TransactionRowViewModel("17-04-2026 14:33", "BUY", "AAPL", 4, "¤ 138.00", "¤ 552.00"));
        transactions.add(new TransactionRowViewModel("17-04-2026 12:10", "SELL", "TSLA", 2, "¤ 198.00", "¤ 396.00"));
        transactions.add(new TransactionRowViewModel("16-04-2026 16:45", "BUY", "NVDA", 5, "¤ 102.00", "¤ 510.00"));

        transactionsCountText.set(transactions.size() + " transactions");
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

    private void recalculateSummary()
    {
        double totalStockValueNumber = 0.0;
        double totalPLNumber = 0.0;
        int totalSharesNumber = 0;

        String bestSymbol = "N/A";
        double bestPL = Double.NEGATIVE_INFINITY;

        String worstSymbol = "N/A";
        double worstPL = Double.POSITIVE_INFINITY;

        for (HoldingRowViewModel row : holdings) {
            totalStockValueNumber += parseCurrency(row.getValue());
            totalPLNumber += parseCurrency(row.getPl());
            totalSharesNumber += row.getShares();

            double currentPL = parseCurrency(row.getPl());
            if (currentPL > bestPL) {
                bestPL = currentPL;
                bestSymbol = row.getSymbol();
            }
            if (currentPL < worstPL) {
                worstPL = currentPL;
                worstSymbol = row.getSymbol();
            }
        }

        double cashBalanceNumber = 2500.00;
        double netWorthNumber = cashBalanceNumber + totalStockValueNumber;

        cashBalance.set(formatCurrency(cashBalanceNumber));
        totalStockValue.set(formatCurrency(totalStockValueNumber));
        totalPL.set(formatCurrency(totalPLNumber));
        netWorth.set(formatCurrency(netWorthNumber));

        ownedStocks.set(String.valueOf(holdings.size()));
        totalShares.set(String.valueOf(totalSharesNumber));
        ownedStocksCount.set(holdings.size() + " owned stocks");

        bestPerformer.set(bestSymbol);
        worstPerformer.set(worstSymbol);

        cashStatus.set(cashBalanceNumber > 0 ? "Available cash" : "No cash available");
        netWorthChange.set("+0.00%");
        totalPLPercent.set("+0.00%");
    }

    private String formatCurrency(double value)
    {
        return String.format("¤ %.2f", value);
    }

    private double parseCurrency(String text)
    {
        if (text == null || text.isBlank()) {
            return 0.0;
        }

        String normalized = text
                .replace("¤", "")
                .replace(",", "")
                .trim();

        try {
            return Double.parseDouble(normalized);
        } catch (NumberFormatException e) {
            return 0.0;
        }
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

    public ReadOnlyStringWrapper netWorthChangeProperty()
    {
        return netWorthChange;
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