package test;

import LogicaTienda.Model.Productos;
import LogicaTienda.Services.FacturaService;
import LogicaTienda.Services.ProductoService;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FacturaTest {
    
    private List<Productos> productosDePrueba;
    
    @BeforeAll
    public void setUp() {
        // Crear productos de prueba
        productosDePrueba = new ArrayList<>();
        
        // Producto 1
        Productos producto1 = new Productos("PROD001", "Martillo", 15000, 20, 10, 5);
        producto1.calcularPrecioVenta();
        
        // Producto 2
        Productos producto2 = new Productos("PROD002", "Destornillador", 8000, 15, 20, 10);
        producto2.calcularPrecioVenta();
        
        // Guardar productos en la base de datos
        ProductoService.guardarProducto(producto1);
        ProductoService.guardarProducto(producto2);
        
        productosDePrueba.add(producto1);
        productosDePrueba.add(producto2);
    }
    
    @Test
    public void testCrearFactura() {
        // Preparar datos de prueba
        String clienteNombre = "Cliente de Prueba";
        String clienteIdentificacion = "123456789";
        
        // Crear una lista de productos para la factura
        List<Productos> productosFactura = new ArrayList<>();
        
        // Tomar el primer producto y establecer cantidad
        Productos p1 = productosDePrueba.getFirst();
        p1.setCantidad(2); // Comprar 2 unidades
        productosFactura.add(p1);
        
        // Tomar el segundo producto y establecer cantidad
        Productos p2 = productosDePrueba.get(1);
        p2.setCantidad(3); // Comprar 3 unidades
        productosFactura.add(p2);
        
        // Calcular el total esperado
        double totalEsperado = productosFactura.stream()
                .mapToDouble(p -> p.getPrecioParaVender() * p.getCantidad())
                .sum();
        
        try {
            // Crear la factura
            String facturaId = FacturaService.crearFactura(
                    productosFactura,
                    clienteNombre,
                    clienteIdentificacion,
                    totalEsperado
            );
            
            // Verificar que se creó la factura
            assertNotNull(facturaId, "El ID de la factura no debe ser nulo");
            assertFalse(facturaId.isEmpty(), "El ID de la factura no debe estar vacío");
            
            // Verificar que se actualizó el inventario
            Productos p1Actualizado = ProductoService.buscarProductoPorId(p1.getIdProducto());
            Productos p2Actualizado = ProductoService.buscarProductoPorId(p2.getIdProducto());
            
            // Verificar que se descontaron las cantidades
            assertEquals(8, p1Actualizado.getCantidad(), "Deberían quedar 8 unidades del producto 1");
            assertEquals(17, p2Actualizado.getCantidad(), "Deberían quedar 17 unidades del producto 2");
            
            System.out.println("✅ Test de creación de factura exitoso. ID de factura: " + facturaId);
            
        } catch (Exception e) {
            fail("Error al crear la factura: " + e.getMessage());
        }
    }
    
    @AfterAll
    public void limpiarDatos() {
        // Limpiar los productos de prueba
        for (Productos producto : productosDePrueba) {
            try {
                ProductoService.eliminarProducto(producto.getIdProducto());
            } catch (Exception e) {
                System.err.println("Error al limpiar producto de prueba: " + e.getMessage());
            }
        }
    }
}
