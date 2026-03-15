package persistence.fileImplementation;

import entities.Transaction;
import persistence.interfaces.TransactionDao;

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
        uow.appendTransaction(transaction);
    }

    @Override
    public List<Transaction> getAll()
    {
        return new ArrayList<>(uow.getTransactions());
    }

    @Override
    public List<Transaction> getAllFromPortfolioId(UUID portfolioId)
    {
        return uow.getTransactions().stream()
                .filter(t -> t.portfolioId().equals(portfolioId))
                .toList();
    }

    @Override
    public Optional<Transaction> getById(UUID id)
    {
        return uow.getTransactions().stream()
                .filter(t -> Objects.equals(t.id(), id))
                .findFirst();
    }
}
