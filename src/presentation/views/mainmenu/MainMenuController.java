package presentation.views.mainmenu;

import javafx.fxml.FXML;

public class MainMenuController
{
    private final MainMenuViewModel mainMenuViewModel;

    public MainMenuController(MainMenuViewModel mainMenuViewModel)
    {
        this.mainMenuViewModel = mainMenuViewModel;
    }

    @FXML
    public void handleStartGame()
    {
        mainMenuViewModel.startGame();
    }
}
