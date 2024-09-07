package org.ofz.management.utils;

import java.util.HashMap;
import java.util.Map;

public enum StockStability {
    GROUP_1(1, 0.65),
    GROUP_2(2, 0.60),
    GROUP_3(3, 0.55),
    GROUP_4(4, 0.50),
    GROUP_5(5, 0.40);

    private final int group;
    private final double percent;

    private static final Map<Integer, StockStability> groupToStabilityMap = new HashMap<>();

    static {
        for (StockStability stability : StockStability.values()) {
            groupToStabilityMap.put(stability.group, stability);
        }
    }

    StockStability(int group, double percent) {
        this.group = group;
        this.percent = percent;
    }

    public int getGroup() {
        return group;
    }

    public double getPercent() {
        return percent;
    }

    public static double getPercentByGroup(int group) {
        StockStability stability = groupToStabilityMap.get(group);

        if (stability == null) {
            throw new IllegalArgumentException("Invalid group: " + group);
        }

        return stability.getPercent();
    }

    public static double calculateLimitPrice(int group, int stockPrice) {
        double limitRate = StockStability.getPercentByGroup(group);
        double price = stockPrice * limitRate;
        return Math.round(price * 100.0) / 100.0;
    }
}
