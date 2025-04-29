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

    @Getter
    private static final ObservableList<Productos> productos = FXCollections.observableArrayList();
    @Getter
    private static final ObservableList<Productos> carritoVentas = FXCollections.observableArrayList();
    @Getter
    private static final ObservableList<Pago> pagos = FXCollections.observableArrayList();
    @Getter
    private static final ObservableList<Factura> facturas = FXCollections.observableArrayList();
    @Getter
    private static final ObservableList<Domicilio> domicilios = FXCollections.observableArrayList();

    @Getter @Setter
    private static Domicilio domicilioActual;

    private static final String FACTURAS_FILE = "facturas.dat";

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

        Factura factura = new Factura(productosComprados, pago, clienteNombre, clienteIdentificacion);
        facturas.add(factura);
        carritoVentas.clear(); // ✅ limpia el carrito tras la compra
        guardarFacturas();
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
    public static void cargarFacturas() {
        File file = new File(FACTURAS_FILE);
        if (!file.exists()) {
            System.out.println("ℹ️ No existe archivo de facturas. Se creará uno nuevo.");
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            ArrayList<Factura> lista = (ArrayList<Factura>) in.readObject();
            facturas.setAll(lista);
            System.out.println("✅ Facturas cargadas exitosamente: " + facturas.size());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error al cargar las facturas: " + e.getMessage());
        }
    }

    public static void inicializar() {
        cargarFacturas();
        System.out.println("✅ DataModel inicializado correctamente");
    }
}
