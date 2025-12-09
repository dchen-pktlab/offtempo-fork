package burp.ui;

import burp.model.TimingAnalysisResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class AnalysisPanel extends JPanel {

    private final JPanel resultPanel;

    public AnalysisPanel(ActionListener runAction, ActionListener plotAction, ActionListener saveAction) {
        super(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton runBtn = new JButton("Run Analysis");
        runBtn.addActionListener(runAction);

        JButton plotBtn = new JButton("Show Timing Plot");
        plotBtn.addActionListener(plotAction);

        JButton saveBtn = new JButton("Save Plot");
        saveBtn.addActionListener(saveAction);

        buttonPanel.add(runBtn);
        buttonPanel.add(plotBtn);
        buttonPanel.add(saveBtn);

        add(buttonPanel, BorderLayout.NORTH);

        JPanel analysisContainer = new JPanel(new BorderLayout());
        analysisContainer.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(BorderFactory.createTitledBorder("Analysis Results"));

        JScrollPane resultScroll = new JScrollPane(resultPanel);
        resultScroll.setPreferredSize(new Dimension(300, 140));
        analysisContainer.add(resultScroll, BorderLayout.CENTER);

        JTextPane interpretationPane = new JTextPane();
        interpretationPane.setContentType("text/html");
        interpretationPane.setEditable(false);
        interpretationPane.setBorder(BorderFactory.createTitledBorder("Interpretation"));
        interpretationPane.setText(getInterpretationText());

        JScrollPane interpScroll = new JScrollPane(interpretationPane);
        interpScroll.setPreferredSize(new Dimension(250, 140));
        analysisContainer.add(interpScroll, BorderLayout.EAST);

        add(analysisContainer, BorderLayout.CENTER);
    }

    public void showResult(TimingAnalysisResult result) {
        resultPanel.removeAll();

        if (result == null) {
            resultPanel.add(new JLabel("No data available"));
        } else {
            resultPanel.add(createLabel("Existing samples: " + result.getExistingCount()));
            resultPanel.add(createLabel("Non-existing samples: " + result.getNonExistingCount()));

            resultPanel.add(createLabel("AUC: " + result.getAUC()));
            resultPanel.add(createDescriptionLabel("Probability attacker guesses correctly"));

            resultPanel.add(createLabel("Cohen's d: " + result.getCohensD()));
            resultPanel.add(createDescriptionLabel("Effect size of difference between groups"));
        }

        resultPanel.revalidate();
        resultPanel.repaint();
    }

    public void clearResult() {
        resultPanel.removeAll();
        resultPanel.revalidate();
        resultPanel.repaint();
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        return label;
    }

    private JLabel createDescriptionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setBorder(BorderFactory.createEmptyBorder(0, 20, 4, 5));
        return label;
    }

    private String getInterpretationText() {
        return """
        <html>
        <b>AUC (Area Under Curve):</b><br>
        - &lt;0.7: unreliable, likely noise<br>
        - 0.7-0.8: weak, small difference<br>
        - 0.8-0.9: moderate evidence<br>
        - &gt;0.9: strong evidence<br>
        <br>
        <b>Cohen's d (effect size):</b><br>
        - &lt;0.2: negligible effect<br>
        - 0.2-0.5: small effect<br>
        - 0.5-0.8: medium effect<br>
        - &gt;0.8: large effect
        </html>
        """;
    }
}
