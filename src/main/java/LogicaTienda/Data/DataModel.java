package LogicaTienda.Data;

import LogicaTienda.Model.Domicilio;
import LogicaTienda.Model.Factura;
import LogicaTienda.Model.Pago;
import LogicaTienda.Model.Productos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataModel {

    // Static fields for global access
    @Getter
    private static final ObservableList<Productos> productos = FXCollections.observableArrayList();
    @Getter
    private static final ObservableList<Productos> carritoVentas = FXCollections.observableArrayList();
    @Getter
    private static final ObservableList<Pago> pagos = FXCollections.observableArrayList();

    // Non-static fields for instance-based access
    @Getter
    private static final ObservableList<Factura> facturas = FXCollections.observableArrayList();
    @Getter
    private static final ObservableList<Factura> facturasActivas = FXCollections.observableArrayList();
    @Getter
    private final ObservableList<Domicilio> domicilios = FXCollections.observableArrayList();

    @Getter @Setter
    private Domicilio domicilioActual;

    private static final String FACTURAS_FILE = "facturas.dat";
    private final String DOMICILIOS_FILE = "domicilios.json";
    private final DataSerializer serializer;

    // Constructor
    public DataModel() {
        this.serializer = new DataSerializer(DOMICILIOS_FILE);
    }

    // Static method to get a new instance
    public static DataModel getInstance() {
        return new DataModel();
    }

    public void guardarDomicilios() {
        serializer.serializeDomicilios(domicilios);
        System.out.println("✅ Domicilios guardados: " + domicilios.size());
    }

    public void cargarDomicilios() {
        List<Domicilio> lista = serializer.deserializeDomicilios();
        domicilios.setAll(lista);
        System.out.println("✅ Domicilios cargados: " + domicilios.size());
    }

    public static double calcularTotalCarrito() {
        return carritoVentas.stream()
                .mapToDouble(p -> p.getPrecio() * p.getCantidad())
                .sum();
    }

    public static void crearFactura(Pago pago, String clienteNombre, String clienteIdentificacion) {
        List<Productos> productosComprados = new ArrayList<>();
        for (Productos p : carritoVentas) {
            productosComprados.add(new Productos(
                    p.getIdProducto(), p.getNombre(), p.getPrecio(), p.getCantidad(), p.getStock()
            ));
            descontarInventario(p);
        }

        // Usar el ID del pago como ID de la factura para mantener consistencia
        Factura factura = new Factura(pago.getId(), productosComprados, pago, clienteNombre, clienteIdentificacion);
        facturas.add(factura);
        carritoVentas.clear(); // ✅ limpia el carrito tras la compra
        guardarFacturas();
        System.out.println("✅ Factura creada con ID: " + factura.getId());
    }

    private static void descontarInventario(Productos vendido) {
        for (Productos prod : productos) {
            if (prod.getIdProducto().equals(vendido.getIdProducto())) {
                if (prod.getCantidad() >= vendido.getCantidad()) {
                    prod.setCantidad(prod.getCantidad() - vendido.getCantidad());
                } else {
                    int restante = vendido.getCantidad() - prod.getCantidad();
                    prod.setCantidad(0);
                    prod.setStock(prod.getStock() - restante);
                }
                break;
            }
        }
    }

    public static void crearFacturaDialogo(Pago pago, String nombre, String cedula) {
        crearFactura(pago, nombre, cedula);
    }

    public static void guardarFacturas() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FACTURAS_FILE))) {
            out.writeObject(new ArrayList<>(facturas));
            System.out.println("✅ Facturas guardadas exitosamente: " + facturas.size());
        } catch (IOException e) {
            System.err.println("❌ Error al guardar las facturas: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void cargarFacturas() {
        File file = new File(FACTURAS_FILE);
        if (!file.exists()) {
            System.out.println("ℹ️ No existe archivo de facturas. Se creará uno nuevo.");
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            ArrayList<Factura> lista = (ArrayList<Factura>) in.readObject();
            facturas.setAll(lista);
            actualizarFacturasActivas();
            System.out.println("✅ Facturas cargadas exitosamente: " + facturas.size());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error al cargar las facturas: " + e.getMessage());
        }
    }

    public void inicializar() {
        cargarFacturas();
        actualizarFacturasActivas();
        System.out.println("✅ DataModel inicializado correctamente");
    }

    public List<Productos> getListaProductos() {
        return productos;
    }

    /**
     * Actualiza la lista de facturas activas filtrando las que no están marcadas como eliminadas
     */
    public static void actualizarFacturasActivas() {
        facturasActivas.clear();
        for (Factura factura : facturas) {
            if (!factura.isEliminada()) {
                facturasActivas.add(factura);
            }
        }
    }

    /**
     * Marca una factura como eliminada (borrado lógico)
     * @param factura La factura a eliminar
     */
    public static void eliminarFactura(Factura factura) {
        factura.setEliminada(true);
        actualizarFacturasActivas();
        guardarFacturas();
        System.out.println("✅ Factura marcada como eliminada: " + factura.getId());
    }

    /**
     * Restaura una factura eliminada
     * @param factura La factura a restaurar
     */
    public static void restaurarFactura(Factura factura) {
        factura.setEliminada(false);
        actualizarFacturasActivas();
        guardarFacturas();
        System.out.println("✅ Factura restaurada: " + factura.getId());
    }

    /**
     * Actualiza los datos de una factura existente
     * @param factura La factura con los datos actualizados
     */
    public static void actualizarFactura(Factura factura) {
        for (int i = 0; i < facturas.size(); i++) {
            if (facturas.get(i).getId().equals(factura.getId())) {
                facturas.set(i, factura);
                break;
            }
        }
        actualizarFacturasActivas();
        guardarFacturas();
        System.out.println("✅ Factura actualizada: " + factura.getId());
    }

    /**
     * Devuelve productos al inventario
     * @param productos Lista de productos a devolver al inventario
     */
    public static void devolverProductosAlInventario(List<Productos> productos) {
        for (Productos devuelto : productos) {
            boolean encontrado = false;
            for (Productos prod : DataModel.productos) {
                if (prod.getIdProducto().equals(devuelto.getIdProducto())) {
                    // Incrementar el stock o la cantidad según corresponda
                    if (prod.getStock() > 0) {
                        prod.setStock(prod.getStock() + devuelto.getCantidad());
                    } else {
                        prod.setCantidad(prod.getCantidad() + devuelto.getCantidad());
                    }
                    encontrado = true;
                    break;
                }
            }

            // Si el producto no existe en el inventario, lo agregamos
            if (!encontrado) {
                Productos nuevoProducto = new Productos(
                    devuelto.getIdProducto(), 
                    devuelto.getNombre(), 
                    devuelto.getPrecio(), 
                    devuelto.getCantidad(), 
                    0
                );
                DataModel.productos.add(nuevoProducto);
            }
        }
        System.out.println("✅ Productos devueltos al inventario: " + productos.size());
    }

    /**
     * Elimina productos de una factura y los devuelve al inventario
     * @param factura La factura de la que se eliminarán los productos
     * @param productosAEliminar Lista de productos a eliminar de la factura
     */
    public static void eliminarProductosDeFactura(Factura factura, List<Productos> productosAEliminar) {
        // Devolver los productos al inventario
        devolverProductosAlInventario(productosAEliminar);

        // Eliminar los productos de la factura
        factura.getProductos().removeAll(productosAEliminar);

        // Actualizar la factura
        actualizarFactura(factura);
        System.out.println("✅ Productos eliminados de la factura: " + productosAEliminar.size());
    }
}
