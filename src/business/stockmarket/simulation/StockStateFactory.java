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
            case RAPID_GROWTH -> new RapidGrowthStockState(ctx);
            case RAPID_DECLINE -> new RapidDecliningStockState(ctx);
            case RAPID_CRASH -> new RapidCrashStockState(ctx);
            case BANKRUPT -> new BankruptStockState(ctx);
            case RESET -> new ResetStockState(ctx);
        };
    }
}
