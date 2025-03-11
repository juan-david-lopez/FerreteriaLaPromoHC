package LogicaTienda;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import java.util.Optional;

public class formularioProduct extends Stage {
    private TextField idProductoField, nombreField, precioField, cantidadField, stockField;
    private Button submitButton;
    private ObservableList<Productos> productos;
    private DataSerializer dataSerializer;

    public formularioProduct(String titulo, ObservableList<Productos> productos, boolean esEliminacion, DataSerializer dataSerializer, Productos producto) {
        this.productos = productos;
        this.dataSerializer = dataSerializer;
        setTitle(titulo);
        initModality(Modality.APPLICATION_MODAL);

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

        // Si estamos en edición, llenamos los datos y deshabilitamos ID
        if (producto != null) {
            idProductoField.setText(producto.getIdProducto());
            nombreField.setText(producto.getNombre());
            precioField.setText(String.valueOf(producto.getPrecio()));
            cantidadField.setText(String.valueOf(producto.getCantidad()));
            stockField.setText(String.valueOf(producto.getStock()));
            idProductoField.setDisable(true);
        }

        // Si estamos eliminando, solo permitir editar el ID
        if (esEliminacion) {
            nombreField.setDisable(true);
            precioField.setDisable(true);
            cantidadField.setDisable(true);
            stockField.setDisable(true);
        }

        submitButton.setOnAction(e -> {
            boolean cambiosRealizados = esEliminacion ? eliminarProducto() : actualizarOAgregarProducto(producto);

            if (cambiosRealizados) {
                dataSerializer.serializeData(productos);
                mostrarAlerta("Éxito", "Operación realizada correctamente.", Alert.AlertType.INFORMATION);
                close();
            }
        });

        setScene(new Scene(grid, 350, 250));
    }

    private boolean actualizarOAgregarProducto(Productos productoExistente) {
        String nombre = nombreField.getText().trim();
        if (nombre.isEmpty()) {
            return mostrarError("El nombre no puede estar vacío.");
        }

        // Validación de valores numéricos
        double precio;
        int cantidad, stock;

        if (!validarCamposNumericos(precioField, cantidadField, stockField)) {
            return false;
        }

        precio = Double.parseDouble(precioField.getText().trim());
        cantidad = Integer.parseInt(cantidadField.getText().trim());
        stock = Integer.parseInt(stockField.getText().trim());

        if (productoExistente != null) {
            // Modo edición
            productoExistente.setNombre(nombre);
            productoExistente.setPrecio(precio);
            productoExistente.setCantidad(cantidad);
            productoExistente.setStock(stock);
        } else {
            // Modo creación
            String idProducto = idProductoField.getText().trim();
            if (idProducto.isEmpty()) {
                return mostrarError("El ID del producto no puede estar vacío.");
            }

            if (productos.stream().anyMatch(p -> p.getIdProducto().equals(idProducto))) {
                return mostrarError("Ya existe un producto con este ID.");
            }

            productos.add(new Productos(idProducto, nombre, precio, cantidad, stock));
        }
        return true;
    }

    private boolean eliminarProducto() {
        String idProducto = idProductoField.getText().trim();
        if (idProducto.isEmpty()) {
            return mostrarError("Debes ingresar un ID de producto válido.");
        }

        Optional<Productos> productoAEliminar = productos.stream()
                .filter(p -> p.getIdProducto().equals(idProducto))
                .findFirst();

        if (productoAEliminar.isPresent()) {
            productos.remove(productoAEliminar.get());
            System.out.println("✅ Producto eliminado correctamente: " + idProducto);
            return true;
        } else {
            return mostrarError("No se encontró un producto con ese ID.");
        }
    }

    private boolean validarCamposNumericos(TextField precioField, TextField cantidadField, TextField stockField) {
        try {
            double precio = Double.parseDouble(precioField.getText().trim());
            int cantidad = Integer.parseInt(cantidadField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());

            if (precio < 0 || cantidad < 0 || stock < 0) {
                return mostrarError("Los valores numéricos deben ser positivos.");
            }
            return true;
        } catch (NumberFormatException e) {
            return mostrarError("Datos inválidos. Verifica los valores numéricos.");
        }
    }

    private boolean mostrarError(String mensaje) {
        mostrarAlerta("Error", mensaje);
        return false;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        mostrarAlerta(titulo, mensaje, Alert.AlertType.ERROR);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
