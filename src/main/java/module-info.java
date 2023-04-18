module com.melniknow.fd {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.melniknow.fd to javafx.fxml;
    exports com.melniknow.fd;
}