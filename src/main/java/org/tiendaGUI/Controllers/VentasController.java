package org.tiendaGUI.Controllers;

import LogicaTienda.Data.DataSerializer;
import LogicaTienda.Data.DataModel;
import LogicaTienda.Model.Productos;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class VentasController {

    private final DataSerializer dataSerializer = new DataSerializer("productos.json");

    @FXML private Button btnVolver, btnVender, btnActualizar, btnIrCarrito;
    @FXML private TableView<Productos> tablaNumero2;
    @FXML private TableColumn<Productos, String> columnaNombre;
    @FXML private TableColumn<Productos, Double> columnaPrecio;
    @FXML private TableColumn<Productos, Integer> columnaCantidad;
    @FXML private TableColumn<Productos, Integer> columnaStock;
    @FXML private TableColumn<Productos, String> columnaId;

    private void cambiarVentana(ActionEvent event, String fxmlFile, String title) {
        try {
            // Verifica que la URL no sea null
            URL url = getClass().getResource("/org/tiendaGUI/" + fxmlFile);
            System.out.println("URL de " + fxmlFile + " → " + url);

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
            System.out.println("✅ Ventana cargada: " + title);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista: " + fxmlFile, Alert.AlertType.ERROR);
        }
    }


    private void cargarProductosDesdeJSON() {
        List<Productos> productosCargados = dataSerializer.deserializeData();
        if (productosCargados != null) {
            DataModel.getProductos().setAll(productosCargados);
        }
        tablaNumero2.setItems(DataModel.getProductos());
    }

    @FXML
    private void actualizarTabla() {
        cargarProductosDesdeJSON();
        tablaNumero2.refresh();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void btnVolverAction(ActionEvent event) {
        System.out.println("🔙 Volviendo al menú principal...");
        cambiarVentana(event, "hello-view.fxml", "Menú Principal");
    }

    @FXML
    private void btnVenderAction(ActionEvent event) {
        Productos productoSeleccionado = tablaNumero2.getSelectionModel().getSelectedItem();

        if (productoSeleccionado == null) {
            mostrarAlerta("Error", "No se seleccionó ningún producto para vender.", Alert.AlertType.ERROR);
            return;
        }

        // Pregunta al usuario cuántas unidades desea vender
        TextInputDialog dialogoCantidad = new TextInputDialog("1");
        dialogoCantidad.setTitle("Cantidad a Vender");
        dialogoCantidad.setHeaderText("Venta de Producto: " + productoSeleccionado.getNombre());
        dialogoCantidad.setContentText("Ingrese la cantidad a vender:");

        dialogoCantidad.showAndWait().ifPresent(input -> {
            try {
                int cantidadDeseada = Integer.parseInt(input);

                if (cantidadDeseada <= 0) {
                    mostrarAlerta("Error", "La cantidad debe ser mayor a cero.", Alert.AlertType.ERROR);
                    return;
                }

                int cantidadMostrador = productoSeleccionado.getCantidad();
                int stockAlmacen = productoSeleccionado.getStock();

                // Verifica si hay suficientes productos disponibles (mostrador + bodega)
                if (cantidadDeseada > (cantidadMostrador + stockAlmacen)) {
                    mostrarAlerta("Error", "No hay suficiente inventario para completar la venta.", Alert.AlertType.ERROR);
                    return;
                }

                // Lógica: usar primero del mostrador
                int restante = cantidadDeseada;

                if (cantidadMostrador >= restante) {
                    productoSeleccionado.setCantidad(cantidadMostrador - restante);
                } else {
                    // Tomamos lo que hay en el mostrador
                    restante -= cantidadMostrador;
                    productoSeleccionado.setCantidad(0);

                    // Tomamos el resto del stock
                    productoSeleccionado.setStock(stockAlmacen - restante);
                }

                // Agregamos al carrito lo que se vendió
                Productos productoEnCarrito = new Productos(
                        productoSeleccionado.getIdProducto(),
                        productoSeleccionado.getNombre(),
                        productoSeleccionado.getPrecio(),
                        cantidadDeseada,
                        0 // stock en el carrito no aplica
                );
                DataModel.getCarritoVentas().add(productoEnCarrito);

                // Guardamos y actualizamos
                dataSerializer.serializeData(DataModel.getProductos());
                actualizarTabla();

                mostrarAlerta("Éxito", "Se vendieron " + cantidadDeseada + " unidades correctamente.", Alert.AlertType.INFORMATION);

            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "Ingrese un número válido.", Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void btnActualizarAction(ActionEvent event) {
        tablaNumero2.setItems(DataModel.getProductos());
        tablaNumero2.refresh();
    }

    @FXML
    private void btnIrCarritoAction(ActionEvent event) {
        System.out.println("🔄 Cambiando a carrito...");
        cambiarVentana(event, "pedido-view.fxml", "Carrito de Compras");
    }
    @FXML
    public void initialize() {
        System.out.println("📦 Inicializando controlador de ventas.");
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnaCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad")); // Mostrador
        columnaStock.setCellValueFactory(new PropertyValueFactory<>("stock")); // Almacén
        columnaId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        cargarProductosDesdeJSON();
    }

}
