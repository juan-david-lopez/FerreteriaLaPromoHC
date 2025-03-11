package org.tiendaGUI.Controllers;

import LogicaTienda.DataSerializer;
import LogicaTienda.Productos;
import LogicaTienda.formularioProduct;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class InventarioController implements Initializable {
    private ObservableList<Productos> productosLocales = FXCollections.observableArrayList();
    private final DataSerializer dataSerializer = new DataSerializer("productos.json");

    @FXML private Button btnNuevo, btnVolver, btnEliminar, btnActualizar;
    @FXML private TableView<Productos> tablaNumero1;
    @FXML private TableColumn<Productos, String> columnaNombre;
    @FXML private TableColumn<Productos, Double> columnaPrecio;
    @FXML private TableColumn<Productos, Integer> columnaCantidad;
    @FXML private TableColumn<Productos, String> columnaId;

    @FXML
    private void volver(ActionEvent event) {
        cambiarVentana(event, "hello-view.fxml", "Menú Principal");
    }

    @FXML
    private void PresionarBotonNuevo(ActionEvent event) {
        Platform.runLater(() -> {
            new formularioProduct("Nuevo Producto", productosLocales, false, dataSerializer, null).showAndWait();
            actualizarTabla();
        });
    }

    @FXML
    private void PresionarBotonEliminar(ActionEvent event) {
        Productos productoSeleccionado = tablaNumero1.getSelectionModel().getSelectedItem();

        if (productoSeleccionado == null) {
            mostrarAlerta("Error", "No se seleccionó ningún producto para eliminar.");
            return;
        }

        System.out.println("📂 Antes de eliminar: " + productosLocales);
        productosLocales.remove(productoSeleccionado);
        System.out.println("📂 Después de eliminar: " + productosLocales);

        // Si la lista está vacía, guardamos un JSON vacío para evitar que los productos reaparezcan
        dataSerializer.serializeData(productosLocales);
        System.out.println("✅ Datos guardados correctamente en productos.json");

        actualizarTabla();
        System.out.println("✅ Producto eliminado correctamente.");
    }


    @FXML
    private void PresionarBotonActualizar(ActionEvent event) {
        Productos productoSeleccionado = tablaNumero1.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            mostrarAlerta("Error", "No se seleccionó ningún producto para actualizar.");
            return;
        }

        Platform.runLater(() -> {
            new formularioProduct("Actualizar Producto", productosLocales, false, dataSerializer, productoSeleccionado)
                    .showAndWait();
            actualizarTabla();
        });
    }

    private void actualizarTabla() {
        productosLocales.setAll(dataSerializer.deserializeData());
        tablaNumero1.refresh();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnaCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        columnaId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));

        cargarProductosDesdeJSON();
    }

    private void cargarProductosDesdeJSON() {
        List<Productos> productosCargados = dataSerializer.deserializeData();
        if (productosCargados != null) {
            productosLocales.setAll(productosCargados);
        }
        tablaNumero1.setItems(productosLocales);
    }

    protected void cambiarVentana(ActionEvent event, String fxmlFile, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/" + fxmlFile));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la vista: " + fxmlFile);
        }
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
    public void bindListaProductos(ObservableList<Productos> productos) {
        this.productosLocales.setAll(productos);
        tablaNumero1.setItems(productosLocales);
        System.out.println("📦 Productos cargados en InventarioController: " + productosLocales.size());
    }
}
