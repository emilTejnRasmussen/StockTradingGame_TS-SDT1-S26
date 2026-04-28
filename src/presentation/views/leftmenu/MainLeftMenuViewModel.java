package presentation.views.leftmenu;

import business.services.GameService;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import presentation.core.ViewManager;
import presentation.core.Views;

import java.util.Objects;

public class MainLeftMenuViewModel
{
    private final GameService gameService;

    public MainLeftMenuViewModel(GameService gameService)
    {
        this.gameService = gameService;
    }

    public void logoClicked(Button[] buttons)
    {
        ViewManager.showView(Views.DASHBOARD);
        setActiveBtn(buttons, null);
    }

    public void portfolioClicked(Button[] buttons, Button portfolioBtn)
    {
        ViewManager.showView(Views.PORTFOLIO);
        setActiveBtn(buttons, portfolioBtn);
    }

    public void stockMarketClicked(Button[] buttons, Button stockMarketBtn)
    {
        ViewManager.showView(Views.STOCK_MARKET);
        setActiveBtn(buttons, stockMarketBtn);
    }

    public void transactionsClicked(Button[] buttons, Button transactionBtn)
    {
        ViewManager.showView(Views.TRANSACTION);
        setActiveBtn(buttons, transactionBtn);
    }

    public void exitClicked()
    {
        gameService.stopGame();
        Platform.exit();
    }

    public void setupMenu(VBox menu, Label[] labels)
    {
        for (Label label : labels) {
            label.setVisible(false);
            label.setManaged(false);
        }

        menu.setOnMouseEntered(_ -> {
            for (Label label : labels) {
                label.setVisible(true);
                label.setManaged(true);
            }
        });

        menu.setOnMouseExited(_ -> {
            for (Label label : labels) {
                label.setVisible(false);
                label.setManaged(false);
            }
        });
    }

    public void setButtonIcons(Button button, String normalIconPath, String activeIconPath)
    {
        button.setUserData(new ButtonIcons(normalIconPath, activeIconPath));
    }



    private ImageView getButtonIcon(Button button)
    {
        if (button == null || button.getGraphic() == null) {
            return null;
        }

        return findImageView(button.getGraphic());
    }

    private ImageView findImageView(Node node)
    {
        if (node instanceof ImageView imageView) {
            return imageView;
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                ImageView result = findImageView(child);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    private Image loadImage(String path)
    {
        return new Image(
                Objects.requireNonNull(
                        getClass().getResourceAsStream(path),
                        "Could not find resource: " + path
                )
        );
    }

    private void setActiveBtn(Button[] buttons, Button activeBtn)
    {
        for (Button button : buttons) {
            button.getStyleClass().remove("active");

            ImageView icon = getButtonIcon(button);
            if (icon == null) {
                continue;
            }

            Object userData = button.getUserData();
            if (!(userData instanceof ButtonIcons(String normalIconPath, String activeIconPath))) {
                System.out.println("No icon paths set for button: " + button);
                continue;
            }

            if (button == activeBtn) {
                button.getStyleClass().add("active");
                icon.setImage(loadImage(activeIconPath));
            } else {
                icon.setImage(loadImage(normalIconPath));
            }
        }
    }
}