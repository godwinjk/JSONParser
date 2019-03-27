package com.jsonparse.ui.forms;

import com.jsonparse.ui.IParserWidget;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ParserWindow extends JFrame {

    private IParserWidget mParserWidget;
    private WindowAdapter mWindowAdapter;

   public ParserWindow(IParserWidget parserWidget,String title) {
        this.mParserWidget = parserWidget;

        setupView();
        setTitle(title);
    }

    public void setWindowAdapter(WindowAdapter mWindowAdapter) {
        this.mWindowAdapter = mWindowAdapter;
    }

    private void setupView() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int height = screenSize.height  / 2;
        int width = screenSize.width / 4;

        height = height == 0 ? 500 : height;
        width = width == 0 ? 300 : width;

        setPreferredSize(new Dimension(width, height));
        add(mParserWidget.getNewComponent(), BorderLayout.CENTER);
        setSize(new Dimension(width, height));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
                if (mWindowAdapter != null)
                    mWindowAdapter.windowClosing(e);
            }
        });
        java.awt.EventQueue.invokeLater(() -> {
            setVisible(true);
            toFront();
            repaint();
        });
    }
}
