package business.stockmarket.simulation;

import entities.Stock;

import java.util.Random;

import static business.stockmarket.simulation.MarketPercentConstants.*;

public class RapidDecliningStockState implements StockState
{
    private final LiveStock ctx;
    private static final Random random = new Random();

    public RapidDecliningStockState(LiveStock ctx)
    {
        this.ctx = ctx;
    }

    @Override
    public double calculatePriceChange()
    {
        double drift = RD_DRIFT_MIN + random.nextDouble() * RD_DRIFT_RANGE;
        double noise = (random.nextDouble() * 2 - 1) * RD_NOISE_MAX_ABS;

        Stock.State nextState = TransitionManager.nextState(Stock.State.RAPID_DECLINE);
        ctx.setState(
                StockStateFactory.create(nextState, ctx)
        );

        return noise - drift;
    }

    @Override
    public Stock.State getName()
    {
        return Stock.State.RAPID_DECLINE;
    }
}
