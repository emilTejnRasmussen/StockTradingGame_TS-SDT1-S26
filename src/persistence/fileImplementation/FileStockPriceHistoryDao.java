package persistence.fileImplementation;

import entities.StockPriceHistory;
import persistence.interfaces.StockPriceHistoryDao;

import java.util.*;

public class FileStockPriceHistoryDao implements StockPriceHistoryDao
{
    private final FileUnitOfWork uow;

    public FileStockPriceHistoryDao(FileUnitOfWork uow)
    {
        this.uow = uow;
    }

    @Override
    public void create(StockPriceHistory stockPriceHistory)
    {
        uow.appendStockPriceHistory(stockPriceHistory);
    }

    @Override
    public List<StockPriceHistory> getAll()
    {
        return new ArrayList<>(uow.getStockPriceHistories());
    }

    @Override
    public Optional<StockPriceHistory> getById(UUID id)
    {
        return uow.getStockPriceHistories().stream()
                .filter(s -> Objects.equals(s.id(), id))
                .findFirst();
    }
}
