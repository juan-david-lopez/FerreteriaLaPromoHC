package org.tiendaGUI.Controllers;

import LogicaTienda.Data.DataModel;
import LogicaTienda.Data.DataSerializer;
import LogicaTienda.Model.Domicilio;
import LogicaTienda.Model.Factura;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.tiendaGUI.DTO.DomicilioDTO;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ConfirmacionDomicilioController implements Initializable {

    @FXML private Label lblDireccion;
    @FXML private Label lblReferencia;
    @FXML private Label lblPostal;
    @FXML private Label lblApartamento;
    @FXML private Label lblCelular;
    @FXML private Label lblFechaEntrega;
    @FXML private Label lblFactura;
    @FXML private Button btnContinuar;
    @FXML private Button btnVolver;

    private DomicilioDTO domicilioDTO;
    private final DataSerializer serializer = new DataSerializer("domicilios.json");
    private final DataModel dataModel = new DataModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataModel.cargarFacturas();
        dataModel.cargarDomicilios();
        System.out.println("✅ DataModel inicializado correctamente");
    }

    /**
     * Recibe el DTO desde DomicilioController y muestra sus datos.
     */
    public void setDomicilioDTO(DomicilioDTO dto) {
        this.domicilioDTO = dto;
        mostrarDatos();
    }

    private void mostrarDatos() {
        if (domicilioDTO == null) {
            System.err.println("No hay datos de domicilio para mostrar");
            return;
        }
        lblDireccion.setText(domicilioDTO.getDireccion());
        lblReferencia.setText(domicilioDTO.getReferenciaDireccion());
        lblPostal.setText(domicilioDTO.getNumeroPostal());
        lblApartamento.setText(domicilioDTO.getNumeroApartamento());
        lblCelular.setText(domicilioDTO.getNumeroCelular());
        lblFechaEntrega.setText(domicilioDTO.getFechaEntrega());
        lblFactura.setText(domicilioDTO.getIdFactura());
    }

    @FXML
    private void btnContinuarAction(ActionEvent event) {
        if (!validarCampos()) return;

        // Convertir DTO a modelo
        Domicilio domicilio = new Domicilio(
                domicilioDTO.getDireccion(),
                domicilioDTO.getReferenciaDireccion(),
                domicilioDTO.getNumeroPostal(),
                domicilioDTO.getNumeroApartamento(),
                domicilioDTO.getNumeroCelular(),
                LocalDate.parse(domicilioDTO.getFechaEntrega()),
                domicilioDTO.getIdFactura()
        );

        // Leer los domicilios actuales desde el archivo (por si DataModel está desactualizado)
        ObservableList<Domicilio> domiciliosActuales = serializer.deserializeDomicilios();

        domiciliosActuales.add(domicilio);

        serializer.serializeDomicilios(domiciliosActuales);

        dataModel.setDomicilioActual(domicilio);

        navegar(event, "/org/tiendaGUI/pedido-view.fxml", "Carrito de Compras");
    }


    @FXML
    private void btnVolverAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/domicilio-view.fxml"));
            Parent root = loader.load();
            DomicilioController ctrl = loader.getController();
            // Pasa DTO de nuevo
            ctrl.setDomicilioDTO(domicilioDTO);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Domicilio");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo volver al formulario").showAndWait();
        }
    }

    private boolean validarCampos() {
        if (domicilioDTO == null) return false;
        if (!Pattern.matches("\\d{10}", domicilioDTO.getNumeroCelular())) {
            new Alert(Alert.AlertType.ERROR, "El celular debe tener 10 dígitos").showAndWait(); return false;
        }
        LocalDate fecha = LocalDate.parse(domicilioDTO.getFechaEntrega());
        if (fecha.isBefore(LocalDate.now())) {
            new Alert(Alert.AlertType.ERROR, "Fecha de entrega inválida").showAndWait(); return false;
        }
        List<Factura> facs = dataModel.getFacturas();
        String idFacturaIngresado = domicilioDTO.getIdFactura().trim().toLowerCase();
        if (!facs.stream().anyMatch(f -> f.getId().trim().toLowerCase().equals(idFacturaIngresado))) {
            new Alert(Alert.AlertType.ERROR, "Factura no encontrada").showAndWait(); return false;
        }
        return true;
    }

    private void navegar(ActionEvent event, String fxml, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
