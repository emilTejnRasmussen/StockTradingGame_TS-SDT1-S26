package business.services;

import entities.OwnedStock;
import entities.Portfolio;
import entities.Stock;
import persistence.interfaces.OwnedStockDao;
import persistence.interfaces.PortfolioDao;
import persistence.interfaces.StockDao;
import persistence.interfaces.UnitOfWork;
import shared.logging.Logger;

import java.math.BigDecimal;
import java.util.UUID;

public class PortfolioService
{
    private final StockDao stockDao;
    private final OwnedStockDao ownedStockDao;
    private final PortfolioDao portfolioDao;

    public PortfolioService(StockDao stockDao, OwnedStockDao ownedStockDao, PortfolioDao portfolioDao)
    {
        this.stockDao = stockDao;
        this.ownedStockDao = ownedStockDao;
        this.portfolioDao = portfolioDao;
    }

    public void updateAllPortfolioCurrentPrice() {
        for (Portfolio portfolio : portfolioDao.getAll()){
            updateCurrentBalance(portfolio);
        }
    }

    public void updateCurrentBalance(Portfolio portfolio)
    {
        BigDecimal newCurrentPrice = getNewCurrentPrice(portfolio.getId());
        portfolio.setCurrentBalance(newCurrentPrice);

        portfolioDao.update(portfolio);
    }

    private BigDecimal getNewCurrentPrice(UUID portfolioId)
    {
        BigDecimal currentPrice = BigDecimal.ZERO;

        for (OwnedStock ownedStock : ownedStockDao.getAll())
        {
            if (!ownedStock.getPortfolioId().equals(portfolioId)) continue;

            BigDecimal stockPrice = getStockPrice(ownedStock);
            int numOfShareOwned = ownedStock.getNumberOfShares();

            BigDecimal price = stockPrice.multiply(BigDecimal.valueOf(numOfShareOwned));
            currentPrice = currentPrice.add(price);
        }
        return currentPrice;
    }

    private BigDecimal getStockPrice(OwnedStock ownedStock)
    {
        String stockSymbol = ownedStock.getStockSymbol();
        Stock stock = stockDao.getBySymbol(stockSymbol)
                .orElseThrow(() -> new IllegalArgumentException("No stock with symbol=" + stockSymbol));
        return stock.getCurrentPrice();
    }
}
