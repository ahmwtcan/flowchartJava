package com.example;

import java.awt.*;

public class ResultNode extends RuleNode {
    public ResultNode(String text) {
        super(text);
        setPreferredSize(new Dimension(160, 80)); // Typical size for a rectangle
    }

    @Override
    public Point getConnectionPoint() {
        return new Point(getX() + getWidth() / 2, getY() + getHeight() / 2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(getForeground());
        g.drawRect(0, 0, getWidth(), getHeight());

        super.paintComponent(g);

    }
}
