package LogicaTienda.Services;

import LogicaTienda.Data.MongoDBConnection;
import LogicaTienda.Model.Productos;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class ProductoService {
    private static final MongoCollection<Productos> productosCollection = 
            MongoDBConnection.getCollection("productos", Productos.class);

    public static List<Productos> obtenerTodosLosProductos() {
        return productosCollection.find().into(new ArrayList<>());
    }

    public static Productos buscarProductoPorId(String id) {
        return productosCollection.find(Filters.eq("idProducto", id)).first();
    }

    public static void guardarProducto(Productos producto) {
        // Si el producto ya existe, actualizarlo
        if (producto.getIdProducto() != null && buscarProductoPorId(producto.getIdProducto()) != null) {
            actualizarProducto(producto);
        } else {
            // Si es un producto nuevo, generamos un ID
            if (producto.getIdProducto() == null || producto.getIdProducto().isEmpty()) {
                producto.setIdProducto(java.util.UUID.randomUUID().toString().substring(0, 8));
            }
            productosCollection.insertOne(producto);
        }
    }

    public static void actualizarProducto(Productos producto) {
        Bson filter = Filters.eq("idProducto", producto.getIdProducto());
        Bson updates = Updates.combine(
            Updates.set("nombre", producto.getNombre()),
            Updates.set("precio", producto.getPrecio()),
            Updates.set("precioParaVender", producto.getPrecioParaVender()),
            Updates.set("porcentajeGanancia", producto.getPorcentajeGanancia()),
            Updates.set("cantidad", producto.getCantidad()),
            Updates.set("stock", producto.getStock())
        );
        productosCollection.updateOne(filter, updates);
    }

    public static void eliminarProducto(String idProducto) {
        productosCollection.deleteOne(Filters.eq("idProducto", idProducto));
    }

    public static void actualizarStock(String idProducto, int cantidad, boolean esVenta) {
        if (idProducto == null || idProducto.isEmpty()) {
            System.err.println("Error: ID de producto no válido");
            return;
        }

        // Usar una operación atómica para actualizar el stock directamente en MongoDB
        Bson filter = Filters.eq("idProducto", idProducto);
        
        try {
            if (esVenta) {
                // Para ventas: Primero intentamos descontar de la cantidad disponible
                Bson update = Updates.combine(
                    Updates.inc("cantidad", -Math.min(cantidad, buscarProductoPorId(idProducto).getCantidad())),
                    Updates.inc("stock", -Math.max(0, cantidad - buscarProductoPorId(idProducto).getCantidad()))
                );
                productosCollection.updateOne(filter, update);
                
                // Verificar si hay suficiente stock después de la operación
                Productos productoActualizado = buscarProductoPorId(idProducto);
                if (productoActualizado.getCantidad() < 0 || productoActualizado.getStock() < 0) {
                    // Ajustar valores negativos a cero
                    Bson ajuste = Updates.combine(
                        Updates.set("cantidad", Math.max(0, productoActualizado.getCantidad())),
                        Updates.set("stock", Math.max(0, productoActualizado.getStock()))
                    );
                    productosCollection.updateOne(filter, ajuste);
                    
                    System.out.println("⚠️ No hay suficiente stock para el producto: " + 
                                      productoActualizado.getNombre() + 
                                      ". Se ajustaron los valores.");
                }
            } else {
                // Para devoluciones: Añadir al inventario
                Bson update = Updates.inc("cantidad", cantidad);
                productosCollection.updateOne(filter, update);
            }
            
            // Registrar la operación
            System.out.println("Stock actualizado para el producto " + idProducto + 
                             ". Operación: " + (esVenta ? "Venta" : "Devolución") + 
                             ", Cantidad: " + cantidad);
        } catch (Exception e) {
            System.err.println("Error al actualizar el stock del producto " + idProducto + ": " + e.getMessage());
        }
    }
}
