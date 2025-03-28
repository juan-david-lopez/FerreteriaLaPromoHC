module org.example.tiendacasa {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    exports app to javafx.graphics, javafx.fxml;
    opens LogicaTienda to javafx.base, com.google.gson;
    opens org.tiendaGUI to javafx.fxml;
    opens org.tiendaGUI.Controllers to javafx.fxml;
    opens LogicaTienda.Model to com.google.gson, javafx.base;
    opens LogicaTienda.Data to com.google.gson, javafx.base;
    opens LogicaTienda.Forms to com.google.gson, javafx.base;

    exports org.tiendaGUI;
    exports org.tiendaGUI.Controllers;
    exports LogicaTienda;
    exports LogicaTienda.Model;
    exports LogicaTienda.Data;
    exports LogicaTienda.Forms;

    requires org.controlsfx.controls;
    requires java.desktop;
    requires com.google.gson;
    requires java.logging;
    requires static lombok;
}
