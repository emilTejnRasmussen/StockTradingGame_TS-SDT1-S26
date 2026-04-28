package integration.transactions;

import business.feecalc.FeeCalculationContext;
import business.feecalc.FlatFeeStrategy;
import business.services.PortfolioService;
import business.services.StockHistoryService;
import business.services.StockService;
import business.services.TradingService;
import business.services.listener.StockListenerService;
import entities.Stock;
import entities.Transaction;
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

public class BuyStockUseCaseTest
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

    FeeCalculationContext feeCalculationContext;

    ObjectProperty<UUID> activePortfolioId;

    @BeforeAll
    static void initToolkit()
    {
        Platform.startup(() -> {
        });
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

        feeCalculationContext = new FeeCalculationContext(new FlatFeeStrategy());

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
                feeCalculationContext
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

            activePortfolioId.set(portfolioId);

            stockMarketViewModel.load();
            stockMarketViewModel.setSelectedStockSymbol(STOCK_SYMBOL);
            stockMarketViewModel.setSelectedQuantity(quantity);
        }

        @Test
        void buyButton_isDisabled()
        {
            assertTrue(stockMarketViewModel.buyDisabledProperty().get());
        }

        @Test
        void buyingStock_doesNotPersistTransaction()
        {
            tryBuyIgnoringException(quantity);

            assertTrue(transactionDao.getAll().isEmpty());
        }

        @Test
        void buyingStock_doesNotPersistOwnedStock()
        {
            tryBuyIgnoringException(quantity);

            assertTrue(portfolioService.getOwnedStocks(portfolioId).isEmpty());
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

            activePortfolioId.set(portfolioId);

            stockMarketViewModel.load();
            stockMarketViewModel.setSelectedStockSymbol(STOCK_SYMBOL);
            stockMarketViewModel.setSelectedQuantity(quantity);
        }

        @Test
        void buyingStock_persistsOneOwnedShare()
        {
            stockMarketViewModel.buy(quantity);

            assertEquals(1, portfolioService.getNumberOfSharesOwned(portfolioId, STOCK_SYMBOL));
        }

        @Test
        void buyingStock_persistsBuyTransaction()
        {
            stockMarketViewModel.buy(quantity);

            Transaction transaction = transactionDao.getAll().getFirst();

            assertEquals(Transaction.Type.BUY, transaction.type());
        }

        @Test
        void buyingStock_persistsTransactionWithSelectedStockSymbol()
        {
            stockMarketViewModel.buy(quantity);

            Transaction transaction = transactionDao.getAll().getFirst();

            assertEquals(STOCK_SYMBOL, transaction.stockSymbol());
        }

        @Test
        void buyingStock_persistsTransactionWithSelectedQuantity()
        {
            stockMarketViewModel.buy(quantity);

            Transaction transaction = transactionDao.getAll().getFirst();

            assertEquals(quantity, transaction.quantity());
        }

        @Test
        void buyingStock_reducesPersistedCashBalance()
        {
            BigDecimal cashBefore = portfolioService.getPortfolioBalance(portfolioId);

            stockMarketViewModel.buy(quantity);

            BigDecimal cashAfter = portfolioService.getPortfolioBalance(portfolioId);

            assertTrue(cashAfter.compareTo(cashBefore) < 0);
        }

        @Test
        void buyingStock_updatesStockRowOwnedAmountAfterReload()
        {
            stockMarketViewModel.buy(quantity);
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

            activePortfolioId.set(portfolioId);

            stockMarketViewModel.load();
            stockMarketViewModel.setSelectedStockSymbol(STOCK_SYMBOL);
            stockMarketViewModel.setSelectedQuantity(quantity);
        }

        @Test
        void buyingStock_persistsCorrectNumberOfOwnedShares()
        {
            stockMarketViewModel.buy(quantity);

            assertEquals(quantity, portfolioService.getNumberOfSharesOwned(portfolioId, STOCK_SYMBOL));
        }

        @Test
        void buyingStock_persistsOneTransaction()
        {
            stockMarketViewModel.buy(quantity);

            assertEquals(1, transactionDao.getAll().size());
        }

        @Test
        void buyingStock_updatesViewModelCashBalancePropertyAfterReload()
        {
            stockMarketViewModel.buy(quantity);
            stockMarketViewModel.load();

            assertNotEquals("¤ 1500.00", stockMarketViewModel.cashBalanceProperty().get());
        }

        @Test
        void buyingStock_updatesStockRowOwnedAmountAfterReload()
        {
            stockMarketViewModel.buy(quantity);
            stockMarketViewModel.load();

            StockTableRow row = findStockRow();

            assertEquals("2", row.getOwned());
        }
    }

    @Nested
    class GivenExactAffordableCash
    {
        UUID portfolioId;
        int quantity;

        @BeforeEach
        void arrange()
        {
            quantity = 2;

            BigDecimal basePrice = STOCK_PRICE.multiply(BigDecimal.valueOf(quantity));
            BigDecimal fee = BigDecimal.valueOf(feeCalculationContext.calculateFee(basePrice));
            BigDecimal exactAffordableBalance = basePrice.add(fee);

            portfolioId = createPortfolioWithBalance(exactAffordableBalance);
            createStock();

            activePortfolioId.set(portfolioId);

            stockMarketViewModel.load();
            stockMarketViewModel.setSelectedStockSymbol(STOCK_SYMBOL);
            stockMarketViewModel.setSelectedQuantity(quantity);
        }

        @Test
        void buyButton_isEnabled()
        {
            assertFalse(stockMarketViewModel.buyDisabledProperty().get());
        }

        @Test
        void buyingStock_persistsSharesToPortfolio()
        {
            stockMarketViewModel.buy(quantity);

            assertEquals(quantity, portfolioService.getNumberOfSharesOwned(portfolioId, STOCK_SYMBOL));
        }

        @Test
        void buyingStock_persistsTransaction()
        {
            stockMarketViewModel.buy(quantity);

            assertEquals(1, transactionDao.getAll().size());
        }

        @Test
        void buyingStock_reducesPersistedCashBalanceToZero()
        {
            stockMarketViewModel.buy(quantity);

            BigDecimal cashAfter = portfolioService.getPortfolioBalance(portfolioId);

            assertEquals(0, BigDecimal.ZERO.compareTo(cashAfter));
        }
    }

    @Nested
    class GivenInsufficientCash
    {
        UUID portfolioId;
        int quantity;

        @BeforeEach
        void arrange()
        {
            quantity = 2;

            portfolioId = createPortfolioWithBalance(BigDecimal.valueOf(50));
            createStock();

            activePortfolioId.set(portfolioId);

            stockMarketViewModel.load();
            stockMarketViewModel.setSelectedStockSymbol(STOCK_SYMBOL);
            stockMarketViewModel.setSelectedQuantity(quantity);
        }

        @Test
        void buyButton_isDisabled()
        {
            assertTrue(stockMarketViewModel.buyDisabledProperty().get());
        }

        @Test
        void buyingStock_doesNotPersistTransaction()
        {
            tryBuyIgnoringException(quantity);

            assertTrue(transactionDao.getAll().isEmpty());
        }

        @Test
        void buyingStock_doesNotPersistOwnedStock()
        {
            tryBuyIgnoringException(quantity);

            assertTrue(portfolioService.getOwnedStocks(portfolioId).isEmpty());
        }

        @Test
        void buyingStock_doesNotChangePersistedCashBalance()
        {
            BigDecimal cashBefore = portfolioService.getPortfolioBalance(portfolioId);

            tryBuyIgnoringException(quantity);

            BigDecimal cashAfter = portfolioService.getPortfolioBalance(portfolioId);

            assertEquals(0, cashBefore.compareTo(cashAfter));
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
            quantity = 2;

            portfolioId = createPortfolioWithBalance(BigDecimal.valueOf(1500));

            activePortfolioId.set(portfolioId);

            stockMarketViewModel.load();
            stockMarketViewModel.setSelectedStockSymbol(unknownStockSymbol);
            stockMarketViewModel.setSelectedQuantity(quantity);
        }

        @Test
        void buyingStock_doesNotPersistTransaction()
        {
            tryBuyIgnoringException(quantity);

            assertTrue(transactionDao.getAll().isEmpty());
        }

        @Test
        void buyingStock_doesNotPersistOwnedStock()
        {
            tryBuyIgnoringException(quantity);

            assertTrue(portfolioService.getOwnedStocks(portfolioId).isEmpty());
        }
    }

    @Nested
    class GivenNoActivePortfolio
    {
        int quantity;

        @BeforeEach
        void arrange()
        {
            quantity = 2;

            createStock();

            activePortfolioId.set(null);

            stockMarketViewModel.load();
            stockMarketViewModel.setSelectedStockSymbol(STOCK_SYMBOL);
            stockMarketViewModel.setSelectedQuantity(quantity);
        }

        @Test
        void buyButton_isDisabled()
        {
            assertTrue(stockMarketViewModel.buyDisabledProperty().get());
        }

        @Test
        void buyingStock_doesNotPersistTransaction()
        {
            tryBuyIgnoringException(quantity);

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
            quantity = 2;

            portfolioId = createPortfolioWithBalance(BigDecimal.valueOf(1500));
            createStock();

            activePortfolioId.set(portfolioId);

            stockMarketViewModel.load();
            stockMarketViewModel.setSelectedStockSymbol(null);
            stockMarketViewModel.setSelectedQuantity(quantity);
        }

        @Test
        void buyButton_isDisabled()
        {
            assertTrue(stockMarketViewModel.buyDisabledProperty().get());
        }

        @Test
        void buyingStock_doesNotPersistTransaction()
        {
            tryBuyIgnoringException(quantity);

            assertTrue(transactionDao.getAll().isEmpty());
        }
    }

    @Nested
    class GivenMultipleBuysOfSameStock
    {
        UUID portfolioId;
        int quantity;

        @BeforeEach
        void arrange()
        {
            quantity = 2;

            portfolioId = createPortfolioWithBalance(BigDecimal.valueOf(1500));
            createStock();

            activePortfolioId.set(portfolioId);

            stockMarketViewModel.load();
            stockMarketViewModel.setSelectedStockSymbol(STOCK_SYMBOL);
            stockMarketViewModel.setSelectedQuantity(quantity);

            stockMarketViewModel.buy(quantity);
        }

        @Test
        void buyingStockAgain_increasesPersistedOwnedShares()
        {
            stockMarketViewModel.buy(quantity);

            assertEquals(4, portfolioService.getNumberOfSharesOwned(portfolioId, STOCK_SYMBOL));
        }

        @Test
        void buyingStockAgain_persistsSecondTransaction()
        {
            stockMarketViewModel.buy(quantity);

            assertEquals(2, transactionDao.getAll().size());
        }

        @Test
        void buyingStockAgain_updatesViewModelOwnedAmountAfterReload()
        {
            stockMarketViewModel.buy(quantity);
            stockMarketViewModel.load();

            StockTableRow row = findStockRow();

            assertEquals("4", row.getOwned());
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

    private StockTableRow findStockRow()
    {
        return stockMarketViewModel.getStocks()
                .stream()
                .filter(row -> row.getSymbol().equals(BuyStockUseCaseTest.STOCK_SYMBOL))
                .findFirst()
                .orElseThrow();
    }

    private void tryBuyIgnoringException(int quantity)
    {
        try
        {
            stockMarketViewModel.buy(quantity);
        } catch (Exception ignored)
        {
        }
    }

    private void deleteDirectory(File file)
    {
        if (file == null || !file.exists()) return;

        File[] files = file.listFiles();

        if (files != null)
        {
            for (File child : files)
            {
                deleteDirectory(child);
            }
        }

        file.delete();
    }
}