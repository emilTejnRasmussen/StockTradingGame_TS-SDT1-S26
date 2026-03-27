package presentation.core;

import javafx.util.Callback;
import presentation.views.leftmenu.MainLeftMenuController;
import presentation.views.leftmenu.MainLeftMenuViewModel;
import presentation.views.mainmenu.MainMenuController;
import presentation.views.mainmenu.MainMenuViewModel;


public class ControllerFactory implements Callback<Class<?>, Object>
{

    @Override
    public Object call(Class<?> controllerType)
    {
        if (controllerType == MainMenuController.class) {
            MainMenuViewModel mainMenuViewModel = new MainMenuViewModel();

            return new MainMenuController(mainMenuViewModel);
        }

        if (controllerType == MainLeftMenuController.class) {
            MainLeftMenuViewModel mainLeftMenuViewModel = new MainLeftMenuViewModel();
            return new MainLeftMenuController(mainLeftMenuViewModel);
        }

        throw new RuntimeException("Controller of type '" + controllerType.getSimpleName() + "' is not supported!");
    }
}