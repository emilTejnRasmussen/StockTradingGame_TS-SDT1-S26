package _mocks.dao;

import entities.OwnedStock;
import persistence.interfaces.OwnedStockDao;

import java.util.*;

public class MockOwnedStockDao implements OwnedStockDao
{
    private List<OwnedStock> ownedStocks = new ArrayList<>();

    @Override
    public void create(OwnedStock ownedStock)
    {
        ownedStocks.add(ownedStock);
    }

    @Override
    public void update(OwnedStock ownedStock)
    {
        for (int i = 0; i < ownedStocks.size(); i++)
        {
            if (ownedStock.getId() == ownedStocks.get(i).getId())
            {
                ownedStocks.set(i, ownedStock);
            }
        }

        throw new IllegalArgumentException("Owned stock with id '" + ownedStock.getId() + "' does not exist - nothing updated");
    }

    @Override
    public void delete(UUID id)
    {
        boolean wasRemoved = ownedStocks.removeIf(o -> Objects.equals(o.getId(), id));

        if (!wasRemoved)
        {
            throw new IllegalArgumentException("Owned stock with id '" + id + "' does not exist - nothing deleted");
        }
    }

    @Override
    public List<OwnedStock> getAll()
    {
        return new ArrayList<>(ownedStocks);
    }

    @Override
    public List<OwnedStock> getAllByStockSymbol(String stockSymbol)
    {
        return ownedStocks.stream()
                .filter(os -> os.getStockSymbol().equals(stockSymbol))
                .toList();
    }

    @Override
    public List<OwnedStock> getAllByPortfolioId(UUID portfolioId)
    {
        return ownedStocks.stream()
                .filter(os -> os.getPortfolioId().equals(portfolioId))
                .toList();
    }

    @Override
    public Optional<OwnedStock> getById(UUID id)
    {
        return ownedStocks.stream()
                .filter(o -> Objects.equals(o.getId(), id))
                .findFirst();
    }

    @Override
    public Optional<OwnedStock> getByPortfolioIdAndStockSymbol(UUID portfolioId, String stockSymbol)
    {
        return getAllByStockSymbol(stockSymbol).stream()
                .filter(os -> Objects.equals(os.getPortfolioId(), portfolioId))
                .findFirst();
    }
}
