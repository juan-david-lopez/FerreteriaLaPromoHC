package org.tiendaGUI.Controllers;

import LogicaTienda.Data.DataModel;
import LogicaTienda.Data.DataSerializer;
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
import java.net.URL;
import java.util.List;

public class HelloController {

    @FXML private Button botonEstadisticas, botonInventario, botonVentas;
    @FXML private Pane imagenDelMedio, panelPrincipal;
    @FXML private ToolBar panelAbajo;
    @FXML private Label panelArriba;

    /**
     * Inicializa el controlador principal del sistema.
     * Carga productos si aún no están en memoria.
     */
    @FXML
    public void initialize() {
        System.out.println("📦 HelloController inicializado.");
        cargarProductosSiNoEstanEnMemoria();
    }

    private void cargarProductosSiNoEstanEnMemoria() {
        if (DataModel.getProductos().isEmpty()) {
            System.out.println("📁 Cargando productos desde JSON...");
            DataSerializer serializer = new DataSerializer("productos.json");
            List<Productos> productos = serializer.deserializeData();
            if (productos != null) {
                DataModel.getProductos().setAll(productos);
                System.out.println("✅ Productos cargados: " + productos.size());
            } else {
                System.out.println("⚠️ No se pudieron cargar productos.");
            }
        } else {
            System.out.println("✅ Productos ya presentes en memoria: " + DataModel.getProductos().size());
        }
    }

    @FXML
    private void presionarBotonInventario(ActionEvent event) {
        cambiarVentana(event, "inventario-view.fxml", "Inventario");
    }

    @FXML
    private void presionarBotonEstadisticas(ActionEvent event) {
        cambiarVentana(event, "estadisticas-view.fxml", "Estadísticas");
    }

    @FXML
    private void presionarBotonVentas(ActionEvent event) {
        cambiarVentana(event, "ventas-view.fxml", "Ventas");
    }

    /**
     * Cambia de vista a otra interfaz FXML.
     */
    private void cambiarVentana(ActionEvent event, String fxmlFile, String titulo) {
        try {
            URL url = getClass().getResource("/org/tiendaGUI/" + fxmlFile);
            System.out.println("🔄 Cargando vista: " + fxmlFile + " → " + url);

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            // Si es Inventario, pasamos la lista
            Object controller = loader.getController();
            if (controller instanceof InventarioController) {
                ((InventarioController) controller).bindListaProductos(DataModel.getProductos());
            }

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();
            System.out.println("✅ Vista cargada: " + titulo);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista: " + fxmlFile, Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
