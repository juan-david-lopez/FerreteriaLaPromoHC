package app;

import LogicaTienda.Model.Productos;
import LogicaTienda.Services.ProductoService;
import LogicaTienda.Utils.MongoDBInitializer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloApplication extends Application {
    private static final Logger LOGGER = Logger.getLogger(HelloApplication.class.getName());

    @Override
    public void start(Stage stage) {
        try {
            // Inicializar MongoDB
            MongoDBInitializer.initialize();
            
            // Cargar productos desde MongoDB
            List<Productos> productosCargados = ProductoService.obtenerTodosLosProductos();
            System.out.println("✅ Productos cargados al iniciar la aplicación: " + productosCargados.size());

            // Cargar la interfaz de usuario
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/hello-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
                stage.setTitle("Ferretería La Promo H&C");
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al cargar la interfaz de usuario", e);
                showErrorDialog("Error de interfaz", "No se pudo cargar la interfaz de usuario: " + e.getMessage());
                System.exit(1);
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al iniciar la aplicación", e);
            showErrorDialog("Error al iniciar la aplicación", e.getMessage());
        }
    }

    @Override
    public void stop() {
        try {
            // Cerrar la conexión con MongoDB
            MongoDBInitializer.shutdown();
            LOGGER.info("✅ Conexión con MongoDB cerrada correctamente");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al cerrar la conexión con MongoDB", e);
        }
        System.out.println("Aplicación cerrada");
    }

    private void showErrorDialog(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
