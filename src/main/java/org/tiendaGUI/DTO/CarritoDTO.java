package org.tiendaGUI.DTO;

import LogicaTienda.Model.Productos;
import java.util.List;

public class CarritoDTO {

    private List<Productos> productos;
    private double total;

    public CarritoDTO(List<Productos> productos, double total) {
        this.productos = productos;
        this.total = total;
    }

    public List<Productos> getProductos() {
        return productos;
    }

    public double getTotal() {
        return total;
    }
}
