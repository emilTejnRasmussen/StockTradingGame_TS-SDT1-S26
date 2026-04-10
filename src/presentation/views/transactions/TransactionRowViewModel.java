package presentation.views.transactions;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.UUID;

public class TransactionRowViewModel
{
    private final SimpleObjectProperty<UUID> transactionId = new SimpleObjectProperty<>();
    private final SimpleStringProperty time = new SimpleStringProperty();
    private final SimpleStringProperty type = new SimpleStringProperty();
    private final SimpleStringProperty symbol = new SimpleStringProperty();
    private final SimpleIntegerProperty shares = new SimpleIntegerProperty();
    private final SimpleStringProperty price = new SimpleStringProperty();
    private final SimpleStringProperty total = new SimpleStringProperty();

    public TransactionRowViewModel(UUID transactionId,
                                   String time,
                                   String type,
                                   String symbol,
                                   int shares,
                                   String price,
                                   String total
    )
    {
        this.transactionId.set(transactionId);
        setTime(time);
        setType(type);
        setSymbol(symbol);
        setShares(shares);
        setPrice(price);
        setTotal(total);
    }

    public UUID getTransactionId()
    {
        return transactionId.get();
    }

    public SimpleObjectProperty<UUID> transactionIdProperty()
    {
        return transactionId;
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

    private void setTotal(String total)
    {
        this.total.set(total);
    }

    private void setPrice(String price)
    {
        this.price.set(price);
    }

    private void setShares(int shares)
    {
        this.shares.set(shares);
    }

    private void setSymbol(String symbol)
    {
        this.symbol.set(symbol);
    }

    private void setType(String type)
    {
        this.type.set(type);
    }

    private void setTime(String time)
    {
        this.time.set(time);
    }
}
