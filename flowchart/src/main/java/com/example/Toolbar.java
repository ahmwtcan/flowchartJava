package com.example;

import javax.swing.JButton;
import javax.swing.JToolBar;

public class Toolbar extends JToolBar {
    private JButton desicionButton = new JButton("Karar Ekle");
    private JButton resultButton = new JButton(" Sonuç Ekle");
    private JButton startButton = new JButton("Başlangıç Ekle");
    private JButton conditionButton = new JButton(" Ders Not Durumu Ekle");
    private JButton saveButton = new JButton("Kaydet");
    private JButton cgPaButton = new JButton("Not Ortalaması ");
    private JButton maximumButton = new JButton("Eğitim süresi Ekle");
    private int id = 0;

    public Toolbar(WorkspacePanel workspacePanel) {

        desicionButton.addActionListener(e -> {
            RuleNode newNode = new DecisionNode("Karar", NodeTypes.DECISION, id++, workspacePanel);
            newNode.setLocation(100, 100);
            workspacePanel.addRuleNode(newNode);
        });

        resultButton.addActionListener(e -> {
            RuleNode newNode = new ResultNode("Sonuç", NodeTypes.RESULT, id++, workspacePanel);
            newNode.setLocation(200, 200);
            workspacePanel.addRuleNode(newNode);
        });

        startButton.addActionListener(e -> {
            RuleNode newNode = new StartNode("Başlangıç", NodeTypes.START, id++, workspacePanel);
            newNode.setLocation(300, 300);
            workspacePanel.addRuleNode(newNode);
        });

        conditionButton.addActionListener(e -> {
            RuleNode newNode = new ConditionNode("Ders Not Durumu", NodeTypes.RULE, id++, workspacePanel);
            newNode.setLocation(400, 400);
            workspacePanel.addRuleNode(newNode);
        });

        cgPaButton.addActionListener(e -> {
            RuleNode newNode = new CGPANode("Not Ortalaması", NodeTypes.CGPA, id++, workspacePanel);
            newNode.setLocation(500, 500);
            workspacePanel.addRuleNode(newNode);
        });

        maximumButton.addActionListener(e -> {
            RuleNode newNode = new MaximumStudyDurationNode("Eğitim süresi", NodeTypes.MAXIMUM_STUDY_DURATION,
                    id++, workspacePanel);
            newNode.setLocation(600, 600);
            workspacePanel.addRuleNode(newNode);
        });

        saveButton.addActionListener(e -> {
            workspacePanel.save();
        });

        this.add(saveButton);
        this.add(conditionButton);
        this.add(startButton);
        this.add(desicionButton);
        this.add(resultButton);
        this.add(cgPaButton);
        this.add(maximumButton);

    }
}
