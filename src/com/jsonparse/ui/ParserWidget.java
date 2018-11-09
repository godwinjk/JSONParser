package com.jsonparse.ui;

import com.google.common.collect.Sets;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBPanel;
import com.jsonparse.ui.tab.IParserTabs;
import com.jsonparse.ui.tab.ParserTabsIml;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * crea
 */
public class ParserWidget extends JPanel implements IParserWidget {
    private Project mProject;
    private Disposable mParent;
    private JBPanel<JBPanel> mPanel;
    private IParserTabs mTabs;
    private JComponent mInnerDebuggerWidget;

    public ParserWidget(Project project, Disposable disposable) {
        super(new BorderLayout());
        mProject = project;
        mParent = disposable;
        mPanel = new JBPanel<>(new BorderLayout());
        mPanel.add(this, BorderLayout.CENTER);
    }

    private IParserTabs setupTabs() {
        IParserTabs tabs = createTabPanel();
        tabs.addListener(createTabsListener());
        remove(mInnerDebuggerWidget);
        addTab(mInnerDebuggerWidget, tabs);
        add(tabs.getComponent(), BorderLayout.CENTER);
        mInnerDebuggerWidget = null;
        return tabs;
    }

    private ParserTabsIml.DebuggerTabListener createTabsListener() {
        return this::removeTabbedPanel;
    }

    private void removeTabbedPanel() {
        mInnerDebuggerWidget = mTabs.getCurrentComponent();
        remove(mTabs.getComponent());
//        mTabs = null;
        add(mInnerDebuggerWidget, BorderLayout.CENTER);
    }


    private IParserTabs createTabPanel() {
        return new ParserTabsIml(mProject, mParent);
    }

    private void addTab(JComponent innerDebuggerWidget, IParserTabs tabs) {
        String uniqueName = generateUniqueName("Tab", tabs);//TODO configuration
        tabs.addTab(innerDebuggerWidget, uniqueName);
    }

    private  String generateUniqueName(String suggestedName, IParserTabs tabs) {
        Set<String> names = Sets.newHashSet();
        for (int i = 0; i < tabs.getTabCount(); i++) {
            names.add(tabs.getTitleAt(i));
        }
        String newSdkName = suggestedName;
        int i = 0;
        while (names.contains(newSdkName)) {
            newSdkName = suggestedName + " (" + (++i) + ")";
        }
        return newSdkName;
    }

    @Override
    public void createParserSession() {
        JComponent innerDebuggerWidget = createInnerDebuggerWidget();
        if (mInnerDebuggerWidget == null && mTabs == null) {
            mInnerDebuggerWidget = innerDebuggerWidget;
            add(mInnerDebuggerWidget, BorderLayout.CENTER);
        }else {
            if (mTabs == null) {
                mTabs = setupTabs();
            }
            addTab(innerDebuggerWidget, mTabs);
        }
    }

    @Override
    public void closeCurrentParserSession() {
        mTabs.closeCurrentTab();
    }

    private JComponent createInnerDebuggerWidget() {
        return new com.jsonparse.ui.forms.ParserWidget(mProject, mParent).getContainer();
    }

    @Override
    public JComponent getComponent() {
        return mPanel;
    }
}
