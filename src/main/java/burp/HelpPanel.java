package burp;

import javax.swing.*;
import javax.swing.text.rtf.RTFEditorKit;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HelpPanel extends JPanel {

    final static String HELP_FILE_RTF = "help.rtf";

    public HelpPanel() {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditorKit(new RTFEditorKit());
        editorPane.setEditable(false);
        editorPane.setContentType("text/rtf");
        try (var in = getClass().getClassLoader().getResourceAsStream(HELP_FILE_RTF)) {
            editorPane.setText(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("failed to load help file: "+e.getMessage());
        }
        add(editorPane);
    }
}