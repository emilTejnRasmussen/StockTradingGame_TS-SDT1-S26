package entities;

import shared.configuration.AppConfig;

import java.math.BigDecimal;
import java.util.UUID;

public class Portfolio
{
    private final UUID id;
    private String name;
    private BigDecimal currentBalance;

    public Portfolio(String name)
    {
        this.id = UUID.randomUUID();
        this.currentBalance = AppConfig.getInstance().getStartingBalance();
        this.name = name;
    }

    public Portfolio(UUID id, BigDecimal currentBalance, String name)
    {
        this.id = id;
        this.currentBalance = currentBalance;
        this.name = name;
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
}
