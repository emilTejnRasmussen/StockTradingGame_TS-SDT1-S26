package entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Transaction(UUID id, UUID portfolioId, String stockSymbol, Type type, int quantity,
                          BigDecimal pricePerShare,
                          BigDecimal fee, LocalDateTime timeStamp)
{
    public enum Type
    {
        BUY,
        SELL
    }

    public BigDecimal getTotalPriceWithFee()
    {
        return pricePerShare.multiply(BigDecimal.valueOf(quantity)).add(fee);
    }

    public BigDecimal getGrossAmount()
    {
        return pricePerShare.multiply(BigDecimal.valueOf(quantity));
    }

    public static Transaction create(UUID portfolioId, String stockSymbol, Type type, int quantity, BigDecimal pricePerShare,
                                     BigDecimal fee)
    {
        return new Transaction(UUID.randomUUID(), portfolioId, stockSymbol, type, quantity, pricePerShare, fee, LocalDateTime.now());
    }
}

