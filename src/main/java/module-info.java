module org.example.tiendacasa {
    requires javafx.controls;
    requires javafx.fxml;
    opens LogicaTienda to javafx.base, com.google.gson;

    exports org.tiendaGUI;
    requires org.controlsfx.controls;
    requires java.desktop;
    requires java.base; // Este normalmente no es necesario, pero lo dejo por claridad
    requires com.google.gson; // Agrega esto para permitir el uso de Gson

    // Permite que Gson acceda a las clases en LogicaTienda
    exports LogicaTienda;
    exports org.tiendaGUI.Controllers;
    opens org.tiendaGUI.Controllers to javafx.fxml;
    opens org.tiendaGUI to javafx.fxml;

}