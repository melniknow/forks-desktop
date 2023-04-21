package com.melniknow.fd.ui.panels.impl;

import com.melniknow.fd.ui.panels.IPanel;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class CurrencyPanel implements IPanel {
    @Override
    public Node getGrid() {
        return new GridPane();
    }
}
