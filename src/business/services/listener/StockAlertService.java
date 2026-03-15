package business.services.listener;

import business.dto.StockDTO;
import entities.Stock;
import shared.logging.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;

public class StockAlertService implements PropertyChangeListener
{
    private final Logger logger = Logger.getInstance();

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        StockDTO oldStock = (StockDTO) evt.getOldValue();
        StockDTO newStock = (StockDTO) evt.getNewValue();

        if (hasBecomeBankrupt(oldStock, newStock)) {
            logger.info("ALERT: Stock " + newStock.symbol() + " went bankrupt!");
        }

        if (hasReset(oldStock, newStock)) {
            logger.info("ALERT: Stock " + newStock.symbol() + " reset and is tradable again!");
        }

        if (hasExceededPriceThreshold(oldStock, newStock, 200)){
            logger.info("ALERT: " + newStock.symbol() + " exceeded price 200!");
        }

        if (hasExceededPriceThreshold(oldStock, newStock, 500)){
            logger.info("ALERT: " + newStock.symbol() + " exceeded price 500!");
        }
    }

    private boolean hasExceededPriceThreshold(StockDTO oldStock, StockDTO newStock, int threshold)
    {
        BigDecimal priceThreshold = BigDecimal.valueOf(threshold);
        boolean oldPriceAtOrBelowThreshold = oldStock.currentPrice().compareTo(priceThreshold) <= 0;
        boolean newPriceAboveThreshold = newStock.currentPrice().compareTo(priceThreshold) > 0;

        return oldPriceAtOrBelowThreshold && newPriceAboveThreshold;
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
}
