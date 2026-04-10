package presentation.views.transactions;

import business.services.PortfolioService;
import business.services.listener.StockListenerService;
import entities.Transaction;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import presentation.core.ApplicationContext;
import business.dto.PageResult;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class TransactionsViewModel implements PropertyChangeListener
{
    private static final int PAGE_SIZE = 12;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ReadOnlyStringWrapper totalTransactions = new ReadOnlyStringWrapper("0");
    private final ReadOnlyStringWrapper buyCount = new ReadOnlyStringWrapper("0");
    private final ReadOnlyStringWrapper sellCount = new ReadOnlyStringWrapper("0");
    private final ReadOnlyStringWrapper latestActivity = new ReadOnlyStringWrapper("No activity");
    private final ReadOnlyStringWrapper pageInfo = new ReadOnlyStringWrapper("Page 1 of 1");
    private final ReadOnlyStringWrapper resultInfo = new ReadOnlyStringWrapper("Showing 0 transactions");

    private final ObservableList<TransactionRowViewModel> transactions = FXCollections.observableArrayList();

    private final PortfolioService portfolioService;

    private UUID portfolioId;
    private int currentPage = 0;
    private int totalPages = 1;
    private int totalItems = 0;

    public TransactionsViewModel(ApplicationContext appContext, PortfolioService portfolioService, StockListenerService stockListenerService)
    {
        this.portfolioService = portfolioService;

        ChangeListener<UUID> activePortfolioListener = (_, _, newId) -> {
            this.portfolioId = newId;
            this.currentPage = 0;
            load();
        };

        appContext.activePortfolioIdProperty().addListener(activePortfolioListener);
        stockListenerService.addListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        Platform.runLater(this::refresh);
    }

    public void load()
    {
        if (portfolioId == null)
        {
            clearData();
            return;
        }

        loadTransactions();
        recalculateSummary();
    }

    public void refresh()
    {
        load();
    }

    public void previousPage()
    {
        if (currentPage <= 0) return;

        currentPage--;
        load();
    }

    public void nextPage()
    {
        if (currentPage >= totalPages - 1) return;

        currentPage++;
        load();
    }

    private void loadTransactions()
    {
        transactions.clear();

        if (portfolioId == null) return;

        PageResult<Transaction> pageResult = portfolioService.getTransactionHistory(portfolioId, currentPage, PAGE_SIZE);
        List<Transaction> items = pageResult.items();

        totalItems = pageResult.totalItems();
        totalPages = Math.max(1, pageResult.totalPages());

        for (Transaction transaction : items)
        {
            String time = transaction.timeStamp().format(TIME_FORMATTER);
            String type = transaction.type().toString();
            String symbol = transaction.stockSymbol();
            int shares = transaction.quantity();

            BigDecimal price = transaction.type() == Transaction.Type.BUY
                    ? transaction.getTotalPriceWithFee()
                    : transaction.getTotalPriceFeeSubtracted();

            BigDecimal total = price.multiply(BigDecimal.valueOf(shares));

            transactions.add(new TransactionRowViewModel(
                    transaction.id(),
                    time,
                    type,
                    symbol,
                    shares,
                    formatCurrency(price),
                    formatCurrency(total)
            ));
        }
    }

    private void recalculateSummary()
    {
        if (portfolioId == null)
        {
            totalTransactions.set("0");
            buyCount.set("0");
            sellCount.set("0");
            latestActivity.set("No activity");
            pageInfo.set("Page 1 of 1");
            resultInfo.set("Showing 0 transactions");
            return;
        }

        PageResult<Transaction> summaryResult = portfolioService.getTransactionHistory(portfolioId, 0, 20);
        List<Transaction> allTransactions = summaryResult.items();

        int buys = 0;
        int sells = 0;

        for (Transaction transaction : allTransactions)
        {
            if (transaction.type() == Transaction.Type.BUY) {
                buys++;
            } else if (transaction.type() == Transaction.Type.SELL) {
                sells++;
            }
        }

        totalTransactions.set(String.valueOf(summaryResult.totalItems()));
        buyCount.set(String.valueOf(buys));
        sellCount.set(String.valueOf(sells));

        if (allTransactions.isEmpty()) {
            latestActivity.set("No activity");
        } else {
            latestActivity.set(allTransactions.getFirst().timeStamp().format(TIME_FORMATTER));
        }

        pageInfo.set("Page " + (currentPage + 1) + " of " + totalPages);

        if (totalItems == 0) {
            resultInfo.set("Showing 0 transactions");
            return;
        }

        int start = currentPage * PAGE_SIZE + 1;
        int end = Math.min((currentPage + 1) * PAGE_SIZE, totalItems);
        resultInfo.set("Showing " + start + "-" + end + " of " + totalItems + " transactions");
    }

    private void clearData()
    {
        transactions.clear();
        totalTransactions.set("0");
        buyCount.set("0");
        sellCount.set("0");
        latestActivity.set("No activity");
        pageInfo.set("Page 1 of 1");
        resultInfo.set("Showing 0 transactions");
        totalItems = 0;
        totalPages = 1;
        currentPage = 0;
    }

    private String formatCurrency(BigDecimal value)
    {
        return String.format("¤ %.2f", value);
    }

    public ObservableList<TransactionRowViewModel> getTransactions()
    {
        return transactions;
    }

    public ReadOnlyStringWrapper totalTransactionsProperty()
    {
        return totalTransactions;
    }

    public ReadOnlyStringWrapper buyCountProperty()
    {
        return buyCount;
    }

    public ReadOnlyStringWrapper sellCountProperty()
    {
        return sellCount;
    }

    public ReadOnlyStringWrapper latestActivityProperty()
    {
        return latestActivity;
    }

    public ReadOnlyStringWrapper pageInfoProperty()
    {
        return pageInfo;
    }

    public ReadOnlyStringWrapper resultInfoProperty()
    {
        return resultInfo;
    }

    public boolean hasPreviousPage()
    {
        return currentPage > 0;
    }

    public boolean hasNextPage()
    {
        return currentPage < totalPages - 1;
    }
}