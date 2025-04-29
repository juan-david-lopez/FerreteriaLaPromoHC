package LogicaTienda.Model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
public class Factura implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private LocalDateTime fecha;
    private List<Productos> productos;
    private Pago pago;
    private String clienteNombre;
    private String clienteIdentificacion;
    private String tipoFactura;

    public Factura() {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.fecha = LocalDateTime.now();
        this.productos = new ArrayList<>();
    }

    public Factura(List<Productos> productos, Pago pago) {
        this();
        this.productos = new ArrayList<>(productos);
        this.pago = pago;
    }

    public Factura(List<Productos> productos, Pago pago, String clienteNombre, String clienteIdentificacion) {
        this(productos, pago);
        this.clienteNombre = clienteNombre;
        this.clienteIdentificacion = clienteIdentificacion;
    }
    public double getTotal() {
        return productos.stream()
                .mapToDouble(p -> p.getPrecio() * p.getCantidad())
                .sum();
    }
    @Override
    public String toString() {
        return "Factura [ID=" + id + ", Fecha=" + fecha + ", Total=" + getTotal() + ", Cliente=" + clienteNombre + "]";
    }
}