package presentation.views.dashboard;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;

public class TransactionTableRow
{
    private final ReadOnlyStringWrapper time = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper type = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper symbol = new ReadOnlyStringWrapper();
    private final ReadOnlyIntegerWrapper shares = new ReadOnlyIntegerWrapper();
    private final ReadOnlyStringWrapper price = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper total = new ReadOnlyStringWrapper();

    public TransactionTableRow(String time, String type, String symbol, int shares, String price, String total)
    {
        this.time.set(time);
        this.type.set(type);
        this.symbol.set(symbol);
        this.shares.set(shares);
        this.price.set(price);
        this.total.set(total);
    }

    public String getTime()
    {
        return time.get();
    }

    public ReadOnlyStringWrapper timeProperty()
    {
        return time;
    }

    public String getType()
    {
        return type.get();
    }

    public ReadOnlyStringWrapper typeProperty()
    {
        return type;
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

    public String getPrice()
    {
        return price.get();
    }

    public ReadOnlyStringWrapper priceProperty()
    {
        return price;
    }

    public String getTotal()
    {
        return total.get();
    }

    public ReadOnlyStringWrapper totalProperty()
    {
        return total;
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public void setType(String type) {
        this.type.set(type);
    }
    public void setSymbol(String symbol) {
        this.symbol.set(symbol);
    }
    public void setShares(Integer shares) {
        this.shares.set(shares);
    }
    public void setPrice(String price) {
        this.price.set(price);
    }
    public void setTotal(String total) {
        this.total.set(total);
    }


}