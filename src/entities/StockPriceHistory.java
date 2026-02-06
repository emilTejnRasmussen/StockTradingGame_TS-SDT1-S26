package entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class StockPriceHistory
{
    private final UUID id;
    private final UUID stockId;
    private final double price;
    private final LocalDateTime timeStamp;

    public StockPriceHistory(UUID stockId, double price)
    {
        this.id = UUID.randomUUID();
        this.stockId = stockId;
        this.price = price;
        this.timeStamp = LocalDateTime.now();
    }

    public StockPriceHistory(UUID id, UUID stockId, double price, LocalDateTime timeStamp)
    {
        this.id = id;
        this.stockId = stockId;
        this.price = price;
        this.timeStamp = timeStamp;
    }

    public UUID getId()
    {
        return id;
    }

    public UUID getStockId()
    {
        return stockId;
    }

    public double getPrice()
    {
        return price;
    }

    public LocalDateTime getTimeStamp()
    {
        return timeStamp;
    }
}
