package com.melniknow.fd.ui.panels.impl;

import com.melniknow.fd.ui.panels.IPanel;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class SessionPanel implements IPanel {
    @Override
    public GridPane getGrid() {
        var grid = new GridPane();
        grid.add(new Button("SessionPanel"), 1, 0);
        // textarea? Любая хуйня, куда можно красиво текст выводить (не изменяема)
        return grid;
    }
}
