package com.example;

import java.awt.*;

import javax.swing.JOptionPane;

public class ResultNode extends RuleNode {
    private ExamRight examRight = ExamRight.EK_SINAV;

    public ResultNode(String text, NodeTypes type, int id) {
        super(text, type, id);
        setPreferredSize(new Dimension(160, 80)); // Typical size for a rectangle

        // set background color according to exam right

        setBackground(Color.GREEN);

        // set text color
        setForeground(Color.BLACK);
        setText("<html><center>" + examRight + "</center></html>");

        // set text smaller
        setFont(new Font("Arial", Font.PLAIN, 8));

    }

    @Override
    public void handleDoubleClick() {
        // Show the input dialog and capture the user's selection
        Object result = JOptionPane.showInputDialog(
                null,
                "Choose exam right",
                "Exam Right",
                JOptionPane.QUESTION_MESSAGE,
                null,
                ExamRight.values(),
                examRight // Default to current value
        );

        // Check if the user canceled the dialog (result will be null)
        if (result != null) {
            examRight = (ExamRight) result; // Update only if a valid selection is made

            // Update background color based on the selected exam right
            switch (examRight) {
                case EK_SINAV:
                case SINIRSIZ_SINAV:
                case BELIRLI_DONEM_SINAV_HAKKI:
                    setBackground(Color.GREEN);
                    break;
                case HAK_YOK:
                    setBackground(Color.RED);
                    break;
                case ILISIGI_KESILDI:
                    setBackground(Color.GRAY);
                    break;
            }

            // Update the text and text color
            setForeground(Color.BLACK);
            setText("<html><center>" + examRight + "</center></html>");
            setFont(new Font("Arial", Font.PLAIN, 8));
        }
        // If result is null (dialog was canceled), do nothing and retain the current
        // settings
    }

    @Override
    public String getText() {
        return "" + examRight + "";
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

    public enum ExamRight {
        EK_SINAV,
        SINIRSIZ_SINAV,
        BELIRLI_DONEM_SINAV_HAKKI,
        HAK_YOK,
        ILISIGI_KESILDI
    }
}
