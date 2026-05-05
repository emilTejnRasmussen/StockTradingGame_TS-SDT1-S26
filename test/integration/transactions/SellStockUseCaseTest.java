package integration.transactions;

import business.feecalc.FlatFeeStrategy;
import business.services.PortfolioService;
import business.services.StockHistoryService;
import business.services.StockService;
import business.services.TradingService;
import business.services.listener.StockListenerService;
import entities.Stock;
import entities.Transaction;
import exception.TransactionFailedException;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.*;
import persistence.fileImplementation.*;
import presentation.views.stockmarket.StockMarketViewModel;
import presentation.views.stockmarket.StockTableRow;
import shared.logging.Logger;

import java.io.File;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SellStockUseCaseTest
{
    private static final String STOCK_SYMBOL = "AAPL";
    private static final String STOCK_NAME = "Apple";
    private static final BigDecimal STOCK_PRICE = BigDecimal.valueOf(100);

    String testDirPath;

    FileUnitOfWork uow;

    FileStockDao stockDao;
    FilePortfolioDao portfolioDao;
    FileOwnedStockDao ownedStockDao;
    FileStockPriceHistoryDao stockPriceHistoryDao;
    FileTransactionDao transactionDao;

    StockListenerService stockListenerService;
    StockHistoryService stockHistoryService;
    StockService stockService;
    PortfolioService portfolioService;
    TradingService tradingService;

    StockMarketViewModel stockMarketViewModel;

    ObjectProperty<UUID> activePortfolioId;

    @BeforeAll
    static void initToolkit()
    {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // JavaFX toolkit already started
        }
    }

    @BeforeEach
    void setup()
    {
        testDirPath = "test/integration/transactions/test-" + UUID.randomUUID();

        uow = new FileUnitOfWork(testDirPath);

        portfolioDao = new FilePortfolioDao(uow);
        stockDao = new FileStockDao(uow);
        stockPriceHistoryDao = new FileStockPriceHistoryDao(uow);
        ownedStockDao = new FileOwnedStockDao(uow);
        transactionDao = new FileTransactionDao(uow);

        stockListenerService = new StockListenerService(
                uow,
                stockDao,
                stockPriceHistoryDao
        );

        stockHistoryService = new StockHistoryService(stockPriceHistoryDao);
        stockService = new StockService(stockDao);

        portfolioService = new PortfolioService(
                uow,
                portfolioDao,
                ownedStockDao,
                stockDao,
                transactionDao
        );

        tradingService = new TradingService(
                uow,
                stockDao,
                portfolioDao,
                transactionDao,
                ownedStockDao,
                Logger.getInstance(),
                new FlatFeeStrategy()
        );

        activePortfolioId = new SimpleObjectProperty<>();

        stockMarketViewModel = new StockMarketViewModel(
                stockListenerService,
                stockHistoryService,
                stockService,
                portfolioService,
                tradingService,
                activePortfolioId
        );
    }

    @AfterEach
    void cleanup()
    {
        deleteDirectory(new File(testDirPath));
    }

    @Nested
    class GivenZeroQuantity
    {
        UUID portfolioId;
        int quantity;

        @BeforeEach
        void arrange()
        {
            quantity = 0;

            portfolioId = createPortfolioWithBalance(BigDecimal.valueOf(1500));
            createStock();
            buyShares(portfolioId, 2);

            activePortfolioId.set(portfolioId);

            stockMarketViewModel.load();
            stockMarketViewModel.setSelectedStockSymbol(STOCK_SYMBOL);
            stockMarketViewModel.setSelectedQuantity(quantity);
        }

        @Test
        void sellButton_isDisabled()
        {
            assertTrue(stockMarketViewModel.sellDisabledProperty().get());
        }

        @Test
        void sellingStock_doesNotPersistSellTransaction()
        {
            trySellIgnoringException(quantity);

            assertEquals(1, transactionDao.getAll().size());
        }

        @Test
        void sellingStock_doesNotChangePersistedOwnedShares()
        {
            trySellIgnoringException(quantity);

            assertEquals(2, portfolioService.getNumberOfSharesOwned(portfolioId, STOCK_SYMBOL));
        }
    }

    @Nested
    class GivenOneShare
    {
        UUID portfolioId;
        int quantity;

        @BeforeEach
        void arrange()
        {
            quantity = 1;

            portfolioId = createPortfolioWithBalance(BigDecimal.valueOf(1500));
            createStock();
            buyShares(portfolioId, 2);

            activePortfolioId.set(portfolioId);

            stockMarketViewModel.load();
            stockMarketViewModel.setSelectedStockSymbol(STOCK_SYMBOL);
            stockMarketViewModel.setSelectedQuantity(quantity);
        }

        @Test
        void sellingStock_decreasesPersistedOwnedSharesByOne()
        {
            stockMarketViewModel.sell(quantity);

            assertEquals(1, portfolioService.getNumberOfSharesOwned(portfolioId, STOCK_SYMBOL));
        }

        @Test
        void sellingStock_increasesPersistedCashBalance()
        {
            BigDecimal cashBefore = portfolioService.getPortfolioBalance(portfolioId);

            stockMarketViewModel.sell(quantity);

            BigDecimal cashAfter = portfolioService.getPortfolioBalance(portfolioId);

            assertTrue(cashAfter.compareTo(cashBefore) > 0);
        }

        @Test
        void sellingStock_persistsSellTransaction()
        {
            stockMarketViewModel.sell(quantity);

            Transaction transaction = transactionDao.getAll().getLast();

            assertEquals(Transaction.Type.SELL, transaction.type());
        }

        @Test
        void sellingStock_persistsTransactionWithSelectedStockSymbol()
        {
            stockMarketViewModel.sell(quantity);

            Transaction transaction = transactionDao.getAll().getLast();

            assertEquals(STOCK_SYMBOL, transaction.stockSymbol());
        }

        @Test
        void sellingStock_persistsTransactionWithSelectedQuantity()
        {
            stockMarketViewModel.sell(quantity);

            Transaction transaction = transactionDao.getAll().getLast();

            assertEquals(quantity, transaction.quantity());
        }

        @Test
        void sellingStock_updatesStockRowOwnedAmountAfterReload()
        {
            stockMarketViewModel.sell(quantity);
            stockMarketViewModel.load();

            StockTableRow row = findStockRow();

            assertEquals("1", row.getOwned());
        }
    }

    @Nested
    class GivenMultipleShares
    {
        UUID portfolioId;
        int quantity;

        @BeforeEach
        void arrange()
        {
            quantity = 2;

            portfolioId = createPortfolioWithBalance(BigDecimal.valueOf(1500));
            createStock();
            buyShares(portfolioId, 5);

            activePortfolioId.set(portfolioId);

            stockMarketViewModel.load();
            stockMarketViewModel.setSelectedStockSymbol(STOCK_SYMBOL);
            stockMarketViewModel.setSelectedQuantity(quantity);
        }

        @Test
        void sellingStock_decreasesPersistedOwnedSharesByCorrectAmount()
        {
            stockMarketViewModel.sell(quantity);

            assertEquals(3, portfolioService.getNumberOfSharesOwned(portfolioId, STOCK_SYMBOL));
        }

        @Test
        void sellingStock_persistsOneSellTransaction()
        {
            stockMarketViewModel.sell(quantity);

            assertEquals(2, transactionDao.getAll().size());
        }

        @Test
        void sellingStock_updatesViewModelCashBalancePropertyAfterReload()
        {
            String cashBefore = stockMarketViewModel.cashBalanceProperty().get();

            stockMarketViewModel.sell(quantity);
            stockMarketViewModel.load();

            assertNotEquals(cashBefore, stockMarketViewModel.cashBalanceProperty().get());
        }

        @Test
        void sellingStock_updatesStockRowOwnedAmountAfterReload()
        {
            stockMarketViewModel.sell(quantity);
            stockMarketViewModel.load();

            StockTableRow row = findStockRow();

            assertEquals("3", row.getOwned());
        }
    }

    @Nested
    class GivenExactOwnedShares
    {
        UUID portfolioId;
        int quantity;

        @BeforeEach
        void arrange()
        {
            quantity = 2;

            portfolioId = createPortfolioWithBalance(BigDecimal.valueOf(1500));
            createStock();
            buyShares(portfolioId, 2);

            activePortfolioId.set(portfolioId);

            stockMarketViewModel.load();
            stockMarketViewModel.setSelectedStockSymbol(STOCK_SYMBOL);
            stockMarketViewModel.setSelectedQuantity(quantity);
        }

        @Test
        void sellButton_isEnabled()
        {
            assertFalse(stockMarketViewModel.sellDisabledProperty().get());
        }

        @Test
        void sellingStock_removesOrZerosOwnedStock()
        {
            stockMarketViewModel.sell(quantity);

            boolean ownsAapl = portfolioService.getOwnedStocks(portfolioId)
                    .stream()
                    .anyMatch(stock -> stock.getStockSymbol().equals(STOCK_SYMBOL)
                            && stock.getNumberOfShares() > 0);

            assertFalse(ownsAapl);
        }

        @Test
        void sellingStock_persistsSellTransaction()
        {
            stockMarketViewModel.sell(quantity);

            Transaction transaction = transactionDao.getAll().getLast();

            assertEquals(Transaction.Type.SELL, transaction.type());
        }

        @Test
        void sellingStock_updatesStockRowOwnedAmountAfterReload()
        {
            stockMarketViewModel.sell(quantity);
            stockMarketViewModel.load();

            StockTableRow row = findStockRow();

            assertEquals("0", row.getOwned());
        }
    }

    @Nested
    class GivenInsufficientOwnedShares
    {
        UUID portfolioId;
        int quantity;

        @BeforeEach
        void arrange()
        {
            quantity = 3;

            portfolioId = createPortfolioWithBalance(BigDecimal.valueOf(1500));
            createStock();
            buyShares(portfolioId, 2);

            activePortfolioId.set(portfolioId);

            stockMarketViewModel.load();
            stockMarketViewModel.setSelectedStockSymbol(STOCK_SYMBOL);
            stockMarketViewModel.setSelectedQuantity(quantity);
        }

        @Test
        void sellButton_isDisabled()
        {
            assertTrue(stockMarketViewModel.sellDisabledProperty().get());
        }

        @Test
        void sellingStock_doesNotPersistSellTransaction()
        {
            trySellIgnoringException(quantity);

            assertEquals(1, transactionDao.getAll().size());
        }

        @Test
        void sellingStock_doesNotChangePersistedOwnedShares()
        {
            trySellIgnoringException(quantity);

            assertEquals(2, portfolioService.getNumberOfSharesOwned(portfolioId, STOCK_SYMBOL));
        }
    }

    @Nested
    class GivenUnknownStock
    {
        UUID portfolioId;
        String unknownStockSymbol;
        int quantity;

        @BeforeEach
        void arrange()
        {
            unknownStockSymbol = "UNKNOWN";
            quantity = 1;

            portfolioId = createPortfolioWithBalance(BigDecimal.valueOf(1500));
            createStock();
            buyShares(portfolioId, 2);

            activePortfolioId.set(portfolioId);

            stockMarketViewModel.load();
            stockMarketViewModel.setSelectedStockSymbol(unknownStockSymbol);
            stockMarketViewModel.setSelectedQuantity(quantity);
        }

        @Test
        void sellingStock_doesNotPersistSellTransaction()
        {
            trySellIgnoringException(quantity);

            assertEquals(1, transactionDao.getAll().size());
        }
    }

    @Nested
    class GivenNoActivePortfolio
    {
        int quantity;

        @BeforeEach
        void arrange()
        {
            quantity = 1;

            createStock();

            activePortfolioId.set(null);

            stockMarketViewModel.load();
            stockMarketViewModel.setSelectedStockSymbol(STOCK_SYMBOL);
            stockMarketViewModel.setSelectedQuantity(quantity);
        }

        @Test
        void sellButton_isDisabled()
        {
            assertTrue(stockMarketViewModel.sellDisabledProperty().get());
        }

        @Test
        void sellingStock_doesNotPersistTransaction()
        {
            trySellIgnoringException(quantity);

            assertTrue(transactionDao.getAll().isEmpty());
        }
    }

    @Nested
    class GivenNoSelectedStock
    {
        UUID portfolioId;
        int quantity;

        @BeforeEach
        void arrange()
        {
            quantity = 1;

            portfolioId = createPortfolioWithBalance(BigDecimal.valueOf(1500));
            createStock();
            buyShares(portfolioId, 2);

            activePortfolioId.set(portfolioId);

            stockMarketViewModel.load();
            stockMarketViewModel.setSelectedStockSymbol(null);
            stockMarketViewModel.setSelectedQuantity(quantity);
        }

        @Test
        void sellButton_isDisabled()
        {
            assertTrue(stockMarketViewModel.sellDisabledProperty().get());
        }

        @Test
        void sellingStock_doesNotPersistSellTransaction()
        {
            trySellIgnoringException(quantity);

            assertEquals(1, transactionDao.getAll().size());
        }
    }

    @Nested
    class GivenMultipleSellsOfSameStock
    {
        UUID portfolioId;
        int quantity;

        @BeforeEach
        void arrange()
        {
            quantity = 1;

            portfolioId = createPortfolioWithBalance(BigDecimal.valueOf(1500));
            createStock();
            buyShares(portfolioId, 3);

            activePortfolioId.set(portfolioId);

            stockMarketViewModel.load();
            stockMarketViewModel.setSelectedStockSymbol(STOCK_SYMBOL);
            stockMarketViewModel.setSelectedQuantity(quantity);

            stockMarketViewModel.sell(quantity);
        }

        @Test
        void sellingStockAgain_decreasesPersistedOwnedSharesAgain()
        {
            stockMarketViewModel.sell(quantity);

            assertEquals(1, portfolioService.getNumberOfSharesOwned(portfolioId, STOCK_SYMBOL));
        }

        @Test
        void sellingStockAgain_persistsSecondSellTransaction()
        {
            stockMarketViewModel.sell(quantity);

            assertEquals(3, transactionDao.getAll().size());
        }

        @Test
        void sellingStockAgain_updatesViewModelOwnedAmountAfterReload()
        {
            stockMarketViewModel.sell(quantity);
            stockMarketViewModel.load();

            StockTableRow row = findStockRow();

            assertEquals("1", row.getOwned());
        }
    }

    private UUID createPortfolioWithBalance(BigDecimal balance)
    {
        portfolioService.createNewPortfolio("test-portfolio", balance);
        uow.commit();

        return portfolioDao.getAll().getFirst().getId();
    }

    private void createStock()
    {
        Stock testStock = new Stock(
                STOCK_SYMBOL,
                STOCK_NAME,
                STOCK_PRICE
        );

        stockDao.create(testStock);
        uow.commit();
    }

    private void buyShares(UUID portfolioId, int quantity)
    {
        activePortfolioId.set(portfolioId);

        stockMarketViewModel.load();
        stockMarketViewModel.setSelectedStockSymbol(STOCK_SYMBOL);
        stockMarketViewModel.setSelectedQuantity(quantity);
        stockMarketViewModel.buy(quantity);

        uow.commit();
    }

    private StockTableRow findStockRow()
    {
        return stockMarketViewModel.getStocks()
                .stream()
                .filter(row -> row.getSymbol().equals(SellStockUseCaseTest.STOCK_SYMBOL))
                .findFirst()
                .orElseThrow();
    }

    private void trySellIgnoringException(int quantity)
    {
        try {
            stockMarketViewModel.sell(quantity);
        } catch (Exception ignored) {
        }
    }

    private void deleteDirectory(File file)
    {
        if (file == null || !file.exists()) return;

        File[] files = file.listFiles();

        if (files != null) {
            for (File child : files) {
                deleteDirectory(child);
            }
        }

        file.delete();
    }
}