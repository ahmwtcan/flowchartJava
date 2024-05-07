package com.example;

import javax.swing.*;
import java.awt.*;

public class ConditionNode extends RuleNode {
    private ThresholdType ffThresholdType = ThresholdType.FF_COUNT;
    private int ffLowerLimit = 0;
    private int ffUpperLimit = 1;

    private boolean tableCourseFlag = false; // Flag for Table Course

    public ConditionNode(String text, NodeTypes type, int id, WorkspacePanel panel) {
        super(text, type, id, panel);
        setPreferredSize(new Dimension(250, 150));
        setBackground(new Color(173, 216, 230));
        setForeground(Color.BLACK);
        setFont(new Font("Arial", Font.BOLD, 12));
    }

    @Override
    public void handleDoubleClick() {
        SwingUtilities.invokeLater(this::configuration); // Ensure it's called on the EDT
    }

    @Override
    public void configuration() {
        JComboBox<ThresholdType> ffTypeBox = new JComboBox<>(ThresholdType.values());
        ffTypeBox.setSelectedItem(ffThresholdType);
        JTextField ffLowerField = new JTextField(String.valueOf(ffLowerLimit), 5);
        JTextField ffUpperField = new JTextField(String.valueOf(ffUpperLimit), 5);
        JCheckBox tableCourseCheckBox = new JCheckBox("Table Course Flag", tableCourseFlag);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("FF Threshold Type:"));
        panel.add(ffTypeBox);
        panel.add(new JLabel("Lower Limit:"));
        panel.add(ffLowerField);
        panel.add(new JLabel("Upper Limit:"));
        panel.add(ffUpperField);
        panel.add(tableCourseCheckBox);

        int result = JOptionPane.showConfirmDialog(null, panel, "Configure Thresholds", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            ffThresholdType = (ThresholdType) ffTypeBox.getSelectedItem();
            ffLowerLimit = Integer.parseInt(ffLowerField.getText());
            ffUpperLimit = Integer.parseInt(ffUpperField.getText());
            tableCourseFlag = tableCourseCheckBox.isSelected();

            repaint(); // Repaint to update text after changes
        }
    }

    @Override
    protected void paintComponent(Graphics g) {

        // Clear the background
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        // Set text properties
        g.setColor(getForeground());
        g.setFont(getFont());

        // Draw debugging frame
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        super.paintComponent(g);

        // Draw the text
        String typeText = ffThresholdType.toString();
        String rangeText = String.format("(%d < %d)", ffLowerLimit, ffUpperLimit);
        String tableCourseText = "T.Course: " + (tableCourseFlag ? "Enabled" : "Disabled");

        // Calculate y position based on font metrics
        int typeY = 10 + g.getFontMetrics().getHeight();
        int rangeY = typeY + g.getFontMetrics().getHeight();
        int tableCourseY = rangeY + g.getFontMetrics().getHeight();

        g.drawString(typeText, 5, typeY);
        g.drawString(rangeText, 5, rangeY);
        g.drawString(tableCourseText, 5, tableCourseY);
    }

    public enum ThresholdType {
        FF_COUNT, WrL_COUNT, FAILED_COUNT
    }
}
