package business.stockmarket.simulation;

import entities.Stock;

import java.util.Random;

public class ResetStockState implements StockState
{
    private final LiveStock ctx;
    private static final Random random = new Random();

    public ResetStockState(LiveStock ctx)
    {
        this.ctx = ctx;
        ctx.resetCurrentPrice();
    }

    @Override
    public double calculatePriceChange()
    {
        double rand = random.nextDouble();

        if (rand < 0.5) ctx.setState(new GrowingStockState(ctx));
        else ctx.setState(new SteadyStockState(ctx));

        return 0;
    }

    @Override
    public Stock.State getName()
    {
        return Stock.State.RESET;
    }
}
