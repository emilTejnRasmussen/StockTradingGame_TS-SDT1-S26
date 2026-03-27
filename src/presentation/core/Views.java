package presentation.core;

public enum Views
{
    MAIN_MENU("MainMenu"),
    MAIN_LEFT_MENU("MainLeftMenu");


    private final String view;

    Views(String view){
        this.view = view;
    }

    public String getView() {
        return "/fxml/" + this.view + ".fxml";
    }
}
