package business.services;

import entities.OwnedStock;
import persistence.interfaces.OwnedStockDao;
import shared.logging.Logger;

import java.util.List;

public class StockBankruptService
{
    private final OwnedStockDao ownedStockDao;
    private final PortfolioService portfolioService;
    private final Logger logger;

    public StockBankruptService(OwnedStockDao ownedStockDao, PortfolioService portfolioService)
    {
        this.ownedStockDao = ownedStockDao;
        this.portfolioService = portfolioService;
        this.logger = Logger.getInstance();
    }


    public void handleBankruptStock(String stockSymbol) {
        List<OwnedStock> ownedStocks = ownedStockDao.getAllByStockSymbol(stockSymbol);
        logger.info("Handling bankruptcy for " + stockSymbol + ", ownedStocks found " + ownedStocks.size());

        if (ownedStocks.isEmpty()) return;

        for (OwnedStock ownedStock : ownedStocks) {
            ownedStockDao.delete(ownedStock.getId());
        }

        portfolioService.updateAllPortfolioCurrentPrice();
    }
}
