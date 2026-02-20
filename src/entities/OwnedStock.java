package entities;

import java.util.UUID;

public class OwnedStock
{
    private final UUID id;
    private final UUID portfolioId;
    private final UUID stockId;
    private int numberOfShares;

    public OwnedStock(UUID portfolioId, UUID stockId, int numberOfShares)
    {
        this(UUID.randomUUID(), portfolioId, stockId, numberOfShares);
    }

    public OwnedStock(UUID id, UUID portfolioId, UUID stockId, int numberOfShares)
    {
        if (numberOfShares < 0) throw new IllegalArgumentException("number of shares cannot be negative");

        this.id = id;
        this.portfolioId = portfolioId;
        this.stockId = stockId;
        this.numberOfShares = numberOfShares;
    }

    public void addShares(int numberOfShares){
        if (numberOfShares <= 0) throw new IllegalArgumentException("number of shares to add, must be greater than 0");
        this.numberOfShares += numberOfShares;
    }

    public void removeShares(int numberOfShares) {
        if (numberOfShares <= 0) throw new IllegalArgumentException("number of shares to remove, must be greater than 0");
        if (numberOfShares > this.numberOfShares) throw new IllegalArgumentException("cannot remove more shares than owned");
        this.numberOfShares -= numberOfShares;
    }

    public UUID getId()
    {
        return id;
    }

    public UUID getPortfolioId()
    {
        return portfolioId;
    }

    public UUID getStockId()
    {
        return stockId;
    }

    public int getNumberOfShares()
    {
        return numberOfShares;
    }
}
