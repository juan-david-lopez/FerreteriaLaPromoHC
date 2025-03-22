module org.example.tiendacasa {
    requires javafx.controls;
    requires javafx.fxml;
    opens LogicaTienda to javafx.base, com.google.gson;

    exports org.tiendaGUI;
    requires org.controlsfx.controls;
    requires java.desktop;
    // Este normalmente no es necesario, pero lo dejo por claridad
    requires com.google.gson;
    requires java.logging;
    requires static lombok;
    exports LogicaTienda;
    exports org.tiendaGUI.Controllers;
    opens org.tiendaGUI.Controllers to javafx.fxml;
    opens org.tiendaGUI to javafx.fxml;
    exports LogicaTienda.Model;
    opens LogicaTienda.Model to com.google.gson, javafx.base;

}