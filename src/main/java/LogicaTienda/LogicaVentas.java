package LogicaTienda;

import java.util.List;

public class LogicaVentas {
    private List<Productos> listaProductos;
    public boolean Vender(Productos productoVenta, int cantidad, double precioVenta){
        boolean Vendido=false;
        int ubicacion= listaProductos.indexOf(productoVenta);
        if(ubicacion>=0 && listaProductos.get(ubicacion).getStock()>=1 && listaProductos.get(ubicacion).getStock()-cantidad>=0){
            listaProductos.get(ubicacion).setStock(listaProductos.get(ubicacion).getStock()-cantidad);
            Vendido=true;
        }
        return Vendido;
    }
    public boolean actualizarTodosPreciosAumenta(double nuevoPrecio){
        boolean actualizado=false;
        //TODO encontrar la forma de subirlos todos
        return actualizado;
    }
    public boolean actualizarTodosPreciosBaja(double nuevoPrecio){
        boolean actualizado=false;
        //TODO encontrar la forma de bajarlos todos
        return actualizado;
    }
    public boolean actualizarListaProductos(List<Productos> nuevaLista){
        boolean actualizado=false;
        if(!this.listaProductos.isEmpty()){
            actualizado=true;
            limpiarListaProductos();
            this.listaProductos=nuevaLista;
            return actualizado;
        }
        this.listaProductos=nuevaLista;
        return actualizado;
    }
    public boolean limpiarListaProductos(){
        boolean limpiado=false;
        this.listaProductos.clear();
        return limpiado;
    }

}
