package business.stockmarket.simulation;

import entities.Stock;

import java.util.Random;

import static business.stockmarket.simulation.MarketPercentConstants.*;

public class DecliningStockState implements StockState
{
    private final LiveStock ctx;
    private final Random random = new Random();

    public DecliningStockState(LiveStock ctx)
    {
        this.ctx = ctx;
    }

    @Override
    public double calculatePriceChange()
    {
        double drift = DECL_DRIFT_MIN + random.nextDouble() * DECL_DRIFT_RANGE;
        double noise = (random.nextDouble() * 2 - 1) * DECL_NOISE_MAX_ABS;

        Stock.State nextState = TransitionManager.nextState(Stock.State.DECLINING);
        ctx.setState(
                StockStateFactory.create(nextState, ctx)
        );

        return noise - drift;
    }

    @Override
    public Stock.State getName()
    {
        return Stock.State.DECLINING;
    }
}
