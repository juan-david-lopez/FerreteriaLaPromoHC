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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloApplication extends Application {
    private static final Logger LOGGER = Logger.getLogger(HelloApplication.class.getName());

    // Ya no es estrictamente necesaria si usamos DataModel global, pero se puede usar para cargar inicialmente.
    private ObservableList<Productos> listaProductos;
    private DataSerializer serializer;

    @Override
    public void start(Stage stage) {
        serializer = new DataSerializer("productos.json");

        // Deserializar los datos UNA sola vez
        List<Productos> productosCargados = serializer.deserializeData();
        if (productosCargados == null) {
            productosCargados = new ArrayList<>();  // Evita valores nulos
            LOGGER.info("No se encontraron productos; se inicializa con lista vacía.");
        }
        listaProductos = FXCollections.observableArrayList(productosCargados);

        // Colocar los productos en el DataModel para uso global
        DataModel.getProductos().setAll(listaProductos);

        try {
            // Cargar la vista
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 390);
            stage.setTitle("Tienda");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar el FXML", e);
            return;
        }

        // Al cerrar, se guarda la lista global de DataModel, que es la única fuente de verdad.
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

    public static void main(String[] args) {
        launch();
    }
}
