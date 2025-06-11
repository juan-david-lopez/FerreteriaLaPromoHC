package LogicaTienda.Services;

import LogicaTienda.Data.MongoDBConnection;
import LogicaTienda.Model.Factura;
import LogicaTienda.Model.Productos;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FacturaService {
    private static final MongoCollection<Factura> facturasCollection = 
            MongoDBConnection.getCollection("facturas", Factura.class);
    private static final MongoCollection<Document> facturasRawCollection = 
            MongoDBConnection.getDatabase().getCollection("facturas");

    public static List<Factura> obtenerTodasLasFacturas() {
        return facturasCollection.find().into(new ArrayList<>());
    }

    public static List<Factura> obtenerFacturasActivas() {
        return facturasCollection.find(Filters.eq("eliminada", false)).into(new ArrayList<>());
    }

    public static Factura buscarFacturaPorId(String id) {
        return facturasCollection.find(Filters.eq("id", id)).first();
    }

    public static String crearFactura(List<Productos> productos, String clienteNombre, String clienteIdentificacion, 
                                    double montoTotal, String metodoPago, String referenciaPago, 
                                    String clienteEmail, String clienteTelefono) {
        String facturaId = UUID.randomUUID().toString().substring(0, 8);
        
        // Crear la factura con los productos
        Factura factura = new Factura();
        factura.setId(facturaId);
        factura.setFecha(LocalDateTime.now());
        factura.setProductos(new ArrayList<>(productos));
        factura.setClienteNombre(clienteNombre);
        factura.setClienteIdentificacion(clienteIdentificacion);
        factura.setClienteEmail(clienteEmail);
        factura.setClienteTelefono(clienteTelefono);
        factura.setTotal(montoTotal);
        factura.setMetodoPago(metodoPago);
        factura.setReferenciaPago(referenciaPago);
        factura.setEliminada(false);
        factura.setEstado("Activa");
        
        // Guardar la factura
        facturasCollection.insertOne(factura);
        
        // Actualizar el inventario
        for (Productos producto : productos) {
            ProductoService.actualizarStock(producto.getIdProducto(), producto.getCantidad(), true);
        }
        
        return facturaId;
    }

    public static void anularFactura(String idFactura) {
        Bson filter = Filters.eq("id", idFactura);
        Bson update = Updates.set("eliminada", true);
        facturasCollection.updateOne(filter, update);
        
        // Devolver productos al inventario
        Factura factura = buscarFacturaPorId(idFactura);
        if (factura != null) {
            for (Productos producto : factura.getProductos()) {
                ProductoService.actualizarStock(producto.getIdProducto(), producto.getCantidad(), false);
            }
        }
    }

    public static void restaurarFactura(String idFactura) {
        Bson filter = Filters.eq("id", idFactura);
        Bson update = Updates.set("eliminada", false);
        facturasCollection.updateOne(filter, update);
        
        // Volver a descontar los productos del inventario
        Factura factura = buscarFacturaPorId(idFactura);
        if (factura != null) {
            for (Productos producto : factura.getProductos()) {
                ProductoService.actualizarStock(producto.getIdProducto(), producto.getCantidad(), true);
            }
        }
    }

    public static double calcularTotalVentas() {
        List<Factura> facturas = obtenerFacturasActivas();
        return facturas.stream()
                .flatMap(f -> f.getProductos().stream())
                .mapToDouble(p -> p.getPrecioParaVender() * p.getCantidad())
                .sum();
    }
    
    /**
     * Actualiza una factura existente en la base de datos
     * @param factura La factura con los datos actualizados
     */
    public static void actualizarFactura(Factura factura) {
        if (factura == null || factura.getId() == null) {
            throw new IllegalArgumentException("La factura y su ID no pueden ser nulos");
        }
        
        Bson filter = Filters.eq("id", factura.getId());
        Bson update = Updates.combine(
            Updates.set("clienteNombre", factura.getClienteNombre()),
            Updates.set("clienteIdentificacion", factura.getClienteIdentificacion()),
            Updates.set("estado", factura.getEstado()),
            Updates.set("eliminada", factura.isEliminada()),
            Updates.set("fecha", factura.getFecha()),
            Updates.set("productos", factura.getProductos())
        );
        
        facturasCollection.updateOne(filter, update);
    }
}
