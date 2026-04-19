package business.services;

import business.dto.StockDTO;
import entities.Stock;
import persistence.interfaces.StockDao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StockService
{
    private final StockDao stockDao;


    public StockService(StockDao stockDao)
    {
        this.stockDao = stockDao;
    }

    public List<StockDTO> getAllAvailableStocks() {
        List<Stock> stocks = stockDao.getAll().stream()
                .filter(s -> s.getCurrentState() != Stock.State.BANKRUPT &&
                        s.getCurrentState() != Stock.State.RESET)
                .toList();

        return mapStockListToDTO(stocks);
    }

    public List<StockDTO> getAll() {
        return mapStockListToDTO(stockDao.getAll());
    }

    public BigDecimal getCurrentPrice(String stockSymbol) {
        Optional<Stock> stockOptional = stockDao.getBySymbol(stockSymbol);

        if (stockOptional.isPresent()) return stockOptional.get().getCurrentPrice();
        return BigDecimal.ZERO;
    }

    public StockDTO getBySymbol (String stockSymbol){
        Stock stock = stockDao.getBySymbol(stockSymbol)
                .orElseThrow(() -> new IllegalArgumentException("No stock wit symbol=" + stockSymbol + " found"));

        return mapStockToDTO(stock);
    }

    public List<StockDTO> mapStockListToDTO(List<Stock> stocks){
        List<StockDTO> response = new ArrayList<>();
        for (Stock stock : stocks) {
            response.add(mapStockToDTO(stock));
        }
        return response;
    }

    public StockDTO mapStockToDTO(Stock stock){
        return new StockDTO(
                stock.getSymbol(),
                stock.getCurrentPrice(),
                stock.getCurrentState()
        );
    }


}
