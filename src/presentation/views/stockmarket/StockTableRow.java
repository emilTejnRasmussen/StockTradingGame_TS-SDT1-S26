package presentation.views.stockmarket;

import javafx.beans.property.SimpleStringProperty;

public class StockTableRow
{
    private final SimpleStringProperty symbol = new SimpleStringProperty();
    private final SimpleStringProperty price = new SimpleStringProperty();
    private final SimpleStringProperty owned = new SimpleStringProperty();

    public StockTableRow(String symbol, String price, String owned)
    {
        this.symbol.set(symbol);
        this.price.set(price);
        this.owned.set(owned);
    }

    public String getSymbol()
    {
        return symbol.get();
    }

    public SimpleStringProperty symbolProperty()
    {
        return symbol;
    }

    public String getPrice()
    {
        return price.get();
    }

    public SimpleStringProperty priceProperty()
    {
        return price;
    }

    public String getOwned()
    {
        return owned.get();
    }

    public SimpleStringProperty ownedProperty()
    {
        return owned;
    }

    public void setSymbol(String symbol) {
        this.symbol.set(symbol);
    }

    public void setPrice(String price) {
        this.price.set(price);
    }

    public void setOwned(String owned) {
        this.owned.set(owned);
    }
}
