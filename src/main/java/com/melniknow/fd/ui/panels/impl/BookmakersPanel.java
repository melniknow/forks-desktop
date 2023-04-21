package com.melniknow.fd.ui.panels.impl;

import com.melniknow.fd.ui.panels.IPanel;
import javafx.scene.layout.GridPane;

public class BookmakersPanel implements IPanel {
    @Override
    public GridPane getGrid() {
        return new GridPane();
    }
}
