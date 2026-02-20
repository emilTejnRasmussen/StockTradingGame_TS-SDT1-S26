package persistence.fileImplementation;

import entities.*;
import persistence.interfaces.UnitOfWork;
import persistence.json.JsonFileStore;
import shared.logging.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileUnitOfWork implements UnitOfWork {
    private final String directoryPath;
    private final JsonFileStore store;
    private static final Object FILE_WRITE_LOCK = new Object();

    private List<Portfolio> portfolios;
    private List<Stock> stocks;
    private List<OwnedStock> ownedStocks;
    private List<Transaction> transactions;
    private List<StockPriceHistory> stockPriceHistories;

    private static final String PORTFOLIOS_FILENAME = "portfolios.json";
    private static final String STOCKS_FILENAME = "stocks.json";
    private static final String OWNED_STOCKS_FILENAME = "owned_stocks.json";
    private static final String TRANSACTIONS_FILENAME = "transactions.json";
    private static final String STOCK_PRICE_HISTORY_FILENAME = "stock_price_history.json";

    public FileUnitOfWork(String directoryPath) {
        this.directoryPath = directoryPath;
        this.store = new JsonFileStore(this.directoryPath);
        ensureFilesExists();
    }

    @Override
    public void begin() {
        clearLists();
    }


    @Override
    public void rollback() {
        clearLists();
    }

    @Override
    public void commit() {
        synchronized (FILE_WRITE_LOCK){
            if (stocks != null) {
                store.saveList(STOCKS_FILENAME, stocks);
            }
            if (ownedStocks != null) {
                store.saveList(OWNED_STOCKS_FILENAME, ownedStocks);
            }
            if (transactions != null) {
                store.saveList(TRANSACTIONS_FILENAME, transactions);
            }
            if (stockPriceHistories != null){
                store.saveList(STOCK_PRICE_HISTORY_FILENAME, stockPriceHistories);
            }
            if (portfolios != null) {
                store.saveList(PORTFOLIOS_FILENAME, portfolios);
            }
        }
    }

    public List<Portfolio> getPortfolios() {
        if (portfolios == null){
            portfolios = store.loadList(PORTFOLIOS_FILENAME, store.listTypeOf(Portfolio.class));
        }
        return portfolios;
    }

    public List<Stock> getStocks() {
        if (stocks == null){
            stocks = store.loadList(STOCKS_FILENAME, store.listTypeOf(Stock.class));
        }
        return stocks;
    }

    public List<OwnedStock> getOwnedStocks() {
        if (ownedStocks == null){
            ownedStocks = store.loadList(OWNED_STOCKS_FILENAME, store.listTypeOf(OwnedStock.class));
        }
        return ownedStocks;
    }

    public List<Transaction> getTransactions() {
        if (transactions == null){
            transactions = store.loadList(TRANSACTIONS_FILENAME, store.listTypeOf(Transaction.class));
        }
        return transactions;
    }

    public List<StockPriceHistory> getStockPriceHistories() {
        if (stockPriceHistories == null){
            stockPriceHistories = store.loadList(STOCK_PRICE_HISTORY_FILENAME, store.listTypeOf(StockPriceHistory.class));
        }
        return stockPriceHistories;
    }

    private void clearLists() {
        portfolios = null;
        stocks = null;
        ownedStocks = null;
        transactions = null;
        stockPriceHistories = null;
    }

    private void ensureFilesExists() {
        try{
            Path dir = Path.of(directoryPath);

            if (Files.notExists(dir)){
                Files.createDirectories(dir);
            }

            List<String> entityFiles = List.of(
                    PORTFOLIOS_FILENAME,
                    STOCKS_FILENAME,
                    OWNED_STOCKS_FILENAME,
                    STOCK_PRICE_HISTORY_FILENAME,
                    TRANSACTIONS_FILENAME
            );

            for (String fileName : entityFiles) {
                Path filePath = dir.resolve(fileName);
                if (Files.notExists(filePath)){
                    Files.writeString(filePath, "[]", StandardOpenOption.CREATE_NEW);
                }
            }
        } catch (IOException e) {
            Logger.getInstance().error("Failed to initialize storage files: " + e.getMessage());
            throw new RuntimeException("Failed to initialize storage files", e);
        }
    }
}
