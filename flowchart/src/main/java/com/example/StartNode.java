package com.example;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

public class StartNode extends RuleNode {

    public StartNode(String text, NodeTypes type, int id) {
        super(text, type, id);

        // Set the preferred size of the start node
        setPreferredSize(new Dimension(80, 80));

        // Set the background color of the start node
        setBackground(Color.BLUE);

        // Set the text color of the start node
        setForeground(Color.WHITE);

        // Set the text of the start node
        setText("<html><center>" + text + "</center></html>");

        // Set the font of the start node
        setFont(new Font("Arial", Font.BOLD, 8));

    }

    @Override
    protected void paintComponent(Graphics g) {
        // Fill the start node with the background color
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw the border of the start node
        g.setColor(getForeground());
        g.drawRect(0, 0, getWidth(), getHeight());

        // Call super to draw the text field on top of the start node
        super.paintComponent(g);

    }
}
