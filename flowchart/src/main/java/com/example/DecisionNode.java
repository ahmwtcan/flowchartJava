
package com.example;

import java.awt.*;

import javax.swing.BorderFactory;

public class DecisionNode extends RuleNode {

    public DecisionNode(String text, NodeTypes type, int id, WorkspacePanel panel) {
        super(text, type, id, panel);

        // Set the preferred size of the decision node
        setPreferredSize(new Dimension(160, 160));

        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Set the background color of the decision node
        setBackground(Color.WHITE);

        // Set the text color of the decision node
        setForeground(Color.BLACK);

        // Set the font of the decision node
        setFont(new Font("Arial", Font.BOLD, 8));

        setText("<html><center>" + text + "</center></html>");

    }

    @Override
    protected void paintComponent(Graphics g) {

        int[] xPoints = { getWidth() / 2, 0, getWidth() / 2, getWidth() };
        int[] yPoints = { 0, getHeight() / 2, getHeight(), getHeight() / 2 };
        Polygon diamond = new Polygon(xPoints, yPoints, 4);

        // Create a new Graphics object to avoid modifying the original
        Graphics2D g2d = (Graphics2D) g;

        // Fill the diamond shape
        g2d.setColor(getBackground());
        g2d.fillPolygon(diamond);

        super.paintComponent(g);

        // Draw the diamond shape's border
        g2d.setColor(getForeground());
        g2d.drawPolygon(diamond);

        // Dispose the graphics object
        g2d.dispose();
    }
}
