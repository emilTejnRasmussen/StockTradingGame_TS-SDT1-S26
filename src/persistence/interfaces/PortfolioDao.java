package persistence.interfaces;

import entities.Portfolio;
import entities.Stock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PortfolioDao {
    void create(Portfolio portfolio);
    void update(Portfolio portfolio);
    void delete(UUID id);
    List<Portfolio> getAll();
    Optional<Portfolio> getById(UUID id);
}
