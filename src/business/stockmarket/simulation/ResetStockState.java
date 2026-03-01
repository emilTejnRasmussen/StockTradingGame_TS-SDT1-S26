package business.stockmarket.simulation;

import entities.Stock;

import java.util.Random;

public class ResetStockState implements StockState
{
    private final LiveStock ctx;

    public ResetStockState(LiveStock ctx)
    {
        this.ctx = ctx;
        ctx.resetCurrentPrice();
    }

    @Override
    public double calculatePriceChange()
    {
        Stock.State nextState = TransitionManager.nextState(Stock.State.RESET);
        ctx.setState(
                StockStateFactory.create(nextState, ctx)
        );

        return 0;
    }

    @Override
    public Stock.State getName()
    {
        return Stock.State.RESET;
    }
}
