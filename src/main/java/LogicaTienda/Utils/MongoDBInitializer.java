package LogicaTienda.Utils;

import LogicaTienda.Data.MongoDBConnection;
import LogicaTienda.Services.MigracionService;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import javafx.application.Platform;
import org.bson.Document;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoDBInitializer {
    
    private static final Logger LOGGER = Logger.getLogger(MongoDBInitializer.class.getName());
    private static boolean initialized = false;
    private static Consumer<ChangeStreamDocument<Document>> changeConsumer;
    
    /**
     * Inicializa la conexión con MongoDB y configura el listener de cambios
     */
    public static synchronized void initialize() {
        initialize(null);
    }

    /**
     * Inicializa la conexión con MongoDB y configura el listener de cambios
     * @param onCollectionChange Callback que se ejecutará cuando haya cambios en las colecciones monitoreadas
     */
    public static synchronized void initialize(Consumer<ChangeStreamDocument<Document>> onCollectionChange) {
        if (!initialized) {
            try {
                // Inicializar la conexión con MongoDB
                MongoDBConnection.getDatabase();
                
                // Ejecutar migración de datos si es necesario
                if (debeEjecutarMigracion()) {
                    MigracionService.migrarDatos();
                    marcarMigracionCompletada();
                }
                
                // Configurar el consumidor de cambios si se proporcionó
                if (onCollectionChange != null) {
                    changeConsumer = onCollectionChange;
                    startWatchingCollections();
                }
                
                initialized = true;
                LOGGER.info("✅ MongoDB inicializado correctamente");
            } catch (Exception e) {
                String errorMsg = "❌ Error al inicializar MongoDB: " + e.getMessage();
                LOGGER.log(Level.SEVERE, errorMsg, e);
                throw new RuntimeException("No se pudo inicializar MongoDB", e);
            }
        } else if (onCollectionChange != null && changeConsumer == null) {
            // Si ya está inicializado pero se proporciona un nuevo consumidor
            changeConsumer = onCollectionChange;
            startWatchingCollections();
        }
    }
    
    private static boolean debeEjecutarMigracion() {
        // Aquí podrías implementar una lógica para verificar si es necesario migrar datos
        // Por ejemplo, podrías verificar si la colección de productos está vacía
        return true; // Por ahora, siempre ejecutamos la migración
    }
    
    private static void marcarMigracionCompletada() {
        // Aquí podrías implementar la lógica para marcar que la migración se completó
        // Por ejemplo, podrías crear una colección de metadatos
    }
    
    /**
     * Inicia el monitoreo de cambios en las colecciones relevantes
     */
    private static void startWatchingCollections() {
        if (changeConsumer == null) {
            LOGGER.warning("No se puede iniciar el monitoreo: no se ha configurado el consumidor de cambios");
            return;
        }

        // Monitorear cambios en la colección de productos
        MongoDBConnection.watchCollection("productos", change -> {
            LOGGER.info("Cambio detectado en productos: " + change.getOperationType());
            changeConsumer.accept(change);
        });

        // Monitorear cambios en la colección de facturas
        MongoDBConnection.watchCollection("facturas", change -> {
            LOGGER.info("Cambio detectado en facturas: " + change.getOperationType());
            changeConsumer.accept(change);
        });

        // Monitorear cambios en la colección de domicilios
        MongoDBConnection.watchCollection("domicilios", change -> {
            LOGGER.info("Cambio detectado en domicilios: " + change.getOperationType());
            changeConsumer.accept(change);
        });

        LOGGER.info("✅ Monitoreo de cambios en colecciones iniciado");
    }

    /**
     * Detiene la aplicación y cierra las conexiones
     */
    public static void shutdown() {
        if (initialized) {
            try {
                // Detener el monitoreo de cambios
                MongoDBConnection.stopChangeStream();
                
                // Cerrar la conexión con MongoDB
                MongoDBConnection.closeConnection();
                
                initialized = false;
                changeConsumer = null;
                LOGGER.info("✅ MongoDB cerrado correctamente");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "❌ Error al cerrar MongoDB", e);
            }
        }
    }
    
    /**
     * Establece un nuevo consumidor de cambios
     * @param consumer El consumidor que procesará los cambios
     */
    public static void setChangeConsumer(Consumer<ChangeStreamDocument<Document>> consumer) {
        if (consumer != null) {
            changeConsumer = consumer;
            if (initialized && !MongoDBConnection.isChangeStreamActive()) {
                startWatchingCollections();
            }
        }
    }
}
