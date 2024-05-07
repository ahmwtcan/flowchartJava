
package com.example;

import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;

public class DecisionNode extends RuleNode {

    public DecisionNode(String text, NodeTypes type, int id) {
        super(text, type, id);

        // Set the preferred size of the decision node
        setPreferredSize(new Dimension(160, 160));

        setBorder(BorderFactory.createLineBorder(Color.BLACK));

    }

    @Override
    public void handleDoubleClick() {
        configuration();
    }

    @Override
    public void configuration() {
        // ask for the decision
        // ask for the decision
        String[] options = { "Yes", "No" };
        int result = JOptionPane.showOptionDialog(null, "Is the student a US Citizen?", "Decision",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        // If the user clicks OK, update the decision
        if (result == 0) {
            setText("Yes");
        } else {
            setText("No");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        int[] xPoints = { getWidth() / 2, 0, getWidth() / 2, getWidth() };
        int[] yPoints = { 0, getHeight() / 2, getHeight(), getHeight() / 2 };
        Polygon diamond = new Polygon(xPoints, yPoints, 4);

        // Create a new Graphics object to avoid modifying the original
        Graphics2D g2d = (Graphics2D) g.create();

        // Fill the diamond shape
        g2d.setColor(getBackground());
        g2d.fillPolygon(diamond);

        // Call super to draw the text field on top of the diamond shape
        super.paintComponent(g2d);

        // Draw the diamond shape's border
        g2d.setColor(getForeground());
        g2d.drawPolygon(diamond);

        // Dispose the graphics object
        g2d.dispose();
    }
}
