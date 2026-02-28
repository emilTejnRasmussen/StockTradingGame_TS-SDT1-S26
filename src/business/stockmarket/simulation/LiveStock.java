package business.stockmarket.simulation;

import entities.Stock;
import shared.configuration.AppConfig;

import java.math.BigDecimal;

public class LiveStock
{
    private String symbol;
    private StockState currentState;
    private BigDecimal currentPrice;

    public LiveStock(String symbol, Stock.State state, BigDecimal currentPrice)
    {
        this.symbol = symbol;
        switch (state){
            case GROWING -> this.currentState = new GrowingStockState(this);
            case DECLINING -> this.currentState = new DecliningStockState(this);
            case BANKRUPT -> this.currentState = new BankruptStockState(this);
            case RESET -> this.currentState = new ResetStockState(this);
            default -> this.currentState = new SteadyStockState(this);
        }
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

    void setCurrentPriceToZero() {
        currentPrice = BigDecimal.ZERO;
    }

    void resetCurrentPrice() {
        currentPrice = AppConfig.getInstance().getStockResetValue();
    }

    public Stock.State getStateName() {
        return currentState.getName();
    }

    public String getSymbol()
    {
        return symbol;
    }

    public BigDecimal getCurrentPrice()
    {
        return currentPrice;
    }
}
