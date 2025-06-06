package org.tiendaGUI.Controllers;

import LogicaTienda.Data.DataModel;
import LogicaTienda.Model.Factura;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import org.tiendaGUI.DTO.CarritoDTO;
import javax.swing.*;

import LogicaTienda.Model.Pago;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import javafx.util.Duration;
import org.tiendaGUI.utils.PDFGenerator;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.UUID;

import static org.tiendaGUI.Controllers.loader.ViewLoader.mostrarAlerta;

public class PagoController implements Initializable {
    private final PDFGenerator pdfGenerator = new PDFGenerator();
    @FXML private Label lblTotalCarrito;
    @FXML private Label lblCambio;
    @FXML private Label lblMontoRequerido;
    @FXML private TextField montoField;
    @FXML private ComboBox<String> metodoPagoCombo;
    @FXML private TextField referenciaField;
    @FXML private Button btnProcesar;
    @FXML private Button btnCancelar;
    @FXML private Button btnVolver;
    @FXML private TableView<Pago> tablaPagos;
    @FXML private TableColumn<Pago, String> columnaId;
    @FXML private TableColumn<Pago, Double> columnaMonto;
    @FXML private TableColumn<Pago, String> columnaMetodo;
    @FXML private TableColumn<Pago, String> columnaFecha;
    @FXML private TableColumn<Pago, String> columnaEstado;
    @FXML private TableColumn<Pago, String> columnaReferencia;

    // DTO recibido desde PedidoController
    private CarritoDTO carritoDTO;
    private double montoTotalCarrito = 0.0;

    @FXML
    public void setCarritoDTO(CarritoDTO carritoDTO) {
        this.carritoDTO = carritoDTO;
        // Inicializar monto y tabla de carrito en base al DTO
        this.montoTotalCarrito = carritoDTO.getTotal();
        actualizarInterfazMonto(montoTotalCarrito);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar columnas de tabla de pagos
        columnaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnaMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        columnaMetodo.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        columnaFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        columnaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        columnaReferencia.setCellValueFactory(new PropertyValueFactory<>("referencia"));
        columnaId.setCellFactory(TextFieldTableCell.forTableColumn());
        columnaId.setOnEditCommit(event -> {
            Pago pago = event.getRowValue();
            pago.setId(event.getNewValue());
        });
        // Cargar pagos actuales
        tablaPagos.setItems(DataModel.getPagos());
        tablaPagos.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // Configurar combo de métodos
        metodoPagoCombo.setItems(FXCollections.observableArrayList(
                "Efectivo", "Tarjeta", "Transferencia", "Otro"
        ));
        metodoPagoCombo.setValue("Transferencia");

        // Formato numérico para monto
        montoField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches("\\d*\\.?\\d*") ? change : null));

        // Listeners
        montoField.textProperty().addListener((obs, oldV, newV) -> {
            try {
                double ingresado = newV.isEmpty() ? 0 : Double.parseDouble(newV);
                actualizarCambio(ingresado);
            } catch (NumberFormatException ignored) {}
        });
        metodoPagoCombo.valueProperty().addListener((obs, o, n) -> {
            boolean esEfectivo = "Efectivo".equals(n);
            referenciaField.setDisable(esEfectivo);
            if (esEfectivo) referenciaField.clear();
        });

        lblCambio.setText("Cambio: $0.00");
    }

    @FXML
    private void btnProcesarAction() {
        // Validaciones básicas
        String text = montoField.getText();
        if (text.isEmpty()) { showError("Debe ingresar un monto"); return; }
        double ingresado = Double.parseDouble(text);
        if (ingresado < montoTotalCarrito) { showError("Monto insuficiente"); return; }
        String metodo = metodoPagoCombo.getValue();
        if (!"Efectivo".equals(metodo) && referenciaField.getText().trim().isEmpty()) {
            showError("Referencia requerida para " + metodo); return;
        }
        double cambio = ingresado - montoTotalCarrito;

        Pago pago = registrarPago(ingresado, cambio);
        crearFacturaConDialogo(pago);
        showInfo(String.format("Pago correcto: pagó $%.2f, cambio $%.2f", ingresado, cambio));
        limpiarCampos();
    }

    @FXML
    private void btnCancelarAction() {
        limpiarCampos();
    }

    @FXML
    private void btnVolverAction(ActionEvent event) {
        // Al volver, puedes pasar el mismo DTO de carrito
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/pedido-view.fxml"));
            Parent root = loader.load();
            PedidoController ctrl = loader.getController();
            ctrl.setCarritoDTO(carritoDTO);
            Stage s = (Stage)((Node)event.getSource()).getScene().getWindow();
            s.setScene(new Scene(root)); s.setTitle("Carrito de Compras"); s.show();
        } catch (IOException e) {
            e.printStackTrace(); showError("No se pudo volver al carrito");
        }
    }

    private Pago registrarPago(double montoPagado, double cambio) {
        String idPago = UUID.randomUUID().toString().substring(0,8);
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String metodo = metodoPagoCombo.getValue();
        String ref = "Efectivo".equals(metodo)
                ? String.format("Pagado $%.2f, cambio $%.2f", montoPagado, cambio)
                : referenciaField.getText().trim();
        Pago p = new Pago(idPago, montoTotalCarrito, metodo, fecha, "Completado", ref);
        DataModel.getPagos().add(p);
        tablaPagos.refresh();
        return p;
    }
    public void crearFacturaConDialogo(Pago pago) {
        // Mostrar el diálogo para ingresar los datos del cliente (nombre y cédula)
        JTextField nombreField = new JTextField();
        JTextField cedulaField = new JTextField();

        Object[] message = {
                "Nombre del Cliente:", nombreField,
                "Cédula del Cliente:", cedulaField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Datos del Cliente", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            // Obtener los valores de los campos de texto
            String nombreCliente = nombreField.getText().trim();
            String cedulaCliente = cedulaField.getText().trim();

            // Validación simple (puedes personalizarla)
            if (nombreCliente.isEmpty() || cedulaCliente.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor ingrese todos los campos.");
                return;
            }

            // Validar formato de cédula (por ejemplo, que sea numérica y tenga una longitud adecuada)
            if (!cedulaCliente.matches("\\d+")) {
                JOptionPane.showMessageDialog(null, "La cédula debe ser numérica.");
                return;
            }

            // Llamar a la función crearFactura con los datos del cliente
            DataModel.crearFacturaDialogo(pago, nombreCliente, cedulaCliente);

            // Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(null, "Factura creada exitosamente.");
        } else {
            JOptionPane.showMessageDialog(null, "Operación cancelada.");
        }
    }
    private void actualizarCambio(double ingresado) {
        double dif = ingresado - montoTotalCarrito;
        lblCambio.setText(String.format(dif>=0?"Cambio: $%.2f":"Falta: $%.2f", Math.abs(dif)));
        lblCambio.setStyle(dif>=0?"-fx-text-fill:green":"-fx-text-fill:red");
        new FadeTransition(Duration.millis(200), lblCambio).play();
    }

    private void actualizarInterfazMonto(double monto) {
        Platform.runLater(() -> {
            lblTotalCarrito.setText(String.format("Total: $%.2f", monto));
            lblMontoRequerido.setText(String.format("Debe: $%.2f", monto));
        });
    }

    private void limpiarCampos() {
        montoField.clear(); referenciaField.clear(); metodoPagoCombo.setValue("Efectivo"); lblCambio.setText("Cambio: $0.00");
    }

    private void showError(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
    private void showInfo(String msg)  { new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }

    public void imprimirPDF(ActionEvent actionEvent) {
        Factura facturaSeleccionada = obtenerFacturaDesdePagoSeleccionado();

        if (facturaSeleccionada == null) {
            mostrarAlerta("cuidado","Debes seleccionar un pago relacionado a una factura.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Obtener la ruta de la carpeta de Descargas
            String userHome = System.getProperty("user.home");
            File downloadsDir = new File(userHome, "Downloads");
            
            // Si no existe la carpeta Downloads, intentar con Descargas (español)
            if (!downloadsDir.exists() || !downloadsDir.isDirectory()) {
                downloadsDir = new File(userHome, "Descargas");
                // Si tampoco existe, crear la carpeta
                if (!downloadsDir.exists()) {
                    boolean created = downloadsDir.mkdirs();
                    if (!created) {
                        throw new IOException("No se pudo crear la carpeta de descargas");
                    }
                }
            }

            // Crear el nombre del archivo con timestamp
            String timeStamp = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
            );
            String fileName = String.format("Factura_%s_%s.pdf", facturaSeleccionada.getId(), timeStamp);
            File pdfFile = new File(downloadsDir, fileName);

            // Generar el PDF directamente en la carpeta de descargas
            PDFGenerator.generarFacturaPDF(facturaSeleccionada);

            // Mostrar mensaje de éxito con la ruta del archivo
            mostrarAlerta("✅ Success", "✅ Factura descargada exitosamente en: " + pdfFile.getAbsolutePath(), Alert.AlertType.INFORMATION);
            
            // Abrir el archivo automáticamente si es posible
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(pdfFile);
                } catch (Exception e) {
                    // Si no se puede abrir, solo mostramos el mensaje de éxito
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("❌ Error","❌ Error al guardar la factura: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Factura obtenerFacturaDesdePagoSeleccionado() {
        Pago pago = tablaPagos.getSelectionModel().getSelectedItem();
        if (pago == null) return null;

        for (Factura f : DataModel.getFacturas()) {
            if (f.getPago().getId().equals(pago.getId())) {
                return f;
            }
        }
        return null;
    }

}
