package business.stockmarket.simulation;

import entities.Stock;

import java.util.Random;

import static business.stockmarket.simulation.MarketPercentConstants.*;

public class HighFluctuatingStockState implements StockState
{
    private final LiveStock ctx;
    private final Random random = new Random();

    public HighFluctuatingStockState(LiveStock ctx)
    {
        this.ctx = ctx;
    }

    @Override
    public double calculatePriceChange()
    {
        double changePercent = (random.nextDouble() * 2 - 1) * HF_BASE_MAX_ABS;
        if (random.nextDouble() < HF_SPIKE_CHANCE){
            double spike = HF_SPIKE_MIN + random.nextDouble() * HF_SPIKE_RANGE;
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
