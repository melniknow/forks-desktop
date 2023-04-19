module com.melniknow.fd {
    requires javafx.controls;
    requires javafx.fxml;
    requires telegrambots;
    requires telegrambots.meta;

    opens com.melniknow.fd to javafx.fxml;
    exports com.melniknow.fd;
    exports com.melniknow.fd.UI;
    opens com.melniknow.fd.UI to javafx.fxml;
}