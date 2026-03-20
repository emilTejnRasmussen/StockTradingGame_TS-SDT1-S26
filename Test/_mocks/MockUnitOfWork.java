package _mocks;

import persistence.interfaces.UnitOfWork;

public class MockUnitOfWork implements UnitOfWork
{
    private int beginCalledAmount = 0;
    private int rollbackCalledAmount = 0;
    private int commitCalledAmount = 0;

    @Override
    public void begin()
    {
        beginCalledAmount++;
    }

    @Override
    public void rollback()
    {
        rollbackCalledAmount++;
    }

    @Override
    public void commit()
    {
        commitCalledAmount++;
    }

    public int getBeginCalledAmount()
    {
        return beginCalledAmount;
    }

    public int getRollbackCalledAmount()
    {
        return rollbackCalledAmount;
    }

    public int getCommitCalledAmount()
    {
        return commitCalledAmount;
    }
}
