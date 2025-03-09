package org.tiendaGUI.Controllers;

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
import java.util.List;
import LogicaTienda.Productos;

public class HelloController {
    private ObservableList<Productos> listaProductos = FXCollections.observableArrayList();
    @FXML
    private Button BotonEstadisticas;

    @FXML
    private Button BotonInventario;

    @FXML
    private Button BotonVentas;

    @FXML
    private Pane ImagenDelMedio;

    @FXML
    private ToolBar PanelAbajo;

    @FXML
    private Pane PanelPrincipal;

    @FXML
    private Label panelArriba;


    @FXML
    private void PresionarBotonInventario(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/inventario-view.fxml"));
            Parent root = fxmlLoader.load();

            // Obtener la ventana actual y cambiar la escena
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Inventario");  // Opcionalmente, cambia el título
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error: No se pudo cargar inventario-view.fxml");
        }
    }


    @FXML
    private void PresionarBotonEstadisticas() {
        cambiarVentana("estadisticas-view.fxml", "Estadísticas");
    }

    @FXML
    private void PresionarBotonVentas() {
        cambiarVentana("ventas-view.fxml", "Ventas");
    }

    private void cambiarVentana(String fxmlFile, String title) {
        Platform.runLater(() -> {
            try {
                System.out.println("Cargando FXML: " + fxmlFile);
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/" + fxmlFile));
                Parent root = fxmlLoader.load();

                // Crear nueva ventana
                Stage stage = new Stage();
                stage.setTitle(title);
                stage.setScene(new Scene(root));
                stage.show();

                // Cerrar la ventana actual solo si la nueva ventana se abre con éxito
                if (PanelPrincipal != null && PanelPrincipal.getScene() != null) {
                    Stage currentStage = (Stage) PanelPrincipal.getScene().getWindow();
                    currentStage.close();
                }
            } catch (IOException e) {
                mostrarError("Error al cargar la vista", "No se pudo cargar el archivo: " + fxmlFile);
            }
        });
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void initialize() {
        // Puedes agregar inicializaciones si es necesario
    }


    public void setListaProductos(ObservableList<Productos> listaProductos) {
        this.listaProductos.setAll(listaProductos);
    }
}
