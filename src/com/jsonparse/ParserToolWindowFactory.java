package com.jsonparse;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

public class ParserToolWindowFactory implements ToolWindowFactory {


    public static final String TOOL_WINDOW_ID = "Json Parser";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ParserComponent apiDebuggerView = ParserComponent.getInstance(project);
        apiDebuggerView.initParser(toolWindow);
    }

}
