package business.stockmarket.simulation;

import entities.Stock;
import shared.configuration.AppConfig;

import java.math.BigDecimal;

public class LiveStock
{
    private String symbol;
    private StockState currentState;
    private BigDecimal currentPrice;

    public LiveStock(String symbol, StockState currentState, BigDecimal currentPrice)
    {
        this.symbol = symbol;
        this.currentState = currentState;
        this.currentPrice = currentPrice;
    }

    public LiveStock(String symbol)
    {
        this.symbol = symbol;
        this.currentPrice = AppConfig.getInstance().getStockResetValue();
        this.currentState = new SteadyStockState(this);
    }

    public void updatePrice() {
        double priceChangeInPercentage = currentState.calculatePriceChange();
        currentPrice = currentPrice.multiply(BigDecimal.valueOf(priceChangeInPercentage));
    }

    void setState(StockState state){
        this.currentState = state;
    }

    public Stock.State getStateName() {
        return currentState.getName();
    }
}
