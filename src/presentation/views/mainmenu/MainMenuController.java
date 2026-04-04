package presentation.views.mainmenu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainMenuController
{
    @FXML
    private Button startBtn;
    @FXML
    private Button continueBtn;

    private final MainMenuViewModel mainMenuViewModel;

    public void initialize() {
        if (mainMenuViewModel.hasGameStored())
        {
            continueBtn.setDisable(false);
            startBtn.setText("Start New Game");
        }
    }
    public MainMenuController(MainMenuViewModel mainMenuViewModel)
    {
        this.mainMenuViewModel = mainMenuViewModel;
    }

    @FXML
    public void handleStartGame()
    {
        mainMenuViewModel.startGame();
    }

    @FXML
    public void handleExit(ActionEvent actionEvent)
    {
        mainMenuViewModel.exitGame();
    }

    @FXML
    public void handleContinue()
    {
        mainMenuViewModel.continueGame();
    }
}
