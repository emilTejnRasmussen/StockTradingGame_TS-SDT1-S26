package business.stockmarket.simulation;

import entities.Stock;

public final class StockStateFactory
{
    public static StockState create(Stock.State state, LiveStock ctx){
        return switch (state){
            case STEADY -> new SteadyStockState(ctx);
            case GROWING -> new GrowingStockState(ctx);
            case DECLINING -> new DecliningStockState(ctx);
            case HIGH_FLUCTUATING -> new HighFluctuatingStockState(ctx);
            case RAPID_GROWTH -> null;
            case RAPID_DECLINE -> null;
            case RAPID_CRASH -> null;
            case BANKRUPT -> new BankruptStockState(ctx);
            case RESET -> new ResetStockState(ctx);
        };
    }
}
