module org.example.tiendacasa {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens org.tiendaGUI to javafx.fxml;
    exports org.tiendaGUI;
}