package org.tiendaGUI.Controllers;

import LogicaTienda.DataSerializer;
import LogicaTienda.Productos;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloController {
    private ObservableList<Productos> listaProductos = FXCollections.observableArrayList();
    private DataSerializer dataSerializer;

    @FXML
    private Button botonEstadisticas, botonInventario, botonVentas;

    @FXML
    private Pane imagenDelMedio, panelPrincipal;

    @FXML
    private ToolBar panelAbajo;

    @FXML
    private Label panelArriba;

    @FXML
    public void initialize() {
        System.out.println("📦 HelloController inicializado.");
    }

    public void bindListaProductos(ObservableList<Productos> listaProductos) {
        this.listaProductos.setAll(listaProductos);
        System.out.println("📦 Lista de productos enlazada en HelloController: " + listaProductos.size());
    }

    @FXML
    private void presionarBotonInventario(ActionEvent event) {
        cambiarVentana("inventario-view.fxml", "Inventario", event);
    }

    @FXML
    private void presionarBotonEstadisticas(ActionEvent event) {
        cambiarVentana("estadisticas-view.fxml", "Estadísticas", event);
    }

    @FXML
    private void presionarBotonVentas(ActionEvent event) {
        cambiarVentana("ventas-view.fxml", "Ventas", event);
    }

    private void cambiarVentana(String fxmlFile, String title, ActionEvent event) {
        try {
            System.out.println("🔄 Cargando: " + fxmlFile);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/" + fxmlFile));
            Parent root = fxmlLoader.load();

            // Verificar si es el controlador de Inventario y pasarle la lista de productos
            Object controller = fxmlLoader.getController();
            if (controller instanceof InventarioController inventarioController) {
                inventarioController.bindListaProductos(listaProductos);
            }

            // Obtener la ventana actual y cambiar la escena
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Para depuración
            mostrarError("Error al cargar la vista", "No se pudo cargar: " + fxmlFile);
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
