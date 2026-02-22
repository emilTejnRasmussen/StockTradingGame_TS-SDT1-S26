package persistence.interfaces;

import entities.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionDao
{
    void create(Transaction transaction);
    List<Transaction> getAll();
    Optional<Transaction> getById(UUID id);
}
