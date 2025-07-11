package app;

import LogicaTienda.Model.Productos;
import LogicaTienda.Services.ProductoService;
import LogicaTienda.Utils.MongoDBInitializer;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.bson.Document;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase principal de la aplicación Ferretería La Promo H&C.
 * Se encarga de la inicialización de la aplicación y la gestión del ciclo de vida.
 */

public class HelloApplication extends Application {
    private static final String TITULO_APLICACION = "Ferretería La Promo H&C";
    private static final int ANCHO_VENTANA = 1024;
    private static final int ALTO_VENTANA = 768;
    
    private static final Logger LOGGER = Logger.getLogger(HelloApplication.class.getName());
    private Object controller; // Variable de instancia para el controlador

    /**
     * Método principal de inicio de la aplicación JavaFX.
     * 
     * @param stage El escenario principal de la aplicación
     */
    @Override
    public void start(Stage stage) {
        try {
            inicializarInterfazUsuario(stage);
            configurarMongoDB();
            cargarDatosIniciales();
            stage.show();
            LOGGER.info("✅ Aplicación iniciada correctamente");
        } catch (Exception e) {
            manejarErrorInicio(e);
        }
    }
    
    /**
     * Inicializa la interfaz de usuario de la aplicación.
     * 
     * @param stage El escenario principal de la aplicación
     * @throws Exception Si ocurre un error al cargar la interfaz
     */
    private void inicializarInterfazUsuario(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tiendaGUI/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), ANCHO_VENTANA, ALTO_VENTANA);
        
        stage.setTitle(TITULO_APLICACION);
        stage.setScene(scene);
        stage.setMaximized(true);
        
        this.controller = fxmlLoader.getController();
    }
    
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> pollingTask;
    private static final long POLLING_INTERVAL_SECONDS = 5;
    private volatile boolean pollingActive = false;
    
    /**
     * Configura la conexión con MongoDB y el sistema de polling para cambios.
     */
    private void configurarMongoDB() {
        try {
            setupChangeListener(controller);
            // Inicializar el scheduler para polling
            scheduler = Executors.newScheduledThreadPool(1);
            startPolling();
            LOGGER.info("Sistema de polling iniciado para detectar cambios en MongoDB");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al configurar MongoDB", e);
        }
    }
    
    /**
     * Inicia el proceso de polling para detectar cambios en la base de datos.
     */
    private void startPolling() {
        if (pollingTask == null || pollingTask.isCancelled()) {
            pollingActive = true;
            pollingTask = scheduler.scheduleAtFixedRate(() -> {
                if (pollingActive) {
                    try {
                        // Notificar al controlador que verifique cambios
                        Platform.runLater(() -> {
                            try {
                                Method checkChanges = controller.getClass().getMethod("verificarCambiosEnBD");
                                checkChanges.invoke(controller);
                            } catch (NoSuchMethodException e) {
                                // Método no implementado en el controlador, ignorar
                            } catch (Exception e) {
                                LOGGER.log(Level.SEVERE, "Error al verificar cambios en la base de datos", e);
                            }
                        });
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error en el proceso de polling", e);
                    }
                }
            }, 0, POLLING_INTERVAL_SECONDS, TimeUnit.SECONDS);
        }
    }
    
    /**
     * Detiene el proceso de polling.
     */
    private void stopPolling() {
        if (pollingTask != null) {
            pollingActive = false;
            pollingTask.cancel(false);
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Carga los datos iniciales necesarios para la aplicación.
     */
    private void cargarDatosIniciales() {
        cargarProductosIniciales(controller);
    }
    
    /**
     * Maneja los errores que ocurren durante el inicio de la aplicación.
     * 
     * @param e La excepción que se produjo
     */
    private void manejarErrorInicio(Exception e) {
        LOGGER.log(Level.SEVERE, "❌ Error al iniciar la aplicación", e);
        showErrorDialog("No se pudo iniciar la aplicación: " + e.getMessage());
        System.exit(1);
    }

    /**
     * Configura el listener para los cambios en la base de datos.
     * Utiliza reflexión para evitar acoplamiento directo con la implementación del controlador.
     * 
     * @param controller El controlador que manejará los cambios
     */
    private void setupChangeListener(Object controller) {
        try {
            Class<?> controllerClass = Class.forName("LogicaTienda.Controllers.ProductoController");
            if (controllerClass.isInstance(controller)) {
                configurarMetodoActualizacion(controller, controllerClass);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.warning("Clase InventarioController no encontrada. Actualizaciones en tiempo real deshabilitadas.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al configurar el listener de cambios", e);
        }
    }
    
    /**
     * Configura el método de actualización usando reflexión.
     * 
     * @param controller Instancia del controlador
     * @param controllerClass Clase del controlador
     * @throws Exception Si ocurre un error al configurar el método
     */
    private void configurarMetodoActualizacion(Object controller, Class<?> controllerClass) throws Exception {
        Method updateMethod = controllerClass.getMethod(
            "actualizarDesdeCambioBD", 
            ChangeStreamDocument.class
        );
        
        MongoDBInitializer.setChangeConsumer(change -> 
            Platform.runLater(() -> {
                try {
                    updateMethod.invoke(controller, change);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error al procesar cambio en BD", e);
                }
            })
        );
    }
    
    /**
     * Maneja los cambios recibidos desde la base de datos.
     * 
     * @param change El documento de cambio recibido de MongoDB
     * @param controller El controlador que manejará el cambio
     */
    private void handleDatabaseChange(ChangeStreamDocument<Document> change, Object controller) {
        try {
            registrarCambioEnLog(change);
            notificarControlador(controller, change);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al procesar cambio en la base de datos", e);
        }
    }
    
    /**
     * Registra información sobre el cambio en el log.
     * 
     * @param change El documento de cambio
     */
    private void registrarCambioEnLog(ChangeStreamDocument<Document> change) {
        String collectionName = Objects.requireNonNull(change.getNamespace()).getCollectionName();
        String operationType = change.getOperationType().getValue();
        LOGGER.info(String.format("Cambio detectado en %s: %s", collectionName, operationType));
    }
    
    /**
     * Notifica al controlador sobre el cambio en la base de datos.
     * 
     * @param controller El controlador a notificar
     * @param change El documento de cambio
     */
    private void notificarControlador(Object controller, ChangeStreamDocument<Document> change) {
        try {
            Class<?> controllerClass = Class.forName("LogicaTienda.Controllers.ProductoController");
            if (controllerClass.isInstance(controller)) {
                Method updateMethod = controllerClass.getMethod(
                    "actualizarDesdeCambioBD", 
                    ChangeStreamDocument.class
                );

                Platform.runLater(() -> {
                    try {
                        updateMethod.invoke(controller, change);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error al notificar al controlador", e);
                    }
                });
            }
        } catch (ClassNotFoundException e) {
            LOGGER.warning("Clase ProductoController no encontrada");
        } catch (NoSuchMethodException e) {
            LOGGER.warning("Método actualizarDesdeCambioBD no encontrado");
        }
    }
    
    /**
     * Carga los productos iniciales al iniciar la aplicación.
     * 
     * @param controller El controlador que manejará los productos cargados
     */
    private void cargarProductosIniciales(Object controller) {
        try {
            List<Productos> productosCargados = ProductoService.obtenerTodosLosProductos();
            LOGGER.info("✅ Productos cargados al iniciar: " + productosCargados.size());

            actualizarControladorConProductos(controller, productosCargados);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cargar productos iniciales", e);
        }
    }
    
    /**
     * Actualiza el controlador con la lista de productos cargados.
     * 
     * @param controller El controlador a actualizar
     * @param productos Lista de productos a enviar al controlador
     */
    private void actualizarControladorConProductos(Object controller, List<Productos> productos) {
        try {
            Class<?> controllerClass = Class.forName("LogicaTienda.Controllers.ProductoController");
            if (controllerClass.isInstance(controller)) {
                Method updateMethod = controllerClass.getMethod(
                    "actualizarListaProductos",
                    List.class
                );

                Platform.runLater(() -> {
                    try {
                        updateMethod.invoke(controller, productos);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error al actualizar lista de productos", e);
                    }
                });
            }
        } catch (ClassNotFoundException e) {
            LOGGER.warning("Clase ProductoController no encontrada");
        } catch (NoSuchMethodException e) {
            LOGGER.warning("Método actualizarListaProductos no encontrado");
        }
    }
    
    /**
     * Muestra un diálogo de error al usuario.
     * 
     * @param message El mensaje de error a mostrar
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error al iniciar");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Punto de entrada principal de la aplicación.
     * 
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        try {
            launch();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fatal en la aplicación", e);
            System.exit(1);
        }
    }
    
    @Override
    public void stop() {
        try {
            LOGGER.info("🔴 Cerrando la aplicación...");
            
            // Detener el polling
            stopPolling();
            
            // Cerrar la conexión con MongoDB si es necesario
            if (controller != null) {
                try {
                    Method closeConnection = controller.getClass().getMethod("cerrarConexion");
                    closeConnection.invoke(controller);
                    LOGGER.info("✅ Conexión con MongoDB cerrada");
                } catch (NoSuchMethodException e) {
                    LOGGER.fine("No se encontró el método cerrarConexion() en el controlador");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error al cerrar la conexión con MongoDB", e);
                }
            }
            
            // Cerrar MongoDBInitializer si existe
            try {
                Method shutdownMethod = MongoDBInitializer.class.getMethod("shutdown");
                shutdownMethod.invoke(null);
                LOGGER.info("✅ MongoDBInitializer cerrado correctamente");
            } catch (NoSuchMethodException e) {
                // Método no existe, no hay problema
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al cerrar MongoDBInitializer", e);
            }
            
            LOGGER.info("✅ Aplicación cerrada correctamente");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al cerrar la aplicación", e);
        }
    }
}
