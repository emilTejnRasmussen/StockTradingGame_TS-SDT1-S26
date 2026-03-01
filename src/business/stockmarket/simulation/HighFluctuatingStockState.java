package business.stockmarket.simulation;

import entities.Stock;

import java.util.Random;

public class HighFluctuatingStockState implements StockState
{
    private final LiveStock ctx;
    private static final Random random = new Random();

    public HighFluctuatingStockState(LiveStock ctx)
    {
        this.ctx = ctx;
    }

    @Override
    public double calculatePriceChange()
    {
        double changePercent = (random.nextDouble() * 2 - 1) * 0.08;
        if (random.nextDouble() < 0.15){
            double spike = random.nextDouble() * 0.12 + 0.08;
            changePercent = random.nextBoolean() ? spike : -spike;
        }

        Stock.State nextState = TransitionManager.nextState(Stock.State.HIGH_FLUCTUATING);
        ctx.setState(
                StockStateFactory.create(nextState, ctx)
        );

        return changePercent;
    }

    @Override
    public Stock.State getName()
    {
        return Stock.State.HIGH_FLUCTUATING;
    }
}
