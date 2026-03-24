package _mocks.dao;

import entities.Portfolio;
import persistence.interfaces.PortfolioDao;

import java.util.*;

public class MockPortfolioDao implements PortfolioDao
{
    private List<Portfolio> portfolios = new ArrayList<>();

    @Override
    public void create(Portfolio portfolio)
    {
        portfolios.add(portfolio);
    }

    @Override
    public void update(Portfolio portfolio)
    {
        for (int i = 0; i < portfolios.size(); i++)
        {
            if (portfolio.getId().equals(portfolios.get(i).getId())){
                portfolios.set(i, portfolio);
                return;
            }
        }
        throw new IllegalArgumentException("Portfolio with id '" + portfolio.getId() + "' does not exist - nothing updated");
    }

    @Override
    public void delete(UUID id)
    {
        boolean wasRemoved = portfolios.removeIf(p -> Objects.equals(p.getId(), id));

        if (!wasRemoved)
        {
            throw new IllegalArgumentException("Portfolio with id '" + id + "' does not exist - nothing deleted");
        }
    }

    @Override
    public List<Portfolio> getAll()
    {
        return new ArrayList<>(portfolios);
    }

    @Override
    public Optional<Portfolio> getById(UUID id)
    {
        return portfolios.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }
}
