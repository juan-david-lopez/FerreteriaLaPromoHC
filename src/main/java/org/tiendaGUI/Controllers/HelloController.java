package org.tiendaGUI.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import LogicaTienda.Productos;

public class HelloController {

    @FXML
    private Button BotonEstadisticas;

    @FXML
    private Button BotonInventario;

    @FXML
    private Button BotonVentas;

    @FXML
    private Pane ImagenDelMedio;

    @FXML
    private ToolBar PanelAbajo;

    @FXML
    private Pane PanelPrincipal;

    @FXML
    private Label panelArriba;

    private List<Productos> listaProductos;

    @FXML
    private void PresionarBotonInventario(){
        cambiarVentana("inventario-view.fxml", "Inventario");
    }

    @FXML
    private void PresionarBotonEstadisticas(){
        cambiarVentana("estadisticas-view.fxml", "Estadísticas");
    }

    @FXML
    private void PresionarBotonVentas(){
        cambiarVentana("ventas-view.fxml", "Ventas");
    }

    private void cambiarVentana(String fxmlFile, String title) {
        try {
            System.out.println("Cargando FXML: " + fxmlFile);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/" + fxmlFile));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
            Stage currentStage = (Stage) PanelPrincipal.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void initialize() {

    }

    public void setListaProductos(List<Productos> listaProductos) {
        this.listaProductos = listaProductos;
    }
}
