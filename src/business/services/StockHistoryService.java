package business.services;

import entities.StockPriceHistory;
import persistence.interfaces.StockPriceHistoryDao;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StockHistoryService
{
    private final StockPriceHistoryDao stockPriceHistoryDao;

    public StockHistoryService(StockPriceHistoryDao stockPriceHistoryDao)
    {
        this.stockPriceHistoryDao = stockPriceHistoryDao;
    }

    public Map<String, List<StockPriceHistory>> getLatestStockUpdatesForAll(int amount)
    {
        return stockPriceHistoryDao.getAll().stream()
                .collect(Collectors.groupingBy(StockPriceHistory::stockSymbol))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .sorted(Comparator.comparing(StockPriceHistory::timeStamp).reversed())
                                .limit(amount)
                                .sorted(Comparator.comparing(StockPriceHistory::timeStamp))
                                .toList()
                ));
    }

    public List<StockPriceHistory> getLatestStockUpdatesForStock(String stockSymbol, int amount)
    {
        return stockPriceHistoryDao.getAll().stream()
                .filter(h -> h.stockSymbol().equals(stockSymbol))
                .sorted(Comparator.comparing(StockPriceHistory::timeStamp).reversed())
                .limit(amount)
                .sorted(Comparator.comparing(StockPriceHistory::timeStamp))
                .toList();
    }
}
