package com.jsonparse.ui.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.jsonparse.ui.IParserWidget;
import org.jetbrains.annotations.NotNull;

/**
 * Created by fingerart on 17/2/20.
 */
public class CloseTabAction extends AnAction {
    private final IParserWidget mParserWidget;

    public CloseTabAction(IParserWidget parserWidget) {
        super("Close Tab", "Close ApiDebugger Session", AllIcons.Actions.Cancel);
        mParserWidget = parserWidget;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        mParserWidget.closeCurrentParserSession();
        System.out.println("CloseTabAction.actionPerformed");
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        e.getPresentation().setVisible(mParserWidget.getTabCount() > 1);
    }
}