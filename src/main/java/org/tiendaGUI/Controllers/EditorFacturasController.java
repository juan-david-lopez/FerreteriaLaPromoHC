package org.tiendaGUI.Controllers;

import LogicaTienda.Model.Factura;
import LogicaTienda.Model.Productos;
import LogicaTienda.Services.FacturaService;
import LogicaTienda.Services.ProductoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EditorFacturasController {

    @FXML private TableView<Factura> tablaFacturas;
    @FXML private TableColumn<Factura, String> colId;
    @FXML private TableColumn<Factura, String> colFecha;
    @FXML private TableColumn<Factura, String> colCliente;
    @FXML private TableColumn<Factura, String> colCedula;
    @FXML private TableColumn<Factura, String> colTotal;
    @FXML private TableColumn<Factura, String> colEstado;

    @FXML private TableView<Productos> tablaProductos;
    @FXML private TableColumn<Productos, String> colProductoId;
    @FXML private TableColumn<Productos, String> colProductoNombre;
    @FXML private TableColumn<Productos, Double> colProductoPrecio;
    @FXML private TableColumn<Productos, Integer> colProductoCantidad;
    @FXML private TableColumn<Productos, String> colProductoSubtotal;

    @FXML private TextField txtClienteNombre;
    @FXML private TextField txtClienteIdentificacion;
    @FXML private ComboBox<String> comboEstado;
    @FXML private Label lblTotal;
    @FXML private Label lblId;
    @FXML private Label lblFecha;

    private Factura facturaSeleccionada;
    private ObservableList<Productos> productosSeleccionados = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("📋 EditorFacturasController inicializado");

        // Configurar columnas de la tabla de facturas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFecha.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return new SimpleStringProperty(cellData.getValue().getFecha().format(formatter));
        });
        colCliente.setCellValueFactory(new PropertyValueFactory<>("clienteNombre"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("clienteIdentificacion"));
        colTotal.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.format("$%.2f", cellData.getValue().getTotal())));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Configurar columnas de la tabla de productos
        colProductoId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colProductoNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colProductoPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colProductoCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colProductoSubtotal.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.format("$%.2f", 
                cellData.getValue().getPrecio() * cellData.getValue().getCantidad())));

        // Configurar opciones de estado
        comboEstado.setItems(FXCollections.observableArrayList("Activa", "Anulada", "Pagada", "Pendiente"));

        // Cargar facturas
        cargarFacturas();

        // Configurar selección de factura
        tablaFacturas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarDetalleFactura(newSelection);
            }
        });

        // Configurar selección de productos
        tablaProductos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void cargarFacturas() {
        // Cargar todas las facturas desde MongoDB
        List<Factura> facturas = FacturaService.obtenerTodasLasFacturas();
        tablaFacturas.setItems(FXCollections.observableArrayList(facturas));

        // Limpiar selección actual
        tablaFacturas.getSelectionModel().clearSelection();
        facturaSeleccionada = null;
        productosSeleccionados.clear();

        // Limpiar campos de detalle
        lblId.setText("");
        lblFecha.setText("");
        txtClienteNombre.setText("");
        txtClienteIdentificacion.setText("");
        comboEstado.setValue(null);
        lblTotal.setText("");
    }

    private void mostrarDetalleFactura(Factura factura) {
        facturaSeleccionada = factura;

        // Mostrar detalles de la factura
        lblId.setText(factura.getId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        lblFecha.setText(factura.getFecha().format(formatter));
        txtClienteNombre.setText(factura.getClienteNombre());
        txtClienteIdentificacion.setText(factura.getClienteIdentificacion());
        comboEstado.setValue(factura.getEstado());
        lblTotal.setText(String.format("$%.2f", factura.getTotal()));

        // Cargar productos de la factura
        productosSeleccionados.clear();
        productosSeleccionados.addAll(factura.getProductos());
        tablaProductos.setItems(productosSeleccionados);
    }

    @FXML
    private void guardarCambios() {
        if (facturaSeleccionada == null) {
            mostrarAlerta("Error", "No hay factura seleccionada", Alert.AlertType.ERROR);
            return;
        }

        // Actualizar datos de la factura
        facturaSeleccionada.setClienteNombre(txtClienteNombre.getText());
        facturaSeleccionada.setClienteIdentificacion(txtClienteIdentificacion.getText());
        facturaSeleccionada.setEstado(comboEstado.getValue());

        try {
            // Actualizar factura en MongoDB usando el servicio
            Factura facturaActualizada = new Factura();
            facturaActualizada.setId(facturaSeleccionada.getId());
            facturaActualizada.setClienteNombre(facturaSeleccionada.getClienteNombre());
            facturaActualizada.setClienteIdentificacion(facturaSeleccionada.getClienteIdentificacion());
            facturaActualizada.setEstado(facturaSeleccionada.getEstado());
            
            FacturaService.actualizarFactura(facturaActualizada);
            
            // Actualizar tabla
            cargarFacturas();
            mostrarAlerta("Éxito", "Cambios guardados correctamente", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo guardar la factura: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarFactura() {
        if (facturaSeleccionada == null) {
            mostrarAlerta("Error", "No hay factura seleccionada", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar esta factura?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Eliminar factura de MongoDB
                FacturaService.anularFactura(facturaSeleccionada.getId());

                // Actualizar tabla
                cargarFacturas();

                // Limpiar selección
                tablaFacturas.getSelectionModel().clearSelection();
                facturaSeleccionada = null;

                mostrarAlerta("Éxito", "Factura eliminada correctamente", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo eliminar la factura: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void restaurarFactura() {
        if (facturaSeleccionada == null) {
            mostrarAlerta("Error", "No hay factura seleccionada", Alert.AlertType.ERROR);
            return;
        }

        if (!facturaSeleccionada.isEliminada()) {
            mostrarAlerta("Error", "La factura no está eliminada", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Restaurar factura usando el servicio
            facturaSeleccionada.setEliminada(false);
            FacturaService.actualizarFactura(facturaSeleccionada);
            
            // Actualizar tabla
            cargarFacturas();
            mostrarAlerta("Éxito", "Factura restaurada correctamente", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo restaurar la factura: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        // Actualizar tabla
        cargarFacturas();

        mostrarAlerta("Éxito", "Factura restaurada correctamente", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void eliminarProductos() {
        if (facturaSeleccionada == null) {
            mostrarAlerta("Error", "No hay factura seleccionada", Alert.AlertType.ERROR);
            return;
        }

        ObservableList<Productos> productosAEliminar = tablaProductos.getSelectionModel().getSelectedItems();

        if (productosAEliminar.isEmpty()) {
            mostrarAlerta("Error", "No hay productos seleccionados", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación de productos");
        confirmacion.setHeaderText("¿Está seguro de eliminar los productos seleccionados?");
        confirmacion.setContentText("Los productos serán devueltos al inventario.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Crear una lista con los productos a eliminar
            List<Productos> listaProductosAEliminar = new ArrayList<>(productosAEliminar);

            // Eliminar productos de la factura usando el servicio
            try {
                // Actualizar la factura en la base de datos
                facturaSeleccionada.getProductos().removeAll(listaProductosAEliminar);
                FacturaService.actualizarFactura(facturaSeleccionada);
                
                // Actualizar inventario (devolver productos al stock)
                // Nota: Necesitarás implementar este método en el servicio correspondiente
                // ProductoService.devolverProductosAlInventario(listaProductosAEliminar);
                
                // Actualizar tabla de productos
                mostrarDetalleFactura(facturaSeleccionada);
                
                // Actualizar tabla de facturas
                cargarFacturas();
                
                mostrarAlerta("Éxito", "Productos eliminados correctamente", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudieron eliminar los productos: " + e.getMessage(), Alert.AlertType.ERROR);
            }

            mostrarAlerta("Éxito", "Productos eliminados correctamente", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void volverAlInicio(ActionEvent event) {
        try {
            URL url = getClass().getResource("/org/tiendaGUI/hello-view.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tienda Castillo");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista principal", Alert.AlertType.ERROR);
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
