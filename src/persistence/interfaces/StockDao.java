package persistence.interfaces;

import entities.Stock;

import java.util.List;
import java.util.Optional;

public interface StockDao {
    void create(Stock stock);
    void update(Stock stock);
    void delete(String symbol);
    List<Stock> getAll();
    Optional<Stock> getBySymbol(String symbol);
    List<Stock> getByState(Stock.State state);

}
