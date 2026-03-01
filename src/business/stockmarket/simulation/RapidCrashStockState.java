package business.stockmarket.simulation;

import entities.Stock;

import java.util.Random;

import static business.stockmarket.simulation.MarketPercentConstants.*;

public class RapidCrashStockState implements StockState
{
    private final LiveStock ctx;
    private static final Random random = new Random();

    public RapidCrashStockState(LiveStock ctx)
    {
        this.ctx = ctx;
    }

    @Override
    public double calculatePriceChange()
    {
        double roll = random.nextDouble();
        double changePercent;

        if (roll < RC_REBOUND_CHANCE){
            changePercent = RC_REBOUND_MIN + random.nextDouble() * RC_REBOUND_RANGE;
        } else {
            changePercent = -(RC_DROP_MIN + random.nextDouble() * RC_DROP_RANGE);
        }

        Stock.State nextState = TransitionManager.nextState(Stock.State.RAPID_CRASH);
        ctx.setState(
                StockStateFactory.create(nextState, ctx)
        );

        return changePercent;
    }

    @Override
    public Stock.State getName()
    {
        return Stock.State.RAPID_CRASH;
    }
}
