
package com.example;

import java.awt.*;

public class DecisionNode extends RuleNode {

    public DecisionNode(String text) {
        super(text);
        setPreferredSize(new Dimension(120, 120)); // Suitable size for diamond shape
    }

    @Override
    protected void paintComponent(Graphics g) {

        int w = getWidth();
        int h = getHeight();

        // Define the points for a rotated diamond shape (90 degrees clockwise)
        int[] xPoints = { h / 3, 0, h / 3, w };
        int[] yPoints = { w / 3, w / 5, 3 * w / 4, w / 2 };

        // Fill the rotated diamond shape
        g.setColor(getBackground());
        g.fillPolygon(xPoints, yPoints, 4);

        // Draw the outline of the rotated diamond
        g.setColor(getForeground());
        g.drawPolygon(xPoints, yPoints, 4);

        super.paintComponent(g); // Call super to clear the background and setup.

    }
}
