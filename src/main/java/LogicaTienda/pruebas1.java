package LogicaTienda;

import LogicaTienda.Data.DataSerializer;
import LogicaTienda.Model.Productos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class pruebas1 {
    public static void main(String[] args) {
        // Crear un serializador para el archivo productos.json
        DataSerializer dataSerializer = new DataSerializer("productos.json");
        
        // Cargar productos existentes
        ObservableList<Productos> productos = dataSerializer.deserializeData();
        System.out.println("Productos cargados: " + productos.size());
        
        // Mostrar los productos cargados
        for (Productos producto : productos) {
            System.out.println("Producto: " + producto.getNombre());
            System.out.println("  Precio: " + producto.getPrecio());
            System.out.println("  % Ganancia: " + producto.getPorcentajeGanancia());
            System.out.println("  Precio de venta: " + producto.getPrecioParaVender());
        }
        
        // Actualizar el porcentaje de ganancia de los productos existentes
        for (Productos producto : productos) {
            if (producto.getPorcentajeGanancia() == 0.0) {
                producto.setPorcentajeGanancia(20.0); // Establecer un 20% de ganancia
                producto.calcularPrecioVenta(); // Recalcular el precio de venta
                System.out.println("Actualizado " + producto.getNombre() + " con 20% de ganancia");
                System.out.println("  Nuevo precio de venta: " + producto.getPrecioParaVender());
            }
        }
        
        // Guardar los productos actualizados
        dataSerializer.serializeData(productos);
        System.out.println("Productos guardados en productos.json");
        
        // Cargar nuevamente para verificar
        ObservableList<Productos> productosRecargados = dataSerializer.deserializeData();
        System.out.println("\nProductos recargados: " + productosRecargados.size());
        
        // Mostrar los productos recargados
        for (Productos producto : productosRecargados) {
            System.out.println("Producto: " + producto.getNombre());
            System.out.println("  Precio: " + producto.getPrecio());
            System.out.println("  % Ganancia: " + producto.getPorcentajeGanancia());
            System.out.println("  Precio de venta: " + producto.getPrecioParaVender());
        }
    }
}