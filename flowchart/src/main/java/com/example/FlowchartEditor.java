package com.example;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class FlowchartEditor extends JFrame {
    private WorkspacePanel workspacePanel;
    private Toolbar toolbar;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private static AtomicInteger sessionCounter = new AtomicInteger(1);

    public FlowchartEditor() {
        setTitle("Sınav Hakkı Belirleme Akış Şeması Düzenleyicisi");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        workspacePanel = new WorkspacePanel();
        toolbar = new Toolbar(workspacePanel);

        add(toolbar, BorderLayout.NORTH);
        add(workspacePanel, BorderLayout.CENTER);

        // Uygulama başladığında zamanı kaydet
        startTime = LocalDateTime.now();

        // Pencere kapatıldığında bitiş zamanını kaydet ve log dosyasına yaz
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                endTime = LocalDateTime.now();
                Duration duration = Duration.between(startTime, endTime);
                logTimes(startTime, endTime, duration);
                super.windowClosing(e);
            }
        });
    }

    private void logTimes(LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
        int sessionNumber = sessionCounter.getAndIncrement();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt", true))) {
            writer.write("Oturum Numarası: " + sessionNumber + "\n");
            writer.write("Uygulama başlangıç zamanı: " + startTime + "\n");
            writer.write("Uygulama bitiş zamanı: " + endTime + "\n");
            writer.write("Geçen süre: " + duration.toHoursPart() + " saat " + duration.toMinutesPart() + " dakika "
                    + duration.toSecondsPart() + " saniye\n");
            writer.write("-------------\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlowchartEditor editor = new FlowchartEditor();
            editor.setVisible(true);
        });
    }
}
