package presentation.views.transactions;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.UUID;

public class TransactionRowViewModel
{
    private final SimpleStringProperty time = new SimpleStringProperty();
    private final SimpleStringProperty type = new SimpleStringProperty();
    private final SimpleStringProperty symbol = new SimpleStringProperty();
    private final SimpleIntegerProperty shares = new SimpleIntegerProperty();
    private final SimpleStringProperty price = new SimpleStringProperty();
    private final SimpleStringProperty total = new SimpleStringProperty();

    public TransactionRowViewModel(String time,
                                   String type,
                                   String symbol,
                                   int shares,
                                   String price,
                                   String total
    )
    {
        setTime(time);
        setType(type);
        setSymbol(symbol);
        setShares(shares);
        setPrice(price);
        setTotal(total);
    }

    public String getTime()
    {
        return time.get();
    }

    public SimpleStringProperty timeProperty()
    {
        return time;
    }

    public String getType()
    {
        return type.get();
    }

    public SimpleStringProperty typeProperty()
    {
        return type;
    }

    public String getSymbol()
    {
        return symbol.get();
    }

    public SimpleStringProperty symbolProperty()
    {
        return symbol;
    }

    public int getShares()
    {
        return shares.get();
    }

    public SimpleIntegerProperty sharesProperty()
    {
        return shares;
    }

    public String getPrice()
    {
        return price.get();
    }

    public SimpleStringProperty priceProperty()
    {
        return price;
    }

    public String getTotal()
    {
        return total.get();
    }

    public SimpleStringProperty totalProperty()
    {
        return total;
    }

    public void setTotal(String total)
    {
        this.total.set(total);
    }

    public void setPrice(String price)
    {
        this.price.set(price);
    }

    public void setShares(int shares)
    {
        this.shares.set(shares);
    }

    public void setSymbol(String symbol)
    {
        this.symbol.set(symbol);
    }

    public void setType(String type)
    {
        this.type.set(type);
    }

    public void setTime(String time)
    {
        this.time.set(time);
    }
}
