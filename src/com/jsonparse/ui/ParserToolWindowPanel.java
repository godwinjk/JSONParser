package com.jsonparse.ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;

public class ParserToolWindowPanel extends SimpleToolWindowPanel {
    private PropertiesComponent myPropertiesComponent;
    private ToolWindow myWindow;

    public ParserToolWindowPanel(PropertiesComponent propertiesComponent, ToolWindow window) {
        super(false, true);
        myPropertiesComponent = propertiesComponent;
        myWindow = window;
    }
}
