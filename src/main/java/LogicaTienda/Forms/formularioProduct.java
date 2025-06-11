package LogicaTienda.Forms;

import LogicaTienda.Model.Productos;
import LogicaTienda.Services.ProductoService;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class formularioProduct extends Stage {
    private TextField idProductoField, nombreField, precioField, porcentajeGananciaField, cantidadField, stockField;
    private Button submitButton;
    private ObservableList<Productos> productos;
    private static final Logger LOGGER = Logger.getLogger(formularioProduct.class.getName());

    public formularioProduct(String titulo, ObservableList<Productos> productos, boolean esEliminacion, Object unused, Productos producto) {
        this.productos = productos;
        setTitle(titulo);
        initModality(Modality.APPLICATION_MODAL);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        idProductoField = new TextField();
        nombreField = new TextField();
        precioField = new TextField();
        porcentajeGananciaField = new TextField();
        cantidadField = new TextField();
        stockField = new TextField();

        grid.add(new Label("ID Producto:"), 0, 0);
        grid.add(idProductoField, 1, 0);
        grid.add(new Label("Nombre:"), 0, 1);
        grid.add(nombreField, 1, 1);

        if (!esEliminacion) {
            grid.add(new Label("Precio:"), 0, 2);
            grid.add(precioField, 1, 2);
            grid.add(new Label("% Ganancia:"), 0, 3);
            grid.add(porcentajeGananciaField, 1, 3);
            grid.add(new Label("Cantidad:"), 0, 4);
            grid.add(cantidadField, 1, 4);
            grid.add(new Label("Stock:"), 0, 5);
            grid.add(stockField, 1, 5);
        }

        submitButton = new Button(esEliminacion ? "Eliminar" : "Guardar");
        grid.add(submitButton, 1, 6);

        // Si estamos en edición, llenamos los datos y deshabilitamos ID
        if (producto != null) {
            idProductoField.setText(producto.getIdProducto());
            nombreField.setText(producto.getNombre());
            precioField.setText(String.valueOf(producto.getPrecio()));
            porcentajeGananciaField.setText(String.valueOf(producto.getPorcentajeGanancia()));
            cantidadField.setText(String.valueOf(producto.getCantidad()));
            stockField.setText(String.valueOf(producto.getStock()));
            idProductoField.setDisable(true);
        }

        // Si estamos eliminando, solo permitir editar el ID
        if (esEliminacion) {
            nombreField.setDisable(true);
            precioField.setDisable(true);
            porcentajeGananciaField.setDisable(true);
            cantidadField.setDisable(true);
            stockField.setDisable(true);
        }

        submitButton.setOnAction(e -> {
            try {
                boolean cambiosRealizados = esEliminacion ? eliminarProducto() : actualizarOAgregarProducto(producto);
                if (cambiosRealizados) {
                    mostrarAlerta("Éxito", "Operación realizada correctamente.", Alert.AlertType.INFORMATION);
                    close();
                }
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error en la operación del formulario", ex);
                mostrarAlerta("Error", "Ocurrió un error: " + ex.getMessage(), Alert.AlertType.ERROR);
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
        double porcentajeGanancia = 20.0; // Valor por defecto del 20%
        int cantidad, stock;

        if (!validarCamposNumericos(precioField, cantidadField, stockField)) {
            return false;
        }

        precio = Double.parseDouble(precioField.getText().trim());
        String porcentajeText = porcentajeGananciaField.getText().trim();
        if (!porcentajeText.isEmpty()) {
            try {
                porcentajeGanancia = Double.parseDouble(porcentajeText);
                if (porcentajeGanancia < 0) {
                    return mostrarError("El porcentaje de ganancia no puede ser negativo.");
                }
            } catch (NumberFormatException e) {
                return mostrarError("El porcentaje de ganancia debe ser un número válido.");
            }
        }
        cantidad = Integer.parseInt(cantidadField.getText().trim());
        stock = Integer.parseInt(stockField.getText().trim());

        try {
            if (productoExistente != null) {
                // Modo edición
                productoExistente.setNombre(nombre);
                productoExistente.setPrecio(precio);
                productoExistente.setPorcentajeGanancia(porcentajeGanancia);
                productoExistente.setCantidad(cantidad);
                productoExistente.setStock(stock);
                productoExistente.calcularPrecioVenta();
                
                // Actualizar en MongoDB
                try {
                    ProductoService.actualizarProducto(productoExistente);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error al actualizar el producto", e);
                    return mostrarError("No se pudo actualizar el producto en la base de datos: " + e.getMessage());
                }
            } else {
                // Modo creación
                String idProducto = idProductoField.getText().trim();
                if (idProducto.isEmpty()) {
                    return mostrarError("El ID del producto no puede estar vacío.");
                }

                // Verificar si el ID ya existe en MongoDB
                if (ProductoService.buscarProductoPorId(idProducto) != null) {
                    return mostrarError("Ya existe un producto con este ID.");
                }

                Productos nuevoProducto = new Productos(idProducto, nombre, precio, porcentajeGanancia, cantidad, stock);
                nuevoProducto.calcularPrecioVenta();
                
                // Guardar en MongoDB
                try {
                    ProductoService.guardarProducto(nuevoProducto);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error al guardar el producto", e);
                    return mostrarError("No se pudo guardar el producto en la base de datos: " + e.getMessage());
                }
                
                // Actualizar la lista local si es necesario
                if (productos != null) {
                    productos.add(nuevoProducto);
                }
            }
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar el producto", e);
            return mostrarError("Error al guardar el producto: " + e.getMessage());
        }
    }

    private boolean eliminarProducto() {
        String idProducto = idProductoField.getText().trim();
        if (idProducto.isEmpty()) {
            return mostrarError("Debes ingresar un ID de producto válido.");
        }

        try {
            // Verificar si el producto existe
            if (ProductoService.buscarProductoPorId(idProducto) == null) {
                return mostrarError("No se encontró un producto con ese ID.");
            }

            // Mostrar confirmación
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Está seguro de eliminar este producto?");
            confirmacion.setContentText("Esta acción no se puede deshacer.");

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                // Eliminar de MongoDB
                ProductoService.eliminarProducto(idProducto);
                
                // Actualizar la lista local si es necesario
                if (productos != null) {
                    productos.removeIf(p -> p.getIdProducto().equals(idProducto));
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar el producto", e);
            return mostrarError("Error al eliminar el producto: " + e.getMessage());
        }
    }

    private boolean validarCamposNumericos(TextField precioField, TextField cantidadField, TextField stockField) {
        try {
            // Validar que los campos no estén vacíos
            if (precioField.getText().trim().isEmpty() || 
                cantidadField.getText().trim().isEmpty() || 
                stockField.getText().trim().isEmpty()) {
                return mostrarError("Todos los campos numéricos son obligatorios.");
            }
            
            double precio = Double.parseDouble(precioField.getText().trim());
            double porcentajeGanancia = 0;
            if (!porcentajeGananciaField.getText().trim().isEmpty()) {
                porcentajeGanancia = Double.parseDouble(porcentajeGananciaField.getText().trim());
            }
            int cantidad = Integer.parseInt(cantidadField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());

            if (precio < 0 || porcentajeGanancia < 0 || cantidad < 0 || stock < 0) {
                return mostrarError("Los valores numéricos deben ser positivos.");
            }
            return true;
        } catch (NumberFormatException e) {
            return mostrarError("Datos inválidos. Verifica los valores numéricos.");
        }
    }

    private boolean mostrarError(String mensaje) {
        mostrarAlerta("Error", mensaje, Alert.AlertType.ERROR);
        return false;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        mostrarAlerta(titulo, mensaje, Alert.AlertType.INFORMATION);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        try {
            Alert alerta = new Alert(tipo);
            alerta.setTitle(titulo);
            alerta.setHeaderText(null);
            alerta.setContentText(mensaje);
            alerta.showAndWait();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al mostrar la alerta", e);
            // Si hay un error mostrando la alerta, al menos mostrarlo en consola
            System.err.println(titulo + ": " + mensaje);
        }
    }
}
