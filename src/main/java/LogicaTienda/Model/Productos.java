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
    private int cantidad;
    private int stock;
    public Productos(String idProducto, String nombre, double precio, int cantidad, int stock) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.stock = stock;
    }
    public String toString() {
        return "nombre del producto: "+this.nombre+" ID del producto: "+this.idProducto+" cantidad en almacen: "+this.stock+" el precio del producto "+this.precio;
    }
}