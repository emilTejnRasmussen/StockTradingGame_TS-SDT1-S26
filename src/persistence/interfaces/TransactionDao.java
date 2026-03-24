package persistence.interfaces;

import entities.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionDao
{
    void create(Transaction transaction);

    List<Transaction> getAll();

    List<Transaction> findTransactionsByPortfolioId(UUID portfolioId);

    List<Transaction> findTransactionsByPortfolioIdPaginated(
            UUID portfolioId,
            int page,
            int pageSize
    );

    int countTransactionsByPortfolioId(UUID portfolioId);

    Optional<Transaction> getById(UUID id);
}
