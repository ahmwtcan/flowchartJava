package com.example;

import java.awt.*;

import javax.swing.*;

public class CGPANode extends RuleNode {
    private double cgpaThreshold;

    public CGPANode(String text, NodeTypes type, int id, WorkspacePanel panel) {
        super(text, type, id, panel);

        // Set the preferred size of the CGPA node
        setPreferredSize(new Dimension(160, 160));

        // Set the background color of the CGPA node
        setBackground(Color.YELLOW);

        // Set the text color of the CGPA node
        setForeground(Color.BLACK);

        // Set the font of the CGPA node
        setFont(new Font("Arial", Font.PLAIN, 8));

    }

    @Override
    public void handleDoubleClick() {
        configuration();
    }

    @Override
    public void configuration() {
        // ask for the CGPA threshold

        JTextField cgpaField = new JTextField(null, 5);

        // Create a panel to hold the text field
        JPanel panel = new JPanel();
        panel.add(new JLabel("CGPA Threshold:"));
        panel.add(cgpaField);

        // Show a dialog box with the panel
        int result = JOptionPane.showConfirmDialog(null, panel, "Configure CGPA Threshold",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        // If the user clicks OK, update the CGPA threshold
        if (result == JOptionPane.OK_OPTION) {
            try {
                cgpaThreshold = Double.parseDouble(cgpaField.getText());
                setText(String.valueOf(cgpaThreshold));

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number.");
            }
        }

    }

    @Override
    public String getText() {
        return "CGPA Threshold: " + cgpaThreshold;

    }

    @Override
    protected void paintComponent(Graphics g) {
        // Fill the CGPA node with the background color
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw the border of the CGPA node
        g.setColor(getForeground());
        g.drawRect(0, 0, getWidth(), getHeight());

        // Call super to draw the text field on top of the CGPA node
        super.paintComponent(g);

        String text = "<html><center>CGPA Threshold: " + cgpaThreshold + "<br>";
        setText(text);

    }

}
