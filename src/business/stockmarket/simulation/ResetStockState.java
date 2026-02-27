package business.stockmarket.simulation;

import entities.Stock;

import java.math.BigDecimal;

public class ResetStockState implements StockState
{

    @Override
    public double calculatePriceChange()
    {
        return 0;
    }

    @Override
    public Stock.State getName()
    {
        return Stock.State.RESET;
    }
}
