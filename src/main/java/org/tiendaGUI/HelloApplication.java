package org.tiendaGUI;

import LogicaTienda.formularioProduct;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import LogicaTienda.DataSerializer;
import LogicaTienda.Productos;
import org.tiendaGUI.Controllers.HelloController;
import org.tiendaGUI.Controllers.InventarioController;

public class HelloApplication extends Application {
    private List<Productos> listaProductos;
    private formularioProduct product;
    private DataSerializer serializer;

    @Override
    public void start(Stage stage) throws IOException {
        serializer = new DataSerializer("productos.txt");
        listaProductos = serializer.deserializeData();
        if(product != null){
            listaProductos.addAll(product.getProductos());
        } else if (listaProductos == null) {
            listaProductos = new ArrayList<>();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 390);
        stage.setTitle("Tienda");
        stage.setScene(scene);
        stage.show();

        HelloController controller = fxmlLoader.getController();
        controller.setListaProductos(listaProductos);

        stage.setOnCloseRequest(event -> {
            serializer.serializeData(listaProductos);
            Platform.exit();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
