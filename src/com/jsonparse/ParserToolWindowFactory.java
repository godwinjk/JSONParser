package com.jsonparse;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Godwin on 4/21/2018 12:32 PM for json.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class ParserToolWindowFactory implements ToolWindowFactory {

   public static final String TOOL_WINDOW_ID = "Json Parser";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ParserComponent apiDebuggerView = ParserComponent.getInstance(project);
        apiDebuggerView.initParser(toolWindow);

    }

}
