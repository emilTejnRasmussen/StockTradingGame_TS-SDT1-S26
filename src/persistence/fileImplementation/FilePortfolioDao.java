package persistence.fileImplementation;

import entities.Portfolio;
import persistence.interfaces.PortfolioDao;
import shared.logging.Logger;

import java.util.*;

public class FilePortfolioDao implements PortfolioDao
{
    private final FileUnitOfWork uow;

    public FilePortfolioDao(FileUnitOfWork uow)
    {
        this.uow = uow;
    }

    @Override
    public void create(Portfolio portfolio)
    {
        List<Portfolio> portfolios = uow.getPortfolios();
        Optional<Portfolio> optional = getById(portfolio.getId());

        if (optional.isPresent())
        {
            Logger.getInstance().warning("Portfolio with id '" + portfolio.getId() + "' already exists");
            throw new IllegalArgumentException("Portfolio with id '" + portfolio.getId() + "' already exists");
        }

        portfolios.add(portfolio);
    }

    @Override
    public void update(Portfolio portfolio)
    {
        List<Portfolio> portfolios = uow.getPortfolios();

        int index = indexOfPortfolio(portfolio, portfolios);

        if (index == -1)
        {
            Logger.getInstance().warning("Portfolio with id '" + portfolio.getId() + "' does not exist - nothing updated");
            throw new IllegalArgumentException("Portfolio with id '" + portfolio.getId() + "' does not exist - nothing updated");
        }

        portfolios.set(index, portfolio);
    }

    @Override
    public void delete(UUID id)
    {
        List<Portfolio> portfolios = uow.getPortfolios();

        boolean wasRemoved = portfolios.removeIf(p -> Objects.equals(p.getId(), id));

        if (!wasRemoved)
        {
            Logger.getInstance().warning("Portfolio with id '" + id + "' does not exist - nothing deleted");
            throw new IllegalArgumentException("Portfolio with id '" + id + "' does not exist - nothing deleted");
        }
    }

    @Override
    public List<Portfolio> getAll()
    {
        return new ArrayList<>(uow.getPortfolios());
    }

    @Override
    public Optional<Portfolio> getById(UUID id)
    {
        return uow.getPortfolios().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    private int indexOfPortfolio(Portfolio portfolio, List<Portfolio> portfolios)
    {
        for (int i = 0; i < portfolios.size(); i++)
        {
            if (Objects.equals(portfolios.get(i).getId(), portfolio.getId()))
            {
                return i;
            }
        }
        return -1;
    }
}
