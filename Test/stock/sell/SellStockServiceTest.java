package stock.sell;

import _mocks.*;
import _mocks.dao.MockOwnedStockDao;
import _mocks.dao.MockPortfolioDao;
import _mocks.dao.MockStockDao;
import _mocks.dao.MockTransactionDao;
import business.dto.transaction.SellStockRequestDTO;
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

public class SellStockServiceTest
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
    void sellStock_validOwnedStock_beginCalledOnce()
    {
        setupSellStock_WithValidOwnedStock();
        assertEquals(1, uow.getBeginCalledAmount());
    }

    @Test
    void sellStock_validOwnedStock_commitCalledOnce()
    {
        setupSellStock_WithValidOwnedStock();
        assertEquals(1, uow.getCommitCalledAmount());
    }

    @Test
    void sellStock_validOwnedStock_rollbackNotCalled()
    {
        setupSellStock_WithValidOwnedStock();
        assertEquals(0, uow.getRollbackCalledAmount());
    }

    @Test
    void sellStock_validOwnedStock_transactionCreated()
    {
        setupSellStock_WithValidOwnedStock();
        assertEquals(1, transactionDao.getAll().size());
    }

    @Test
    void sellStock_validOwnedStock_portfolioBalanceIncreasedByProceeds()
    {
        setupSellStock_WithValidOwnedStock();

        BigDecimal fee = BigDecimal.valueOf(AppConfig.getInstance().getTransactionFee());
        BigDecimal proceeds = stock.getCurrentPrice().subtract(fee);
        BigDecimal result = BigDecimal.valueOf(1000).add(proceeds);

        assertEquals(result, portfolio.getCurrentBalance());
    }

    @Test
    void sellStock_validOwnedStock_quantityDecreased()
    {
        setupSellStock_WithValidOwnedStock();

        OwnedStock ownedStock = ownedStockDao.getAll().getFirst();

        assertEquals(2, ownedStock.getNumberOfShares());
    }

    @Test
    void sellStock_allOwnedShares_ownedStockDeleted()
    {
        setupSellStock_WithAllSharesSold();
        assertEquals(0, ownedStockDao.getAll().size());
    }

    @Test
    void sellStock_partialOwnedShares_ownedStockStillExists()
    {
        setupSellStock_WithValidOwnedStock();
        assertEquals(1, ownedStockDao.getAll().size());
    }

    @Test
    void sellStock_multipleOwnedShares_proceedsCalculatedCorrectly()
    {
        setupSellStock_WithMultipleShares();

        BigDecimal fee = BigDecimal.valueOf(AppConfig.getInstance().getTransactionFee());
        BigDecimal proceeds = stock.getCurrentPrice().multiply(BigDecimal.valueOf(2)).subtract(fee);
        BigDecimal result = BigDecimal.valueOf(1000).add(proceeds);

        assertEquals(result, portfolio.getCurrentBalance());
    }

    @Test
    void sellStock_zeroQuantity_rollbackCalled()
    {
        setupSellStock_WithZeroQuantity();
        assertEquals(1, uow.getRollbackCalledAmount());
    }

    @Test
    void sellStock_zeroQuantity_noTransactionCreated()
    {
        setupSellStock_WithZeroQuantity();
        assertEquals(0, transactionDao.getAll().size());
    }

    @Test
    void sellStock_negativeQuantity_rollbackCalled()
    {
        setupSellStock_WithNegativeQuantity();
        assertEquals(1, uow.getRollbackCalledAmount());
    }

    @Test
    void sellStock_negativeQuantity_noTransactionCreated()
    {
        setupSellStock_WithNegativeQuantity();
        assertEquals(0, transactionDao.getAll().size());
    }

    @Test
    void sellStock_unknownStock_rollbackCalled()
    {
        setupSellStock_WithUnknownStock();
        assertEquals(1, uow.getRollbackCalledAmount());
    }

    @Test
    void sellStock_unknownStock_noTransactionCreated()
    {
        setupSellStock_WithUnknownStock();
        assertEquals(0, transactionDao.getAll().size());
    }

    @Test
    void sellStock_invalidPortfolio_rollbackCalled()
    {
        setupSellStock_WithInvalidPortfolio();
        assertEquals(1, uow.getRollbackCalledAmount());
    }

    @Test
    void sellStock_invalidPortfolio_noTransactionCreated()
    {
        setupSellStock_WithInvalidPortfolio();
        assertEquals(0, transactionDao.getAll().size());
    }

    @Test
    void sellStock_noOwnedShares_rollbackCalled()
    {
        setupSellStock_WithNoOwnedShares();
        assertEquals(1, uow.getRollbackCalledAmount());
    }

    @Test
    void sellStock_noOwnedShares_noTransactionCreated()
    {
        setupSellStock_WithNoOwnedShares();
        assertEquals(0, transactionDao.getAll().size());
    }

    @Test
    void sellStock_insufficientOwnedShares_rollbackCalled()
    {
        setupSellStock_WithInsufficientOwnedShares();
        assertEquals(1, uow.getRollbackCalledAmount());
    }

    @Test
    void sellStock_insufficientOwnedShares_noTransactionCreated()
    {
        setupSellStock_WithInsufficientOwnedShares();
        assertEquals(0, transactionDao.getAll().size());
    }

    @Test
    void sellStock_bankruptStock_rollbackCalled()
    {
        setupSellStock_WithBankruptStock();
        assertEquals(1, uow.getRollbackCalledAmount());
    }

    @Test
    void sellStock_bankruptStock_noTransactionCreated()
    {
        setupSellStock_WithBankruptStock();
        assertEquals(0, transactionDao.getAll().size());
    }

    @Test
    void sellStock_resetStock_rollbackCalled()
    {
        setupSellStock_WithResetStock();
        assertEquals(1, uow.getRollbackCalledAmount());
    }

    @Test
    void sellStock_resetStock_noTransactionCreated()
    {
        setupSellStock_WithResetStock();
        assertEquals(0, transactionDao.getAll().size());
    }

    @Test
    void sellStock_negativeProceeds_rollbackCalled()
    {
        setupSellStock_WithNegativeProceeds();
        assertEquals(1, uow.getRollbackCalledAmount());
    }

    @Test
    void sellStock_negativeProceeds_noTransactionCreated()
    {
        setupSellStock_WithNegativeProceeds();
        assertEquals(0, transactionDao.getAll().size());
    }

    private void setupSellStock_WithValidOwnedStock()
    {
        setupSellStock(1000, 100, 3, 1);
    }

    private void setupSellStock_WithAllSharesSold()
    {
        setupSellStock(1000, 100, 1, 1);
    }

    private void setupSellStock_WithMultipleShares()
    {
        setupSellStock(1000, 100, 5, 2);
    }

    private void setupSellStock_WithZeroQuantity()
    {
        setupSellStock(1000, 100, 3, 0);
    }

    private void setupSellStock_WithNegativeQuantity()
    {
        setupSellStock(1000, 100, 3, -1);
    }

    private void setupSellStock_WithNoOwnedShares()
    {
        UUID portfolioId = UUID.randomUUID();

        portfolio = new Portfolio(portfolioId, BigDecimal.valueOf(1000));
        stock = new Stock("AAPL", "Apple", BigDecimal.valueOf(100));

        stockDao.create(stock);
        portfolioDao.create(portfolio);

        SellStockRequestDTO request = new SellStockRequestDTO("AAPL", portfolioId, 1);

        try
        {
            tradingService.sellStock(request);
        } catch (Exception ignored)
        {
        }
    }

    private void setupSellStock_WithInsufficientOwnedShares()
    {
        setupSellStock(1000, 100, 1, 2);
    }

    private void setupSellStock_WithUnknownStock()
    {
        UUID portfolioId = UUID.randomUUID();

        portfolio = new Portfolio(portfolioId, BigDecimal.valueOf(1000));
        portfolioDao.create(portfolio);

        SellStockRequestDTO request = new SellStockRequestDTO("AAPL", portfolioId, 1);

        try
        {
            tradingService.sellStock(request);
        } catch (Exception ignored)
        {
        }
    }

    private void setupSellStock_WithInvalidPortfolio()
    {
        UUID portfolioId = UUID.randomUUID();

        stock = new Stock("AAPL", "Apple", BigDecimal.valueOf(100));
        stockDao.create(stock);

        SellStockRequestDTO request = new SellStockRequestDTO("AAPL", portfolioId, 1);

        try
        {
            tradingService.sellStock(request);
        } catch (Exception ignored)
        {
        }
    }

    private void setupSellStock_WithBankruptStock()
    {
        UUID portfolioId = UUID.randomUUID();

        portfolio = new Portfolio(portfolioId, BigDecimal.valueOf(1000));
        stock = new Stock("AAPL", "Apple", BigDecimal.valueOf(100));
        stock.setCurrentState(Stock.State.BANKRUPT);

        stockDao.create(stock);
        portfolioDao.create(portfolio);
        ownedStockDao.create(new OwnedStock(portfolioId, "AAPL", 3));

        SellStockRequestDTO request = new SellStockRequestDTO("AAPL", portfolioId, 1);

        try
        {
            tradingService.sellStock(request);
        } catch (Exception ignored)
        {
        }
    }

    private void setupSellStock_WithResetStock()
    {
        UUID portfolioId = UUID.randomUUID();

        portfolio = new Portfolio(portfolioId, BigDecimal.valueOf(1000));
        stock = new Stock("AAPL", "Apple", BigDecimal.valueOf(100));
        stock.setCurrentState(Stock.State.RESET);

        stockDao.create(stock);
        portfolioDao.create(portfolio);
        ownedStockDao.create(new OwnedStock(portfolioId, "AAPL", 3));

        SellStockRequestDTO request = new SellStockRequestDTO("AAPL", portfolioId, 1);

        try
        {
            tradingService.sellStock(request);
        } catch (Exception ignored)
        {
        }
    }

    private void setupSellStock_WithNegativeProceeds()
    {
        UUID portfolioId = UUID.randomUUID();

        portfolio = new Portfolio(portfolioId, BigDecimal.valueOf(1000));
        stock = new Stock("AAPL", "Apple", BigDecimal.ZERO);

        stockDao.create(stock);
        portfolioDao.create(portfolio);
        ownedStockDao.create(new OwnedStock(portfolioId, "AAPL", 3));

        SellStockRequestDTO request = new SellStockRequestDTO("AAPL", portfolioId, 1);

        try
        {
            tradingService.sellStock(request);
        } catch (Exception ignored)
        {
        }
    }

    private void setupSellStock(int portfolioBalance, int stockPricePerShare, int ownedShares, int quantityToSell)
    {
        UUID portfolioId = UUID.randomUUID();

        portfolio = new Portfolio(portfolioId, BigDecimal.valueOf(portfolioBalance));
        stock = new Stock("AAPL", "Apple", BigDecimal.valueOf(stockPricePerShare));

        stockDao.create(stock);
        portfolioDao.create(portfolio);
        ownedStockDao.create(new OwnedStock(portfolioId, "AAPL", ownedShares));

        SellStockRequestDTO request = new SellStockRequestDTO("AAPL", portfolioId, quantityToSell);

        try
        {
            tradingService.sellStock(request);
        } catch (Exception ignored)
        {
        }
    }
}