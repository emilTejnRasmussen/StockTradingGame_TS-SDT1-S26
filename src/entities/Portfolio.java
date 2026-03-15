package entities;

import shared.configuration.AppConfig;

import java.math.BigDecimal;
import java.util.UUID;

public class Portfolio
{
    private final UUID id;
    private BigDecimal currentBalance;

    public Portfolio()
    {
        this.id = UUID.randomUUID();
        this.currentBalance = AppConfig.getInstance().getStartingBalance();
    }

    public Portfolio(UUID id, BigDecimal currentBalance)
    {
        this.id = id;
        this.currentBalance = currentBalance;
    }

    public UUID getId()
    {
        return id;
    }

    public BigDecimal getCurrentBalance()
    {
        return currentBalance;
    }

    public void pay(BigDecimal amount){
        currentBalance = currentBalance.subtract(amount);
    }

    public void earn(BigDecimal amount){
        currentBalance = currentBalance.add(amount);
    }
}
