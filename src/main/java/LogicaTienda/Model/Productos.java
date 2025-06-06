package LogicaTienda.Model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
public class Productos implements Serializable {

    private String idProducto;
    private String nombre;
    private double precio;
    private double precioParaVender;
    private double porcentajeGanancia;
    private int cantidad;
    private int stock;
    public Productos(String idProducto, String nombre, double precio, int cantidad, int stock) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.stock = stock;
        this.porcentajeGanancia = 0;
        this.precioParaVender = precio;
    }

    public Productos(String idProducto, String nombre, double precio, double porcentajeGanancia, int cantidad, int stock) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.porcentajeGanancia = porcentajeGanancia;
        this.cantidad = cantidad;
        this.stock = stock;
        calcularPrecioVenta();
    }

    public void calcularPrecioVenta() {
        this.precioParaVender = this.precio * (1 + (this.porcentajeGanancia / 100));
    }
    public String toString() {
        return "nombre del producto: "+this.nombre+" ID del producto: "+this.idProducto+" cantidad en almacen: "+this.stock+" el precio de costo: "+this.precio+" el precio de venta: "+this.precioParaVender;
    }
}
