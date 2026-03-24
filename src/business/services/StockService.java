package business.services;

import business.dto.StockDTO;
import business.dto.StockResponseDTO;
import entities.Stock;
import persistence.interfaces.StockDao;

import java.util.ArrayList;
import java.util.List;

public class StockService
{
    private final StockDao stockDao;


    public StockService(StockDao stockDao)
    {
        this.stockDao = stockDao;
    }

    public List<StockResponseDTO> getAllAvailableStocks() {
        List<Stock> stocks = stockDao.getAll().stream()
                .filter(s -> s.getCurrentState() != Stock.State.BANKRUPT &&
                        s.getCurrentState() != Stock.State.RESET)
                .toList();

        return mapStockListToDTO(stocks);
    }

    public StockResponseDTO getBySymbol (String stockSymbol){
        Stock stock = stockDao.getBySymbol(stockSymbol)
                .orElseThrow(() -> new IllegalArgumentException("No stock wit symbol=" + stockSymbol + " found"));

        return mapStockToDTO(stock);
    }

    public List<StockResponseDTO> mapStockListToDTO(List<Stock> stocks){
        List<StockResponseDTO> response = new ArrayList<>();
        for (Stock stock : stocks) {
            response.add(mapStockToDTO(stock));
        }
        return response;
    }

    public StockResponseDTO mapStockToDTO(Stock stock){
        return new StockResponseDTO(
                stock.getSymbol(),
                stock.getName(),
                stock.getCurrentPrice(),
                stock.getCurrentState()
        );
    }


}
