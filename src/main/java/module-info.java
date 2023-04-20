module com.melniknow.fd {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.telegram.bot.api;
    requires atlantafx.base;
    requires java.net.http;
    requires urlbuilder;
    requires com.google.gson;

    opens com.melniknow.fd to javafx.fxml;
    exports com.melniknow.fd;
    exports com.melniknow.fd.ui;
    opens com.melniknow.fd.ui to javafx.fxml;
    exports com.melniknow.fd.ui.panels;
    opens com.melniknow.fd.ui.panels to javafx.fxml;
    exports com.melniknow.fd.ui.panels.impl;
    opens com.melniknow.fd.ui.panels.impl to javafx.fxml;
    exports com.melniknow.fd.core;
    exports com.melniknow.fd.oddscorp;
    exports com.melniknow.fd.context;
    opens com.melniknow.fd.context to javafx.fxml;
}