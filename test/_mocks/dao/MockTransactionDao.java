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
    public List<Transaction> findTransactionsByPortfolioId(UUID portfolioId)
    {
        return transactions.stream()
                .filter(t -> t.portfolioId().equals(portfolioId))
                .toList();
    }

    @Override
    public List<Transaction> findTransactionsByPortfolioIdPaginated(UUID portfolioId, int page, int pageSize)
    {
        return transactions.stream()
                .filter(t -> t.portfolioId().equals(portfolioId))
                .sorted(Comparator.comparing(Transaction::timeStamp).reversed())
                .skip((long) page * pageSize)
                .limit(pageSize)
                .toList();
    }

    @Override
    public int countTransactionsByPortfolioId(UUID portfolioId)
    {
        return (int) transactions.stream()
                .filter(t -> t.portfolioId().equals(portfolioId))
                .count();
    }

    @Override
    public Optional<Transaction> getById(UUID id)
    {
        return transactions.stream()
                .filter(t -> Objects.equals(t.id(), id))
                .findFirst();
    }
}
