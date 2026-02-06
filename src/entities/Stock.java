package entities;

import java.util.UUID;

public class Stock
{
    private final UUID id;
    private final String symbol;
    private String name;
    private double currentPrice;
    private State currentState;

    public Stock(String symbol, String name, double currentPrice)
    {
        this.id = UUID.randomUUID();
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

    public UUID getId()
    {
        return id;
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

    public double getCurrentPrice()
    {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice)
    {
        this.currentPrice = currentPrice;
    }
}
