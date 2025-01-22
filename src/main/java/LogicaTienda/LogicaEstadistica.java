package LogicaTienda;

import java.util.List;
import java.util.OptionalDouble;

public class LogicaEstadistica {
    private List<Productos> listaProductos;

    public LogicaEstadistica(List<Productos> listaProductos) {
        this.listaProductos = listaProductos;
    }

    // Calcular el promedio de precios de los productos
    public double calcularPromedioPrecios() {
        OptionalDouble promedio = listaProductos.stream()
                .mapToDouble(Productos::getPrecio)
                .average();
        return promedio.isPresent() ? promedio.getAsDouble() : 0.0;
    }

    // Calcular la suma total de todos los precios de los productos
    public double calcularSumaTotalPrecios() {
        return listaProductos.stream()
                .mapToDouble(Productos::getPrecio)
                .sum();
    }

    // Calcular la cantidad total de productos en stock
    public int calcularTotalProductosEnStock() {
        return listaProductos.stream()
                .mapToInt(Productos::getStock)
                .sum();
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
}
