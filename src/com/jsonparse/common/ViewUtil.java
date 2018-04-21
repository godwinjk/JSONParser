package com.jsonparse.common;

import java.awt.*;

public class ViewUtil {
    public static void setCursor(int cursor, Component... components) {
        for (Component component : components) {
            component.setCursor(Cursor.getPredefinedCursor(cursor));
        }
    }
}
