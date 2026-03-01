package business.stockmarket.simulation;

import entities.Stock;

import java.util.Random;

import static business.stockmarket.simulation.MarketPercentConstants.*;

public class GrowingStockState implements StockState
{
    private final LiveStock ctx;
    private final Random random = new Random();

    public GrowingStockState(LiveStock ctx)
    {
        this.ctx = ctx;
    }

    @Override
    public double calculatePriceChange()
    {
        double drift = GROW_DRIFT_MIN + random.nextDouble() * GROW_DRIFT_RANGE;
        double noise = (random.nextDouble() * 2 - 1) * GROW_NOISE_MAX_ABS;

        Stock.State nextState = TransitionManager.nextState(Stock.State.GROWING);
        ctx.setState(
                StockStateFactory.create(nextState, ctx)
        );

        return noise + drift;
    }

    @Override
    public Stock.State getName()
    {
        return Stock.State.GROWING;
    }
}
