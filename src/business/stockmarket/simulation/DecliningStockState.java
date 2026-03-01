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
        double changePercent = (random.nextDouble() - 1) * 0.1;

        Stock.State nextState = TransitionManager.nextState(Stock.State.DECLINING);
        ctx.setState(
                StockStateFactory.create(nextState, ctx)
        );

        return changePercent;
    }

    @Override
    public Stock.State getName()
    {
        return Stock.State.DECLINING;
    }
}
