package presentation.views.transactions;

import business.dto.PageResult;
import business.services.PortfolioService;
import entities.Transaction;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import presentation.core.ApplicationContext;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TransactionsViewModel
{
    private final ReadOnlyStringWrapper totalTransactions = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper buyCount = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper sellCount = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper latestActivity = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper pageInfo = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper resultInfo = new ReadOnlyStringWrapper();

    private final ObservableList<TransactionRowViewModel> transactions = FXCollections.observableArrayList();
    private final PortfolioService portfolioService;

    private UUID portfolioId;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final static int PAGE_SIZE = 10;
    private int pageNumber = 0;
    private int maxPageNumber = 0;

    public TransactionsViewModel(ApplicationContext appContext, PortfolioService portfolioService)
    {
        this.portfolioService = portfolioService;

        ChangeListener<UUID> activePortfolioListener = (_, _, newId) -> this.portfolioId = newId;
        appContext.activePortfolioIdProperty().addListener(activePortfolioListener);
    }

    public void load()
    {
        transactions.clear();

        loadTransactionTable();
        refreshValues();
    }

    private void loadTransactionTable()
    {
        if (portfolioId == null) return;

        PageResult<Transaction> transactionPageResult = portfolioService.getTransactionHistory(portfolioId, pageNumber, PAGE_SIZE);
        maxPageNumber = transactionPageResult.totalPages();
        if (maxPageNumber == 0) maxPageNumber = 1;

        for (Transaction transaction : transactionPageResult.items()) {
            int quantity = transaction.quantity();
            Transaction.Type type = transaction.type();
            BigDecimal total = type == Transaction.Type.BUY ?
                    transaction.getTotalPriceWithFee() :
                    transaction.getTotalPriceFeeSubtracted();

            transactions.add(new TransactionRowViewModel(
                    transaction.timeStamp().format(formatter),
                    type.toString(),
                    transaction.stockSymbol(),
                    quantity,
                    transaction.pricePerShare().toString(),
                    total.toString()
            ));
        }

    }

    private void refreshValues()
    {
        totalTransactions.set(portfolioService.getTotalTransactions(portfolioId) + "");
        buyCount.set(portfolioService.getTransactionTotalBuyCount(portfolioId) + "");
        sellCount.set(portfolioService.getTransactionTotalSellCount(portfolioId) + "");

        try {
            Transaction.Type type = portfolioService.getLatestTransactionType(portfolioId);
            latestActivity.set(type.toString());
        } catch (Exception e)
        {
            latestActivity.set("no transactions");
        }

        pageInfo.set((pageNumber + 1) + "/"+ (maxPageNumber));
    }

    public void nextPage()
    {
        if (pageNumber + 1 >= maxPageNumber) return;
        pageNumber++;
        load();
    }

    public void previousPage()
    {
        if (pageNumber == 0) return;
        pageNumber--;
        load();
    }

    public ReadOnlyStringWrapper totalTransactionsProperty()
    {
        return totalTransactions;
    }

    public ReadOnlyStringWrapper buyCountProperty()
    {
        return buyCount;
    }

    public ReadOnlyStringWrapper latestActivityProperty()
    {
        return latestActivity;
    }

    public ReadOnlyStringWrapper sellCountProperty()
    {
        return sellCount;
    }

    public ReadOnlyStringWrapper pageInfoProperty()
    {
        return pageInfo;
    }

    public ReadOnlyStringWrapper resultInfoProperty()
    {
        return resultInfo;
    }

    public ObservableList<TransactionRowViewModel> getTransactions()
    {
        return transactions;
    }
}