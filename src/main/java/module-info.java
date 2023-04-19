module com.melniknow.fd {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.telegram.bot.api;

    opens com.melniknow.fd to javafx.fxml;
    exports com.melniknow.fd;
}