module com.sentomero.sufeeds.javasufeeds {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens com.sentomero.sufeeds.javasufeeds to javafx.fxml;
    exports com.sentomero.sufeeds.javasufeeds;
    exports com.sentomero.sufeeds.javasufeeds.Controllers;
    opens com.sentomero.sufeeds.javasufeeds.Controllers to javafx.fxml;
}