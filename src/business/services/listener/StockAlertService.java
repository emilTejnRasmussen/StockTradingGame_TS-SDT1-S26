package business.services.listener;

import business.dto.StockDTO;
import entities.Stock;
import shared.logging.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class StockAlertService implements PropertyChangeListener
{
    private static final BigDecimal PRICE_THRESHOLD_200 = BigDecimal.valueOf(200);
    private static final BigDecimal PRICE_THRESHOLD_500 = BigDecimal.valueOf(500);
    private static final BigDecimal PRICE_THRESHOLD_1000 = BigDecimal.valueOf(1000);

    private static final BigDecimal SURGE_PERCENT = BigDecimal.valueOf(8);
    private static final BigDecimal DROP_PERCENT = BigDecimal.valueOf(-8);
    private static final BigDecimal RECOVERY_PERCENT = BigDecimal.valueOf(12);

    private static final BigDecimal LOW_PRICE_WARNING = BigDecimal.valueOf(25);

    private final Logger logger = Logger.getInstance();
    private final PropertyChangeSupport support;

    public StockAlertService()
    {
        this.support = new PropertyChangeSupport(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        StockDTO oldStock = (StockDTO) evt.getOldValue();
        StockDTO newStock = (StockDTO) evt.getNewValue();

        if (hasBecomeBankrupt(oldStock, newStock))
        {
            fireAlert(
                    "bankrupt",
                    newStock.symbol() + " is now bankrupt and can no longer be traded."
            );
        }

        if (hasReset(oldStock, newStock))
        {
            fireAlert(
                    "reset",
                    newStock.symbol() + " has reset and is tradable again."
            );
        }

        if (crossedAbove(oldStock, newStock, PRICE_THRESHOLD_200))
        {
            fireAlert(
                    "goal",
                    newStock.symbol() + " moved above 200.00."
            );
        }

        if (crossedAbove(oldStock, newStock, PRICE_THRESHOLD_500))
        {
            fireAlert(
                    "goal",
                    newStock.symbol() + " moved above 500.00."
            );
        }

        if (crossedAbove(oldStock, newStock, PRICE_THRESHOLD_1000))
        {
            fireAlert(
                    "milestone",
                    newStock.symbol() + " broke above 1000.00."
            );
        }

        if (hasSharpPriceSurge(oldStock, newStock))
        {
            fireAlert(
                    "surge",
                    newStock.symbol() + " surged " + formatPercent(getPercentChange(oldStock, newStock)) + " since the last update."
            );
        }

        if (hasSharpPriceDrop(oldStock, newStock))
        {
            fireAlert(
                    "drop",
                    newStock.symbol() + " dropped " + formatPercent(getPercentChange(oldStock, newStock).abs()) + " since the last update."
            );
        }

        if (enteredLowPriceZone(oldStock, newStock))
        {
            fireAlert(
                    "warning",
                    newStock.symbol() + " fell below 25.00 and is now in a risky price zone."
            );
        }

        if (hasStrongRecovery(oldStock, newStock))
        {
            fireAlert(
                    "recovery",
                    newStock.symbol() + " is recovering fast with a gain of " + formatPercent(getPercentChange(oldStock, newStock)) + "."
            );
        }
    }

    private void fireAlert(String type, String message)
    {
        logger.info("ALERT: " + message);
        support.firePropertyChange(type, null, message);
    }

    private boolean crossedAbove(StockDTO oldStock, StockDTO newStock, BigDecimal threshold)
    {
        boolean oldAtOrBelow = oldStock.currentPrice().compareTo(threshold) <= 0;
        boolean newAbove = newStock.currentPrice().compareTo(threshold) > 0;
        return oldAtOrBelow && newAbove;
    }

    private boolean enteredLowPriceZone(StockDTO oldStock, StockDTO newStock)
    {
        boolean oldAbove = oldStock.currentPrice().compareTo(LOW_PRICE_WARNING) >= 0;
        boolean newBelow = newStock.currentPrice().compareTo(LOW_PRICE_WARNING) < 0;
        return oldAbove && newBelow && newStock.currentState() != Stock.State.BANKRUPT;
    }

    private boolean hasSharpPriceSurge(StockDTO oldStock, StockDTO newStock)
    {
        return getPercentChange(oldStock, newStock).compareTo(SURGE_PERCENT) >= 0;
    }

    private boolean hasSharpPriceDrop(StockDTO oldStock, StockDTO newStock)
    {
        return getPercentChange(oldStock, newStock).compareTo(DROP_PERCENT) <= 0;
    }

    private boolean hasStrongRecovery(StockDTO oldStock, StockDTO newStock)
    {
        boolean oldWasLow = oldStock.currentPrice().compareTo(LOW_PRICE_WARNING) < 0;
        boolean gainIsStrong = getPercentChange(oldStock, newStock).compareTo(RECOVERY_PERCENT) >= 0;
        boolean stillTradable = newStock.currentState() != Stock.State.BANKRUPT;

        return oldWasLow && gainIsStrong && stillTradable;
    }

    private BigDecimal getPercentChange(StockDTO oldStock, StockDTO newStock)
    {
        if (oldStock.currentPrice().compareTo(BigDecimal.ZERO) == 0)
        {
            return BigDecimal.ZERO;
        }

        return newStock.currentPrice()
                .subtract(oldStock.currentPrice())
                .divide(oldStock.currentPrice(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private String formatPercent(BigDecimal percent)
    {
        return percent.setScale(2, RoundingMode.HALF_UP) + "%";
    }

    private boolean hasReset(StockDTO oldStock, StockDTO newStock)
    {
        boolean oldStateWasNotReset = oldStock.currentState() != Stock.State.RESET;
        boolean newStateIsReset = newStock.currentState() == Stock.State.RESET;
        return oldStateWasNotReset && newStateIsReset;
    }

    private boolean hasBecomeBankrupt(StockDTO oldStock, StockDTO newStock)
    {
        boolean oldStateIsNotBankrupt = oldStock.currentState() != Stock.State.BANKRUPT;
        boolean newStateIsBankrupt = newStock.currentState() == Stock.State.BANKRUPT;
        return oldStateIsNotBankrupt && newStateIsBankrupt;
    }

    public void addListener(PropertyChangeListener listener)
    {
        support.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener)
    {
        support.removePropertyChangeListener(listener);
    }
}