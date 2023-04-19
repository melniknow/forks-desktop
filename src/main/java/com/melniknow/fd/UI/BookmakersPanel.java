package com.melniknow.fd.UI;

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
