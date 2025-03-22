package org.tiendaGUI.Controllers;

import LogicaTienda.DataSerializer;
import LogicaTienda.DataModel;
import LogicaTienda.Model.Productos;
import LogicaTienda.formularioProduct;
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
import java.util.ResourceBundle;

public class InventarioController implements Initializable {
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
    private void presionarBotonNuevo(ActionEvent event) {
        // Usa la lista global para el formulario
        new formularioProduct("Nuevo Producto", DataModel.getProductos(), false, dataSerializer, null)
                .showAndWait();
        actualizarTabla();
    }

    @FXML
    private void presionarBotonEliminar(ActionEvent event) {
        Productos productoSeleccionado = tablaNumero1.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            mostrarAlerta("Error", "No se seleccionó ningún producto para eliminar.");
            return;
        }
        System.out.println("📂 Antes de eliminar: " + DataModel.getProductos());
        DataModel.getProductos().remove(productoSeleccionado);
        System.out.println("📂 Después de eliminar: " + DataModel.getProductos());

        dataSerializer.serializeData(DataModel.getProductos());
        System.out.println("✅ Datos guardados correctamente en productos.json");

        actualizarTabla();
        System.out.println("✅ Producto eliminado correctamente.");
    }

    @FXML
    private void presionarBotonActualizar(ActionEvent event) {
        Productos productoSeleccionado = tablaNumero1.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            mostrarAlerta("Error", "No se seleccionó ningún producto para actualizar.", Alert.AlertType.ERROR);
            return;
        }
        // Abre el formulario para actualizar el producto seleccionado
        new formularioProduct("Actualizar Producto", DataModel.getProductos(), false, dataSerializer, productoSeleccionado)
                .showAndWait();
        actualizarTabla();
    }

    /**
     * Actualiza la tabla usando la lista global (sin recargar del JSON)
     */
    private void actualizarTabla() {
        tablaNumero1.setItems(DataModel.getProductos());
        tablaNumero1.refresh();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnaCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        columnaId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));

        // Asigna directamente la lista global ya cargada en HelloController
        tablaNumero1.setItems(DataModel.getProductos());
        tablaNumero1.refresh();
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
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista: " + fxmlFile + "\nDetalles: " + e.getMessage());
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

    // Este método es útil si en algún momento necesitas inyectar la lista global a este controlador.
    public void bindListaProductos(ObservableList<Productos> productos) {
        tablaNumero1.setItems(productos);
        System.out.println("📦 Productos cargados en InventarioController: " + productos.size());
    }
}
