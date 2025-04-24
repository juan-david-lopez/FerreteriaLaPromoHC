package LogicaTienda.Data;

import LogicaTienda.Model.Pago;
import LogicaTienda.Model.Productos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataModel {
    private static final ObservableList<Productos> productos = FXCollections.observableArrayList();
    public static ObservableList<Productos> getProductos() {
        return productos;
    }
    private static ObservableList<Productos> carritoVentas = FXCollections.observableArrayList();

    public static ObservableList<Productos> getCarritoVentas() {return carritoVentas;}
    private static final ObservableList<Pago> pagos = FXCollections.observableArrayList();

    public static ObservableList<Pago> getPagos() {return pagos;}

    public static double calcularTotalCarrito() {
        double total = 0.0;
        for (Productos producto : carritoVentas) {
            total += producto.getPrecio() * producto.getCantidad();
        }
        return total;
    }
}
