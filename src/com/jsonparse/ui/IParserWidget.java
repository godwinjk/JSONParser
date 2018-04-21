package com.jsonparse.ui;

import javax.swing.*;

public interface IParserWidget {
    void createParserSession();

    void closeCurrentParserSession();

    JComponent getComponent();
}
