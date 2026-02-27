package business.stockmarket.simulation;

import entities.Stock;

import java.math.BigDecimal;
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
        double changePercent = (random.nextDouble() * 2 - 1) / 10;

        double rand = random.nextDouble();
        if (rand < 0.05) ctx.setState(new GrowingStockState(ctx));
        if (rand < 0.1) ctx.setState(new DecliningStockState(ctx));

        return changePercent;
    }

    @Override
    public Stock.State getName()
    {
        return Stock.State.STEADY;
    }
}
