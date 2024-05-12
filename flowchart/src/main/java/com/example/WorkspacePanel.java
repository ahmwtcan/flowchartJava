package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class WorkspacePanel extends JPanel {
    private RuleNode draggingNode = null;
    public final List<Connection> connections = new ArrayList<>();
    private Point draggingStartPoint;
    private final AffineTransform viewTransform = new AffineTransform();
    private Point viewOrigin = new Point(0, 0);
    private double currentScale = 1.0; // Track current scale
    private final double minScale = 0.1; // Minimum scale factor
    private final double maxScale = 10.0; // Maximum scale factor

    public WorkspacePanel() {
        setBackground(Color.WHITE);
        setLayout(null); // Allows for absolute positioning
        // allow background to scroll with the panel
        setAutoscrolls(true);

        MouseHandler mouseHandler = new MouseHandler();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        addMouseWheelListener(mouseHandler);
    }

    private class MouseHandler extends MouseAdapter {
        private Point2D lastMouseDrag;
        private final Point2D dragStart = new Point2D.Double();

        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);

            Point2D modelPoint = transformPointToModel(e.getPoint());
            draggingStartPoint = new Point((int) modelPoint.getX(), (int) modelPoint.getY());
            System.out.println("Mouse Pressed - Screen: " + e.getPoint() + ", Model: " + modelPoint);

            if (e.getButton() == MouseEvent.BUTTON3) { // Check if right-click
                Connection clickedConnection = findConnectionNearPoint(modelPoint);
                if (clickedConnection != null) {
                    showConnectionContextMenu(e, clickedConnection);
                }
            } else if (draggingNode != null) {
                draggingNode = null;
                draggingStartPoint = null;
                repaint();
            } else if (SwingUtilities.isLeftMouseButton(e)) {
                lastMouseDrag = transformPointToModel(e.getPoint());
                dragStart.setLocation(lastMouseDrag);
                repaint();
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }

        }

        @Override
        public void mouseDragged(MouseEvent e) {

            if (draggingNode == null && lastMouseDrag != null) {
                // Get the current mouse position in the transformed model space
                Point2D current = transformPointToModel(e.getPoint());

                // Calculate the change in x and y from the last drag point
                double dx = current.getX() - lastMouseDrag.getX();
                double dy = current.getY() - lastMouseDrag.getY();

                // Update the view origin by adding the delta (dx, dy)
                viewOrigin.translate((int) dx, (int) dy);

                // Update positions of all components to follow the drag
                for (Component comp : getComponents()) {
                    if (comp instanceof RuleNode) {
                        RuleNode node = (RuleNode) comp;
                        Point loc = node.getLocation();
                        // Add the deltas to the current node positions to move them with the drag
                        node.setLocation((int) (loc.x + dx), (int) (loc.y + dy));
                    }
                }

                // Update the last drag point
                lastMouseDrag = current;

                // Revalidate and repaint the panel to reflect changes
                revalidate();
                repaint();
            }
        }

        // @Override
        // public void mouseWheelMoved(MouseWheelEvent e) {
        // double scaleDelta = e.getWheelRotation() < 0 ? 1.1 : 0.9;
        // double newScale = currentScale * scaleDelta;

        // // Clamp the scale to prevent too much zoom in or out
        // if (newScale < minScale) {
        // scaleDelta = minScale / currentScale;
        // } else if (newScale > maxScale) {
        // scaleDelta = maxScale / currentScale;
        // }

        // currentScale *= scaleDelta; // Update current scale

        // Point2D p = e.getPoint();
        // try {
        // Point2D transformedPoint = viewTransform.inverseTransform(p, null);
        // viewTransform.translate(transformedPoint.getX(), transformedPoint.getY());
        // viewTransform.scale(scaleDelta, scaleDelta);
        // viewTransform.translate(-transformedPoint.getX(), -transformedPoint.getY());
        // } catch (NoninvertibleTransformException ex) {
        // ex.printStackTrace();
        // }

        // updateNodePositionsAndSizes(scaleDelta);
        // repaint();
        // }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isMiddleMouseButton(e)) {
                lastMouseDrag = null;
                setCursor(Cursor.getDefaultCursor());
            }
        }

    }

    public void startDraggingForConnection(RuleNode node, Point start) {
        draggingNode = node;
        draggingStartPoint = SwingUtilities.convertPoint(node, start, this);
    }

    public void updateNodePositionsAndSizes(double scaleDelta) {

        for (Component comp : getComponents()) {
            if (comp instanceof RuleNode) {
                RuleNode node = (RuleNode) comp;
                Point loc = node.getLocation();
                // Scale the node's location
                loc.x *= scaleDelta;
                loc.y *= scaleDelta;
                node.setLocation(loc);

                // Scale the node's size
                Dimension size = node.getSize();
                size.width *= scaleDelta;
                size.height *= scaleDelta;
                node.setSize(size);
            }
        }

    }

    private void drawBackgroundGrid(Graphics2D g2) {
        int dotInterval = 20;
        g2.setColor(Color.LIGHT_GRAY);
        int offsetX = viewOrigin.x % dotInterval;
        int offsetY = viewOrigin.y % dotInterval;

        // draw dots
        for (int x = offsetX; x < getWidth(); x += dotInterval) {
            for (int y = offsetY; y < getHeight(); y += dotInterval) {
                g2.fillOval(x - 1, y - 1, 3, 3);
            }
        }
    }

    Point2D transformPointToModel(Point p) {
        try {
            return viewTransform.inverseTransform(p, null);
        } catch (NoninvertibleTransformException ex) {
            System.err.println("Error inverting view transform: " + ex.getMessage());
            return new Point2D.Double(p.x, p.y); // Return original point if inversion fails
        }
    }

    private Connection findConnectionNearPoint(Point2D point) {
        final int PROXIMITY_THRESHOLD = 5; // Adjust threshold as needed
        point = transformPointToModel(new Point((int) point.getX(), (int) point.getY()));
        for (Connection conn : connections) {
            if (isNearConnection(point, conn, PROXIMITY_THRESHOLD)) {
                return conn;
            }
        }
        return null;
    }

    private boolean isNearConnection(Point2D point, Connection conn, int threshold) {
        Point start = conn.getStartNode().getConnectionPoint();
        Point end = conn.getEndNode().getConnectionPoint();
        Line2D line = new Line2D.Double(start, end);
        return line.ptSegDist(point) < threshold;

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
        // add the node to the panel and set its location consider transform
        Point2D modelPoint = transformPointToModel(node.getLocation());
        node.setLocation((int) modelPoint.getX(), (int) modelPoint.getY());
        add(node);
        repaint();

    }

    public void startDraggingForMoving(RuleNode node, Point initialClick) {
        draggingNode = node;
        draggingStartPoint = initialClick;
    }

    public void moveNode(RuleNode node, int deltaX, int deltaY) {
        Point location = node.getLocation();
        location.translate(deltaX, deltaY);
        node.setLocation(location);
        repaint();
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

    public AffineTransform getTransform() {
        return viewTransform;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create(); // Cast to use Graphics2D features
        g2d.transform(viewTransform); // Apply the view transform

        drawBackgroundGrid(g2d);
        // for (Component comp : getComponents()) {
        // if (comp instanceof RuleNode) {
        // RuleNode node = (RuleNode) comp;
        // Graphics2D gNode = (Graphics2D) g2d.create();
        // gNode.translate(node.getX(), node.getY());
        // gNode.dispose();
        // }
        // }
        for (Connection conn : connections) {
            RuleNode startNode = conn.getStartNode();
            RuleNode endNode = conn.getEndNode();
            Point start = startNode.getConnectionPoint();
            Point end = endNode.getConnectionPoint();
            Rectangle endNodeBounds = endNode.getBounds();
            if (conn.getType()) {
                g2d.setColor(Color.GREEN);
            } else {
                g2d.setColor(Color.RED);
            }
            g2d.drawLine(start.x, start.y, end.x, end.y);
            drawArrow(g2d, start, end, endNodeBounds); // Call the drawArrow method here

            // Draw connection type label
            int midX = (start.x + end.x) / 2;
            int midY = (start.y + end.y) / 2;
            g2d.drawString(conn.getType() ? "True" : "False", midX, midY);

            // set color of the connection

        }

        // Draw line while dragging to create a new connection
        if (draggingNode != null && draggingStartPoint != null) {
            Point from = draggingNode.getConnectionPoint();
            g2d.setColor(Color.GREEN);

            Point to = SwingUtilities.convertPoint(this, draggingStartPoint, this);
            g2d.drawLine(from.x, from.y, to.x, to.y);
            drawArrow(g2d, from, to, new Rectangle(to.x - 5, to.y - 5, 10, 10));

        }

        g2d.dispose();
    }

    private void drawArrow(Graphics2D g2d, Point start, Point end, Rectangle targetNodeBounds) {
        int arrowLength = 12; // Length of the arrow head
        // Calculate the direction of the line
        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double angle = Math.atan2(dy, dx);

        // Calculate intersection with the target node bounds
        Point adjustedEnd = calculateIntersection(start, end, targetNodeBounds);

        // Draw line
        g2d.drawLine(start.x, start.y, adjustedEnd.x, adjustedEnd.y);

        // Draw arrow head
        double x1 = adjustedEnd.x - arrowLength * Math.cos(angle - Math.PI / 6);
        double y1 = adjustedEnd.y - arrowLength * Math.sin(angle - Math.PI / 6);
        double x2 = adjustedEnd.x - arrowLength * Math.cos(angle + Math.PI / 6);
        double y2 = adjustedEnd.y - arrowLength * Math.sin(angle + Math.PI / 6);

        g2d.drawLine(adjustedEnd.x, adjustedEnd.y, (int) x1, (int) y1);
        g2d.drawLine(adjustedEnd.x, adjustedEnd.y, (int) x2, (int) y2);
    }

    private Point calculateIntersection(Point start, Point end, Rectangle bounds) {
        // Adjust method to handle horizontal and vertical lines
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

        // Find closest point within the line segment
        Point closestPoint = new Point(end);
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < xPoints.length; i++) {
            double px = xPoints[i];
            double py = yPoints[i];
            if (px >= bounds.x && px <= bounds.x + bounds.width && py >= bounds.y && py <= bounds.y + bounds.height) {
                double dist = Math.hypot(px - start.x, py - start.y);
                if (dist < minDistance && dist < Math.hypot(dx, dy)) { // Check within segment
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

    @JsonIgnoreProperties(value = { "accessibleContext", "toolkit", "graphicsConfiguration", "dropTarget",
            "transferHandler", "actionMap", "inputMap", "componentPopupMenu", "appContext", "bufferStrategy",
            "focusTraversalKeys", "mouseWheelListeners", "focusTraversalKeysEnabled", "focusCycleRoot",
            "focusTraversalPolicyProvider", "focusTraversalPolicy", "focusTraversalPolicySet", "rootPane",
            "contentPane", "parent", "graphics", "mouseMotionListeners", "mouseListeners", "focusListeners",
            "keyListeners", "componentListeners", "insets", "popups", "opaque", "focusOwner", "autoscrolls",
            "focusCycleRootAncestor", "bufferStrategy", "ignoreRepaint", "maxSize", "minSize", "peer", "valid",
            "visible", "showing", "enabled", "doubleBuffered", "ignoreRepaint", "requestedFocus", "focusCycleRoot", })
    public interface ComponentMixin {

    }

    public void save() {

        // list nodes and connections
        for (RuleNode node : getNodes()) {
            System.out.println("Node: " + node.getText() + " id " + node.getId() + " at " + node.getLocation());
        }

        for (Connection conn : connections) {
            System.out.println("Connection: " + conn.getStartNode().getText() + " id " + conn.getStartNode().getId()
                    + " -> " + conn.getEndNode().getText() + " id " + conn.getEndNode().getId() + " "
                    + conn.getType());
        }

        // Save nodes and connections to file
        SimpleModule module = new SimpleModule();
        module.addSerializer(RuleNode.class, new RuleNodeSerializer());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);

        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.addMixIn(Component.class, ComponentMixin.class);

        // save nodes and connections to file export as json
        try {
            List<Connection> connections = this.connections;

            String connectionsJson = mapper.writeValueAsString(connections);
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = now.format(formatter);

            // Construct file paths with the timestamp
            String connectionsFilename = "connections_" + timestamp + ".json";
            Files.write(Paths.get(connectionsFilename), connectionsJson.getBytes());

        } catch (IOException e) {
            System.err.println("Failed to save nodes or connections: " + e.getMessage());
        }

    }

    private RuleNode[] getNodes() {
        return Arrays.stream(getComponents())
                .filter(c -> c instanceof RuleNode)
                .map(c -> (RuleNode) c)
                .toArray(RuleNode[]::new);
    }
}