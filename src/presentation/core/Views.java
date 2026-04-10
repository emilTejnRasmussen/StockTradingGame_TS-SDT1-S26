package presentation.core;

public enum Views
{
    MAIN_MENU("MainMenu"),
    MAIN_APPLICATION("MainLeftMenu"),

    DASHBOARD("Dashboard"),
    PORTFOLIO("Portfolio"),
    STOCK_MARKET("StockMarket"),
    TRANSACTION("Transactions");


    private final String view;

    Views(String view){
        this.view = view;
    }

    public String getView() {
        return "/fxml/" + this.view + ".fxml";
    }
}
