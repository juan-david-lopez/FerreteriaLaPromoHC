package LogicaTienda.Services;

import LogicaTienda.Data.MongoDBConnection;
import LogicaTienda.Model.Domicilio;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DomicilioService {
    private static final MongoCollection<Domicilio> domiciliosCollection = 
            MongoDBConnection.getCollection("domicilios", Domicilio.class);

    public static List<Domicilio> obtenerTodosLosDomicilios() {
        return domiciliosCollection.find().into(new ArrayList<>());
    }

    public static Domicilio buscarDomicilioPorId(String id) {
        return domiciliosCollection.find(Filters.eq("id", id)).first();
    }

    public static List<Domicilio> buscarDomiciliosPorFactura(String idFactura) {
        return domiciliosCollection.find(Filters.eq("idFactura", idFactura)).into(new ArrayList<>());
    }

    public static String guardarDomicilio(Domicilio domicilio) {
        if (domicilio.getId() == null || domicilio.getId().isEmpty()) {
            domicilio.setId(UUID.randomUUID().toString().substring(0, 8));
        }
        
        if (buscarDomicilioPorId(domicilio.getId()) != null) {
            actualizarDomicilio(domicilio);
        } else {
            domiciliosCollection.insertOne(domicilio);
        }
        
        return domicilio.getId();
    }

    public static void actualizarDomicilio(Domicilio domicilio) {
        Bson filter = Filters.eq("id", domicilio.getId());
        domiciliosCollection.replaceOne(filter, domicilio);
    }

    public static void eliminarDomicilio(String id) {
        domiciliosCollection.deleteOne(Filters.eq("id", id));
    }

    public static List<Domicilio> buscarDomiciliosPorCliente(String clienteIdentificacion) {
        return domiciliosCollection.find(Filters.eq("clienteIdentificacion", clienteIdentificacion))
                .into(new ArrayList<>());
    }
}
