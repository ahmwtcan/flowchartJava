package com.example;

import javax.swing.*;
import java.awt.*;

public class ConditionNode extends RuleNode {
    private ThresholdType ffThresholdType = ThresholdType.FF_SAYISI;
    private int ffLowerLimit = 0;
    private int ffUpperLimit = 1;
    private boolean tableCourseFlag = false; // Flag for Table Course

    public ConditionNode(String text, NodeTypes type, int id, WorkspacePanel panel) {
        super(text, type, id, panel);
        setBackground(new Color(173, 216, 230));
        setForeground(Color.BLACK);
        setFont(new Font("Arial", Font.BOLD, 12));
        setPreferredSize(getPreferredSize());
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(130, 65); // Change dimensions as needed
    }

    @Override
    public void handleDoubleClick() {
        SwingUtilities.invokeLater(this::configuration); // Ensure it's called on the EDT
    }

    @Override
    public void configuration() {
        JComboBox<ThresholdType> ffTypeBox = new JComboBox<>(ThresholdType.values());
        ffTypeBox.setSelectedItem(ffThresholdType);
        JTextField ffLowerField = new JTextField(String.valueOf(ffLowerLimit), 5);
        JTextField ffUpperField = new JTextField(String.valueOf(ffUpperLimit), 5);
        JCheckBox tableCourseCheckBox = new JCheckBox("Sınavla Not verilmeyen ders", tableCourseFlag);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("FF Eşiği Type:"));
        panel.add(ffTypeBox);
        panel.add(new JLabel("Alt Limit:"));
        panel.add(ffLowerField);
        panel.add(new JLabel("Üst Limit:"));
        panel.add(ffUpperField);
        panel.add(tableCourseCheckBox);

        int result = JOptionPane.showConfirmDialog(this.panel, panel, "Ders Not Durumu Ayarla",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            ffThresholdType = (ThresholdType) ffTypeBox.getSelectedItem();
            ffLowerLimit = Integer.parseInt(ffLowerField.getText());
            ffUpperLimit = Integer.parseInt(ffUpperField.getText());
            tableCourseFlag = tableCourseCheckBox.isSelected();

            repaint(); // Repaint to update text after changes
        }
    }

    @Override
    public String getText() {

        return ffThresholdType + " (" + ffLowerLimit + " <= " + ffUpperLimit + ")"
                + (tableCourseFlag ? "T.Dersi: Evet" : " T.Dersi: Hayır");

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Clear the background
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        // Set text properties
        g.setColor(getForeground());
        g.setFont(getFont());

        // Draw debugging frame
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        // Draw the text
        String typeText = ffThresholdType.toString();
        String rangeText = String.format("(%d <= %d)", ffLowerLimit, ffUpperLimit);
        String tableCourseText = "T.Dersi: " + (tableCourseFlag ? "Evet" : "Hayır");

        // Calculate y position based on font metrics
        int typeY = 10 + g.getFontMetrics().getHeight();
        int rangeY = typeY + g.getFontMetrics().getHeight();
        int tableCourseY = rangeY + g.getFontMetrics().getHeight();

        g.drawString(typeText, 5, typeY);
        g.drawString(rangeText, 5, rangeY);
        g.drawString(tableCourseText, 5, tableCourseY);
    }

    public enum ThresholdType {
        FF_SAYISI, WrL_SAYISI, BAŞARISIZ_SAYISI
    }
}
