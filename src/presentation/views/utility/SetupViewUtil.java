package presentation.views.utility;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Map;

public class SetupViewUtil
{

    public static <T> void setupTableView(
            TableView<T> tableView,
            Map<TableColumn<T, ?>, String> columnMappings
    )
    {
        for (Map.Entry<TableColumn<T, ?>, String> entry : columnMappings.entrySet())
        {
            TableColumn<T, ?> column = entry.getKey();
            String propertyName = entry.getValue();

            column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        }

        tableView.getSelectionModel().setCellSelectionEnabled(false);

        tableView.setRowFactory(_ -> {
            TableRow<T> row = new TableRow<>();
            row.setOnMousePressed(_ -> {
                if (!row.isEmpty())
                {
                    tableView.getSelectionModel().clearSelection();
                    tableView.getFocusModel().focus(-1);
                }
            });
            return row;
        });
    }
}