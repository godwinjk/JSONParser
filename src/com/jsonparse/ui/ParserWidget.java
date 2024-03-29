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

    public ParserWidget(Project project, Disposable disposable) {
        super(new BorderLayout());
        mProject = project;
        mParent = disposable;
        mPanel = new JBPanel<>(new BorderLayout());
        mPanel.add(this, BorderLayout.CENTER);
    }


    private ParserTabsIml.DebuggerTabListener createTabsListener() {
        return new ParserTabsIml.DebuggerTabListener() {
            @Override
            public void onLast() {

            }
        };
    }


    private IParserTabs createTabPanel() {
        return new ParserTabsIml(mProject, mParent);
    }

    private void addTab(JComponent innerDebuggerWidget, IParserTabs tabs) {
        String uniqueName = generateUniqueName(tabs);
        tabs.addTab(innerDebuggerWidget, uniqueName);
    }

    private String generateUniqueName(IParserTabs tabs) {
        Set<String> names = Sets.newHashSet();
        for (int i = 0; i < tabs.getTabCount(); i++) {
            names.add(tabs.getTitleAt(i));
        }
        String suggestedName = "Parser";
        String newSdkName = suggestedName;
        int i = 0;
        while (names.contains(newSdkName)) {
            newSdkName = suggestedName + " (" + (++i) + ")";
        }
        return newSdkName;
    }

    @Override
    public void createParserSession() {
        setupTabs(createInnerDebuggerWidget());
    }

    private void setupTabs(JComponent nextComponent) {
        if (null == mTabs) {
            mTabs = createTabPanel();
            mTabs.addListener(createTabsListener());
            add(mTabs.getComponent(), BorderLayout.CENTER);

        }
        addTab(nextComponent, mTabs);
    }

    @Override
    public void closeCurrentParserSession() {
        if (mTabs != null)
            mTabs.closeCurrentTab();
    }

    @Override
    public int getTabCount() {
        return mTabs==null? 0:mTabs.getTabCount();
    }

    private JComponent createInnerDebuggerWidget() {
        return new com.jsonparse.ui.forms.ParserWidget(mProject, mParent, this).getContainer();
    }

    @Override
    public JComponent getComponent() {
        return mPanel;
    }

    @Override
    public JComponent getNewComponent() {
        return createInnerDebuggerWidget();
    }
}
