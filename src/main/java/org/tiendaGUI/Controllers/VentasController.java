package org.tiendaGUI.Controllers;

// DataModel dependency removed - using MongoDB directly
import LogicaTienda.Model.Productos;
import LogicaTienda.Services.ProductoService;
import javafx.application.Platform;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class VentasController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(VentasController.class.getName());

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
    
    private ObservableList<ProductoSimpleDTO> productosDTO;

    private CarritoDTO carritoDTO;
    
    public void setCarritoDTO(CarritoDTO carritoDTO) {
        this.carritoDTO = carritoDTO;
        if (carritoDTO != null && carritoDTO.getProductos() != null) {
            // Actualizar la tabla con los productos del carrito si existe
            cargarProductos();
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("📦 Inicializando controlador de ventas con MongoDB");
        
        // Configurar columnas de la tabla
        columnaId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnaCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        columnaStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        
        // Inicializar lista observable
        productosDTO = FXCollections.observableArrayList();
        tablaProductos.setItems(productosDTO);
        
        // Cargar productos desde MongoDB
        cargarProductosDesdeMongoDB();
    }

    private void cargarProductos() {
        new Thread(() -> {
            try {
                List<Productos> productos = ProductoService.obtenerTodosLosProductos();
                Platform.runLater(() -> {
                    if (productos != null && !productos.isEmpty()) {
                        // Convertir a DTOs y actualizar la tabla
                        productosDTO.setAll(productos.stream()
                            .map(p -> new ProductoSimpleDTO(
                                p.getIdProducto(), 
                                p.getNombre(), 
                                p.getPrecioParaVender(), 
                                p.getCantidad(), 
                                p.getStock()
                            ))
                            .collect(Collectors.toList())
                        );
                        
                        tablaProductos.refresh();
                        System.out.println("✅ " + productos.size() + " productos cargados");
                    } else {
                        System.out.println("ℹ️ No se encontraron productos");
                    }
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al cargar productos", e);
                Platform.runLater(() -> 
                    mostrarAlerta("Error", "No se pudieron cargar los productos: " + e.getMessage(), 
                                Alert.AlertType.ERROR)
                );
            }
        }).start();
    }
    
    private void cargarProductosDesdeMongoDB() {
        cargarProductos();
    }

    @FXML
    private void btnVolverAction(ActionEvent event) {
        System.out.println("🔙 Volviendo al menú principal...");
        cambiarVentana(event, "hello-view.fxml", "Menú Principal");
    }

    @FXML
    private void btnActualizarAction(ActionEvent event) {
        cargarProductosDesdeMongoDB();
    }

    @FXML
    private void btnVenderAction(ActionEvent event) {
        ProductoSimpleDTO seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Error", "Selecciona un producto para vender.", Alert.AlertType.ERROR);
            return;
        }
        
        // Obtener el producto directamente de MongoDB
        Productos producto = ProductoService.buscarProductoPorId(seleccionado.getIdProducto());
        
        if (producto == null) {
            mostrarAlerta("Error", "El producto seleccionado no existe o no está disponible.", Alert.AlertType.ERROR);
            return;
        }
        int stockDisponible = producto.getCantidad() + producto.getStock();
        
        // Diálogo para ingresar la cantidad
        TextInputDialog dialogoCantidad = new TextInputDialog("1");
        dialogoCantidad.setTitle("Cantidad a Vender");
        dialogoCantidad.setHeaderText(String.format("Venta de producto: %s\nStock disponible: %d", 
            producto.getNombre(), stockDisponible));
        dialogoCantidad.setContentText("Ingrese la cantidad a vender:");
        
        dialogoCantidad.showAndWait().ifPresent(input -> {
            try {
                int cantidadDeseada = Integer.parseInt(input);
                if (cantidadDeseada <= 0) {
                    throw new NumberFormatException("La cantidad debe ser mayor a cero");
                }
                
                if (cantidadDeseada > stockDisponible) {
                    mostrarAlerta("Error", "No hay suficiente stock disponible.", Alert.AlertType.ERROR);
                    return;
                }
                
                // Calcular cuánto vender de cantidad y cuánto de stock
                int restante = cantidadDeseada;
                if (producto.getCantidad() >= restante) {
                    producto.setCantidad(producto.getCantidad() - restante);
                } else {
                    restante -= producto.getCantidad();
                    producto.setCantidad(0);
                    producto.setStock(producto.getStock() - restante);
                }
                
                // Actualizar el producto en MongoDB
                try {
                    ProductoService.actualizarProducto(producto);
                    // Si llegamos aquí, la actualización fue exitosa
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error al actualizar el producto en MongoDB", e);
                    throw new Exception("No se pudo actualizar el inventario: " + e.getMessage());
                }
                
                // Crear una copia del producto para el carrito
                Productos productoCarrito = new Productos(
                    producto.getIdProducto(), 
                    producto.getNombre(), 
                    producto.getPrecio(), 
                    producto.getPorcentajeGanancia(),
                    cantidadDeseada, 
                    0
                );
                productoCarrito.calcularPrecioVenta();
                
                // Actualizar el carrito DTO
                if (carritoDTO == null) {
                    carritoDTO = new CarritoDTO(new ArrayList<>(), 0);
                }
                carritoDTO.getProductos().add(productoCarrito);
                carritoDTO.actualizarTotal();
                
                Platform.runLater(() -> {
                    cargarProductos();
                    mostrarAlerta("Éxito", "Producto agregado al carrito.", Alert.AlertType.INFORMATION);
                });
                
            } catch (NumberFormatException e) {
                String mensaje = e.getMessage() != null && e.getMessage().contains("mayor a cero") 
                    ? "La cantidad debe ser un número mayor a cero" 
                    : "Ingrese un número válido.";
                mostrarAlerta("Error", mensaje, Alert.AlertType.ERROR);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al procesar la venta", e);
                mostrarAlerta("Error", "Ocurrió un error al procesar la venta: " + e.getMessage(), 
                            Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void btnIrCarritoAction(ActionEvent event) {
        if (carritoDTO == null || carritoDTO.getProductos().isEmpty()) {
            mostrarAlerta("Carrito vacío", "No hay productos en el carrito.", Alert.AlertType.INFORMATION);
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/pedido-view.fxml"));
            Parent root = loader.load();
            PedidoController pedidoCtrl = loader.getController();
            // Calcular el total actualizado
            carritoDTO.actualizarTotal();
            pedidoCtrl.setCarritoDTO(carritoDTO);
            
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Carrito de Compras");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al abrir el carrito", e);
            mostrarAlerta("Error", "No se pudo abrir el carrito: " + e.getMessage(), 
                        Alert.AlertType.ERROR);
        }
    }

    private void cambiarVentana(ActionEvent event, String fxmlFile, String title) {
        ViewLoader.cargarVista(event, fxmlFile, title);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Platform.runLater(() -> {
            Alert alerta = new Alert(tipo);
            alerta.setTitle(titulo);
            alerta.setHeaderText(null);
            alerta.setContentText(mensaje);
            alerta.showAndWait();
        });
    }
}
