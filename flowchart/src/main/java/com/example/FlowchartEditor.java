package com.example;

import javax.swing.JFrame;

import javax.swing.SwingUtilities;
import java.awt.BorderLayout;

public class FlowchartEditor extends JFrame {
    private WorkspacePanel workspacePanel;
    private Toolbar toolbar;

    public FlowchartEditor() {
        setTitle("Exam Right Determination Flowchart Editor");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        workspacePanel = new WorkspacePanel();
        toolbar = new Toolbar(workspacePanel);
        System.out.println("FlowchartEditor: workspacePanel = " + workspacePanel.connections);

        add(toolbar, BorderLayout.NORTH);
        add(workspacePanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlowchartEditor editor = new FlowchartEditor();
            editor.setVisible(true);
        });
    }
}
