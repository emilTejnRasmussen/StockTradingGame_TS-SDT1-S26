package business.stockmarket.simulation;

import entities.Stock;

public class BankruptStockState implements StockState
{
    private final LiveStock ctx;
    private int counter;

    public BankruptStockState(LiveStock ctx)
    {
        this.ctx = ctx;
        this.counter = 0;
    }

    @Override
    public double calculatePriceChange()
    {
        counter++;
        if ()
        return 0;
    }

    @Override
    public Stock.State getName()
    {
        return Stock.State.BANKRUPT;
    }
}
