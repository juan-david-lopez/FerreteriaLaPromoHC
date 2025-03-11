package org.tiendaGUI.Controllers;

import LogicaTienda.DataSerializer;
import LogicaTienda.Productos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class VentasController implements Initializable {
    private ObservableList<Productos> productosLocales = FXCollections.observableArrayList();
    private ObservableList<Productos> productosSeleccionados = FXCollections.observableArrayList();
    private final DataSerializer dataSerializer = new DataSerializer("productos.json");

    @FXML
    private InventarioController inventarioController;
    @FXML private Button BtnVolver,BotonVender,BtonActualizar;
    @FXML private TableView<Productos> tablaNumero2;
    @FXML private TableColumn<Productos, String> columnaNombre;
    @FXML private TableColumn<Productos, Double> columnaPrecio;
    @FXML private TableColumn<Productos, Integer> columnaCantidad;
    @FXML private TableColumn<Productos, String> columnaId;
    @FXML
    private void volver(ActionEvent event) {
        cambiarVentana(event, "hello-view.fxml", "Menú Principal");
    }

    private void cambiarVentana(ActionEvent event, String fxmlFile, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/" + fxmlFile));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la vista: " + fxmlFile,Alert.AlertType.ERROR);
        }
    }

    private void cargarProductosDesdeJSON() {
        List<Productos> productosCargados = dataSerializer.deserializeData();
        if (productosCargados != null) {
            productosLocales.setAll(productosCargados);
        }
        tablaNumero2.setItems(productosLocales);
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
        productosLocales.setAll(dataSerializer.deserializeData());
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
    private void botonVender(ActionEvent event){
        Productos productoSeleccionado = tablaNumero2.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null && productoSeleccionado.getCantidad()<=0) {
            mostrarAlerta("Error", "No se seleccionó ningún producto para actualizar.", Alert.AlertType.ERROR);
            return;
        }

        mostrarAlerta("Éxito", "Operación realizada correctamente.", Alert.AlertType.INFORMATION);
    }
}
