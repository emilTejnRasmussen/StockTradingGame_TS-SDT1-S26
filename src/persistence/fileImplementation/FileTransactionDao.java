package persistence.fileImplementation;

import entities.Transaction;
import persistence.interfaces.TransactionDao;
import persistence.interfaces.UnitOfWork;
import shared.logging.Logger;

import java.util.*;

public class FileTransactionDao implements TransactionDao
{
    private final FileUnitOfWork uow;

    public FileTransactionDao(FileUnitOfWork uow)
    {
        this.uow = uow;
    }

    @Override
    public void create(Transaction transaction)
    {
        List<Transaction> transactions = uow.getTransactions();
        boolean exists = transactions.stream()
                .anyMatch(t -> Objects.equals(t.id(), transaction.id()));

        if (exists)
        {
            Logger.getInstance().warning("Transaction with id '" + transaction.id() + "' already exists - nothing created");
            throw new IllegalArgumentException("Transaction with id '" + transaction.id() + "' already exists - nothing created");
        }

        uow.appendTransaction(transaction);
    }

    @Override
    public List<Transaction> getAll()
    {
        return new ArrayList<>(uow.getTransactions());
    }

    @Override
    public Optional<Transaction> getById(UUID id)
    {
        return uow.getTransactions().stream()
                .filter(t -> Objects.equals(t.id(), id))
                .findFirst();
    }
}
