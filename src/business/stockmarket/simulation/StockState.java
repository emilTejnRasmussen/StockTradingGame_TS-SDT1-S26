package business.stockmarket.simulation;

import entities.Stock;

public interface StockState
{
    double calculatePriceChange();

    Stock.State getName();
}
