package stock.buy;

import _mocks.*;
import _mocks.dao.MockOwnedStockDao;
import _mocks.dao.MockPortfolioDao;
import _mocks.dao.MockStockDao;
import _mocks.dao.MockTransactionDao;
import business.dto.transaction.BuyStockRequestDTO;
import business.services.TradingService;
import entities.OwnedStock;
import entities.Portfolio;
import entities.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.interfaces.*;
import shared.configuration.AppConfig;

import java.math.BigDecimal;
import java.util.UUID;

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

    @BeforeEach
    void setup()
    {
        uow = new MockUnitOfWork();
        stockDao = new MockStockDao();
        portfolioDao = new MockPortfolioDao();
        transactionDao = new MockTransactionDao();
        ownedStockDao = new MockOwnedStockDao();


        tradingService = new TradingService(uow, stockDao, portfolioDao, transactionDao, ownedStockDao, new MockLogger());
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
    void buyOneStock_WithValidAffordableStock_PortfolioBalanceReducedByStockPrice()
    {
        setupBuyStock_WithValidAffordableStock();

        BigDecimal fee = BigDecimal.valueOf(AppConfig.getInstance().getTransactionFee());
        BigDecimal totalAmount = stock.getCurrentPrice().add(fee);

        BigDecimal result = BigDecimal.valueOf(1000.0).subtract(totalAmount);

        assertEquals(result, portfolio.getCurrentBalance());
    }

    @Test
    void buyMultipleStocks_WithValidAffordableStock_TotalCostCalculatedCorrectly()
    {
        setupBuyMultipleStocks_WithValidAffordableStock();
        BigDecimal fee = BigDecimal.valueOf(AppConfig.getInstance().getTransactionFee());
        BigDecimal totalAmount = stock.getCurrentPrice().multiply(BigDecimal.valueOf(3)).add(fee);

        BigDecimal result = BigDecimal.valueOf(1000.0).subtract(totalAmount);

        assertEquals(result, portfolio.getCurrentBalance());
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
        assertEquals(BigDecimal.valueOf(50), portfolio.getCurrentBalance());
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


    private void setupBuyStock_WithQuantityZero() {
        setupBuyStock(1000, 200, 0);
    }

    private void setupBuyStock_WithValidUnaffordableStock()
    {
        setupBuyStock(50, 100, 1);
    }

    private void setupBuyStock_WithValidAffordableStock()
    {
        setupBuyStock(1000, 100, 1);
    }

    private void setupBuyMultipleStocks_WithValidAffordableStock()
    {
        setupBuyStock(1000, 100, 3);
    }

    private void setupBuyStock(int portfolioBalance, int stockPricePerShare, int quantityToBuy)
    {
        UUID portfolioId = UUID.randomUUID();

        portfolio = new Portfolio(portfolioId, BigDecimal.valueOf(portfolioBalance));
        stock = new Stock("AAPL", "Apple", BigDecimal.valueOf(stockPricePerShare));

        stockDao.create(stock);
        portfolioDao.create(portfolio);

        BuyStockRequestDTO request = new BuyStockRequestDTO("AAPL", portfolioId, quantityToBuy);

        try
        {
            tradingService.buyStock(request);
        } catch (Exception ignored)
        {
        }
    }
}