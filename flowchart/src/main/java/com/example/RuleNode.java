package com.example;

import javax.accessibility.AccessibleContext;
import javax.swing.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class RuleNode extends JLabel {
    private static final int EDGE_THRESHOLD = 10; // Edge threshold for connections
    public Point initialClick; // Store initial click location for dragging calculations
    private String nodeName; // Store the actual node name without HTML tags
    private boolean resizing;
    private int id;
    private WorkspacePanel panel;

    public RuleNode(String text, NodeTypes type, int id, WorkspacePanel panel) {
        super(text);
        nodeName = text;
        this.id = id;

        this.panel = panel;

        initializeUI();

    }

    private void initializeUI() {
        updateText(); // Better text formatting
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setOpaque(true);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(66, 60)); // Make nodes larger
        setSize(getPreferredSize());
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

        addMouseEvents();
    }

    @Override
    @JsonIgnore
    public AccessibleContext getAccessibleContext() {
        return super.getAccessibleContext();
    }

    private void addMouseEvents() {
        MouseAdapter mouseAdapter = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                Point2D transformedPoint = panel.transformPointToModel(e.getPoint());
                System.out.println("RuleNode: transformedPoint = " + transformedPoint);
                initialClick = new Point((int) transformedPoint.getX(), (int) transformedPoint.getY());
                System.out.println("RuleNode: initialClick = " + initialClick);
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {

                    handleDoubleClick();

                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    if (isNearCorner(initialClick)) {
                        System.out.println("RuleNode: isNearCorner = " + isNearCorner(initialClick));
                        resizing = true;
                        setCursor(resizing ? Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR)
                                : Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    } else
                        startDragging(e);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem deleteItem = new JMenuItem("Delete");
                    JMenuItem configurationItem = new JMenuItem("Configuration");
                    deleteItem.addActionListener(e1 -> {
                        WorkspacePanel panel = (WorkspacePanel) getParent();
                        panel.removeRuleNode(RuleNode.this);
                    });
                    configurationItem.addActionListener(e1 -> configuration());
                    popup.add(configurationItem);

                    popup.add(deleteItem);
                    popup.show(RuleNode.this, e.getX(), e.getY());

                    repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (resizing) {
                    resizeNode(e);
                } else {
                    dragNode(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                if (resizing) {
                    resizing = false;
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                } else {
                    finishDragging(e);
                }
            }

            private void startDragging(MouseEvent e) {
                if (isNearEdge(initialClick)) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    panel.startDraggingForConnection(RuleNode.this, e.getPoint());
                } else {
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    panel.startDraggingForMoving(RuleNode.this, initialClick);
                }
            }

            private void dragNode(MouseEvent e) {
                if (isNearEdge(initialClick)) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    panel.dragConnection(SwingUtilities.convertPoint(RuleNode.this, e.getPoint(), panel));
                } else {
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    panel.moveNode(RuleNode.this, e.getX() - initialClick.x, e.getY() - initialClick.y);
                }
            }

            private void finishDragging(MouseEvent e) {
                if (isNearEdge(initialClick)) {
                    panel.completeConnection(SwingUtilities.convertPoint(RuleNode.this, e.getPoint(), panel));
                }
                panel.stopDraggingNode();
            }

            private void resizeNode(MouseEvent e) {
                int widthChange = e.getX() - initialClick.x;
                int heightChange = e.getY() - initialClick.y;
                int newWidth = Math.max(getWidth() + widthChange, 50); // Minimum width of 50
                int newHeight = Math.max(getHeight() + heightChange, 50); // Minimum height of 50
                setSize(newWidth, newHeight);
                initialClick = e.getPoint();
                revalidate();
                repaint();
            }

        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    public void configuration() {
        // Override this method in subclasses to show configuration dialog
    }

    public void handleDoubleClick() {
        String newName = JOptionPane.showInputDialog("Enter new name for the node:", nodeName);
        if (newName != null && !newName.trim().isEmpty()) {
            nodeName = newName; // Update the plain text name
            updateText(); // Update the display text
        }
    }

    @Override
    public String getText() {
        return nodeName;
    }

    public int getId() {
        return id;
    }

    @Override
    @JsonIgnore
    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    private boolean isNearCorner(Point p) {
        return p.x >= getWidth() - EDGE_THRESHOLD && p.y >= getHeight() - EDGE_THRESHOLD;
    }

    private boolean isNearEdge(Point p) {
        int w = getWidth();
        int h = getHeight();
        return p.x <= EDGE_THRESHOLD || p.x >= w - EDGE_THRESHOLD ||
                p.y <= EDGE_THRESHOLD || p.y >= h - EDGE_THRESHOLD;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // transform the graphics to the model coordinates
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.RED);
        g2d.fillRect(getWidth() - EDGE_THRESHOLD, getHeight() - EDGE_THRESHOLD, EDGE_THRESHOLD, EDGE_THRESHOLD);

    }

    public void updateText() {
        setText("<html><center>" + nodeName + "</center></html>"); // Format text with HTML for display
    }

    public Point getConnectionPoint() {
        return new Point(getX() + getWidth() / 2, getY() + getHeight() / 2);
    }
}
