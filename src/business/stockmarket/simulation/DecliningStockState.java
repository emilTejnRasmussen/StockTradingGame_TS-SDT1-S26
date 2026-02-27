package business.stockmarket.simulation;

import entities.Stock;

import java.util.Random;

public class DecliningStockState implements StockState
{
    private final LiveStock ctx;
    private static final Random random = new Random();

    public DecliningStockState(LiveStock ctx)
    {
        this.ctx = ctx;
    }

    @Override
    public double calculatePriceChange()
    {
        double changePercent = (random.nextDouble() - 1) / 10;

        double rand = random.nextDouble();
        if (rand < 0.25) ctx.setState(new SteadyStockState(ctx));
        else if (rand < 0.3) ctx.setState(new GrowingStockState(ctx));

        return changePercent;
    }

    @Override
    public Stock.State getName()
    {
        return Stock.State.DECLINING;
    }
}
