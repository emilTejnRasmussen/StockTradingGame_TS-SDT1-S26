package presentation.core.notification;

import business.services.listener.StockAlertService;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class NotificationImpl implements NotificationHandler, PropertyChangeListener
{
    private static final int MAX_NOTIFICATIONS = 5;

    private VBox notificationContainer;

    public NotificationImpl(StockAlertService stockAlertService)
    {
        stockAlertService.addListener(this);
    }

    @Override
    public void showNotification(String title, String message, String type)
    {
        Platform.runLater(() -> {
            Label titleLabel = new Label(title);
            titleLabel.getStyleClass().add("toast-title");

            Label messageLabel = new Label(message);
            messageLabel.getStyleClass().add("toast-message");
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(300);

            Label typeBadge = new Label(getBadgeText(type));
            typeBadge.getStyleClass().add("toast-badge");
            typeBadge.getStyleClass().add("toast-badge-" + type);

            HBox header = new HBox(10, typeBadge, titleLabel);
            header.setAlignment(Pos.CENTER_LEFT);

            VBox content = new VBox(6, header, messageLabel);
            content.setAlignment(Pos.CENTER_LEFT);

            StackPane toast = new StackPane(content);
            toast.getStyleClass().add("toast-wrapper");
            toast.getStyleClass().add("toast-" + type);
            toast.setOpacity(0);

            if (notificationContainer.getChildren().size() >= MAX_NOTIFICATIONS)
            {
                notificationContainer.getChildren().removeLast();
            }

            notificationContainer.getChildren().addFirst(toast);

            getTransition(toast, type).play();
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        String type = evt.getPropertyName();
        String message = String.valueOf(evt.getNewValue());

        switch (type)
        {
            case "bankrupt" -> showNotification(
                    "Bankruptcy alert",
                    message,
                    "bankrupt"
            );
            case "reset" -> showNotification(
                    "Trading resumed",
                    message,
                    "reset"
            );
            case "goal" -> showNotification(
                    "Price target reached",
                    message,
                    "goal"
            );
            case "milestone" -> showNotification(
                    "Major milestone",
                    message,
                    "milestone"
            );
            case "surge" -> showNotification(
                    "Price surge",
                    message,
                    "surge"
            );
            case "drop" -> showNotification(
                    "Price drop",
                    message,
                    "drop"
            );
            case "warning" -> showNotification(
                    "Risk warning",
                    message,
                    "warning"
            );
            case "recovery" -> showNotification(
                    "Recovery signal",
                    message,
                    "recovery"
            );
            default -> showNotification(
                    "Market update",
                    message,
                    "default"
            );
        }
    }

    private SequentialTransition getTransition(StackPane toast, String type)
    {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(220), toast);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition stay = new PauseTransition(getVisibleDuration(type));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(420), toast);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(_ -> notificationContainer.getChildren().remove(toast));

        return new SequentialTransition(fadeIn, stay, fadeOut);
    }

    private Duration getVisibleDuration(String type)
    {
        return switch (type)
        {
            case "bankrupt" -> Duration.seconds(7);
            case "warning", "milestone" -> Duration.seconds(5);
            case "reset" -> Duration.seconds(4.5);
            case "goal" -> Duration.seconds(3.5);
            default -> Duration.seconds(4);
        };
    }

    private String getBadgeText(String type)
    {
        return switch (type)
        {
            case "bankrupt" -> "CRITICAL";
            case "reset" -> "RECOVERY";
            case "goal" -> "TARGET";
            case "milestone" -> "MILESTONE";
            case "surge" -> "SURGE";
            case "drop" -> "DROP";
            case "warning" -> "WARNING";
            case "recovery" -> "SIGNAL";
            default -> "UPDATE";
        };
    }

    public void setNotificationContainer(VBox notificationContainer)
    {
        this.notificationContainer = notificationContainer;
    }
}