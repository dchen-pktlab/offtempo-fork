package burp.logic;

import burp.model.PoolStats;
import burp.model.TimingAnalysisResult;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.rank.Median;

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

    private PoolStats computePoolStats(double[] arr) {
        DescriptiveStatistics stats = new DescriptiveStatistics(arr);
        double median = new Median().evaluate(arr);

        return new PoolStats(
                arr.length,
                stats.getMean(),
                median,
                stats.getMin(),
                stats.getMax()
        );
    }

    private double computeSNR(double[] a, double[] b) {
        int n1 = a.length;
        int n2 = b.length;

        DescriptiveStatistics statsA = new DescriptiveStatistics(a);
        DescriptiveStatistics statsB = new DescriptiveStatistics(b);

        double pooledVariance =
                ((n1 - 1) * statsA.getVariance() + (n2 - 1) * statsB.getVariance()) / (n1 + n2 - 2);
        double pooledStd = Math.sqrt(pooledVariance);
        double snr = pooledStd == 0 ? 0.0 : Math.abs(statsA.getMean() - statsB.getMean()) / pooledStd;

        return snr;
    }

    public TimingAnalysisResult computeStats(double[] a, double[] b) {
        int n1 = a.length;
        int n2 = b.length;

        double u = computeU(a, b);
        double auc = computeAUC(u, n1, n2);

        PoolStats plotAStats = computePoolStats(a);
        PoolStats plotBStats = computePoolStats(b);

        double snr = computeSNR(a, b);

        return new TimingAnalysisResult(plotAStats, plotBStats, u, auc, snr);
    }
}
