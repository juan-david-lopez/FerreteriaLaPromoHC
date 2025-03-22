package LogicaTienda;

import LogicaTienda.Model.Productos;

import java.util.ArrayList;
import java.util.List;

public class LogicaEstadistica {
    private List<Productos> listaProductos;

    public LogicaEstadistica(List<Productos> listaProductos) {
        this.listaProductos = (listaProductos != null) ? listaProductos : new ArrayList<>();
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
        return listaProductos.stream().mapToInt(Productos::getStock).sum();
    }

    // Calcular el precio más alto entre los productos
    public double calcularPrecioMasAlto() {
        return listaProductos.stream().mapToDouble(Productos::getPrecio).max().orElse(0.0);
    }

    // Calcular el precio más bajo entre los productos
    public double calcularPrecioMasBajo() {
        return listaProductos.stream().mapToDouble(Productos::getPrecio).min().orElse(0.0);
    }

    // Convertir un string en un objeto Productos
    public static Productos ConvertirAobjetoProducto(String productos) {
        try {
            String[] centinela = productos.split(";");
            if (centinela.length < 5) {
                throw new IllegalArgumentException("El formato del string de producto es incorrecto.");
            }
            return new Productos(
                    centinela[0],                      // Nombre
                    centinela[1],                      // Categoría
                    Double.parseDouble(centinela[2]),  // Precio
                    Integer.parseInt(centinela[3]),    // Stock
                    Integer.parseInt(centinela[4])     // ID u otro dato
            );
        } catch (NumberFormatException e) {
            System.err.println("Error al convertir valores numéricos: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error en el formato de entrada: " + e.getMessage());
        }
        return null; // Retorna null si la conversión falla
    }
}
