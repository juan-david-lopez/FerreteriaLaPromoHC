package org.tiendaGUI.Controllers;

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
import javax.swing.SwingUtilities;

public class InventarioController {
    @FXML
    private Button btnVolver;
    @FXML
    private Button btnNuevo;

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
                new formularioProduct().setVisible(true);
            }
        });
    }
}
