package org.tiendaGUI.Controllers;

import LogicaTienda.Data.DataModel;
import LogicaTienda.Model.Productos;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PedidoController implements Initializable {

    @FXML private TableView<Productos> tblProductos;
    @FXML private TableColumn<Productos, String>   ColumnaNombre;
    @FXML private TableColumn<Productos, Double>   ColumnaValor;
    @FXML private TableColumn<Productos, Integer>  ColumnaStock;
    @FXML private TableColumn<Productos, String>   ColumnaIdProducto;

    @FXML private Button btnFacturaElectro;
    @FXML private Button btonFacturaImprs;
    @FXML private Button btonEliminarProdct;
    @FXML private Button btnDomicilio;
    @FXML private Button BtnVolver;
    @FXML private Button btnPagar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ColumnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        ColumnaValor.setCellValueFactory(new PropertyValueFactory<>("precio"));
        ColumnaStock.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        ColumnaIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));

        tblProductos.setItems(DataModel.getCarritoVentas());
    }

    @FXML
    private void eliminarProducto(ActionEvent event) {
        Productos seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            DataModel.getCarritoVentas().remove(seleccionado);
        } else {
            mostrarAlerta("Aviso", "Selecciona un producto para eliminar.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void volverMenu(ActionEvent event) {
        cambiarVentana(event, "ventas-view.fxml", "Ventana ventas");
    }

    @FXML
    private void irADomicilio(ActionEvent event) {
        if (DataModel.getCarritoVentas().isEmpty()) {
            mostrarAlerta("Carrito vacío", "Agrega productos antes de continuar.", Alert.AlertType.WARNING);
            return;
        }
        cambiarVentana(event, "domicilio-view.fxml", "Domicilio");
    }
    @FXML
    private void irApagar(ActionEvent event) {
        if (DataModel.getCarritoVentas().isEmpty() || tblProductos.getItems().isEmpty()
                || calcularTotalCarrito() == 0) {
            mostrarAlerta("Carrito vacío", "Agrega productos antes de continuar.", Alert.AlertType.WARNING);
            return;
        }
        cambiarVentana(event, "Pago-view.fxml", "Pagos");
    }
    private double calcularTotalCarrito() {
        return tblProductos.getItems().stream()
                .mapToDouble(producto -> producto.getPrecio() * producto.getCantidad())
                .sum();
    }

    @FXML
    private void generarFacturaElectronica(ActionEvent event) {
        mostrarAlerta("Factura", "Factura electrónica generada (pendiente).", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void imprimirFactura(ActionEvent event) {
        mostrarAlerta("Impresión", "Factura enviada a impresión (pendiente).", Alert.AlertType.INFORMATION);
    }

    private void cambiarVentana(ActionEvent event, String fxmlFile, String title) {
        try {
            URL url = getClass().getResource("/org/tiendaGUI/" + fxmlFile);
            if (url == null) {
                throw new IllegalStateException("No se pudo encontrar el archivo FXML: " + fxmlFile);
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            // Si es la ventana de pago, configurar el monto total
            if (fxmlFile.equals("pago-view.fxml")) {
                PagoController pagoController = loader.getController();
                double totalCarrito = calcularTotalCarrito();
                pagoController.setMontoTotalCarrito(totalCarrito);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al cargar la ventana: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }



    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
