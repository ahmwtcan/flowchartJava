package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class WorkspacePanel extends JPanel {
    private RuleNode draggingNode = null;
    private Point draggingStartPoint;
    public final List<Connection> connections = new ArrayList<>();
    private final AffineTransform viewTransform = new AffineTransform();

    public WorkspacePanel() {
        setBackground(Color.WHITE);
        setLayout(null); // Allows for absolute positioning
        // allow background to scroll with the panel
        setAutoscrolls(true);
        addMouseEvents();
    }

    private void addMouseEvents() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) { // Check if right-click
                    Connection clickedConnection = findConnectionNearPoint(e.getPoint());
                    if (clickedConnection != null) {
                        showConnectionContextMenu(e, clickedConnection);
                    }
                } else if (draggingNode != null) {
                    draggingNode = null;
                    draggingStartPoint = null;
                    repaint();
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggingNode != null) {
                    moveNode(e);
                }
            }

            private void moveNode(MouseEvent e) {
                Point2D transformedPoint = transformPointToModel(e.getPoint());
                int deltaX = (int) (transformedPoint.getX() - draggingStartPoint.getX());
                int deltaY = (int) (transformedPoint.getY() - draggingStartPoint.getY());

                draggingNode.setLocation(draggingNode.getX() + deltaX, draggingNode.getY() + deltaY);
                draggingStartPoint = e.getPoint();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    setCursor(Cursor.getDefaultCursor()); // Reset cursor after panning
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double scale = Math.pow(1.1, -e.getWheelRotation()); // Zoom in or out
                Point2D p = e.getPoint();
                viewTransform.translate(p.getX(), p.getY());
                viewTransform.scale(scale, scale);
                viewTransform.translate(-p.getX(), -p.getY());
                repaint();
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);
    }

    public void startDraggingForConnection(RuleNode node, Point start) {
        draggingNode = node;
        draggingStartPoint = SwingUtilities.convertPoint(node, start, this);
    }

    private Connection findConnectionNearPoint(Point point) {
        final int PROXIMITY_THRESHOLD = 5; // Adjust threshold as needed
        for (Connection conn : connections) {
            if (isNearConnection(point, conn, PROXIMITY_THRESHOLD)) {
                return conn;
            }
        }
        return null;
    }

    private Point2D transformPointToModel(Point2D p) {
        try {
            return viewTransform.inverseTransform(p, null);
        } catch (Exception ex) {
            return p; // Fallback: if transform fails, return original point
        }
    }

    private boolean isNearConnection(Point point, Connection conn, int threshold) {
        Point start = conn.getStartNode().getConnectionPoint();
        Point end = conn.getEndNode().getConnectionPoint();
        return Line2D.ptSegDist(start.x, start.y, end.x, end.y, point.x, point.y) <= threshold;
    }

    private void showConnectionContextMenu(MouseEvent e, Connection connection) {
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete Connection");
        deleteItem.addActionListener(event -> {
            connections.remove(connection);
            repaint();
        });
        contextMenu.add(deleteItem);
        contextMenu.show(this, e.getX(), e.getY());
    }

    public void addRuleNode(RuleNode node) {
        this.add(node);
        node.setLocation(node.getX(), node.getY()); // Ensure the node is positioned properly
        node.setSize(node.getPreferredSize()); // Use the preferred size or a fixed size
        repaint(); // Repaint the panel to show the new node
    }

    public void startDraggingForMoving(RuleNode node, Point initialClick) {
        draggingNode = node;
        draggingStartPoint = initialClick;
    }

    public void moveNode(RuleNode node, int deltaX, int deltaY) {
        int newX = node.getX() + deltaX;
        int newY = node.getY() + deltaY;
        node.setLocation(newX, newY);
        repaint(); // Ensure updates are immediately visible
    }

    public void dragConnection(Point current) {
        draggingStartPoint = current;
        repaint();
    }

    public void completeConnection(Point releaseAt) {
        if (draggingNode == null) {
            System.out.println("No node is currently being dragged for connection.");
            return; // Early exit if no node is being dragged
        }

        Point convertedPoint = SwingUtilities.convertPoint(this, releaseAt, this);
        Component comp = getComponentAt(convertedPoint);
        if (comp instanceof RuleNode && comp != draggingNode) {
            // Check if connection already exists to prevent duplicates
            if (connections.stream().noneMatch(c -> (c.getStartNode() == draggingNode && c.getEndNode() == comp) ||
                    (c.getStartNode() == comp && c.getEndNode() == draggingNode))) {
                showConnectionTypeMenu(releaseAt, draggingNode, (RuleNode) comp);
            } else {
                System.out.println("Connection already exists between: " + draggingNode.getText() + " and "
                        + ((RuleNode) comp).getText());
            }
        }
        resetDragging();
    }

    private void showConnectionTypeMenu(Point point, RuleNode startNode, RuleNode endNode) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem trueItem = new JMenuItem("True");
        trueItem.addActionListener(e -> finalizeConnection(startNode, endNode, true));
        JMenuItem falseItem = new JMenuItem("False");
        falseItem.addActionListener(e -> finalizeConnection(startNode, endNode, false));
        popupMenu.add(trueItem);
        popupMenu.add(falseItem);
        popupMenu.show(this, point.x, point.y);
    }

    private void finalizeConnection(RuleNode startNode, RuleNode endNode, boolean type) {
        connections.add(new Connection(startNode, endNode, type));
        System.out.println(
                "New connection added: " + startNode.getText() + " -> " + endNode.getText() + " [" + type + "]");
        repaint();
    }

    private void resetDragging() {
        draggingNode = null;
        draggingStartPoint = null;
        repaint();
    }

    public void removeConnection(Connection conn) {
        connections.remove(conn);
        repaint();
    }

    public void removeRuleNode(RuleNode node) {
        connections.removeIf(c -> c.getStartNode() == node || c.getEndNode() == node);
        remove(node);
        repaint();
    }

    public void stopDraggingNode() {
        draggingNode = null; // Clear any references to a dragging node
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setTransform(viewTransform); // Apply the current view transformations

        drawBackgroundGrid(g2d); // Draw the grid on the panel

        // Iterate over connections to draw them
        for (Connection conn : connections) {
            Point start = conn.getStartNode().getConnectionPoint();
            Point end = conn.getEndNode().getConnectionPoint();
            Rectangle endNodeBounds = conn.getEndNode().getBounds();
            g2d.setColor(Color.BLACK);
            g2d.drawLine(start.x, start.y, end.x, end.y);
            drawArrow(g2d, start, end, endNodeBounds); // Draw arrow for each connection

            // Draw connection labels (True/False)
            int midX = (start.x + end.x) / 2;
            int midY = (start.y + end.y) / 2;
            g2d.drawString(conn.getType() ? "True" : "False", midX, midY);
        }

        // Draw a line while dragging to create a new connection
        if (draggingNode != null && draggingStartPoint != null) {
            Point from = draggingNode.getConnectionPoint();
            g2d.setColor(Color.GREEN);
            g2d.drawLine(from.x, from.y, draggingStartPoint.x, draggingStartPoint.y);
        }
    }

    private void drawBackgroundGrid(Graphics2D g2) {
        int dotInterval = 20; // Distance between dots
        int dotSize = 2;
        g2.setColor(Color.LIGHT_GRAY);

        // Calculate offsets based on current view origin
        int offsetX = Math.floorMod((int) viewTransform.getTranslateX(), dotInterval);
        int offsetY = Math.floorMod((int) viewTransform.getTranslateY(), dotInterval);

        for (int x = 0; x < getWidth(); x += dotInterval) {
            for (int y = 0; y < getHeight(); y += dotInterval) {
                g2.fillOval(x + offsetX - dotSize / 2, y + offsetY - dotSize / 2, dotSize, dotSize);
            }
        }
    }

    private void drawArrow(Graphics2D g2d, Point start, Point end, Rectangle targetNodeBounds) {
        int arrowLength = 12; // Length of the arrow head
        double angle = Math.atan2(end.y - start.y, end.x - start.x);

        // Calculate position for the arrowhead to draw
        double x1 = end.x - arrowLength * Math.cos(angle - Math.PI / 6);
        double y1 = end.y - arrowLength * Math.sin(angle - Math.PI / 6);
        double x2 = end.x - arrowLength * Math.cos(angle + Math.PI / 6);
        double y2 = end.y - arrowLength * Math.sin(angle + Math.PI / 6);

        g2d.drawLine(end.x, end.y, (int) x1, (int) y1);
        g2d.drawLine(end.x, end.y, (int) x2, (int) y2);
    }

    private Point calculateIntersection(Point start, Point end, Rectangle bounds) {
        // Logic to calculate the intersection of the line with node bounds
        double dx = end.x - start.x;
        double dy = end.y - start.y;

        if (dx == 0) { // Vertical line
            return new Point(start.x, dy > 0 ? bounds.y : bounds.y + bounds.height);
        }
        if (dy == 0) { // Horizontal line
            return new Point(dx > 0 ? bounds.x : bounds.x + bounds.width, start.y);
        }

        double slope = dy / dx;
        double intercept = start.y - slope * start.x;

        // Check intersections with all four bounds
        double[] xPoints = new double[] {
                bounds.x,
                bounds.x + bounds.width,
                (bounds.y - intercept) / slope,
                (bounds.y + bounds.height - intercept) / slope
        };

        double[] yPoints = new double[] {
                slope * bounds.x + intercept,
                slope * (bounds.x + bounds.width) + intercept,
                bounds.y,
                bounds.y + bounds.height
        };

        // Find the closest point within the line segment
        Point closestPoint = new Point(end);
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < xPoints.length; i++) {
            double px = xPoints[i];
            double py = yPoints[i];
            if (px >= bounds.x && px <= bounds.x + bounds.width && py >= bounds.y && py <= bounds.y + bounds.height) {
                double dist = Math.hypot(px - start.x, py - start.y);
                if (dist < minDistance && dist < Math.hypot(dx, dy)) {
                    minDistance = dist;
                    closestPoint.setLocation(px, py);
                }
            }
        }

        return closestPoint;
    }

    private class Connection {
        private final RuleNode startNode;
        private final RuleNode endNode;
        private final boolean type;

        public Connection(RuleNode start, RuleNode end, boolean type) {
            this.startNode = start;
            this.endNode = end;
            this.type = type;
        }

        public RuleNode getStartNode() {
            return startNode;
        }

        public RuleNode getEndNode() {
            return endNode;
        }

        public boolean getType() {
            return type;
        }
    }
}
