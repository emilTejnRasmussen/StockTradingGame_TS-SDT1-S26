package presentation.core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class ViewManager
{
    private static Stage stage;
    private static StackPane rootLayout;
    private static BorderPane mainLayout;
    private static final ControllerFactory controllerFactory = new ControllerFactory();

    public static void setStage(Stage primaryStage)
    {
        stage = primaryStage;
        stage.initStyle(StageStyle.TRANSPARENT);
    }

    public static void showScene(Views view)
    {
        try
        {
            Parent root = load(view);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

            if (root instanceof StackPane stackPane)
            {
                rootLayout = stackPane;
                mainLayout = findBorderPane(stackPane);
            }
            else if (root instanceof BorderPane borderPane)
            {
                mainLayout = borderPane;
                rootLayout = null;
            }
        }
        catch (IOException e)
        {
            showError(view, e);
            e.printStackTrace();
        }
    }

    public static void setCenter(Views view)
    {
        verifyMainLayoutIsNotNull();

        try
        {
            Parent centerView = load(view);
            mainLayout.setCenter(centerView);
        }
        catch (IOException e)
        {
            showError(view, e);
            e.printStackTrace();
        }
    }

    private static Parent load(Views view) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(ViewManager.class.getResource(view.getView()))
        );
        loader.setControllerFactory(controllerFactory);
        return loader.load();
    }

    private static BorderPane findBorderPane(StackPane stackPane)
    {
        return stackPane.getChildren().stream()
                .filter(node -> node instanceof BorderPane)
                .map(node -> (BorderPane) node)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No BorderPane found inside StackPane root."));
    }

    private static void showError(Views view, Exception e)
    {
        Alert error = new Alert(Alert.AlertType.ERROR, "Cannot find view: " + view);
        error.show();
        System.out.println(e.getMessage());
    }

    private static void verifyMainLayoutIsNotNull()
    {
        if (mainLayout == null)
        {
            throw new IllegalStateException("Main layout has not been loaded yet.");
        }
    }
}