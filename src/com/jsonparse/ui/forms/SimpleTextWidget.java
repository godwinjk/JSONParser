package com.jsonparse.ui.forms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.jsonparse.ui.TreeNodeCreator;
import org.apache.http.util.TextUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

public class SimpleTextWidget {
    private JPanel container;
    private JPanel inputContainer;
    private JTree outputTree;

    private GridLayoutManager mGridLayout;

    private Editor mInputEditor;
    private Editor mOutputEditor;

    private Project mProject;
    private Disposable mParent;

    public SimpleTextWidget(Project project, Disposable disposable) {
        this.mProject = project;
        this.mParent = disposable;

        this.mGridLayout = (GridLayoutManager) container.getLayout();
        this.mInputEditor = createEditor();
        this.mOutputEditor = createEditor();

        inputContainer.add(mInputEditor.getComponent(), BorderLayout.CENTER);

        setEventListeners();
        changeIcon();
        setEmptyTree();
    }

    private Editor createEditor() {
        PsiFile myFile = null;
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document doc = myFile == null
                ? editorFactory.createDocument("")
                : PsiDocumentManager.getInstance(mProject).getDocument(myFile);
        Editor editor = editorFactory.createEditor(doc, mProject);
        EditorSettings editorSettings = editor.getSettings();
        editorSettings.setVirtualSpace(false);
        editorSettings.setLineMarkerAreaShown(false);
        editorSettings.setIndentGuidesShown(false);
        editorSettings.setFoldingOutlineShown(true);
        editorSettings.setAdditionalColumnsCount(3);
        editorSettings.setAdditionalLinesCount(3);
        editorSettings.setLineNumbersShown(true);
        editorSettings.setCaretRowShown(true);


//        ((EditorEx) editor).setHighlighter(createHighlighter(FileTypes.PLAIN_TEXT));
        return editor;
    }

    private void setEventListeners() {
        mInputEditor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(DocumentEvent e) {
                if (e != null && !TextUtils.isEmpty(e.getDocument().getText())) {
                    try {
                        String jsonString = e.getDocument().getText();

                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        JsonParser parser = new JsonParser();
                        JsonElement je = parser.parse(jsonString);
                        String prettyJsonString = gson.toJson(je);

                        DefaultTreeModel model = TreeNodeCreator.getTreeModel(prettyJsonString);
                        outputTree.setModel(model);
                        expandAllNodes(outputTree, 0, outputTree.getRowCount());
//                        mOutputEditor.getDocument().setText(prettyJsonString);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private void changeIcon() {
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) outputTree.getCellRenderer();
        Icon icon = new ImageIcon();
        renderer.setClosedIcon(icon);
        renderer.setOpenIcon(icon);
    }

    private void setEmptyTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("");

        DefaultTreeModel model = new DefaultTreeModel(root);
        outputTree.setModel(model);

    }

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    public JPanel getContainer() {
        return container;
    }
}
