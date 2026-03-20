package _mocks.dao;

import entities.Transaction;
import persistence.interfaces.TransactionDao;

import java.util.*;

public class MockTransactionDao implements TransactionDao
{
    private List<Transaction> transactions = new ArrayList<>();

    @Override
    public void create(Transaction transaction)
    {
        transactions.add(transaction);
    }

    @Override
    public List<Transaction> getAll()
    {
        return new ArrayList<>(transactions);
    }

    @Override
    public List<Transaction> getAllFromPortfolioId(UUID portfolioId)
    {
        return transactions.stream()
                .filter(t -> t.portfolioId().equals(portfolioId))
                .toList();
    }

    @Override
    public Optional<Transaction> getById(UUID id)
    {
        return transactions.stream()
                .filter(t -> Objects.equals(t.id(), id))
                .findFirst();
    }
}
