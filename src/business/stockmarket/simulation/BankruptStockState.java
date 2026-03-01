package business.stockmarket.simulation;

import entities.Stock;
import shared.configuration.AppConfig;

public class BankruptStockState implements StockState
{
    private final LiveStock ctx;
    private int tickCounter = 0;

    public BankruptStockState(LiveStock ctx)
    {
        this.ctx = ctx;
        ctx.setCurrentPriceToZero();
    }

    @Override
    public double calculatePriceChange()
    {
        tickCounter++;

        if (tickCounter >= AppConfig.getInstance().getBankruptTimeInTicks()){
            Stock.State nextState = TransitionManager.nextState(Stock.State.BANKRUPT);
            ctx.setState(
                    StockStateFactory.create(nextState, ctx)
            );
        }

        return 0;
    }

    @Override
    public Stock.State getName()
    {
        return Stock.State.BANKRUPT;
    }
}
