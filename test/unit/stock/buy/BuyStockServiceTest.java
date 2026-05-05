package unit.stock.buy;

import business.dto.transaction.BuyStockRequestDTO;
import business.feecalc.FeeCalculationStrategy;
import business.feecalc.FlatFeeStrategy;
import business.services.TradingService;
import entities.Portfolio;
import entities.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.interfaces.*;
import unit._mocks.MockLogger;
import unit._mocks.MockUnitOfWork;
import unit._mocks.dao.MockOwnedStockDao;
import unit._mocks.dao.MockPortfolioDao;
import unit._mocks.dao.MockStockDao;
import unit._mocks.dao.MockTransactionDao;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuyStockServiceTest
{
    private MockUnitOfWork uow;
    private StockDao stockDao;
    private PortfolioDao portfolioDao;
    private TransactionDao transactionDao;
    private OwnedStockDao ownedStockDao;

    private Stock stock;
    private Portfolio portfolio;

    private TradingService tradingService;

    private FeeCalculationStrategy feeCalculationStrategy;

    @BeforeEach
    void setup()
    {
        uow = new MockUnitOfWork();
        stockDao = new MockStockDao();
        portfolioDao = new MockPortfolioDao();
        transactionDao = new MockTransactionDao();
        ownedStockDao = new MockOwnedStockDao();
        feeCalculationStrategy = new FlatFeeStrategy();

        tradingService = new TradingService(
                uow,
                stockDao,
                portfolioDao,
                transactionDao,
                ownedStockDao,
                new MockLogger(),
                feeCalculationStrategy
        );
    }

    @Test
    void buyOneStock_WithValidAffordableStock_BeginCalledOnce()
    {
        setupBuyStock_WithValidAffordableStock();

        assertEquals(1, uow.getBeginCalledAmount());
    }

    @Test
    void buyOneStock_WithValidAffordableStock_CommitCalledOnce()
    {
        setupBuyStock_WithValidAffordableStock();

        assertEquals(1, uow.getCommitCalledAmount());
    }

    @Test
    void buyOneStock_WithValidAffordableStock_RollbackNotCalled()
    {
        setupBuyStock_WithValidAffordableStock();

        assertEquals(0, uow.getRollbackCalledAmount());
    }

    @Test
    void buyOneStock_WithValidAffordableStock_TransactionCreated()
    {
        setupBuyStock_WithValidAffordableStock();

        assertEquals(1, transactionDao.getAll().size());
    }

    @Test
    void buyOneStock_WithValidAffordableStock_OwnedStockCreated()
    {
        setupBuyStock_WithValidAffordableStock();

        assertEquals(1, ownedStockDao.getAll().size());
    }

    @Test
    void buyOneStock_WithValidAffordableStock_PortfolioBalanceReducedByStockPriceAndFee()
    {
        setupBuyStock_WithValidAffordableStock();

        BigDecimal basePrice = stock.getCurrentPrice();
        BigDecimal fee = BigDecimal.valueOf(feeCalculationStrategy.calculateFee(basePrice));
        BigDecimal totalAmount = basePrice.add(fee);

        BigDecimal expectedBalance = BigDecimal.valueOf(1000).subtract(totalAmount);

        assertEquals(0, expectedBalance.compareTo(portfolio.getCurrentBalance()));
    }

    @Test
    void buyMultipleStocks_WithValidAffordableStock_TotalCostCalculatedCorrectly()
    {
        setupBuyMultipleStocks_WithValidAffordableStock();

        BigDecimal basePrice = stock.getCurrentPrice().multiply(BigDecimal.valueOf(3));
        BigDecimal fee = BigDecimal.valueOf(feeCalculationStrategy.calculateFee(basePrice));
        BigDecimal totalAmount = basePrice.add(fee);

        BigDecimal expectedBalance = BigDecimal.valueOf(1000).subtract(totalAmount);

        assertEquals(0, expectedBalance.compareTo(portfolio.getCurrentBalance()));
    }

    @Test
    void buyStock_WhenAlreadyOwned_QuantityIncreases()
    {
        setupBuyStock_WithValidAffordableStock();

        BuyStockRequestDTO secondRequest = new BuyStockRequestDTO("AAPL", portfolio.getId(), 1);
        tradingService.buyStock(secondRequest);

        var ownedStock = ownedStockDao.getAll().getFirst();

        assertEquals(2, ownedStock.getNumberOfShares());
    }

    @Test
    void buyStock_WithInsufficientFunds_RollbackCalledOnce()
    {
        setupBuyStock_WithValidUnaffordableStock();

        assertEquals(1, uow.getRollbackCalledAmount());
    }

    @Test
    void buyStock_WithInsufficientFunds_BeginCalledOnce()
    {
        setupBuyStock_WithValidUnaffordableStock();

        assertEquals(1, uow.getBeginCalledAmount());
    }

    @Test
    void buyStock_WithInsufficientFunds_CommitNotCalled()
    {
        setupBuyStock_WithValidUnaffordableStock();

        assertEquals(0, uow.getCommitCalledAmount());
    }

    @Test
    void buyStock_WithInsufficientFunds_NoTransactionCreated()
    {
        setupBuyStock_WithValidUnaffordableStock();

        assertEquals(0, transactionDao.getAll().size());
    }

    @Test
    void buyStock_WithInsufficientFunds_PortfolioBalanceUnchanged()
    {
        setupBuyStock_WithValidUnaffordableStock();

        assertEquals(0, BigDecimal.valueOf(50).compareTo(portfolio.getCurrentBalance()));
    }

    @Test
    void buyStock_WithQuantityZero_RollbackCalled()
    {
        setupBuyStock_WithQuantityZero();

        assertEquals(1, uow.getRollbackCalledAmount());
    }

    @Test
    void buyStock_WithQuantityZero_NoTransactionCreated()
    {
        setupBuyStock_WithQuantityZero();

        assertEquals(0, transactionDao.getAll().size());
    }

    private void setupBuyStock_WithQuantityZero()
    {
        setupFailedBuyStock(1000, 200, 0);
    }

    private void setupBuyStock_WithValidUnaffordableStock()
    {
        setupFailedBuyStock(50, 100, 1);
    }

    private void setupBuyStock_WithValidAffordableStock()
    {
        setupSuccessfulBuyStock(1000, 100, 1);
    }

    private void setupBuyMultipleStocks_WithValidAffordableStock()
    {
        setupSuccessfulBuyStock(1000, 100, 3);
    }

    private void setupSuccessfulBuyStock(int portfolioBalance, int stockPricePerShare, int quantityToBuy)
    {
        setupStockAndPortfolio(portfolioBalance, stockPricePerShare);

        BuyStockRequestDTO request = new BuyStockRequestDTO("AAPL", portfolio.getId(), quantityToBuy);

        tradingService.buyStock(request);
    }

    private void setupFailedBuyStock(int portfolioBalance, int stockPricePerShare, int quantityToBuy)
    {
        setupStockAndPortfolio(portfolioBalance, stockPricePerShare);

        BuyStockRequestDTO request = new BuyStockRequestDTO("AAPL", portfolio.getId(), quantityToBuy);

        try
        {
            tradingService.buyStock(request);
        } catch (Exception ignored)
        {
        }
    }

    private void setupStockAndPortfolio(int portfolioBalance, int stockPricePerShare)
    {
        portfolio = new Portfolio("test-portfolio", BigDecimal.valueOf(portfolioBalance));
        stock = new Stock("AAPL", "Apple", BigDecimal.valueOf(stockPricePerShare));

        stockDao.create(stock);
        portfolioDao.create(portfolio);
    }
}