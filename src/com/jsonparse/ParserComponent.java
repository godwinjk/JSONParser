package com.jsonparse;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.jsonparse.common.Logger;
import com.jsonparse.ui.IParserWidget;
import com.jsonparse.ui.ParserToolWindowPanel;
import com.jsonparse.ui.ParserWidget;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by Godwin on 4/21/2018 12:32 PM for json.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class ParserComponent extends AbstractProjectComponent {
    long time = 0;

    protected ParserComponent(Project project) {
        super(project);
    }

    public static ParserComponent getInstance(Project project) {
        return project.getComponent(ParserComponent.class);
    }

    public void initParser(ToolWindow toolWindow) {
        Content content = createParserContentPanel(toolWindow);
        content.setCloseable(true);
        toolWindow.getContentManager().addContent(content);
        ((ToolWindowManagerEx) ToolWindowManager.getInstance(myProject)).addToolWindowManagerListener(getToolWindowListener());
    }

    private Content createParserContentPanel(ToolWindow toolWindow) {
        toolWindow.setToHideOnEmptyContent(true);

        ParserToolWindowPanel panel = new ParserToolWindowPanel(PropertiesComponent.getInstance(myProject), toolWindow);
        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", false);

        IParserWidget debuggerWidget = createContent(content);
        ActionToolbar toolBar = createToolBar(debuggerWidget);

        panel.setToolbar(toolBar.getComponent());
        panel.setContent(debuggerWidget.getComponent());

        return content;
    }

    private IParserWidget createContent(Content content) {
        IParserWidget debuggerWidget = new ParserWidget(myProject, content);
        debuggerWidget.createParserSession();

        return debuggerWidget;
    }

    private ActionToolbar createToolBar(IParserWidget debuggerWidget) {
        DefaultActionGroup group = new DefaultActionGroup();

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, group, false);
        toolbar.setOrientation(SwingConstants.VERTICAL);
        return toolbar;
    }

    private ToolWindowManagerListener getToolWindowListener() {
        return new ToolWindowManagerListener() {
            @Override
            public void toolWindowRegistered(@NotNull String s) {
                Logger.d("DebuggerComponent.toolWindowRegistered");
            }

            @Override
            public void stateChanged() {
                ToolWindow toolWindow = ToolWindowManager.getInstance(myProject).getToolWindow(ParserToolWindowFactory.TOOL_WINDOW_ID);
                if (toolWindow != null) {
                    if (toolWindow.isVisible() && toolWindow.getContentManager().getContentCount() == 0) {
                        Logger.d("DebuggerComponent.isVisible ContentCount>0");
                        initParser(toolWindow);
                    } else if (!toolWindow.isVisible()) {
                        if (time <= 0 || time < System.currentTimeMillis() - 3600000) {
                            time = System.currentTimeMillis();
                            Notifications.Bus.notify(new Notification(
                                    "Json Parser",
                                    "Like it",
                                    "Like this plugin? <a href=https://paypal.me/godwinj>Donate</a> or <b>Give it a star</b>  <a href=https://plugins.jetbrains.com/plugin/10650-json-parser>Json Parser</a> and spread the word",
                                    NotificationType.INFORMATION,
                                    new NotificationListener.UrlOpeningListener(true)));
                        }

                    }
                }
            }
        };
    }
}
