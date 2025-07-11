package LogicaTienda.Data;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import javafx.application.Platform;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.function.Consumer;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDBConnection {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "ferreteriaDB";
    private static volatile MongoClient mongoClient;
    private static volatile MongoDatabase database;
    private static volatile boolean changeStreamActive = false;
    private static Thread changeStreamThread = null;

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
        stopChangeStream();
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("✅ Conexión a MongoDB cerrada");
        }
    }

    private static boolean collectionExists(String collectionName) {
        try {
            for (String name : getDatabase().listCollectionNames()) {
                if (name.equalsIgnoreCase(collectionName)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error al verificar colección " + collectionName + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Inicia un Change Stream para escuchar cambios en una colección
     * @param collectionName Nombre de la colección a monitorear
     * @param changeConsumer Consumidor que procesará los cambios
     * @param <T> Tipo de documento de la colección
     */
    public static <T> void watchCollection(String collectionName, Consumer<ChangeStreamDocument<Document>> changeConsumer) {
        if (changeStreamActive) {
            System.out.println("⚠️ Ya hay un Change Stream activo");
            return;
        }

        changeStreamActive = true;
        changeStreamThread = new Thread(() -> {
            try {
                MongoCollection<Document> collection = getDatabase().getCollection(collectionName);
                ChangeStreamIterable<Document> changeStream = collection.watch();
                
                System.out.println("👂 Escuchando cambios en la colección: " + collectionName);
                
                // Procesar cada cambio
                for (ChangeStreamDocument<Document> change : changeStream) {
                    if (!changeStreamActive) break;
                    
                    // Usar Platform.runLater para actualizar la UI de forma segura
                    ChangeStreamDocument<Document> finalChange = change;
                    Platform.runLater(() -> {
                        try {
                            changeConsumer.accept(finalChange);
                        } catch (Exception e) {
                            System.err.println("Error al procesar cambio: " + e.getMessage());
                        }
                    });
                }
            } catch (Exception e) {
                if (changeStreamActive) { // Solo registrar el error si no lo detuvimos intencionalmente
                    System.err.println("❌ Error en el Change Stream: " + e.getMessage());
                    e.printStackTrace();
                    // Intentar reiniciar el cambio después de un retraso
                    if (changeStreamActive) {
                        try {
                            Thread.sleep(5000); // Esperar 5 segundos antes de reintentar
                            if (changeStreamActive) {
                                watchCollection(collectionName, changeConsumer);
                            }
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }, "MongoDB-ChangeStream-Thread");
        
        changeStreamThread.setDaemon(true); // Hilo demonio para que no impida que la aplicación termine
        changeStreamThread.start();
    }
    
    /**
     * Detiene el monitoreo de cambios
     */
    /**
     * Verifica si hay un Change Stream activo
     * @return true si hay un Change Stream activo, false en caso contrario
     */
    public static boolean isChangeStreamActive() {
        return changeStreamActive;
    }
    
    /**
     * Detiene el monitoreo de cambios
     */
    public static void stopChangeStream() {
        changeStreamActive = false;
        if (changeStreamThread != null && changeStreamThread.isAlive()) {
            changeStreamThread.interrupt();
            try {
                changeStreamThread.join(2000); // Esperar hasta 2 segundos a que termine
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
