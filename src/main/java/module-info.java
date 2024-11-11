module com.sentomero.sufeeds.javasufeeds {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    opens com.sentomero.sufeeds.javasufeeds to javafx.fxml;
    opens com.sentomero.sufeeds.javasufeeds.Controllers to javafx.fxml;

    exports com.sentomero.sufeeds.javasufeeds;
    exports com.sentomero.sufeeds.javasufeeds.Controllers;
}