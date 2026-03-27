package presentation.views.mainmenu;

import presentation.core.ViewManager;
import presentation.core.Views;

public class MainMenuViewModel
{
    public void startGame() {
        ViewManager.showScene(Views.MAIN_LEFT_MENU);
    }
}
