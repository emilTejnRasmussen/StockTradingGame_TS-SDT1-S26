package entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction
{
    private final UUID id;
    private final UUID portfolioId;
    private final UUID stockID;
    private final Type type;
    private final int quantity;
    private final double pricePerShare;
    private final double totalAmount;
    private final double fee;
    private final LocalDateTime timeStamp;

    public Transaction(UUID portfolioId, UUID stockID, Type type, int quantity, double pricePerShare, double fee)
    {
        this.id = UUID.randomUUID();
        this.portfolioId = portfolioId;
        this.stockID = stockID;
        this.type = type;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.fee = fee;
        this.totalAmount = quantity * pricePerShare + fee;
        this.timeStamp = LocalDateTime.now();
    }

    public Transaction(UUID id, UUID portfolioId, UUID stockID, Type type, int quantity, double pricePerShare, double fee, LocalDateTime timeStamp)
    {
        this.id = id;
        this.portfolioId = portfolioId;
        this.stockID = stockID;
        this.type = type;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.totalAmount = quantity * pricePerShare + fee;
        this.fee = fee;
        this.timeStamp = timeStamp;
    }

    public enum Type {
        BUY,
        SELL
    }

    public UUID getId()
    {
        return id;
    }

    public UUID getPortfolioId()
    {
        return portfolioId;
    }

    public UUID getStockID()
    {
        return stockID;
    }

    public Type getType()
    {
        return type;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public double getPricePerShare()
    {
        return pricePerShare;
    }

    public double getTotalAmount()
    {
        return totalAmount;
    }

    public double getFee()
    {
        return fee;
    }

    public LocalDateTime getTimeStamp()
    {
        return timeStamp;
    }
}
