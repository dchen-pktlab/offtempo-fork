package burp.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Winsorizer {
    public static List<Double> winsorize(List<Double> values, double percentile) {
        if (values.isEmpty()) return new ArrayList<>(values);
        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int idx = (int) (percentile * sorted.size());
        idx = Math.min(Math.max(0, idx), sorted.size() - 1);
        double cap = sorted.get(idx);

        List<Double> out = new ArrayList<>(values.size());
        for (double v : values) out.add(Math.min(v, cap));
        return out;
    }
}
