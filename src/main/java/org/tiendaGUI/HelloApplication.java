package org.tiendaGUI;

import LogicaTienda.DataSerializer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import LogicaTienda.Productos;
import org.tiendaGUI.Controllers.HelloController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class HelloApplication extends Application {
    private ObservableList<Productos> listaProductos;  // Cambio de List a ObservableList
    private DataSerializer serializer;

    @Override
    public void start(Stage stage) throws IOException {
        serializer = new DataSerializer("productos.json");

        // Intentar deserializar, si falla, inicializar con una lista vacía
        listaProductos = FXCollections.observableArrayList(serializer.deserializeData());
        if (listaProductos == null) {
            listaProductos = FXCollections.observableArrayList();
        }

        // Cargar la vista
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 390);
        stage.setTitle("Tienda");
        stage.setScene(scene);
        stage.show();

        // Pasar la lista de productos al controlador
        HelloController controller = fxmlLoader.getController();
        controller.setListaProductos(listaProductos);  // Ahora recibe ObservableList

        // Guardar datos al cerrar la aplicación
        stage.setOnCloseRequest(event -> {
            try {
                serializer.serializeData(listaProductos);
                System.out.println("✅ Datos guardados correctamente en productos.json");
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("❌ Error al guardar los datos antes de cerrar.");
            }
            Platform.exit();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
