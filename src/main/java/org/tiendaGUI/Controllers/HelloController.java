package org.tiendaGUI.Controllers;

import LogicaTienda.DataModel;
import LogicaTienda.DataSerializer;
import LogicaTienda.Model.Productos;
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

public class HelloController {

    @FXML
    private Button botonEstadisticas, botonInventario, botonVentas;

    @FXML
    private Pane imagenDelMedio, panelPrincipal;

    @FXML
    private ToolBar panelAbajo;

    @FXML
    private Label panelArriba;

    /**
     * Se ejecuta al inicializar el controlador. Carga el JSON solo si el DataModel está vacío.
     */
    @FXML
    public void initialize() {
        System.out.println("📦 HelloController inicializado.");
        // Carga los datos solo si aún no se han cargado
        if (DataModel.getProductos().isEmpty()) {
            DataSerializer dataSerializer = new DataSerializer("productos.json");
            List<Productos> productosCargados = dataSerializer.deserializeData();
            if (productosCargados != null) {
                DataModel.getProductos().setAll(productosCargados);
            }
            System.out.println("📦 Productos cargados en DataModel: " + DataModel.getProductos().size());
        } else {
            System.out.println("📦 DataModel ya contiene " + DataModel.getProductos().size() + " producto(s).");
        }
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

    /**
     * Cambia la vista actual a la indicada en el FXML.
     *
     * @param fxmlFile nombre del archivo FXML (se asume que está en /org/tiendaGUI/)
     * @param title    título de la ventana
     * @param event    evento que origina el cambio de vista
     */
    private void cambiarVentana(String fxmlFile, String title, ActionEvent event) {
        try {
            System.out.println("🔄 Cargando: " + fxmlFile);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/" + fxmlFile));
            Parent root = fxmlLoader.load();

            // Si se carga el controlador de inventario, se le pasa la lista global
            Object controller = fxmlLoader.getController();
            if (controller instanceof InventarioController) {
                ((InventarioController) controller).bindListaProductos(DataModel.getProductos());
            }

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
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
