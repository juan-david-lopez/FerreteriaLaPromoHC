package LogicaTienda;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.ObservableList;

public class formularioProduct extends Stage {
    private TextField idProductoField, nombreField, precioField, cantidadField, stockField;
    private Button submitButton;
    private ObservableList<Productos> productos;
    private DataSerializer dataSerializer;  // Serializador JSON

    public formularioProduct(String titulo, ObservableList<Productos> productos, boolean esEliminacion, DataSerializer dataSerializer) {
        this.productos = productos;
        this.dataSerializer = dataSerializer;
        setTitle(titulo);
        initModality(Modality.APPLICATION_MODAL);  // Ventana modal

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        idProductoField = new TextField();
        nombreField = new TextField();
        precioField = new TextField();
        cantidadField = new TextField();
        stockField = new TextField();

        grid.add(new Label("ID Producto:"), 0, 0);
        grid.add(idProductoField, 1, 0);
        grid.add(new Label("Nombre:"), 0, 1);
        grid.add(nombreField, 1, 1);

        if (!esEliminacion) {
            grid.add(new Label("Precio:"), 0, 2);
            grid.add(precioField, 1, 2);
            grid.add(new Label("Cantidad:"), 0, 3);
            grid.add(cantidadField, 1, 3);
            grid.add(new Label("Stock:"), 0, 4);
            grid.add(stockField, 1, 4);
        }

        submitButton = new Button(esEliminacion ? "Eliminar" : "Guardar");
        grid.add(submitButton, 1, 5);

        submitButton.setOnAction(e -> {
            if (esEliminacion) {
                eliminarProducto();
            } else {
                actualizarOAgregarProducto();
            }
            dataSerializer.serializeData(productos);  // 🔥 Guarda cambios en JSON
            close();  // Cierra la ventana después de la acción
        });

        Scene scene = new Scene(grid, 350, 250);
        setScene(scene);
    }

    private void actualizarOAgregarProducto() {
        try {
            String idProducto = idProductoField.getText().trim();
            String nombre = nombreField.getText().trim();
            double precio = Double.parseDouble(precioField.getText().trim());
            int cantidad = Integer.parseInt(cantidadField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());

            boolean encontrado = false;
            for (Productos producto : productos) {
                if (producto.getIdProducto().equals(idProducto) && producto.getNombre().equals(nombre)) {
                    producto.setCantidad(cantidad);
                    producto.setStock(stock);
                    producto.setPrecio(precio);
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                productos.add(new Productos(idProducto, nombre, precio, cantidad, stock));
            }

        } catch (NumberFormatException ex) {
            mostrarAlerta("Error", "Datos inválidos. Verifica los valores numéricos.");
        }
    }

    private void eliminarProducto() {
        String idProducto = idProductoField.getText().trim();
        String nombre = nombreField.getText().trim();
        productos.removeIf(p -> p.getIdProducto().equals(idProducto) && p.getNombre().equals(nombre));
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
