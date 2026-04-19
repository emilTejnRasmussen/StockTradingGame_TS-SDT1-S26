package _mocks.dao;

import entities.Stock;
import persistence.interfaces.StockDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockStockDao implements StockDao
{
    private final List<Stock> stocks = new ArrayList<>();

    @Override
    public void create(Stock stock)
    {
        stocks.add(stock);
    }

    @Override
    public void update(Stock stock)
    {
        for (int i = 0; i < stocks.size(); i++)
        {
            if (stock.getSymbol().equals(stocks.get(i).getSymbol())){
                stocks.set(i, stock);
                return;
            }
        }
        throw new IllegalArgumentException("Stock with symbol '" + stock.getSymbol() + "' does not exist");
    }

    @Override
    public void delete(String symbol)
    {
        boolean wasRemoved = stocks.removeIf(s -> s.getSymbol().equals(symbol));

        if (!wasRemoved)
        {
            throw new IllegalArgumentException("Stock with symbol '" + symbol + "' does not exist");
        }
    }

    @Override
    public List<Stock> getAll()
    {
        return new ArrayList<>(stocks);
    }

    @Override
    public Optional<Stock> getBySymbol(String symbol)
    {
        return stocks.stream()
                .filter(s -> s.getSymbol().equals(symbol))
                .findFirst();
    }
}
