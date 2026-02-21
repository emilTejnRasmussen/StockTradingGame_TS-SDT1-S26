package persistence.fileImplementation;

import entities.Portfolio;
import persistence.interfaces.PortfolioDao;
import shared.logging.Logger;

import java.util.*;
import java.util.stream.IntStream;

public class FilePortfolioDao implements PortfolioDao {
    private final FileUnitOfWork uow;

    public FilePortfolioDao(FileUnitOfWork uow) {
        this.uow = uow;
    }

    @Override
    public void create(Portfolio portfolio) {
        uow.begin();
        List<Portfolio> portfolios = uow.getPortfolios();
        boolean exists = portfolios.stream()
                .anyMatch(p -> Objects.equals(p.getId(), portfolio.getId()));

        if (exists){
            uow.rollback();
            Logger.getInstance().warning("Portfolio with id '" + portfolio.getId() + "' already exists");
            throw new IllegalArgumentException("Portfolio with id '" + portfolio.getId() + "' already exists");
        }

        portfolios.add(portfolio);
        uow.commit();
    }

    @Override
    public void update(Portfolio portfolio) {
        uow.begin();
        List<Portfolio> portfolios = uow.getPortfolios();

        int index = IntStream.range(0, portfolios.size())
                .filter(i -> Objects.equals(portfolios.get(i).getId(), portfolio.getId()))
                .findFirst()
                .orElse(-1);

        if (index == -1) {
            uow.rollback();
            Logger.getInstance().warning("Portfolio with id '" + portfolio.getId() + "' does not exist - nothing updated");
            throw new IllegalArgumentException("Portfolio with id '" + portfolio.getId() + "' does not exist - nothing updated");
        }

        portfolios.set(index, portfolio);
        uow.commit();
    }

    @Override
    public void delete(UUID id) {
        uow.begin();
        List<Portfolio> portfolios = uow.getPortfolios();

        boolean wasRemoved = portfolios.removeIf(p -> Objects.equals(p.getId(), id));

        if (!wasRemoved) {
            uow.rollback();
            Logger.getInstance().warning("Portfolio with id '" + id + "' does not exist - nothing deleted");
            throw new IllegalArgumentException("Portfolio with id '" + id + "' does not exist - nothing deleted");
        }

        uow.commit();
    }

    @Override
    public List<Portfolio> getAll() {
        return new ArrayList<>(uow.getPortfolios());
    }

    @Override
    public Optional<Portfolio> getById(UUID id) {
        return uow.getPortfolios().stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }
}
