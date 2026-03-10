package persistence.fileImplementation;

import entities.OwnedStock;
import persistence.interfaces.OwnedStockDao;
import shared.logging.Logger;

import java.util.*;
import java.util.stream.IntStream;

public class FileOwnedStockDao implements OwnedStockDao
{
    private final FileUnitOfWork uow;

    public FileOwnedStockDao(FileUnitOfWork uow)
    {
        this.uow = uow;
    }

    @Override
    public void create(OwnedStock ownedStock)
    {
        List<OwnedStock> ownedStocks = uow.getOwnedStocks();
        Optional<OwnedStock> optional = getById(ownedStock.getId());

        if (optional.isPresent())
        {
            Logger.getInstance().warning("Owned stock with id '" + ownedStock.getId() + "' already exists");
            throw new IllegalArgumentException("Owned stock with id '" + ownedStock.getId() + "' already exists");
        }

        ownedStocks.add(ownedStock);
    }

    @Override
    public void update(OwnedStock ownedStock)
    {
        List<OwnedStock> ownedStocks = uow.getOwnedStocks();

        int index = indexOfOwnedStock(ownedStock, ownedStocks);

        if (index == -1)
        {
            Logger.getInstance().warning("Owned stock with id '" + ownedStock.getId() + "' does not exist - nothing updated");
            throw new IllegalArgumentException("Owned stock with id '" + ownedStock.getId() + "' does not exist - nothing updated");
        }

        ownedStocks.set(index, ownedStock);
    }

    @Override
    public void delete(UUID id)
    {
        List<OwnedStock> ownedStocks = uow.getOwnedStocks();

        boolean wasRemoved = ownedStocks.removeIf(o -> Objects.equals(o.getId(), id));

        if (!wasRemoved)
        {
            Logger.getInstance().warning("Owned stock with id '" + id + "' does not exist - nothing deleted");
            throw new IllegalArgumentException("Owned stock with id '" + id + "' does not exist - nothing deleted");
        }
    }

    @Override
    public List<OwnedStock> getAll()
    {
        return new ArrayList<>(uow.getOwnedStocks());
    }

    @Override
    public List<OwnedStock> getAllByStockSymbol(String stockSymbol)
    {

        return uow.getOwnedStocks().stream()
                .filter(os -> os.getStockSymbol().equals(stockSymbol))
                .toList();
    }

    @Override
    public Optional<OwnedStock> getById(UUID id)
    {
        return uow.getOwnedStocks().stream()
                .filter(o -> Objects.equals(o.getId(), id))
                .findFirst();
    }

    private int indexOfOwnedStock(OwnedStock ownedStock, List<OwnedStock> ownedStocks)
    {
        for (int i = 0; i < ownedStocks.size(); i++)
        {
            if (Objects.equals(ownedStocks.get(i).getId(), ownedStock.getId()))
            {
                return i;
            }
        }
        return -1;
    }
}
