package presentation.views.mainmenu;

import business.services.GameService;
import business.services.PortfolioService;
import javafx.application.Platform;
import presentation.core.ViewManager;
import presentation.core.Views;

public class MainMenuViewModel
{
    private final GameService gameService;
    private final PortfolioService portfolioService;

    public MainMenuViewModel(GameService gameService, PortfolioService portfolioService)
    {
        this.gameService = gameService;
        this.portfolioService = portfolioService;
    }

    public void startGame() {
        gameService.startGame();
        ViewManager.showScene(Views.MAIN_LEFT_MENU);
    }

    public void exitGame() {
        Platform.exit();
    }

    public void continueGame() {
        gameService.loadGame();
        ViewManager.showScene(Views.MAIN_LEFT_MENU);
    }

    public boolean hasGameStored()
    {
        return portfolioService.hasCreatedPortfolio();
    }
}
