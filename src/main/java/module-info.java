module com.melniknow.fd {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires async.http.client;
    requires io.netty.codec.http;

    opens com.melniknow.fd to javafx.fxml;
    exports com.melniknow.fd;
    exports com.melniknow.fd.ui;
    opens com.melniknow.fd.ui to javafx.fxml;
    exports com.melniknow.fd.ui.panels;
    opens com.melniknow.fd.ui.panels to javafx.fxml;
    exports com.melniknow.fd.ui.panels.impl;
    opens com.melniknow.fd.ui.panels.impl to javafx.fxml;
}