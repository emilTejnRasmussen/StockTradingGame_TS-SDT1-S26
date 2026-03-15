package business.services;

import entities.Stock;
import persistence.interfaces.StockDao;

import java.util.List;

public class StockService
{
    private final StockDao stockDao;


    public StockService(StockDao stockDao)
    {
        this.stockDao = stockDao;
    }

    public List<Stock> getAllAvailableStocks() {
        return stockDao.getAll().stream()
                .filter(s -> s.getCurrentState() != Stock.State.BANKRUPT &&
                        s.getCurrentState() != Stock.State.RESET)
                .toList();
    }

    public Stock getBySymbol (String stockSymbol){
        return stockDao.getBySymbol(stockSymbol)
                .orElseThrow(() -> new IllegalArgumentException("No stock wit symbol=" + stockSymbol + " found"));
    }


}
