package burp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TimingAnalysisResult {
    private final int existingCount;
    private final int nonExistingCount;
    private final double uStatistic;
    private final double AUC;
    private final double cohensD;

}
