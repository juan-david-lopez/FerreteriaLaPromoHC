package org.tiendaGUI.Controllers;

import LogicaTienda.Model.Productos;
import LogicaTienda.Services.ProductoService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloController {
    private static final Logger LOGGER = Logger.getLogger(HelloController.class.getName());
    
    @FXML private Button botonEstadisticas, botonInventario, botonVentas;
    @FXML private Pane imagenDelMedio, panelPrincipal;
    @FXML private ToolBar panelAbajo;
    @FXML private Label panelArriba;

    /**
     * Inicializa el controlador principal del sistema.
     * Carga productos desde MongoDB.
     */
    @FXML
    public void initialize() {
        LOGGER.info("📦 HelloController inicializado");
        cargarProductosDesdeMongoDB();
    }

    private void cargarProductosDesdeMongoDB() {
        new Thread(() -> {
            try {
                LOGGER.info("📡 Cargando productos desde MongoDB...");
                List<Productos> productos = ProductoService.obtenerTodosLosProductos();
                
                Platform.runLater(() -> {
                    if (productos != null && !productos.isEmpty()) {
                        LOGGER.info("✅ " + productos.size() + " productos cargados desde MongoDB");
                    } else {
                        LOGGER.info("ℹ️ No se encontraron productos en la base de datos");
                        mostrarAlerta("Información", "No hay productos disponibles en la base de datos.", AlertType.INFORMATION);
                    }
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "❌ Error al cargar productos desde MongoDB", e);
                Platform.runLater(() -> 
                    mostrarAlerta("Error de conexión", 
                                "No se pudieron cargar los productos desde la base de datos.\nError: " + e.getMessage(), 
                                AlertType.ERROR)
                );
            }
        }).start();
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

    @FXML
    private void presionarBotonEditorFacturas(ActionEvent event) {
        cambiarVentana(event, "editor-facturas-view.fxml", "Editor de Facturas");
    }

    /**
     * Cambia de vista a otra interfaz FXML.
     */
    private void cambiarVentana(ActionEvent event, String fxmlFile, String titulo) {
        try {
            URL url = getClass().getResource("/org/tiendaGUI/" + fxmlFile);
            if (url == null) {
                throw new IOException("No se pudo encontrar el archivo: " + fxmlFile);
            }
            
            LOGGER.info("🔄 Cargando vista: " + fxmlFile);
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            // Los controladores ahora cargarán sus propios datos desde MongoDB
            // No es necesario pasar la lista de productos

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();
            LOGGER.info("✅ Vista cargada: " + titulo);
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar la vista: " + fxmlFile, e);
            mostrarAlerta("Error", "No se pudo cargar la vista: " + fxmlFile + "\nError: " + e.getMessage(), AlertType.ERROR);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al cambiar de ventana", e);
            mostrarAlerta("Error", "Error inesperado al cambiar de ventana: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, AlertType tipo) {
        try {
            Platform.runLater(() -> {
                try {
                    Alert alerta = new Alert(tipo);
                    alerta.setTitle(titulo);
                    alerta.setHeaderText(null);
                    alerta.setContentText(mensaje);
                    alerta.showAndWait();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error al mostrar alerta", e);
                    // Si falla la alerta, al menos lo registramos
                    System.err.println(titulo + ": " + mensaje);
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al mostrar alerta", e);
            System.err.println("[ERROR] " + titulo + ": " + mensaje);
        }
    }
}
