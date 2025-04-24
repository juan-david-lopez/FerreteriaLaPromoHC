package org.tiendaGUI.Controllers;

import LogicaTienda.Data.DataModel;
import LogicaTienda.Model.Pago;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PagoController implements Initializable {
    @FXML private Label lblTotalCarrito;  // Mantenemos esta referencia ya que ahora existe en el FXML
    @FXML private Label lblCambio;
    @FXML private TextField montoField;
    @FXML private ComboBox<String> metodoPagoCombo;
    @FXML private TextField referenciaField;
    @FXML private Button btnProcesar;
    @FXML private Button btnCancelar;
    @FXML private Button btnVolver;
    @FXML private Label lblMontoRequerido;
    @FXML private TableView<Pago> tablaPagos;
    @FXML private TableColumn<Pago, String> columnaId;
    @FXML private TableColumn<Pago, Double> columnaMonto;
    @FXML private TableColumn<Pago, String> columnaMetodo;
    @FXML private TableColumn<Pago, String> columnaFecha;
    @FXML private TableColumn<Pago, String> columnaEstado;
    @FXML private TableColumn<Pago, String> columnaReferencia;

    private ObservableList<Pago> listaPagos = FXCollections.observableArrayList();
    private double montoTotalCarrito = 0.0; // Valor predeterminado

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("📊 Inicializando controlador de pagos");

        // Configurar columnas de la tabla
        columnaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnaMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        columnaMetodo.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        columnaFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        columnaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        columnaReferencia.setCellValueFactory(new PropertyValueFactory<>("referencia"));

        tablaPagos.setItems(listaPagos);

        // Cargar los pagos existentes desde el modelo de datos
        tablaPagos.setItems(DataModel.getPagos());

        // Inicializar el ComboBox con métodos de pago
        ObservableList<String> metodosPago = FXCollections.observableArrayList(
                "Efectivo", "Tarjeta", "Transferencia", "Otro"
        );
        metodoPagoCombo.setItems(metodosPago);
        metodoPagoCombo.setValue("Efectivo"); // Valor por defecto

        // Configurar TextFormatter para solo permitir números y punto decimal
        montoField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*\\.?\\d*")) {
                return change;
            }
            return null;
        }));

        // Listener para actualizar el cambio cuando el monto cambia
        montoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    double montoIngresado = Double.parseDouble(newValue);
                    actualizarCambio(montoIngresado);
                } catch (NumberFormatException e) {
                    // Ignorar si no es un número válido
                }
            } else {
                lblCambio.setText("Cambio: $0.00");
                lblCambio.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
            }
        });

        // Listener para habilitar/deshabilitar campo de referencia según método de pago
        metodoPagoCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Efectivo".equals(newVal)) {
                referenciaField.setDisable(true);
                referenciaField.clear();
            } else {
                referenciaField.setDisable(false);
            }
        });

        // Para debugging - valores por defecto
        lblCambio.setText("Cambio: $0.00");

        // Calcular y mostrar el total del carrito automáticamente
        actualizarTotalCarrito();

        System.out.println("✅ Controlador de pagos inicializado");
    }

    // Método para actualizar el total del carrito
    private void actualizarTotalCarrito() {
        // Utilizar el método de DataModel para calcular el total
        this.montoTotalCarrito = DataModel.calcularTotalCarrito();

        // Actualizar las etiquetas
        if (lblTotalCarrito != null) {
            lblTotalCarrito.setText(String.format("Total a pagar: $%.2f", montoTotalCarrito));
            System.out.println("✅ Etiqueta Total actualizada: " + lblTotalCarrito.getText());
        } else {
            System.out.println("❌ lblTotalCarrito es null");
        }

        if (lblMontoRequerido != null) {
            lblMontoRequerido.setText(String.format("Debe pagar: $%.2f", montoTotalCarrito));
            System.out.println("✅ Etiqueta MontoRequerido actualizada: " + lblMontoRequerido.getText());
        } else {
            System.out.println("❌ lblMontoRequerido es null");
        }
    }

    @FXML
    private void volverMenu(ActionEvent event) {
        cambiarVentana(event, "pedido-view.fxml", "Carrito de Compras");
    }

    private void cambiarVentana(ActionEvent event, String fxmlFile, String title) {
        try {
            URL url = getClass().getResource("/org/tiendaGUI/" + fxmlFile);
            if (url == null) {
                throw new IllegalStateException("No se pudo encontrar el archivo FXML: " + fxmlFile);
            }

            FXMLLoader loader = new FXMLLoader(url);
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

    private void actualizarCambio(double montoIngresado) {
        // Calcular la diferencia entre el monto ingresado y el total del carrito
        double cambio = montoIngresado - montoTotalCarrito;

        if (cambio >= 0) {
            lblCambio.setText(String.format("Cambio: $%.2f", cambio));
            lblCambio.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            lblCambio.setText(String.format("Falta: $%.2f", Math.abs(cambio)));
            lblCambio.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }

        // Animar el cambio para hacerlo más visible
        animarCambioLabel();
    }

    // ESTE ES EL MÉTODO CLAVE
    public void setMontoTotalCarrito(double monto) {
        System.out.println("💰 Estableciendo monto total del carrito: $" + monto);

        // Asegurar que estamos en el hilo JavaFX
        if (Platform.isFxApplicationThread()) {
            actualizarInterfazMonto(monto);
        } else {
            Platform.runLater(() -> actualizarInterfazMonto(monto));
        }
    }

    // Método auxiliar para actualizar la interfaz con el monto
    private void actualizarInterfazMonto(double monto) {
        this.montoTotalCarrito = monto;

        // Actualizar etiquetas
        if (lblTotalCarrito != null) {
            lblTotalCarrito.setText(String.format("Total a pagar: $%.2f", montoTotalCarrito));
            System.out.println("✅ Etiqueta Total actualizada: " + lblTotalCarrito.getText());
        } else {
            System.out.println("❌ lblTotalCarrito es null");
        }

        if (lblMontoRequerido != null) {
            lblMontoRequerido.setText(String.format("Debe pagar: $%.2f", montoTotalCarrito));
            System.out.println("✅ Etiqueta MontoRequerido actualizada: " + lblMontoRequerido.getText());
        } else {
            System.out.println("❌ lblMontoRequerido es null");
        }

        // Si ya hay un monto ingresado, actualizar el cambio
        if (montoField != null && !montoField.getText().isEmpty()) {
            try {
                double montoIngresado = Double.parseDouble(montoField.getText());
                actualizarCambio(montoIngresado);
            } catch (NumberFormatException e) {
                // Ignorar si no es un número válido
            }
        }
    }

    @FXML
    private void btnProcesarAction() {
        try {
            // Verificación de monto ingresado
            if (montoField.getText().isEmpty()) {
                mostrarAlerta("Error", "Debe ingresar un monto", Alert.AlertType.ERROR);
                return;
            }

            double montoIngresado = Double.parseDouble(montoField.getText());

            if (montoIngresado <= 0) {
                mostrarAlerta("Error", "El monto debe ser mayor a cero", Alert.AlertType.ERROR);
                return;
            }

            // Verificar que el monto sea suficiente para cubrir el total
            if (montoIngresado < montoTotalCarrito) {
                montoField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                mostrarAlerta("Error",
                        String.format("El monto ingresado ($%.2f) es menor que el total a pagar ($%.2f)",
                                montoIngresado, montoTotalCarrito),
                        Alert.AlertType.ERROR);
                return;
            } else {
                montoField.setStyle(""); // Limpia el estilo si es válido
            }

            // Calcular el cambio
            double cambio = montoIngresado - montoTotalCarrito;

            // Verificación de referencia para métodos de pago distintos a efectivo
            String metodoPago = metodoPagoCombo.getValue();
            if (!metodoPago.equals("Efectivo") && (referenciaField.getText() == null || referenciaField.getText().trim().isEmpty())) {
                mostrarAlerta("Error", "Debe ingresar una referencia para el método de pago " + metodoPago, Alert.AlertType.ERROR);
                return;
            }

            // Registrar el pago
            registrarPago(montoIngresado, cambio);

            // Limpiar el carrito después de pagar
            DataModel.getCarritoVentas().clear();

            // Mostrar confirmación
            mostrarAlerta("Éxito", String.format(""" 
                                    Pago procesado correctamente
                                    Total pagado: $%.2f
                                    Total de la compra: $%.2f
                                    Cambio: $%.2f""",
                            montoIngresado, montoTotalCarrito, cambio),
                    Alert.AlertType.INFORMATION);

            limpiarCampos();

            // Actualizar el total del carrito después de limpiar
            actualizarTotalCarrito();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El monto debe ser un número válido", Alert.AlertType.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al procesar el pago: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void btnCancelarAction() {
        limpiarCampos();
    }

    private void limpiarCampos() {
        montoField.clear();
        referenciaField.clear();
        metodoPagoCombo.setValue("Efectivo"); // Restablecer al valor por defecto
        lblCambio.setText("Cambio: $0.00");
        lblCambio.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        montoField.setStyle(""); // Limpiar cualquier estilo de error
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void registrarPago(double montoPagado, double cambio) {
        // Generar un ID único para el pago
        String idPago = UUID.randomUUID().toString().substring(0, 8);

        // Obtener la fecha y hora actual formateada
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Obtener el método de pago seleccionado
        String metodoPago = metodoPagoCombo.getValue();

        // Determinar la referencia según el método de pago
        String referencia;
        if ("Efectivo".equals(metodoPago)) {
            referencia = String.format("Pagado: $%.2f, Cambio: $%.2f", montoPagado, cambio);
        } else {
            referencia = referenciaField.getText();
        }

        // Crear el objeto Pago
        Pago nuevoPago = new Pago(
                idPago,
                montoTotalCarrito, // Monto de la compra, no el pagado
                metodoPago,
                fecha,
                "Completado",
                referencia
        );

        // Agregar el pago a la lista observable y al modelo de datos
        listaPagos.add(nuevoPago);
        DataModel.getPagos().add(nuevoPago);

        // Actualizar la tabla
        tablaPagos.refresh();

        System.out.println("✅ Pago registrado: ID=" + idPago + ", Monto=$" + montoTotalCarrito);
    }

    private void animarCambioLabel() {
        FadeTransition ft = new FadeTransition(Duration.millis(200), lblCambio);
        ft.setFromValue(0.5);
        ft.setToValue(1.0);
        ft.setCycleCount(1);
        ft.play();
    }
}