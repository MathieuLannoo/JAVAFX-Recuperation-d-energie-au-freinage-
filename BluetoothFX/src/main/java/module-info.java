module com.example.bluetoothfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.fazecast.jSerialComm;

    opens com.example.bluetoothfx to javafx.fxml;
    exports com.example.bluetoothfx;
}