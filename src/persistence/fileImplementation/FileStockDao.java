package persistence.fileImplementation;

import entities.Stock;
import persistence.interfaces.StockDao;
import shared.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

public class FileStockDao implements StockDao
{

    private final FileUnitOfWork uow;

    public FileStockDao(FileUnitOfWork uow)
    {
        this.uow = uow;
    }

    @Override
    public void create(Stock stock)
    {
        List<Stock> stocks = uow.getStocks();
        Optional<Stock> optional = getBySymbol(stock.getSymbol());

        if (optional.isPresent())
        {
            Logger.getInstance().warning("Stock with symbol '" + stock.getSymbol() + "' already exist - no new instance created");
            throw new IllegalArgumentException("Stock with symbol '" + stock.getSymbol() + "' already exists");
        }

        stocks.add(stock);
    }

    @Override
    public void update(Stock stock)
    {
        List<Stock> stocks = uow.getStocks();

        int index = indexOfStock(stock, stocks);

        if (index == -1)
        {
            Logger.getInstance().warning("Stock with symbol '" + stock.getSymbol() + "' does not exist - no stock updated");
            throw new IllegalArgumentException("Stock with symbol '" + stock.getSymbol() + "' does not exist");
        }

        stocks.set(index, stock);
    }

    @Override
    public void delete(String symbol)
    {
        List<Stock> stocks = uow.getStocks();

        boolean wasRemoved = stocks.removeIf(s -> s.getSymbol().equals(symbol));

        if (!wasRemoved)
        {
            Logger.getInstance().warning("Stock with symbol '" + symbol + "' does not exist - no stock removed");
            throw new IllegalArgumentException("Stock with symbol '" + symbol + "' does not exist");
        }
    }

    @Override
    public List<Stock> getAll()
    {
        return new ArrayList<>(uow.getStocks());
    }

    @Override
    public Optional<Stock> getBySymbol(String symbol)
    {
        return uow.getStocks().stream()
                .filter(s -> s.getSymbol().equals(symbol))
                .findFirst();
    }

    private static int indexOfStock(Stock stock, List<Stock> stocks)
    {
        for (int i = 0; i < stocks.size(); i++)
        {
            if (Objects.equals(stocks.get(i).getSymbol(), stock.getSymbol()))
            {
                return i;
            }
        }
        return -1;
    }
}
