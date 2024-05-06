package com.example;

import javax.swing.*;
import java.awt.*;

public class ConditionNode extends RuleNode {
    private int ffGradeThreshold = 1; // Default threshold for "FF" grades
    private int wrLGradeThreshold = 5; // Default threshold for "WrL" grades
    private int tableCourseThreshold = 1; // Default threshold for "Table Course" grades
    private int neverTakenThreshold = 1; // Default threshold for "Never Taken" grades

    public ConditionNode(String text, NodeTypes type, int id) {
        super(text, type, id);
        setPreferredSize(new Dimension(200, 100)); // Adjusted size for additional text

        // Set the background color to light blue
        setBackground(new Color(173, 216, 230));

        // Set the text color to black
        setForeground(Color.BLACK);

        // Set the font size to 12
        setFont(new Font("Arial", Font.PLAIN, 12));

        // Update the text to display the current thresholds
        updateText();
    }

    @Override
    public void handleDoubleClick() {
        configuration();
    }

    @Override
    public void configuration() {
        JTextField ffField = new JTextField(String.valueOf(ffGradeThreshold), 5);
        JTextField wrLField = new JTextField(String.valueOf(wrLGradeThreshold), 5);
        JTextField tableCourseField = new JTextField(String.valueOf(tableCourseThreshold), 5);
        JTextField neverTakenField = new JTextField(String.valueOf(neverTakenThreshold), 5);

        JPanel panel = new JPanel();
        panel.add(new JLabel("FF Grade Threshold:"));
        panel.add(ffField);
        panel.add(Box.createHorizontalStrut(15)); // Spacer
        panel.add(new JLabel("WrL Grade Threshold:"));
        panel.add(wrLField);
        panel.add(Box.createHorizontalStrut(15)); // Spacer
        panel.add(new JLabel("Table Course Threshold:"));
        panel.add(tableCourseField);
        panel.add(Box.createHorizontalStrut(15)); // Spacer
        panel.add(new JLabel("Never Taken Threshold:"));
        panel.add(neverTakenField);

        int result = JOptionPane.showConfirmDialog(null, panel,
                "Configure Grade Thresholds", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                ffGradeThreshold = Integer.parseInt(ffField.getText());
                wrLGradeThreshold = Integer.parseInt(wrLField.getText());
                tableCourseThreshold = Integer.parseInt(tableCourseField.getText());
                neverTakenThreshold = Integer.parseInt(neverTakenField.getText());
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "Please enter valid integers.");
            }
        }
    }

    private void updateText() {

        setText("<html><center>" +
                "FF Grade Threshold: " + ffGradeThreshold + "<br>" +
                "WrL Grade Threshold: " + wrLGradeThreshold + "<br>" +
                "Table Course Threshold: " + tableCourseThreshold + "<br>" +
                "Never Taken Threshold: " + neverTakenThreshold + "<br>" +
                "</center></html>");
    }

    @Override
    public String getText() {
        return "FF Grade Threshold: " + ffGradeThreshold + " " +
                "WrL Grade Threshold: " + wrLGradeThreshold + " " +
                "Table Course Threshold: " + tableCourseThreshold + " " +
                "Never Taken Threshold: " + neverTakenThreshold;

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Clear the background
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        // Set text properties
        g.setColor(getForeground());
        g.setFont(getFont());

        // Define lines of text
        String[] lines = {
                "FF Grade Threshold: " + ffGradeThreshold,
                "WrL Grade Threshold: " + wrLGradeThreshold,
                "Table Course Threshold: " + tableCourseThreshold,
                "Never Taken Threshold: " + neverTakenThreshold
        };

        // Calculate the starting y position for centered text
        int y = (getHeight() - lines.length * g.getFontMetrics().getHeight()) / 2;

        // Draw each line of text
        for (String line : lines) {
            int textWidth = g.getFontMetrics().stringWidth(line);
            int x = (getWidth() - textWidth) / 2; // Center the text horizontally
            g.drawString(line, x, y);
            y += g.getFontMetrics().getHeight();
        }
    }

}
