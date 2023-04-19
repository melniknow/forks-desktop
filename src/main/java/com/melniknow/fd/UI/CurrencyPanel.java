package com.melniknow.fd.UI;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class CurrencyPanel implements IPanel {
    @Override
    public GridPane getGrid() {
        var grid = new GridPane();
        grid.add(new Button("CurrencyPanel"), 1, 0);

        return grid;
    }
}
