package LogicaTienda.Data;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDBConnection {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "ferreteriaDB";
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    static {
        try {
            // Configuración para soportar POJOs
            CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
            );

            ConnectionString connectionString = new ConnectionString(CONNECTION_STRING);
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .codecRegistry(pojoCodecRegistry)
                    .build();

            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase(DATABASE_NAME);
            
            // Crear colecciones si no existen
            if (!collectionExists("productos")) {
                database.createCollection("productos");
            }
            if (!collectionExists("facturas")) {
                database.createCollection("facturas");
            }
            if (!collectionExists("domicilios")) {
                database.createCollection("domicilios");
            }
            
            System.out.println("✅ Conexión a MongoDB establecida correctamente");
        } catch (Exception e) {
            System.err.println("❌ Error al conectar con MongoDB: " + e.getMessage());
            throw new RuntimeException("No se pudo conectar a MongoDB", e);
        }
    }

    public static MongoDatabase getDatabase() {
        if (database == null) {
            throw new IllegalStateException("La conexión a MongoDB no ha sido inicializada");
        }
        return database;
    }

    public static <T> MongoCollection<T> getCollection(String name, Class<T> clazz) {
        return getDatabase().getCollection(name, clazz);
    }

    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("✅ Conexión a MongoDB cerrada");
        }
    }

    private static boolean collectionExists(String collectionName) {
        for (String name : getDatabase().listCollectionNames()) {
            if (name.equalsIgnoreCase(collectionName)) {
                return true;
            }
        }
        return false;
    }
}
