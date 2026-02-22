package persistence.fileImplementation;

import entities.OwnedStock;
import entities.Portfolio;
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
        uow.begin();
        List<OwnedStock> ownedStocks = uow.getOwnedStocks();
        boolean exists = ownedStocks.stream()
                .anyMatch(p -> Objects.equals(p.getId(), ownedStock.getId()));

        if (exists)
        {
            uow.rollback();
            Logger.getInstance().warning("Owned stock with id '" + ownedStock.getId() + "' already exists");
            throw new IllegalArgumentException("Owned stock with id '" + ownedStock.getId() + "' already exists");
        }

        ownedStocks.add(ownedStock);
        uow.commit();
    }

    @Override
    public void update(OwnedStock ownedStock)
    {
        uow.begin();
        List<OwnedStock> ownedStocks = uow.getOwnedStocks();

        int index = IntStream.range(0, ownedStocks.size())
                .filter(i -> Objects.equals(ownedStocks.get(i).getId(), ownedStock.getId()))
                .findFirst()
                .orElse(-1);

        if (index == -1)
        {
            uow.rollback();
            Logger.getInstance().warning("Owned stock with id '" + ownedStock.getId() + "' does not exist - nothing updated");
            throw new IllegalArgumentException("Owned stock with id '" + ownedStock.getId() + "' does not exist - nothing updated");
        }

        ownedStocks.set(index, ownedStock);
        uow.commit();
    }

    @Override
    public void delete(UUID id)
    {
        uow.begin();
        List<OwnedStock> ownedStocks = uow.getOwnedStocks();

        boolean wasRemoved = ownedStocks.removeIf(o -> Objects.equals(o.getId(), id));

        if (!wasRemoved)
        {
            uow.rollback();
            Logger.getInstance().warning("Owned stock with id '" + id + "' does not exist - nothing deleted");
            throw new IllegalArgumentException("Owned stock with id '" + id + "' does not exist - nothing deleted");
        }

        uow.commit();
    }

    @Override
    public List<OwnedStock> getAll()
    {
        return new ArrayList<>(uow.getOwnedStocks());
    }

    @Override
    public Optional<OwnedStock> getById(UUID id)
    {
        return uow.getOwnedStocks().stream()
                .filter(o -> Objects.equals(o.getId(), id))
                .findFirst();
    }
}
