package com.melniknow.fd.ui.panels.impl;

import com.melniknow.fd.ui.panels.IPanel;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class BookmakersPanel implements IPanel {
    @Override
    public GridPane getGrid() {
        var grid = new GridPane();
        grid.add(new Button("BookmakersPanel"), 1, 0);

        return grid;
    }
}
