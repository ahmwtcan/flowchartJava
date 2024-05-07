package com.example;

import javax.swing.JButton;
import javax.swing.JToolBar;

public class Toolbar extends JToolBar {
    private JButton addRuleButton = new JButton("Add Rule");
    private JButton desicionButton = new JButton("Add Decision");
    private JButton resultButton = new JButton("Add Result");
    private JButton startButton = new JButton("Add Start");
    private JButton conditionButton = new JButton("Add Condition");
    private JButton saveButton = new JButton("Save");
    private JButton cgPaButton = new JButton("Add CGPA");
    private int id = 0;

    public Toolbar(WorkspacePanel workspacePanel) {
        addRuleButton.addActionListener(e -> {
            RuleNode newNode = new RuleNode("New Rule", NodeTypes.RULE, id++, workspacePanel);
            newNode.setLocation(10, 10);
            workspacePanel.addRuleNode(newNode);
        });

        desicionButton.addActionListener(e -> {
            RuleNode newNode = new DecisionNode("Decision", NodeTypes.DECISION, id++, workspacePanel);
            newNode.setLocation(100, 100);
            workspacePanel.addRuleNode(newNode);
        });

        resultButton.addActionListener(e -> {
            RuleNode newNode = new ResultNode("Result", NodeTypes.RESULT, id++, workspacePanel);
            newNode.setLocation(200, 200);
            workspacePanel.addRuleNode(newNode);
        });

        startButton.addActionListener(e -> {
            RuleNode newNode = new StartNode("Start", NodeTypes.START, id++, workspacePanel);
            newNode.setLocation(300, 300);
            workspacePanel.addRuleNode(newNode);
        });

        conditionButton.addActionListener(e -> {
            RuleNode newNode = new ConditionNode("Condition", NodeTypes.RULE, id++, workspacePanel);
            newNode.setLocation(400, 400);
            workspacePanel.addRuleNode(newNode);
        });

        cgPaButton.addActionListener(e -> {
            RuleNode newNode = new CGPANode("CGPA", NodeTypes.CGPA, id++, workspacePanel);
            newNode.setLocation(500, 500);
            workspacePanel.addRuleNode(newNode);
        });

        saveButton.addActionListener(e -> {
            workspacePanel.save();
        });

        this.add(saveButton);
        this.add(conditionButton);
        this.add(startButton);
        this.add(desicionButton);
        this.add(addRuleButton);
        this.add(resultButton);
        this.add(cgPaButton);

    }
}
