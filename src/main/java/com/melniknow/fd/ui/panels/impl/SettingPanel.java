package com.melniknow.fd.ui.panels.impl;

import com.melniknow.fd.ui.panels.IPanel;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class SettingPanel implements IPanel {
    @Override
    public GridPane getGrid() {
        var grid = new GridPane();
        grid.add(new Button("SettingPanel"), 1, 0);

        return grid;
    }
}
