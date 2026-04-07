package burp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PoolStats {
    private final int count;
    private final double mean;
    private final double median;
    private final double stdDev;
    private final double min;
    private final double max;
}
