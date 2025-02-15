package org.tiendaGUI.Controllers;

import LogicaTienda.LogicaEstadistica;
import LogicaTienda.Productos;
import LogicaTienda.formularioProduct;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import javax.swing.SwingUtilities;

public class InventarioController {
    private List<Productos> productosLocales;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnVolver;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnActualizar;

    @FXML
    private TableColumn<?, ?> ColumnaNumero1;

    @FXML
    private TableColumn<?, ?> ColumnaNumero2;

    @FXML
    private TableView<?> tablaNumero1;

    @FXML
    private void volver(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();

        // Abre la vista Hello
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/hello-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage newStage = new Stage();
            newStage.setTitle("Hello View");
            newStage.setScene(new Scene(root));
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void PresionarBotonNuevo(ActionEvent event) {
        // Llama al constructor de formularioProduct para abrir la ventana
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                formularioProduct formularioActual = new formularioProduct();
                formularioActual.setVisible(true);
                productosLocales=formularioActual.getProductos();
            }
        });
    }
    @FXML
    private void PresionarBotonEliminar(ActionEvent event) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                formularioProduct formularioActual = new formularioProduct(1,productosLocales);
                formularioActual.setVisible(true);
            }
        });
    }
    @FXML
    private void PresionarBotonActualizar(ActionEvent event){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                formularioProduct formularioActual = new formularioProduct(productosLocales);
                formularioActual.setVisible(true);
            }
        });
    }

    public List<Productos> getProductos() {
        return productosLocales;
    }

    public void setProductos(List<Productos> productos) {
        this.productosLocales = productos;
    }
}
