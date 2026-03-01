package business.stockmarket.simulation;

import entities.Stock;

import java.util.Random;

public class SteadyStockState implements StockState
{
    private final LiveStock ctx;
    private static final Random random = new Random();

    public SteadyStockState(LiveStock ctx)
    {
        this.ctx = ctx;
    }

    @Override
    public double calculatePriceChange()
    {
        double changePercent = (random.nextDouble() * 2 - 1) * 0.1;

        Stock.State nextState = TransitionManager.nextState(Stock.State.STEADY);
        ctx.setState(
                StockStateFactory.create(nextState, ctx)
        );

        return changePercent;
    }

    @Override
    public Stock.State getName()
    {
        return Stock.State.STEADY;
    }
}
