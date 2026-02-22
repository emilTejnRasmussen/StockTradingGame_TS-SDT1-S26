package persistence.fileImplementation;

import entities.Stock;
import persistence.interfaces.StockDao;
import shared.logging.Logger;

import java.util.ArrayList;
import java.util.List;
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
        uow.begin();
        List<Stock> stocks = uow.getStocks();
        boolean exists = stocks.stream()
                .anyMatch(s -> s.getSymbol().equals(stock.getSymbol()));

        if (exists)
        {
            uow.rollback();
            Logger.getInstance().warning("Stock with symbol '" + stock.getSymbol() + "' already exist - no new instance created");
            throw new IllegalArgumentException("Stock with symbol '" + stock.getSymbol() + "' already exists");
        }

        stocks.add(stock);
        uow.commit();
    }

    @Override
    public void update(Stock stock)
    {
        uow.begin();
        List<Stock> stocks = uow.getStocks();

        int index = IntStream.range(0, stocks.size())
                .filter(i -> stock.getSymbol().equals(stocks.get(i).getSymbol()))
                .findFirst()
                .orElse(-1);

        if (index == -1)
        {
            uow.rollback();
            Logger.getInstance().warning("Stock with symbol '" + stock.getSymbol() + "' does not exist - no stock updated");
            throw new IllegalArgumentException("Stock with symbol '" + stock.getSymbol() + "' does not exist");
        }

        stocks.set(index, stock);
        uow.commit();
    }

    @Override
    public void delete(String symbol)
    {
        uow.begin();
        List<Stock> stocks = uow.getStocks();

        boolean wasRemoved = stocks.removeIf(s -> s.getSymbol().equals(symbol));

        if (!wasRemoved)
        {
            uow.rollback();
            Logger.getInstance().warning("Stock with symbol '" + symbol + "' does not exist - no stock removed");
            throw new IllegalArgumentException("Stock with symbol '" + symbol + "' does not exist");
        }

        uow.commit();
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

    @Override
    public List<Stock> getByState(Stock.State state)
    {
        return uow.getStocks().stream()
                .filter(s -> s.getCurrentState() == state)
                .toList();
    }
}
