package entities;

import shared.configuration.AppConfig;

import java.math.BigDecimal;
import java.util.UUID;

public class Portfolio
{
    private final UUID id;
    private String name;
    private BigDecimal currentBalance;
    private BigDecimal startingBalance;

    public Portfolio(String name)
    {
        this.id = UUID.randomUUID();
        this.startingBalance = AppConfig.getInstance().getStartingBalance();
        this.currentBalance = startingBalance;
        this.name = name;
    }

    public Portfolio(String name, BigDecimal startingBalance)
    {
        this.id = UUID.randomUUID();
        this.name = name;
        this.startingBalance = startingBalance;
        this.currentBalance = this.startingBalance;
    }

    public Portfolio(UUID id, String name, BigDecimal currentBalance, BigDecimal startingBalance)
    {
        this.id = id;
        this.name = name;
        this.currentBalance = currentBalance;
        this.startingBalance = startingBalance;
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

    public String getName()
    {
        return name;
    }

    public void setCurrentBalance(BigDecimal currentBalance)
    {
        this.currentBalance = currentBalance;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public BigDecimal getStartingBalance()
    {
        return startingBalance;
    }
}
