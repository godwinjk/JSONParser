package com.jsonparse.ui.forms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.intellij.icons.AllIcons;
import com.intellij.ide.highlighter.HtmlFileHighlighter;
import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.ide.highlighter.XmlFileHighlighter;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.json.JsonFileType;
import com.intellij.json.highlighting.JsonSyntaxHighlighterFactory;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.util.LayerDescriptor;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.jsonparse.ui.TreeNodeCreator;
import com.jsonparse.ui.action.JBRadioAction;
import org.apache.http.util.TextUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by Godwin on 8/12/2018 12:49 PM for json.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class ParserBodyWidget {
    public JPanel container;
    private JPanel previewTypeContainer;
    private JPanel prettyContainer;
    private JPanel rawContainer;
    private JTree outputTree;

    private JScrollPane treeContainer;
    private JPanel toolBarContainer;

    private ButtonGroup buttonGroup;
    private CardLayout mPreviewTypeCardLayout;
    private SimpleToolWindowPanel simpleToolWindowPanel1;

    private final Editor prettyEditor;
    private final Editor rawEditor;

    private static final IElementType TextElementType = new IElementType("TEXT", Language.ANY);

    private Project mProject;

    private ActionListener previewTypeListener = e -> mPreviewTypeCardLayout.show(previewTypeContainer, e.getActionCommand());

    public ParserBodyWidget(Project mProject) {
        this.mProject = mProject;

        this.mProject = mProject;
        mPreviewTypeCardLayout = ((CardLayout) previewTypeContainer.getLayout());

        prettyEditor = createEditor();
        prettyContainer.add(prettyEditor.getComponent(), BorderLayout.CENTER);
        rawEditor = createEditor();
        rawContainer.add(rawEditor.getComponent(), BorderLayout.CENTER);

        changeIcon();
        setEmptyTree();
    }

    private void createUIComponents() {
        simpleToolWindowPanel1 = new SimpleToolWindowPanel(true, true);

//        JBComboBoxAction comboBoxAction = createFormatTypeComboAction();
        buttonGroup = new ButtonGroup();
        ActionGroup group = new DefaultActionGroup(
                new JBRadioAction("Pretty", "Pretty", buttonGroup, previewTypeListener, true),
                new JBRadioAction("Raw", "Raw", buttonGroup, previewTypeListener),
                new JBRadioAction("Tree", "Tree", buttonGroup, previewTypeListener),
//                new JBRadioAction("Preview", "Preview", buttonGroup, previewTypeListener),
//                comboBoxAction,
                new AnAction("Use Soft Wraps", "Toggle using soft wraps in current editor", AllIcons.Actions.ToggleSoftWrap) {
                    @Override
                    public void actionPerformed(AnActionEvent anActionEvent) {
                        String actionCommand = buttonGroup.getSelection().getActionCommand();
                        if ("Pretty".equalsIgnoreCase(actionCommand)) {
                            EditorSettings settings = prettyEditor.getSettings();
                            settings.setUseSoftWraps(!settings.isUseSoftWraps());
                        } else if ("Raw".equalsIgnoreCase(actionCommand)) {
                            EditorSettings settings = rawEditor.getSettings();
                            settings.setUseSoftWraps(!settings.isUseSoftWraps());
                        }
                    }
                });

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, group, true);
        simpleToolWindowPanel1.setToolbar(toolbar.getComponent());
        simpleToolWindowPanel1.setContent(new JPanel(new BorderLayout()));
//        toolBarContainer.add(simpleToolWindowPanel1);
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

        editor.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            public void documentChanged(DocumentEvent e) {
            }
        });
        ((EditorEx) editor).setHighlighter(createHighlighter(FileTypes.PLAIN_TEXT));
        return editor;
    }

    private EditorHighlighter createHighlighter(LanguageFileType fileType) {
//        FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension("");
//        if (fileType == null) {
//            fileType = FileTypes.PLAIN_TEXT;
//        }

        SyntaxHighlighter originalHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(fileType, null, null);
        if (originalHighlighter == null) {
            originalHighlighter = new PlainSyntaxHighlighter();
        }

        EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
        LayeredLexerEditorHighlighter highlighter = new LayeredLexerEditorHighlighter(getFileHighlighter(fileType), scheme);// TODO: 2017/8/3 HTML
        highlighter.registerLayer(TextElementType, new LayerDescriptor(originalHighlighter, ""));
        return highlighter;
    }

    private SyntaxHighlighter getFileHighlighter(FileType fileType) {
        if (fileType == HtmlFileType.INSTANCE) {
            return new HtmlFileHighlighter();
        } else if (fileType == XmlFileType.INSTANCE) {
            return new XmlFileHighlighter();
        } else if (fileType == JsonFileType.INSTANCE) {
            return JsonSyntaxHighlighterFactory.getSyntaxHighlighter(fileType, mProject, null);
        }
        return new PlainSyntaxHighlighter();
    }

    private LanguageFileType getFileType(/*Header[] contentTypes*/) {
//        if (contentTypes != null && contentTypes.length > 0) {
//            Header contentType = contentTypes[0];
//            if (contentType.getValue().contains("text/html")) {
//                return HtmlFileType.INSTANCE;
//            } else if (contentType.getValue().contains("application/xml")) {
//                return XmlFileType.INSTANCE;
//            } else if (contentType.getValue().contains("application/json")) {
//                return JsonFileType.INSTANCE;
//            }
//        }
//        return PlainTextFileType.INSTANCE;
        return JsonFileType.INSTANCE;
    }

    public void showPretty(String text) {
        if (TextUtils.isEmpty(text))
            return;
        try {
            String prettyJsonString = getPrettyJson(text);

            WriteCommandAction.runWriteCommandAction(mProject, () -> prettyEditor.getDocument().setText(prettyJsonString));
            LanguageFileType fileType = getFileType();
            ((EditorEx) prettyEditor).setHighlighter(createHighlighter(fileType));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showRaw(String text) {
        if (TextUtils.isEmpty(text))
            return;
        try {
            WriteCommandAction.runWriteCommandAction(mProject, () -> rawEditor.getDocument().setText(text));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showTree(String jsonString) {
        if (TextUtils.isEmpty(jsonString))
            return;
        try {
            String prettyJsonString = getPrettyJson(jsonString);

            DefaultTreeModel model = TreeNodeCreator.getTreeModel(prettyJsonString);
            outputTree.setModel(model);
            expandAllNodes(outputTree, 0, outputTree.getRowCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPrettyJson(String jsonString) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser parser = new JsonParser();
        JsonElement je = parser.parse(jsonString);
        return gson.toJson(je);
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
}