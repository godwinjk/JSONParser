package com.jsonparse.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBPanel;
import com.jsonparse.ui.forms.SimpleTextWidget;

import javax.swing.*;
import java.awt.*;

/**
 * crea
 */
public class ParserWidget extends JPanel implements IParserWidget {
    private Project mProject;
    private Disposable mParent;
    private JBPanel<JBPanel> mPanel;
    private JComponent mInnerDebuggerWidget;

    public ParserWidget(Project project, Disposable disposable) {
        super(new BorderLayout());
        mProject = project;
        mParent = disposable;
        mPanel = new JBPanel<>(new BorderLayout());
        mPanel.add(this, BorderLayout.CENTER);
    }

    @Override
    public void createParserSession() {
        JComponent innerDebuggerWidget = createInnerDebuggerWidget();
        if (mInnerDebuggerWidget == null ) {
            mInnerDebuggerWidget = innerDebuggerWidget;
            add(mInnerDebuggerWidget, BorderLayout.CENTER);
        }
    }

    @Override
    public void closeCurrentParserSession() {

    }

    private JComponent createInnerDebuggerWidget() {
        return new SimpleTextWidget(mProject, mParent).getContainer();
    }

    @Override
    public JComponent getComponent() {
        return mPanel;
    }
}
