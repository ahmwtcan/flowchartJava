
package com.example;

import java.awt.*;

import javax.swing.BorderFactory;

public class DecisionNode extends RuleNode {

    public DecisionNode(String text, NodeTypes type, int id, WorkspacePanel panel) {
        super(text, type, id, panel);

        // Set the preferred size of the decision node
        setPreferredSize(new Dimension(160, 160));

        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setOpaque(true);
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        setFont(new Font("Arial", Font.PLAIN, 8));

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

        // Call super to draw the text field on top of the diamond shape
        super.paintComponent(g2d);

        // Draw the diamond shape's border
        g2d.setColor(getForeground());
        g2d.drawPolygon(diamond);

        // Draw the border of the decision node
        g2d.drawRect(0, 0, getWidth(), getHeight());
        g2d.setColor(getForeground());

    }
}
