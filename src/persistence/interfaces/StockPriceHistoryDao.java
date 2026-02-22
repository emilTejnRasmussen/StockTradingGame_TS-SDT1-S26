package persistence.interfaces;

import entities.StockPriceHistory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockPriceHistoryDao
{
    void create(StockPriceHistory stockPriceHistory);
    List<StockPriceHistory> getAll();
    Optional<StockPriceHistory> getById(UUID id);
}
