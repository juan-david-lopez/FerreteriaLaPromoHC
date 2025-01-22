module org.example.tiendacasa {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;

    opens org.tiendaGUI to javafx.fxml;
    exports org.tiendaGUI;
    exports org.tiendaGUI.Controllers;
    opens org.tiendaGUI.Controllers to javafx.fxml;
}