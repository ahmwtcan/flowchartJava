package com.example;

import javax.swing.JButton;
import javax.swing.JToolBar;

public class Toolbar extends JToolBar {
    private JButton addRuleButton = new JButton("Add Rule");
    private JButton desicionButton = new JButton("Add Decision");
    private JButton resultButton = new JButton("Add Result");

    public Toolbar(WorkspacePanel workspacePanel) {
        addRuleButton.addActionListener(e -> {
            RuleNode newNode = new RuleNode("New Rule");
            newNode.setLocation(10, 10);
            workspacePanel.addRuleNode(newNode);
        });

        desicionButton.addActionListener(e -> {
            RuleNode newNode = new DecisionNode("Decision");
            newNode.setLocation(100, 100);
            workspacePanel.addRuleNode(newNode);
        });

        resultButton.addActionListener(e -> {
            RuleNode newNode = new ResultNode("Result");
            newNode.setLocation(200, 200);
            workspacePanel.addRuleNode(newNode);
        });

        this.add(resultButton);
        this.add(desicionButton);
        this.add(addRuleButton);
    }
}
