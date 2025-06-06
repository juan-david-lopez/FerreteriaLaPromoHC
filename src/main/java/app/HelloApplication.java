package app;

import LogicaTienda.Data.DataModel;
import LogicaTienda.Data.DataSerializer;
import LogicaTienda.Model.Productos;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloApplication extends Application {
    private static final Logger LOGGER = Logger.getLogger(HelloApplication.class.getName());

    private DataSerializer serializer;

    @Override
    public void start(Stage stage) {
        serializer = new DataSerializer("productos.json");

        // Cargar productos (ya no es necesario validar null si DataSerializer lo maneja)
        List<Productos> productosCargados = serializer.deserializeData();
        ObservableList<Productos> listaProductos = FXCollections.observableArrayList(productosCargados);

        // Colocar los productos en el DataModel global
        DataModel.getProductos().setAll(listaProductos);

        // Inicializar DataModel para cargar facturas
        DataModel dataModel = DataModel.getInstance();
        dataModel.inicializar();
        System.out.println("✅ Facturas cargadas al iniciar la aplicación: " + DataModel.getFacturas().size());

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 600);
            stage.setTitle("Ferreteria La Promo H&C");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar el FXML", e);
            return;
        }

        // Guardar datos al cerrar la aplicación
        stage.setOnCloseRequest(event -> {
            ObservableList<Productos> productosGlobales = DataModel.getProductos();
            if (!productosGlobales.isEmpty()) {
                try {
                    serializer.serializeData(productosGlobales);
                    LOGGER.info("✅ Datos guardados correctamente en productos.json");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "❌ Error al guardar los datos antes de cerrar.", e);
                }
            } else {
                LOGGER.warning("⚠️ No hay productos que guardar.");
            }
        });
    }

    @Override
    public void stop() {
        try {
            DataModel.guardarFacturas();
            LOGGER.info("✅ Facturas guardadas correctamente al cerrar la aplicación.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al guardar facturas al cerrar.", e);
        }
        System.out.println("Aplicación cerrada - datos guardados");
    }

    public static void main(String[] args) {
        launch();
    }
}
