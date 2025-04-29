package org.tiendaGUI.Controllers;

import LogicaTienda.Data.DataModel;
import LogicaTienda.Data.DataSerializer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Setter;
import org.tiendaGUI.Controllers.loader.ViewLoader;
import org.tiendaGUI.DTO.CarritoDTO;
import org.tiendaGUI.DTO.DomicilioDTO;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class DomicilioController implements Initializable {

    @FXML private TextField txtDireccion;
    @FXML private TextField txtReferenciaDireccion;
    @FXML private TextField txtNumeroPostal;
    @FXML private TextField txtNumeroApartamento;
    @FXML private TextField txtNumeroCelular;
    @FXML private DatePicker dpFechaEntrega;
    @FXML private TextField txtIdFactura;

    @FXML private Button btnEnviar;
    @FXML private Button btnVolver;
    private DataModel dataModel;
    private DomicilioDTO domicilioDTO;
    private final DataSerializer domicilioSerializer = new DataSerializer("domicilios.json");
    @Setter
    private List<String> idFacturasValidas;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DataModel.getFacturas().forEach(facturaDTO -> {
            // Verifica si facturaDTO tiene el método getIdFactura()
            idFacturasValidas.add(facturaDTO.getId()); // Agrega el ID de cada FacturaDTO
        });
        System.out.println("📦 Inicializando controlador de domicilio.");
        if (dpFechaEntrega != null) {
            dpFechaEntrega.setValue(LocalDate.now().plusDays(1));
        }
        cargarDomicilios();
    }

    public void setDomicilioDTO(DomicilioDTO dto) {
        this.domicilioDTO = dto;
        txtDireccion.setText(dto.getDireccion());
        txtReferenciaDireccion.setText(dto.getReferenciaDireccion());
        txtNumeroPostal.setText(dto.getNumeroPostal());
        txtNumeroApartamento.setText(dto.getNumeroApartamento());
        txtNumeroCelular.setText(dto.getNumeroCelular());
        dpFechaEntrega.setValue(LocalDate.parse(dto.getFechaEntrega()));
        txtIdFactura.setText(dto.getIdFactura());
    }

    @FXML
    private void btnEnviarAction(ActionEvent event) {
        if (!validarFormulario()) return;

        DomicilioDTO dto = new DomicilioDTO(
                txtDireccion.getText(),
                txtReferenciaDireccion.getText(),
                txtNumeroPostal.getText(),
                txtNumeroApartamento.getText(),
                txtNumeroCelular.getText(),
                dpFechaEntrega.getValue().toString(),
                txtIdFactura.getText()
        );
        irAVistaConfirmacion(event, dto);
    }

    private void irAVistaConfirmacion(ActionEvent event, DomicilioDTO dto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/domicilio-confirmacion-view.fxml"));
            Parent root = loader.load();
            ConfirmacionDomicilioController controller = loader.getController();
            controller.setDomicilioDTO(dto);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Confirmación de Domicilio");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista de confirmación.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void btnVolverAction(ActionEvent event) {
        cambiarVentana(event, "pedido-view.fxml", "Carrito de Compras");
    }

    private boolean validarFormulario() {
        if (txtDireccion.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "Debe ingresar una dirección", Alert.AlertType.ERROR);
            return false;
        }
        if (!Pattern.matches("\\d{10}", txtNumeroCelular.getText().trim())) {
            mostrarAlerta("Error", "El número de celular debe tener 10 dígitos", Alert.AlertType.ERROR);
            return false;
        }
        if (dpFechaEntrega.getValue() == null || dpFechaEntrega.getValue().isBefore(LocalDate.now())) {
            mostrarAlerta("Error", "La fecha de entrega debe ser hoy o futura", Alert.AlertType.ERROR);
            return false;
        }
        // 🚨 Validar si el ID de factura existe
        String idFacturaIngresado = txtIdFactura.getText();
        if (idFacturasValidas == null || !idFacturasValidas.contains(idFacturaIngresado)) {
            mostrarAlerta("Error", "El ID de factura no es válido.", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }


    private void cargarDomicilios() {
        try {
            DataModel.getDomicilios().clear();
            DataModel.getDomicilios().addAll(domicilioSerializer.deserializeDomicilios());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cambiarVentana(ActionEvent event, String fxmlFile, String title) {
        ViewLoader.cargarVista(event, fxmlFile, title);
    }


}
