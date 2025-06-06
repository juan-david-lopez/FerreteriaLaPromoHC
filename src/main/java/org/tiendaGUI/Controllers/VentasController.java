package org.tiendaGUI.Controllers;

import LogicaTienda.Data.DataModel;
import LogicaTienda.Data.DataSerializer;
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
import org.tiendaGUI.DTO.CarritoDTO;
import org.tiendaGUI.DTO.ProductoSimpleDTO;
import org.tiendaGUI.Controllers.loader.ViewLoader;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class VentasController implements Initializable {

    private final DataSerializer dataSerializer = new DataSerializer("productos.json");

    @FXML private Button btnVolver;
    @FXML private Button btnVender;
    @FXML private Button btnActualizar;
    @FXML private Button btnIrCarrito;

    @FXML private TableView<ProductoSimpleDTO> tablaProductos;
    @FXML private TableColumn<ProductoSimpleDTO, String> columnaNombre;
    @FXML private TableColumn<ProductoSimpleDTO, Double> columnaPrecio;
    @FXML private TableColumn<ProductoSimpleDTO, Integer> columnaCantidad;
    @FXML private TableColumn<ProductoSimpleDTO, Integer> columnaStock;
    @FXML private TableColumn<ProductoSimpleDTO, String> columnaId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("📦 Inicializando controlador de ventas con DTOs.");
        columnaId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnaCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        columnaStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        cargarYMostrarProductos();
    }

    private void cargarYMostrarProductos() {
        List<LogicaTienda.Model.Productos> modelos = dataSerializer.deserializeData();
        DataModel.getProductos().setAll(modelos);
        ObservableList<ProductoSimpleDTO> dtos = FXCollections.observableArrayList(
                DataModel.getProductos().stream()
                        .map(p -> new ProductoSimpleDTO(
                                p.getIdProducto(), p.getNombre(), p.getPrecioParaVender(), p.getCantidad(), p.getStock()
                        ))
                        .collect(Collectors.toList())
        );
        tablaProductos.setItems(dtos);
    }

    @FXML
    private void btnVolverAction(ActionEvent event) {
        System.out.println("🔙 Volviendo al menú principal...");
        cambiarVentana(event, "hello-view.fxml", "Menú Principal");
    }

    @FXML
    private void btnActualizarAction(ActionEvent event) {
        cargarYMostrarProductos();
        tablaProductos.refresh();
    }

    @FXML
    private void btnVenderAction(ActionEvent event) {
        ProductoSimpleDTO seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Error", "Selecciona un producto para vender.", Alert.AlertType.ERROR);
            return;
        }
        TextInputDialog dialogoCantidad = new TextInputDialog("1");
        dialogoCantidad.setTitle("Cantidad a Vender");
        dialogoCantidad.setHeaderText("Venta de producto: " + seleccionado.getNombre());
        dialogoCantidad.setContentText("Ingrese la cantidad a vender:");
        dialogoCantidad.showAndWait().ifPresent(input -> {
            try {
                int cantidadDeseada = Integer.parseInt(input);
                if (cantidadDeseada <= 0) throw new NumberFormatException();
                LogicaTienda.Model.Productos modelo = DataModel.getProductos().stream()
                        .filter(p -> p.getIdProducto().equals(seleccionado.getIdProducto()))
                        .findFirst().orElse(null);
                if (modelo == null || cantidadDeseada > modelo.getCantidad() + modelo.getStock()) {
                    mostrarAlerta("Error", "Inventario insuficiente.", Alert.AlertType.ERROR);
                    return;
                }
                int restante = cantidadDeseada;
                if (modelo.getCantidad() >= restante) {
                    modelo.setCantidad(modelo.getCantidad() - restante);
                } else {
                    restante -= modelo.getCantidad();
                    modelo.setCantidad(0);
                    modelo.setStock(modelo.getStock() - restante);
                }
                // Crear una copia del producto con la cantidad deseada
                LogicaTienda.Model.Productos productoCarrito = new LogicaTienda.Model.Productos(
                        modelo.getIdProducto(), 
                        modelo.getNombre(), 
                        modelo.getPrecio(), 
                        modelo.getPorcentajeGanancia(),
                        cantidadDeseada, 
                        0
                );
                // Asegurarse de que el precio de venta se calcule correctamente
                productoCarrito.calcularPrecioVenta();
                DataModel.getCarritoVentas().add(productoCarrito);
                dataSerializer.serializeData(DataModel.getProductos());
                cargarYMostrarProductos();
                mostrarAlerta("Éxito", "Se vendieron " + cantidadDeseada + " unidades.", Alert.AlertType.INFORMATION);
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "Ingrese un número válido.", Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void btnIrCarritoAction(ActionEvent event) {
        CarritoDTO carritoDTO = new CarritoDTO(
                DataModel.getCarritoVentas(), DataModel.calcularTotalCarrito()
        );
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/pedido-view.fxml"));
            Parent root = loader.load();
            PedidoController pedidoCtrl = loader.getController();
            pedidoCtrl.setCarritoDTO(carritoDTO);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Carrito de Compras");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el carrito.", Alert.AlertType.ERROR);
        }
    }

    private void cambiarVentana(ActionEvent event, String fxmlFile, String title) {
        ViewLoader.cargarVista(event, fxmlFile, title);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
