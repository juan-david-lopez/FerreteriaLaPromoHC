package org.tiendaGUI.Controllers;

import LogicaTienda.DataSerializer;
import LogicaTienda.Productos;
import LogicaTienda.formularioProduct;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class InventarioController implements Initializable {
    private ObservableList<Productos> productosLocales = FXCollections.observableArrayList();
    private DataSerializer dataSerializer = new DataSerializer("productos.json");

    @FXML
    private Button btnNuevo, btnVolver, btnEliminar, btnActualizar;

    @FXML
    private TableView<Productos> tablaNumero1;

    @FXML
    private TableColumn<Productos, String> ColumnaNumero1; // Nombre

    @FXML
    private TableColumn<Productos, Double> ColumnaNumero2; // Precio

    @FXML
    private TableColumn<Productos, Integer> ColumnaNumero3; // Cantidad

    @FXML
    private void volver(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/hello-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Menú Principal");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error: No se pudo cargar hello-view.fxml");
        }
    }

    @FXML
    private void PresionarBotonNuevo(ActionEvent event) {
        Platform.runLater(() -> {
            formularioProduct formularioActual = new formularioProduct("Nuevo Producto", productosLocales, false, dataSerializer);
            formularioActual.showAndWait();  // ⬅ Bloquea la ventana hasta que se cierre
            actualizarTabla();
        });
    }

    @FXML
    private void PresionarBotonEliminar(ActionEvent event) {
        Productos productoSeleccionado = tablaNumero1.getSelectionModel().getSelectedItem();

        if (productoSeleccionado != null) {
            System.out.println("🗑 Eliminando producto: " + productoSeleccionado);

            productosLocales.remove(productoSeleccionado);
            dataSerializer.serializeData(productosLocales); // Guardar cambios

            actualizarTabla(); // Refrescar la tabla desde JSON

            System.out.println("✅ Producto eliminado correctamente.");
        } else {
            System.out.println("⚠️ No se seleccionó ningún producto para eliminar.");
        }
    }

    @FXML
    private void PresionarBotonActualizar(ActionEvent event) {
        Platform.runLater(() -> {
            Productos productoSeleccionado = tablaNumero1.getSelectionModel().getSelectedItem();
            if (productoSeleccionado != null) {
                formularioProduct formularioActual = new formularioProduct("Actualizar Producto", productosLocales, false, dataSerializer);
                formularioActual.showAndWait();
                actualizarTabla(); // Recargar datos en la tabla
            } else {
                System.out.println("⚠️ No se seleccionó ningún producto para actualizar.");
            }
        });
    }


    private void actualizarTabla() {
        List<Productos> productosCargados = dataSerializer.deserializeData();
        if (productosCargados != null && !productosCargados.isEmpty()) {
            productosLocales.setAll(productosCargados);
        } else {
            System.out.println("⚠️ No hay productos en el JSON.");
        }
        tablaNumero1.refresh();
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ColumnaNumero1.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        ColumnaNumero2.setCellValueFactory(new PropertyValueFactory<>("precio"));
        ColumnaNumero3.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        // Cargar productos desde JSON
        List<Productos> productosCargados = dataSerializer.deserializeData();
        System.out.println("🔍 Productos cargados desde JSON: " + productosCargados);

        productosLocales = FXCollections.observableArrayList(productosCargados);
        tablaNumero1.setItems(productosLocales);
    }
}
