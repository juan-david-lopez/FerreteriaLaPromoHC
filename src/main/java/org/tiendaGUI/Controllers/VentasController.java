package org.tiendaGUI.Controllers;

import LogicaTienda.DataSerializer;
import LogicaTienda.DataModel;
import LogicaTienda.Model.Productos;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class VentasController implements Initializable {

    private final DataSerializer dataSerializer = new DataSerializer("productos.json");

    @FXML
    private Button btnVolver, btnVender, btnActualizar;
    @FXML
    private TableView<Productos> tablaNumero2;
    @FXML
    private TableColumn<Productos, String> columnaNombre;
    @FXML
    private TableColumn<Productos, Double> columnaPrecio;
    @FXML
    private TableColumn<Productos, Integer> columnaCantidad;
    @FXML
    private TableColumn<Productos, String> columnaId;

    /**
     * Cambia la vista actual a la indicada.
     */
    private void cambiarVentana(ActionEvent event, String fxmlFile, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/" + fxmlFile));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la vista: " + fxmlFile, Alert.AlertType.ERROR);
        }
    }


    /**
     * Carga los productos desde el JSON y actualiza la lista global.
     */
    private void cargarProductosDesdeJSON() {
        List<Productos> productosCargados = dataSerializer.deserializeData();
        if (productosCargados != null) {
            DataModel.getProductos().setAll(productosCargados);
        }
        tablaNumero2.setItems(DataModel.getProductos());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnaCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        columnaId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        cargarProductosDesdeJSON();
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
        cambiarVentana(event, "hello-view.fxml", "Menú Principal");
    }


    @FXML
    private void btnVenderAction(ActionEvent event) {
        Productos productoSeleccionado = tablaNumero2.getSelectionModel().getSelectedItem();
        // Verifica que se haya seleccionado un producto y que tenga cantidad mayor a 0
        if (productoSeleccionado == null || productoSeleccionado.getCantidad() <= 0) {
            mostrarAlerta("Error", "No se seleccionó ningún producto válido para vender.", Alert.AlertType.ERROR);
            return;
        }
        // Ejemplo: decrementa en 1 la cantidad del producto vendido
        productoSeleccionado.setCantidad(productoSeleccionado.getCantidad() - 1);

        // Guarda los cambios en el JSON
        dataSerializer.serializeData(DataModel.getProductos());

        // Actualiza la tabla para reflejar los cambios
        actualizarTabla();

        mostrarAlerta("Éxito", "Venta realizada correctamente.", Alert.AlertType.INFORMATION);
    }


    @FXML
    private void btnActualizarAction(ActionEvent event) {
        // Actualiza la tabla usando la lista global
        tablaNumero2.setItems(DataModel.getProductos());
        tablaNumero2.refresh();
    }

}
