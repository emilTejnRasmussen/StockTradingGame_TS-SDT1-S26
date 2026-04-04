package presentation.views.portfolio;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.UUID;

public class PortfolioRowViewModel
{
    private final SimpleObjectProperty<UUID> portfolioId = new SimpleObjectProperty<>();
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleStringProperty cash = new SimpleStringProperty();
    private final SimpleStringProperty netWorth = new SimpleStringProperty();
    private final SimpleStringProperty ownedStocks = new SimpleStringProperty();
    private final SimpleStringProperty totalShares = new SimpleStringProperty();
    private final SimpleStringProperty active = new SimpleStringProperty();

    public PortfolioRowViewModel(UUID portfolioId, String name, String cash, String netWorth, String ownedStocks, String totalShares, boolean isActive)
    {
        this.portfolioId.set(portfolioId);
        this.name.set(name);
        this.cash.set(cash);
        this.netWorth.set(netWorth);
        this.ownedStocks.set(ownedStocks);
        this.totalShares.set(totalShares);
        this.active.set(isActive ? "active" : "---");
    }

    public UUID getPortfolioId()
    {
        return portfolioId.get();
    }

    public SimpleObjectProperty<UUID> portfolioIdProperty()
    {
        return portfolioId;
    }

    public String getName()
    {
        return name.get();
    }

    public void setName(String name)
    {
        this.name.set(name);
    }

    public SimpleStringProperty nameProperty()
    {
        return name;
    }

    public String getCash()
    {
        return cash.get();
    }

    public void setCash(String cash)
    {
        this.cash.set(cash);
    }

    public SimpleStringProperty cashProperty()
    {
        return cash;
    }

    public String getNetWorth()
    {
        return netWorth.get();
    }

    public void setNetWorth(String netWorth)
    {
        this.netWorth.set(netWorth);
    }

    public SimpleStringProperty netWorthProperty()
    {
        return netWorth;
    }

    public String getOwnedStocks()
    {
        return ownedStocks.get();
    }

    public void setOwnedStocks(String ownedStocks)
    {
        this.ownedStocks.set(ownedStocks);
    }

    public SimpleStringProperty ownedStocksProperty()
    {
        return ownedStocks;
    }

    public String getTotalShares()
    {
        return totalShares.get();
    }

    public void setTotalShares(String totalShares)
    {
        this.totalShares.set(totalShares);
    }

    public SimpleStringProperty totalSharesProperty()
    {
        return totalShares;
    }

    public String getActive()
    {
        return active.get();
    }

    public void setActive(boolean isActive)
    {
        this.active.set(isActive ? "active" : "---");
    }

    public SimpleStringProperty activeProperty()
    {
        return active;
    }
}