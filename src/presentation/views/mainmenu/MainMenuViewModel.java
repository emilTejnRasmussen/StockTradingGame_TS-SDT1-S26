package presentation.views.mainmenu;

import business.services.GameService;
import presentation.core.ViewManager;
import presentation.core.Views;

public class MainMenuViewModel
{
    private final GameService gameService;

    public MainMenuViewModel(GameService gameService)
    {
        this.gameService = gameService;
    }

    public void startGame() {
        gameService.startGame();
        ViewManager.showScene(Views.MAIN_LEFT_MENU);
    }
}
