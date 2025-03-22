package LogicaTienda.Model;

import java.io.Serializable;

public class Productos implements Serializable {

    private String idProducto;
    private String nombre;
    private double precio;
    private int cantidad;
    private int stock;
    public Productos(String idProducto, String nombre, String descripcion, double precio, int cantidad, int stock) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.stock = stock;
    }

    public Productos(String idProducto, String nombre, double precio, int cantidad, int stock) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.stock = stock;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    public String getIdProducto() {
        return idProducto;
    }
    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }
    @Override
    public String toString() {
        return "nombre del producto: "+this.nombre+" ID del producto: "+this.idProducto+" cantidad en almacen: "+this.stock+" el precio del producto "+this.precio;
    }
}
