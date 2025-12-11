package burp.ui;

import burp.model.TimingAnalysisResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AucPanel extends JPanel {

    private final JPanel resultPanel;
    private final JLabel aucLabel;
    private final JLabel explanationLabel;
    private final JPanel interpretationPanel;

    public AucPanel() {
        super(new BorderLayout());
        setBorder(new EmptyBorder(10, 0, 0, 0));

        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        aucLabel = new JLabel("", SwingConstants.CENTER);
        aucLabel.setFont(aucLabel.getFont().deriveFont(Font.BOLD, 32f));
        aucLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        aucLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        explanationLabel = new JLabel("", SwingConstants.CENTER);
        explanationLabel.setBorder(new EmptyBorder(2, 5, 2, 5));
        explanationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JScrollPane resultScroll = new JScrollPane(resultPanel);
        resultScroll.setBorder(BorderFactory.createEmptyBorder());
        resultScroll.setPreferredSize(new Dimension(300, 140));
        add(resultScroll, BorderLayout.CENTER);

        interpretationPanel = new JPanel(new BorderLayout());
        interpretationPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JTextPane interpretationPane = new JTextPane();
        interpretationPane.setContentType("text/html");
        interpretationPane.setEditable(false);
        interpretationPane.setText(getInterpretationText());
        interpretationPane.setBorder(BorderFactory.createEmptyBorder());
        interpretationPanel.add(interpretationPane, BorderLayout.CENTER);
        interpretationPanel.setPreferredSize(new Dimension(250, 140));
        add(interpretationPanel, BorderLayout.EAST);
        interpretationPanel.setVisible(false);
    }

    public void showResult(TimingAnalysisResult result) {
        resultPanel.removeAll();
        if (result == null) {
            resultPanel.add(new JLabel("No data available"));
            interpretationPanel.setVisible(false);
        } else {
            int aucPercent = (int) Math.round(result.getAuc() * 100);
            String pentestMsg = getPentestInterpretation(aucPercent);
            aucLabel.setText("AUC: " + aucPercent + "% — " + pentestMsg);

            explanationLabel.setText(
                    "The AUC (Area Under Curve) shows how likely it is to correctly tell Plot A requests from Plot B based on response times."
            );

            resultPanel.add(aucLabel);
            resultPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            resultPanel.add(explanationLabel);

            interpretationPanel.setVisible(true);
        }
        resultPanel.revalidate();
        resultPanel.repaint();
        interpretationPanel.revalidate();
        interpretationPanel.repaint();
    }

    public void clear() {
        aucLabel.setText("");
        explanationLabel.setText("");
        resultPanel.removeAll();
        interpretationPanel.setVisible(false);
        resultPanel.revalidate();
        resultPanel.repaint();
        interpretationPanel.revalidate();
        interpretationPanel.repaint();
    }

    private String getPentestInterpretation(int aucPercent) {
        if (aucPercent < 70) return "Not distinguishable — likely just noise";
        if (aucPercent < 80) return "Moderate signal — some differentiation between requests";
        if (aucPercent < 90) return "Strong signal — requests are meaningfully different";
        return "Very strong signal — behavior clearly differs";
    }

    private String getInterpretationText() {
        return """
        <html>
        <b>AUC interpretation</b><br>
        <b>&lt;70%</b> — likely noise<br>
        <b>70%–80%</b> — moderate evidence<br>
        <b>80%–90%</b> — strong evidence<br>
        <b>&gt;90%</b> — very strong evidence<br>
        </html>
        """;
    }
}
