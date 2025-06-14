module ferreteria.la.promo {
    // Módulos requeridos
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires org.controlsfx.controls;
    requires com.google.gson;
    requires static lombok;
    requires java.desktop;
    requires java.logging;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires kernel;
    requires layout;
    requires io;
    requires org.mongodb.driver.core;

    // Exportar paquetes
    exports app;
    
    // Exportar paquetes para FXML y MongoDB
    exports LogicaTienda.Model to javafx.fxml, com.google.gson, org.mongodb.bson, javafx.base;
    exports LogicaTienda.Utils to javafx.fxml, com.google.gson;
    
    // Abrir paquetes para reflexión
    opens app to javafx.fxml, javafx.graphics, com.google.gson;
    opens LogicaTienda to javafx.fxml, com.google.gson, org.mongodb.bson;
    opens LogicaTienda.Model to javafx.fxml, com.google.gson, org.mongodb.bson, javafx.base;
    opens LogicaTienda.Utils to javafx.fxml, com.google.gson;
    
    // Exportar paquetes de la interfaz gráfica
    exports org.tiendaGUI.Controllers to javafx.fxml;
    exports org.tiendaGUI.DTO to javafx.fxml, com.google.gson, org.mongodb.bson;
    
    // Abrir paquetes para reflexión
    opens org.tiendaGUI.Controllers to javafx.fxml, com.google.gson, javafx.graphics;
    opens org.tiendaGUI.DTO to javafx.fxml, com.google.gson, org.mongodb.bson, javafx.base;
}