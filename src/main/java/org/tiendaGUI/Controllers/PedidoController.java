package org.tiendaGUI.Controllers;

import LogicaTienda.Data.DataModel;
import org.tiendaGUI.DTO.CarritoDTO;
import LogicaTienda.Model.Productos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.function.Consumer;

public class PedidoController implements Initializable {

    @FXML private TableView<Productos> tblProductos;
    @FXML private TableColumn<Productos, String> columnaNombre;
    @FXML private TableColumn<Productos, Double> columnaValor;
    @FXML private TableColumn<Productos, Integer> columnaCantidad;
    @FXML private TableColumn<Productos, String> ColumnaIdProducto;

    @FXML private Button btnFacturaElectro;
    @FXML private Button btnImprimirFactura;
    @FXML private Button btnEliminarProducto;
    @FXML private Button btnDomicilio;
    @FXML private Button btnVolver;
    @FXML private Button btnPagar;

    // DTO recibido desde el controlador anterior
    private CarritoDTO carritoDTO;

    public void setCarritoDTO(CarritoDTO carritoDTO) {
        this.carritoDTO = carritoDTO;
        cargarDatosDesdeDTO();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar columnas
        ColumnaIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaValor.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnaCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        // Solo inicializar estructura; datos llegan vía DTO
    }

    private void cargarDatosDesdeDTO() {
        if (carritoDTO == null) {
            mostrarAlerta("Error", "No hay datos de carrito para mostrar.", Alert.AlertType.ERROR);
            return;
        }
        // Poner la lista de productos en la tabla
        ObservableList<Productos> lista = FXCollections.observableArrayList(carritoDTO.getProductos());
        tblProductos.setItems(lista);
    }
    @FXML
    private void eliminarProducto(ActionEvent event) {
        Productos seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            DataModel.getCarritoVentas().remove(seleccionado);
            tblProductos.getItems().remove(seleccionado);
            carritoDTO.getProductos().remove(seleccionado); // <-- Agrega esta línea
        } else {
            mostrarAlerta("Aviso", "Selecciona un producto para eliminar.", Alert.AlertType.WARNING);
        }
    }


    @FXML
    private void volverMenu(ActionEvent event) {
        cambiarVentanaConDTO(event, "Ventas-view.fxml", "Ventas");    }

    @FXML
    private void irADomicilio(ActionEvent event) {
        if (tblProductos.getItems().isEmpty()) {
            mostrarAlerta("Carrito vacío", "Agrega productos antes de continuar.", Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/domicilio-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Domicilio");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista de Domicilio.", Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void irApagar(ActionEvent event) {
        if (tblProductos.getItems().isEmpty() || calcularTotalCarrito() == 0) {
            mostrarAlerta("Carrito vacío", "Agrega productos antes de continuar.", Alert.AlertType.WARNING);
            return;
        }
        // Crear nuevo DTO actualizado
        CarritoDTO nuevoDTO = new CarritoDTO(
                tblProductos.getItems(),
                calcularTotalCarrito()
        );
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/Pago-view.fxml"));
            Parent root = loader.load();
            PagoController pagoController = loader.getController();
            pagoController.setCarritoDTO(nuevoDTO);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Pagos");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la ventana de pagos.", Alert.AlertType.ERROR);
        }
    }

    private double calcularTotalCarrito() {
        return tblProductos.getItems().stream()
                .mapToDouble(p -> p.getPrecio() * p.getCantidad())
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

    private void cambiarVentanaConDTO(ActionEvent event, String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/" + fxmlFile));
            Parent root = loader.load();
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