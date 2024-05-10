package com.example;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.*;

public class MaximumStudyDurationNode extends RuleNode {

    private int semesterCount;

    public MaximumStudyDurationNode(String text, NodeTypes type, int id, WorkspacePanel panel) {
        super(text, type, id, panel);
        setPreferredSize(new Dimension(160, 100)); // Adjust size as needed
        setBackground(Color.lightGray); // Set a different background color
        semesterCount = 0; // Default value
        setFont(new Font("Arial", Font.BOLD, 12));

    }

    @Override
    public void handleDoubleClick() {
        configuration();
    }

    @Override
    public void configuration() {
        // Ask for the maximum study duration threshold
        JTextField semesterField = new JTextField(null, 5);

        // Create a panel to hold the text field
        JPanel panel = new JPanel();
        panel.add(new JLabel("Study Duration Threshold:"));
        panel.add(semesterField);

        // Show a dialog box with the panel
        int result = JOptionPane.showConfirmDialog(this.panel, panel, "Configure Study Duration Threshold",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        // If the user clicks OK, update the study duration threshold
        if (result == JOptionPane.OK_OPTION) {
            try {
                semesterCount = Integer.parseInt(semesterField.getText());
                updateText(); // Update the text to reflect the new value

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this.panel, "Please enter a valid number.");
            }
        }

        repaint(); // Repaint to update text after changes
    }

    @Override
    public String getText() {
        return "Study Duration Threshold: " + semesterCount;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Clear the background
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw the border
        g.setColor(getForeground());
        g.drawRect(0, 0, getWidth(), getHeight());

        // Draw the text

        String text1 = "Study Duration";
        String text2 = "Threshold: " + semesterCount;

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString(text1, 10, 20);
        g.drawString(text2, 10, 40);

    }

}
