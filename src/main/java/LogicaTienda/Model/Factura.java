package LogicaTienda.Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class Factura {
    private String idFactura;
    private String idPedido;
    private String idDomicilio;
    private String fecha;
    private String formaPago;
    private String tipoFactura;
    private double total;
    public Factura(String idFactura, String idPedido, String idDomicilio, String fecha, String formaPago, double total, String tipoFactura) {
        this.idFactura = idFactura;
        this.idPedido = idPedido;
        this.idDomicilio = idDomicilio;
        this.fecha = fecha;
        this.formaPago = formaPago;
        this.total = total;
        this.tipoFactura = tipoFactura;
    }

}
