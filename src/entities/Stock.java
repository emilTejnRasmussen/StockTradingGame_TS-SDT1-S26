package entities;

import java.math.BigDecimal;

public class Stock
{
    private final String symbol;
    private String name;
    private BigDecimal currentPrice;
    private State currentState;

    public Stock(String symbol, String name, BigDecimal currentPrice)
    {
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = currentPrice;
        this.currentState = State.STEADY;
    }

    public enum State{
        STEADY,
        GROWING,
        DECLINING,
        BANKRUPT,
        RESET
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public State getCurrentState()
    {
        return currentState;
    }

    public void setCurrentState(State currentState)
    {
        this.currentState = currentState;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public String getName()
    {
        return name;
    }

    public BigDecimal getCurrentPrice()
    {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice)
    {
        this.currentPrice = currentPrice;
    }
}
