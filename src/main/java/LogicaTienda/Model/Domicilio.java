package LogicaTienda.Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Domicilio implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idDomicilio;
    private String direccion;
    private String referenciaDireccion;
    private String numeroPostal;
    private String numeroApartamento;
    private String numeroCelular;
    private LocalDate fechaEntrega;
    private String idFactura;
    private String estadoDomicilio; // "Pendiente", "En camino", "Entregado", "Cancelado"

    public Domicilio(String direccion, String referenciaDireccion, String numeroPostal,
                     String numeroApartamento, String numeroCelular, LocalDate fechaEntrega,
                     String idFactura) {
        this.idDomicilio = generarIdDomicilio();
        this.direccion = direccion;
        this.referenciaDireccion = referenciaDireccion;
        this.numeroPostal = numeroPostal;
        this.numeroApartamento = numeroApartamento;
        this.numeroCelular = numeroCelular;
        this.fechaEntrega = fechaEntrega;
        this.idFactura = idFactura;
        this.estadoDomicilio = "Pendiente"; // Estado por defecto al crear un domicilio
    }

    private String generarIdDomicilio() {
        // Generamos un ID único basado en timestamp
        return "DOM-" + System.currentTimeMillis();
    }
}