package LogicaTienda.Logic;

import LogicaTienda.Data.DataModel;
import LogicaTienda.Model.Factura;
import LogicaTienda.Model.Productos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LogicaEstadistica {
    private List<Productos> listaProductos;

    public LogicaEstadistica(List<Productos> listaProductos) {
        this.listaProductos = (listaProductos != null) ? listaProductos : new ArrayList<>();
    }

    // Calcular ventas por día
    public Map<LocalDate, Double> calcularVentasPorDia() {
        List<Factura> facturas = DataModel.getFacturas()
                .stream()
                .filter(f -> !"Anulada".equals(f.getEstado()))
                .collect(Collectors.toList());

        // Agrupar facturas por día y sumar los totales
        Map<LocalDate, Double> ventasPorDia = facturas.stream()
                .collect(Collectors.groupingBy(
                        factura -> factura.getFecha().toLocalDate(),
                        Collectors.summingDouble(Factura::getTotal)
                ));

        return ventasPorDia;
    }

    // Calcular el promedio de precios de los productos
    public double calcularPromedioPrecios() {
        return listaProductos.isEmpty() ? 0.0 :
                listaProductos.stream().mapToDouble(Productos::getPrecio).average().orElse(0.0);
    }

    // Calcular la suma total de todos los precios de los productos
    public double calcularSumaTotalPrecios() {
        return listaProductos.stream().mapToDouble(Productos::getPrecio).sum();
    }

    // Calcular la cantidad total de productos en stock
    public int calcularTotalProductosEnStock() {
        return listaProductos.stream().mapToInt(p -> p.getStock() + p.getCantidad()).sum();
    }
    
    public List<Productos> getListaProductos() {
        return listaProductos;
    }

    // Calcular el precio más alto entre los productos
    public double calcularPrecioMasAlto() {
        return listaProductos.stream()
                .mapToDouble(Productos::getPrecio)
                .max()
                .orElse(0.0);
    }

    // Calcular el precio más bajo entre los productos
    public double calcularPrecioMasBajo() {
        return listaProductos.stream()
                .mapToDouble(Productos::getPrecio)
                .min()
                .orElse(0.0);
    }

    // Obtener el producto más caro
    public Productos obtenerProductoMasCaro() {
        return listaProductos.stream()
                .max(Comparator.comparingDouble(Productos::getPrecio))
                .orElse(null);
    }

    // Obtener el producto con más stock
    public Productos obtenerProductoConMasStock() {
        return listaProductos.stream()
                .max(Comparator.comparingInt(Productos::getStock))
                .orElse(null);
    }

    // Calcular el total de ventas sumando todas las facturas (excluyendo anuladas)
    public double calcularTotalVentas() {
        return DataModel.getFacturas().stream()
                .filter(f -> !"Anulada".equals(f.getEstado()))
                .mapToDouble(Factura::getTotal)
                .sum();
    }

    // Calcular el valor total del inventario (precio de venta * stock para todos los productos)
    public double calcularValorTotalInventario() {
        return listaProductos.stream()
                .mapToDouble(producto -> producto.getPrecioParaVender() * (producto.getStock() + producto.getCantidad()))
                .sum();
    }
}
