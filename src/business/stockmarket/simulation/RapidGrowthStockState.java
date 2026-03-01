package business.stockmarket.simulation;

import entities.Stock;

import java.util.Random;

import static business.stockmarket.simulation.MarketPercentConstants.*;

public class RapidGrowthStockState implements StockState
{
    private final LiveStock ctx;
    private static final Random random = new Random();

    public RapidGrowthStockState(LiveStock ctx)
    {
        this.ctx = ctx;
    }

    @Override
    public double calculatePriceChange()
    {
        double drift = RG_DRIFT_MIN + random.nextDouble() * RG_DRIFT_RANGE;
        double noise = (random.nextDouble() * 2 - 1) * RG_NOISE_MAX_ABS;

        Stock.State nextState = TransitionManager.nextState(Stock.State.RAPID_GROWTH);
        ctx.setState(
                StockStateFactory.create(nextState, ctx)
        );

        return noise + drift;
    }

    @Override
    public Stock.State getName()
    {
        return null;
    }
}
