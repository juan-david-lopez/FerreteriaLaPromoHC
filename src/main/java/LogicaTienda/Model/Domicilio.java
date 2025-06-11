package LogicaTienda.Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Domicilio implements Serializable {
    private static final long serialVersionUID = 1L;

    @BsonId
    private String id;

    @BsonProperty("direccion")
    private String direccion;

    @BsonProperty("referencia_direccion")
    private String referenciaDireccion;

    @BsonProperty("numero_postal")
    private String numeroPostal;

    @BsonProperty("numero_apartamento")
    private String numeroApartamento;

    @BsonProperty("numero_celular")
    private String numeroCelular;

    @BsonProperty("fecha_entrega")
    private LocalDate fechaEntrega;

    @BsonProperty("id_factura")
    private String idFactura;

    @BsonProperty("cliente_identificacion")
    private String clienteIdentificacion;

    @BsonProperty("estado_domicilio")
    private String estadoDomicilio; // "Pendiente", "En camino", "Entregado", "Cancelado"

    public Domicilio(String direccion, String referenciaDireccion, String numeroPostal,
                    String numeroApartamento, String numeroCelular, LocalDate fechaEntrega,
                    String idFactura, String clienteIdentificacion) {
        this.direccion = direccion;
        this.referenciaDireccion = referenciaDireccion;
        this.numeroPostal = numeroPostal;
        this.numeroApartamento = numeroApartamento;
        this.numeroCelular = numeroCelular;
        this.fechaEntrega = fechaEntrega;
        this.idFactura = idFactura;
        this.clienteIdentificacion = clienteIdentificacion;
        this.estadoDomicilio = "Pendiente"; // Estado por defecto al crear un domicilio
    }
}