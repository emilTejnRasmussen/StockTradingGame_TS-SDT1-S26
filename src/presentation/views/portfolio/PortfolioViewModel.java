package presentation.views.portfolio;

import business.services.PortfolioService;
import business.services.listener.StockListenerService;
import entities.Portfolio;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import presentation.core.ApplicationContext;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class PortfolioViewModel implements PropertyChangeListener
{
    private final ReadOnlyStringWrapper activePortfolioName = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper activeCashBalance = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper activeNetWorth = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper activeOwnedStocks = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper activeTotalShares = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper portfolioCount = new ReadOnlyStringWrapper();

    private final ObservableList<PortfolioRowViewModel> portfolios = FXCollections.observableArrayList();

    private final PortfolioService portfolioService;
    private final ObjectProperty<UUID> activePortfolioId;
    private UUID portfolioId;

    public PortfolioViewModel(PortfolioService portfolioService, StockListenerService stockListenerService, ObjectProperty<UUID> activePortfolioId)
    {
        this.activePortfolioId = activePortfolioId;
        this.portfolioService = portfolioService;
        this.portfolioId = activePortfolioId.get();

        activePortfolioId.addListener((_, _, newId) -> {
            this.portfolioId = newId;
            refreshValues();
        });

        stockListenerService.addListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        Platform.runLater(this::refreshValues);
    }

    public void load()
    {
        portfolios.clear();

        recalculatePortfolioInfo();
        loadPortfolioTable();
    }

    public void createNewPortfolio(String name, String startingBalance)
    {
        if (name.isEmpty() || startingBalance.isEmpty())
        {
            showErrorAlert("Cannot create portfolio", "Please fill in all fields with valid values.");
            return;
        }

        try
        {
            BigDecimal balance = new BigDecimal(startingBalance);

            if (balance.compareTo(BigDecimal.valueOf(10000)) > 0) {
                showErrorAlert("Cannot create portfolio", "Max starting balance is 10000");
                return;
            }

            portfolioService.createNewPortfolio(name.trim(), balance);
            load();
        }
        catch (NumberFormatException e)
        {
            showErrorAlert("Invalid balance", "Starting balance must be a valid number.");
        }
    }

    public void setActivePortfolio(UUID portfolioId)
    {
        activePortfolioId.set(portfolioId);
        load();
    }

    private void refreshValues()
    {
        recalculatePortfolioInfo();

        for (PortfolioRowViewModel row : portfolios)
        {
            UUID id = row.getPortfolioId();

            row.setCash(formatCurrency(portfolioService.getPortfolioBalance(id)));
            row.setNetWorth(formatCurrency(portfolioService.getPortfolioNetWorth(id)));
            row.setOwnedStocks(String.valueOf(portfolioService.getOwnedStocks(id).size()));
            row.setTotalShares(String.valueOf(portfolioService.getTotalNumberOfShares(id)));
            row.setActive(id.equals(portfolioId));
        }
    }

    private void recalculatePortfolioInfo()
    {
        String portfolioName = portfolioService.getPortfolioName(portfolioId);
        BigDecimal portfolioBalance = portfolioService.getPortfolioBalance(portfolioId);
        BigDecimal portfolioNetWorth = portfolioService.getPortfolioNetWorth(portfolioId);
        int stocksOwned = portfolioService.getOwnedStocks(portfolioId).size();
        int totalSharesOwned = portfolioService.getTotalNumberOfShares(portfolioId);
        int portfoliosSaved = portfolioService.getAllPortfolios().size();

        activePortfolioName.set(portfolioName);
        activeCashBalance.set(formatCurrency(portfolioBalance));
        activeNetWorth.set(formatCurrency(portfolioNetWorth));
        activeOwnedStocks.set(stocksOwned + " owned");
        activeTotalShares.set(totalSharesOwned + " owned");
        portfolioCount.set(portfoliosSaved == 1 ? portfoliosSaved + " portfolio" : portfoliosSaved + " portfolios");

    }

    private void loadPortfolioTable()
    {
        if (portfolioId == null) return;

        List<Portfolio> portfolioList = portfolioService.getAllPortfolios();

        for (Portfolio portfolio : portfolioList)
        {
            String name = portfolio.getName();
            String cashBalance = formatCurrency(portfolioService.getPortfolioBalance(portfolio.getId()));
            String netWorth = formatCurrency(portfolioService.getPortfolioNetWorth(portfolio.getId()));
            String ownedStocks = "" + portfolioService.getOwnedStocks(portfolio.getId()).size();
            String totalShares = "" + portfolioService.getTotalNumberOfShares(portfolio.getId());
            boolean isActive = portfolio.getId().equals(portfolioId);

            portfolios.add(new PortfolioRowViewModel(
                    portfolio.getId(),
                    name,
                    cashBalance,
                    netWorth,
                    ownedStocks,
                    totalShares,
                    isActive
            ));
        }
    }

    private String formatCurrency(BigDecimal value)
    {
        return String.format("¤ %.2f", value);
    }

    public String getActivePortfolioName()
    {
        return activePortfolioName.get();
    }

    public ReadOnlyStringWrapper activePortfolioNameProperty()
    {
        return activePortfolioName;
    }

    public String getActiveCashBalance()
    {
        return activeCashBalance.get();
    }

    public ReadOnlyStringWrapper activeCashBalanceProperty()
    {
        return activeCashBalance;
    }

    public String getActiveNetWorth()
    {
        return activeNetWorth.get();
    }

    public ReadOnlyStringWrapper activeNetWorthProperty()
    {
        return activeNetWorth;
    }

    public String getActiveOwnedStocks()
    {
        return activeOwnedStocks.get();
    }

    public ReadOnlyStringWrapper activeOwnedStocksProperty()
    {
        return activeOwnedStocks;
    }

    public String getActiveTotalShares()
    {
        return activeTotalShares.get();
    }

    public ReadOnlyStringWrapper activeTotalSharesProperty()
    {
        return activeTotalShares;
    }

    public String getPortfolioCount()
    {
        return portfolioCount.get();
    }

    public ReadOnlyStringWrapper portfolioCountProperty()
    {
        return portfolioCount;
    }

    public ObservableList<PortfolioRowViewModel> getPortfolios()
    {
        return portfolios;
    }

    private void showErrorAlert(String title, String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
