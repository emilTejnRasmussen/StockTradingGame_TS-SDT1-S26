package presentation.core.notification;

import business.services.listener.StockAlertService;
import provided.CustomAlertBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class CustomAlertBoxAdapter implements NotificationHandler, PropertyChangeListener
{
    private final CustomAlertBox customAlertBox;

    public CustomAlertBoxAdapter(StockAlertService stockAlertService, CustomAlertBox customAlertBox)
    {
        this.customAlertBox = customAlertBox;
        stockAlertService.addListener(this);
    }

    @Override
    public void showNotification(String title, String message, String type)
    {
        CustomAlertBox.AlertType alertType = convertToAlertType(type);
        customAlertBox.showAlert(message, title, alertType);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        String type = evt.getPropertyName();
        String title = getTitleFromAlertType(type);
        String message = String.valueOf(evt.getNewValue());

        showNotification(title, message, type);
    }

    private String getTitleFromAlertType(String type)
    {
        return switch (type) {
            case "bankrupt" -> "Bankruptcy alert";
            case "reset" -> "Trading resumed";
            case "goal" -> "Price target reached";
            case "milestone" -> "Major milestone";
            case "surge" -> "Price surge";
            case "drop" -> "Price drop";
            case "warning" -> "Risk warning";
            case "recovery" -> "Recovery signal";
            default -> "Market update";
        };
    }

    private CustomAlertBox.AlertType convertToAlertType(String type)
    {
        return switch (type)
        {
            case "reset",
                 "goal",
                 "milestone",
                 "surge",
                 "drop",
                 "recovery" -> CustomAlertBox.AlertType.INFO;

            case "bankrupt",
                 "warning" -> CustomAlertBox.AlertType.WARNING;

            default -> CustomAlertBox.AlertType.ERROR;
        };
    }
}
