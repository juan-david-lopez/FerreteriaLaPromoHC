package LogicaTienda.Data;

import LogicaTienda.Model.Productos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataModel {
    private static final ObservableList<Productos> productos = FXCollections.observableArrayList();
    public static ObservableList<Productos> getProductos() {
        return productos;
    }

}
