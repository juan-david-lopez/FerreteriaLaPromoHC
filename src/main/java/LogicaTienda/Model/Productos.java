package LogicaTienda.Model;

import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Getter
@Setter
public class Productos implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @BsonProperty("_id")
    private ObjectId id;  // MongoDB _id field

    @BsonProperty("idProducto")
    private String idProducto;
    @BsonProperty("nombre")
    private String nombre;
    @BsonProperty("precio")
    private double precio;
    @BsonProperty("precioParaVender")
    private double precioParaVender;
    @BsonProperty("porcentajeGanancia")
    private double porcentajeGanancia;
    @BsonProperty("cantidad")
    private int cantidad;
    @BsonProperty("stock")
    private int stock;
    
    // No-arg constructor required for MongoDB
    public Productos() {
        this.id = new ObjectId();
        this.idProducto = "";
        this.nombre = "";
        this.precio = 0.0;
        this.precioParaVender = 0.0;
        this.porcentajeGanancia = 0.0;
        this.cantidad = 0;
        this.stock = 0;
    }
    public Productos(String idProducto, String nombre, double precio, int cantidad, int stock) {
        this();
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.stock = stock;
        this.porcentajeGanancia = 0;
        this.precioParaVender = precio;
    }

    public Productos(String idProducto, String nombre, double precio, double porcentajeGanancia, int cantidad, int stock) {
        this();
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
