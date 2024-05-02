package com.example;

import javax.swing.*;
import javax.xml.namespace.QName;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RuleNode extends JLabel {
    private static final int EDGE_THRESHOLD = 10; // Edge threshold for connections
    private Point initialClick; // Store initial click location for dragging calculations
    private String nodeName; // Store the actual node name without HTML tags
    private boolean resizing;

    public RuleNode(String text) {
        super(text);
        nodeName = text;
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

    private void addMouseEvents() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    handleDoubleClick();
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    if (isNearCorner(initialClick)) {
                        resizing = true;
                        setCursor(resizing ? Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR)
                                : Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    } else
                        startDragging(e);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem deleteItem = new JMenuItem("Delete");
                    deleteItem.addActionListener(e1 -> {
                        WorkspacePanel panel = (WorkspacePanel) getParent();
                        panel.removeRuleNode(RuleNode.this);
                    });
                    popup.add(deleteItem);
                    popup.show(RuleNode.this, e.getX(), e.getY());
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
                WorkspacePanel panel = (WorkspacePanel) getParent();
                if (isNearEdge(initialClick)) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    panel.startDraggingForConnection(RuleNode.this, e.getPoint());
                } else {
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    panel.startDraggingForMoving(RuleNode.this, initialClick);
                }
            }

            private void dragNode(MouseEvent e) {
                WorkspacePanel panel = (WorkspacePanel) getParent();
                if (isNearEdge(initialClick)) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    panel.dragConnection(SwingUtilities.convertPoint(RuleNode.this, e.getPoint(), panel));
                } else {
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    panel.moveNode(RuleNode.this, e.getX() - initialClick.x, e.getY() - initialClick.y);
                }
            }

            private void finishDragging(MouseEvent e) {
                WorkspacePanel panel = (WorkspacePanel) getParent();
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
            }

            private void handleDoubleClick() {
                String newName = JOptionPane.showInputDialog("Enter new name for the node:", nodeName);
                if (newName != null && !newName.trim().isEmpty()) {
                    nodeName = newName; // Update the plain text name
                    updateText(); // Update the display text
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

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
        g.setColor(Color.RED);
        g.fillRect(getWidth() - EDGE_THRESHOLD, getHeight() - EDGE_THRESHOLD, EDGE_THRESHOLD, EDGE_THRESHOLD);
    }

    private void updateText() {
        setText("<html><center>" + nodeName + "</center></html>"); // Format text with HTML for display
    }

    public Point getConnectionPoint() {
        return new Point(getX() + getWidth() / 2, getY() + getHeight() / 2);
    }
}
