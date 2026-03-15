package persistence.interfaces;

import entities.OwnedStock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OwnedStockDao
{
    void create(OwnedStock ownedStock);

    void update(OwnedStock ownedStock);

    void delete(UUID id);

    List<OwnedStock> getAll();

    List<OwnedStock> getAllByStockSymbol(String stockSymbol);

    List<OwnedStock> getAllByPortfolioId(UUID portfolioId);

    Optional<OwnedStock> getById(UUID id);

    Optional<OwnedStock> getByPortfolioIdAndStockSymbol(UUID portfolioId, String stockSymbol);
}
