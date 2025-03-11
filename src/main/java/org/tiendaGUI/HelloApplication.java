package org.tiendaGUI;

import LogicaTienda.DataSerializer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import LogicaTienda.Productos;
import org.tiendaGUI.Controllers.HelloController;

import java.io.IOException;

public class HelloApplication extends Application {
    private ObservableList<Productos> listaProductos;
    private DataSerializer serializer;

    @Override
    public void start(Stage stage) throws IOException {
        serializer = new DataSerializer("productos.json");

        // Deserializar los datos UNA sola vez
        java.util.List<Productos> productosCargados = serializer.deserializeData();

        if (productosCargados == null) {
            productosCargados = new java.util.ArrayList<>();  // Evita valores nulos
        }

        listaProductos = FXCollections.observableArrayList(productosCargados);

        // Cargar la vista
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 390);
        stage.setTitle("Tienda");
        stage.setScene(scene);
        stage.show();

        // Pasar la lista de productos al controlador
        HelloController controller = fxmlLoader.getController();
        controller.bindListaProductos(listaProductos);

        // Guardar datos solo si la lista tiene productos
        stage.setOnCloseRequest(event -> {
            if (!listaProductos.isEmpty()) {
                try {
                    serializer.serializeData(listaProductos);
                    System.out.println("✅ Datos guardados correctamente en productos.json");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("❌ Error al guardar los datos antes de cerrar.");
                }
            } else {
                System.out.println("⚠️ No hay productos que guardar.");
            }
        });
    }
    public static void main(String[] args) {
        launch();
    }
}
