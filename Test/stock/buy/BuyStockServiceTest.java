package stock.buy;

import _mocks.*;
import _mocks.dao.MockOwnedStockDao;
import _mocks.dao.MockPortfolioDao;
import _mocks.dao.MockStockDao;
import _mocks.dao.MockTransactionDao;
import business.dto.transaction.BuyStockRequestDTO;
import business.services.TradingService;
import entities.Portfolio;
import entities.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.interfaces.*;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuyStockServiceTest {
    private MockUnitOfWork uow;
    private StockDao stockDao;
    private PortfolioDao portfolioDao;
    private TransactionDao transactionDao;
    private OwnedStockDao ownedStockDao;

    private TradingService tradingService;

    @BeforeEach
    void setup() {
        uow = new MockUnitOfWork();
        stockDao = new MockStockDao();
        portfolioDao = new MockPortfolioDao();
        transactionDao = new MockTransactionDao();
        ownedStockDao = new MockOwnedStockDao();


        tradingService = new TradingService(uow, stockDao, portfolioDao,transactionDao, ownedStockDao, new MockLogger());
    }

    @Test
    void buyOneStock_WithValidAffordableStock_TransactionCreated() {
        setupBuyStock_WithValidAffordableStock();
        // Assert
        assertEquals(1, transactionDao.getAll().size());
    }

    @Test
    void buyOneStock_WithValidAffordableStock_BeginCalledOnce() {
        setupBuyStock_WithValidAffordableStock();
        // Assert
        assertEquals(1, uow.getBeginCalledAmount());
    }

    @Test
    void buyOneStock_WithValidAffordableStock_CommitCalledOnce() {
        setupBuyStock_WithValidAffordableStock();
        // Assert
        assertEquals(1, uow.getCommitCalledAmount());
    }

    @Test
    void buyOneStock_WithValidAffordableStock_RollbackNotCalled() {
        setupBuyStock_WithValidAffordableStock();
        // Assert
        assertEquals(0, uow.getRollbackCalledAmount());
    }

    private void setupBuyStock_WithValidAffordableStock()
    {
        // Arrange
        UUID portfolioId = UUID.randomUUID();
        Stock stock = new Stock("AAPL", "Apple", BigDecimal.valueOf(100.0));
        Portfolio portfolio = new Portfolio(portfolioId, BigDecimal.valueOf(1000.0));

        stockDao.create(stock);
        portfolioDao.create(portfolio);

        BuyStockRequestDTO request = new BuyStockRequestDTO("AAPL", portfolioId, 1);

        // Act
        tradingService.buyStock(request);
    }
}