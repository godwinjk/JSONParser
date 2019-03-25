package com.jsonparse.ui.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.components.JBPanel;
import com.jsonparse.ui.IParserWidget;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class NewWindowAction extends AnAction {
    private final IParserWidget mParserWidget;
    private JFrame frame;

    public NewWindowAction(IParserWidget parserWidget) {
        super("Open in new window", "Open in new window", AllIcons.Actions.ChangeView);
        mParserWidget = parserWidget;

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        if (frame == null && mParserWidget != null) {
            frame = new JFrame();
            frame.setLayout(new BorderLayout());
            frame.add(mParserWidget.getComponent(), BorderLayout.CENTER);

            frame.setVisible(true);
        }
    }
}
