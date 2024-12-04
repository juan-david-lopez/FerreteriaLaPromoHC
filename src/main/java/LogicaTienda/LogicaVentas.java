package LogicaTienda;

import java.util.List;

public class LogicaVentas {
    private List<Productos> listaProductos;

    public boolean Vender(Productos productoVenta, int cantidad, double precioVenta){
        boolean Vendido = false;
        int ubicacion = listaProductos.indexOf(productoVenta);
        if (ubicacion >= 0 && listaProductos.get(ubicacion).getStock() >= 1 && listaProductos.get(ubicacion).getStock() - cantidad >= 0) {
            listaProductos.get(ubicacion).setStock(listaProductos.get(ubicacion).getStock() - cantidad);
            Vendido = true;
        }
        return Vendido;
    }

    public boolean actualizarTodosPreciosAumenta(double porcentajeAumento){
        boolean actualizado = false;
        for (Productos producto : listaProductos) {
            double nuevoPrecio = producto.getPrecio() * (1 + porcentajeAumento / 100);
            producto.setPrecio(nuevoPrecio);
        }
        actualizado = true;
        return actualizado;
    }

    public boolean actualizarTodosPreciosBaja(double porcentajeBaja){
        boolean actualizado = false;
        for (Productos producto : listaProductos) {
            double nuevoPrecio = producto.getPrecio() * (1 - porcentajeBaja / 100);
            producto.setPrecio(nuevoPrecio);
        }
        actualizado = true;
        return actualizado;
    }

    public boolean actualizarListaProductos(List<Productos> nuevaLista){
        boolean actualizado = false;
        if (!this.listaProductos.isEmpty()) {
            actualizado = true;
            limpiarListaProductos();
            this.listaProductos = nuevaLista;
            return actualizado;
        }
        this.listaProductos = nuevaLista;
        return actualizado;
    }

    public boolean limpiarListaProductos(){
        boolean limpiado = false;
        this.listaProductos.clear();
        limpiado = true;
        return limpiado;
    }
}

