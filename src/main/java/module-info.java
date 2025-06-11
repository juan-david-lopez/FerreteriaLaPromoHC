module org.tiendaGUI {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires org.controlsfx.controls;
    requires java.desktop;
    requires com.google.gson;
    requires java.logging;
    requires static lombok;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires kernel;
    requires layout;
    requires io;
    
    // Export the main package
    exports org.tiendaGUI;
    exports org.tiendaGUI.Controllers;
    exports org.tiendaGUI.DTO;
    exports org.tiendaGUI.utils;
    exports app;
    
    // Export LogicaTienda packages
    exports LogicaTienda;
    exports LogicaTienda.Model;
    exports LogicaTienda.Data;
    exports LogicaTienda.Forms;
    exports LogicaTienda.Logic;
    exports LogicaTienda.Enum;
    exports LogicaTienda.Services;

    // Open packages for reflection
    opens org.tiendaGUI to javafx.fxml, javafx.graphics, com.google.gson;
    opens org.tiendaGUI.Controllers to javafx.fxml, javafx.base, com.google.gson;
    opens org.tiendaGUI.DTO to javafx.base, com.google.gson;
    opens org.tiendaGUI.utils to javafx.fxml, com.google.gson;
    opens app to javafx.graphics;
    
    // Open LogicaTienda packages for reflection
    opens LogicaTienda to com.google.gson, org.mongodb.bson, javafx.fxml;
    opens LogicaTienda.Model to com.google.gson, javafx.base, org.mongodb.bson, javafx.fxml;
    opens LogicaTienda.Data to com.google.gson, javafx.base, javafx.fxml;
    opens LogicaTienda.Forms to com.google.gson, javafx.base, javafx.fxml;
    opens LogicaTienda.Logic to com.google.gson, javafx.base, org.mongodb.bson, javafx.fxml;
    opens LogicaTienda.Enum to com.google.gson, javafx.base, javafx.fxml;
    opens LogicaTienda.Services to com.google.gson, org.mongodb.bson, javafx.fxml;
}