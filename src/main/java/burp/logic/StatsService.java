package burp.logic;

import burp.model.TimingAnalysisResult;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import java.util.Arrays;

public class StatsService {

    private final MannWhitneyUTest mwu = new MannWhitneyUTest();

    private double computeU(double[] a, double[] b) {
        return mwu.mannWhitneyU(a, b);
    }

    private double computeAUC(double uStat, int n1, int n2) {
        if (n1 <= 0 || n2 <= 0) return 0.0;
        return uStat / ((double) n1 * (double) n2);
    }

    private double computeCohensD(double[] a, double[] b) {
        if (a.length < 1 || b.length < 1) return 0.0;
        double meanA = Arrays.stream(a).average().orElse(0.0);
        double meanB = Arrays.stream(b).average().orElse(0.0);

        double varA = Arrays.stream(a).map(v -> (v - meanA) * (v - meanA)).sum() / a.length;
        double varB = Arrays.stream(b).map(v -> (v - meanB) * (v - meanB)).sum() / b.length;

        double pooledStd = Math.sqrt((varA + varB) / 2.0);
        if (pooledStd == 0.0) return 0.0;
        return (meanA - meanB) / pooledStd;
    }

    public TimingAnalysisResult computeStats(double[] a, double[] b) {
        double u = computeU(a, b);
        int n1 = a.length, n2 = b.length;
        double auc = computeAUC(u, n1, n2);
        double d = computeCohensD(a, b);
        return new TimingAnalysisResult(n1, n2, u, auc, d);
    }
}
