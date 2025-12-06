package burp;

import javax.swing.*;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HelpPanel extends JPanel {

    private final static String HELP_FILE_TXT = "help.txt"; // plain text version

    public HelpPanel() {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // Load help text from classpath
        try (var in = getClass().getClassLoader().getResourceAsStream(HELP_FILE_TXT)) {
            if (in == null) throw new NullPointerException("Resource not found");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                textArea.read(reader, null);
            }
        } catch (Exception e) {
            textArea.setText("Failed to load help content: " + e.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 250));

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }
}