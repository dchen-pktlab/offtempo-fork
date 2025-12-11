package burp.ui;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HelpPanel extends JPanel {

    private final static String HELP_FILE_TXT = "help.txt";

    public HelpPanel() {

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try (var in = getClass().getClassLoader().getResourceAsStream(HELP_FILE_TXT)) {
            if (in == null) throw new NullPointerException("Resource not found");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                textArea.read(reader, null);
            }
        } catch (Exception e) {
            textArea.setText("Failed to load help content: " + e.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(textArea);

        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBorder(null);
        scrollPane.setPreferredSize(null);
        scrollPane.setMinimumSize(null);

        textArea.setSize(new Dimension(500, Short.MAX_VALUE));
        textArea.setPreferredSize(textArea.getPreferredSize());

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }
}
