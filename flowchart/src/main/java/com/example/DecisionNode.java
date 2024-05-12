
package com.example;

import java.awt.*;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;

import javax.swing.BorderFactory;

public class DecisionNode extends RuleNode {

    public DecisionNode(String text, NodeTypes type, int id, WorkspacePanel panel) {
        super(text, type, id, panel);
        // Set the preferred size of the decision node
        setPreferredSize(getPreferredSize());

        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setOpaque(true);
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        setFont(new Font("Arial", Font.BOLD, 10));

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(130, 65); // Change dimensions as needed
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Calls the parent's paint method to handle standard painting logic

        Graphics2D g2d = (Graphics2D) g.create();

        // Define the diamond shape
        int[] xPoints = { getWidth() / 2, 0, getWidth() / 2, getWidth() };
        int[] yPoints = { 0, getHeight() / 2, getHeight(), getHeight() / 2 };
        Polygon diamond = new Polygon(xPoints, yPoints, 4);

        // Fill the diamond with background color
        g2d.setColor(getBackground());
        g2d.fillPolygon(diamond);

        // Draw the diamond border
        g2d.setColor(getForeground());
        g2d.drawPolygon(diamond);

        // Set text color
        g2d.setColor(getForeground());

        // Create an attributed string to handle text wrapping and alignment
        AttributedString attributedString = new AttributedString(getText());
        attributedString.addAttribute(TextAttribute.FONT, getFont());
        AttributedCharacterIterator characterIterator = attributedString.getIterator();

        // Create a line break measurer to handle text wrapping
        LineBreakMeasurer measurer = new LineBreakMeasurer(characterIterator, g2d.getFontRenderContext());
        float wrappingWidth = getWidth() - 10; // Reduce text width for padding

        // Calculate the height of the text block to center it vertically
        float textHeight = 0;
        float posX = 5; // Horizontal padding
        float posY = 0; // Start at top
        ArrayList<TextLayout> layouts = new ArrayList<>();
        while (measurer.getPosition() < characterIterator.getEndIndex()) {
            TextLayout layout = measurer.nextLayout(wrappingWidth);
            layouts.add(layout);
            textHeight += (layout.getAscent() + layout.getDescent() + layout.getLeading());
        }

        // Start drawing from the vertical center minus half text height
        posY = (getHeight() / 2) - (textHeight / 2);
        for (TextLayout layout : layouts) {
            posY += layout.getAscent();
            float drawPosX = posX + (wrappingWidth - layout.getAdvance()) / 2; // Center horizontally
            layout.draw(g2d, drawPosX, posY);
            posY += layout.getDescent() + layout.getLeading();
        }

        g2d.dispose();
    }

}
