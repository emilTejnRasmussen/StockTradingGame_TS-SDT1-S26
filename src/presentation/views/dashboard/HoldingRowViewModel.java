package presentation.views.dashboard;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;

public class HoldingRowViewModel
{
    private final ReadOnlyStringWrapper symbol = new ReadOnlyStringWrapper();
    private final ReadOnlyIntegerWrapper shares = new ReadOnlyIntegerWrapper();
    private final ReadOnlyStringWrapper avgPrice = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper currentPrice = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper value = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper pl = new ReadOnlyStringWrapper();

    public HoldingRowViewModel(String symbol, int shares, String avgPrice, String currentPrice, String value, String pl)
    {
        this.symbol.set(symbol);
        this.shares.set(shares);
        this.avgPrice.set(avgPrice);
        this.currentPrice.set(currentPrice);
        this.value.set(value);
        this.pl.set(pl);
    }

    public String getSymbol()
    {
        return symbol.get();
    }

    public ReadOnlyStringWrapper symbolProperty()
    {
        return symbol;
    }

    public int getShares()
    {
        return shares.get();
    }

    public ReadOnlyIntegerWrapper sharesProperty()
    {
        return shares;
    }

    public String getAvgPrice()
    {
        return avgPrice.get();
    }

    public ReadOnlyStringWrapper avgPriceProperty()
    {
        return avgPrice;
    }

    public String getCurrentPrice()
    {
        return currentPrice.get();
    }

    public ReadOnlyStringWrapper currentPriceProperty()
    {
        return currentPrice;
    }

    public String getValue()
    {
        return value.get();
    }

    public ReadOnlyStringWrapper valueProperty()
    {
        return value;
    }

    public String getPl()
    {
        return pl.get();
    }

    public ReadOnlyStringWrapper plProperty()
    {
        return pl;
    }
}