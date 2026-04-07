package burp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TimingAnalysisResult {
    private final PoolStats plotAStats;
    private final PoolStats plotBStats;

    private final double uStatistic;
    private final double auc;
    private final double cohensD;
    private final double pValue;
}
