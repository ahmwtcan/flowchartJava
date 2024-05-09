package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class RuleNode extends JLabel {
    private static final int EDGE_THRESHOLD = 10; // Threshold for resizing edges
    private Point initialClick; // Store initial click location for dragging calculations
    private String nodeName; // Store the node name for display
    private boolean resizing; // Flag to indicate if the node is in resizing mode
    private int id; // Identifier for the node
    private WorkspacePanel panel; // Reference to the parent panel

    public RuleNode(String text, NodeTypes type, int id, WorkspacePanel panel) {
        super(text);
        this.nodeName = text;
        this.id = id;
        this.panel = panel;
        initializeUI();
    }

    private void initializeUI() {
        updateText();
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setOpaque(true);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(66, 60));
        setSize(getPreferredSize());
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        addMouseEvents();
    }

    private void addMouseEvents() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point2D transformedPoint = panel.transformPointToModel(e.getPoint());
                initialClick = new Point((int) transformedPoint.getX(), (int) transformedPoint.getY());

                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    handleDoubleClick();
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    if (isNearCorner(initialClick)) {
                        resizing = true;
                        setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                    } else {
                        startDragging(e);
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e);
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
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private void resizeNode(MouseEvent e) {
        Point2D transformedPoint = panel.transformPointToModel(e.getPoint());
        int deltaX = (int) transformedPoint.getX() - initialClick.x;
        int deltaY = (int) transformedPoint.getY() - initialClick.y;
        setSize(getWidth() + deltaX, getHeight() + deltaY);
        panel.revalidate();
        panel.repaint();

    }

    private void dragNode(MouseEvent e) {
        Point2D transformedPoint = panel.transformPointToModel(e.getPoint());
        int deltaX = (int) transformedPoint.getX() - initialClick.x;
        int deltaY = (int) transformedPoint.getY() - initialClick.y;
        setLocation(getX() + deltaX, getY() + deltaY);
    }

    private void startDragging(MouseEvent e) {
        // Additional logic to start dragging
    }

    public void handleDoubleClick() {
        String newName = JOptionPane.showInputDialog(this, "Enter new name for the node:", nodeName);
        if (newName != null && !newName.trim().isEmpty()) {
            nodeName = newName;
            updateText();
        }
    }

    private void showContextMenu(MouseEvent e) {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(ev -> panel.removeRuleNode(this));
        JMenuItem configItem = new JMenuItem("Configure");
        configItem.addActionListener(ev -> configuration());
        popup.add(configItem);
        popup.add(deleteItem);
        popup.show(this, e.getX(), e.getY());
    }

    public void configuration() {
        // Configuration logic
    }

    private boolean isNearCorner(Point p) {
        int x = p.x;
        int y = p.y;
        int w = getWidth();
        int h = getHeight();
        return (x >= w - EDGE_THRESHOLD && y >= h - EDGE_THRESHOLD);
    }

    private boolean isNearEdge(Point p) {
        int x = p.x;
        int y = p.y;
        int w = getWidth();
        int h = getHeight();
        return (x <= EDGE_THRESHOLD || x >= w - EDGE_THRESHOLD || y <= EDGE_THRESHOLD || y >= h - EDGE_THRESHOLD);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);
        g2d.fillRect(getWidth() - EDGE_THRESHOLD, getHeight() - EDGE_THRESHOLD, EDGE_THRESHOLD, EDGE_THRESHOLD);
    }

    public void updateText() {
        setText("<html><center>" + nodeName + "</center></html>");
    }

    public Point getConnectionPoint() {
        return new Point(getX() + getWidth() / 2, getY() + getHeight() / 2);
    }

    public int getId() {
        return id;
    }

    @Override
    public String getText() {
        return nodeName;
    }

    public Dimension getOriginalSize() {
        return getPreferredSize();
    }

    public Point getOriginalLocation() {
        return getLocation();
    }
}
