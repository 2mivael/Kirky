package me.mivael.kirky.gemflip.data;

import java.util.List;

public class GemFlipResult {
    public final String gemstone;
    public final double profit;
    public final double inputCost;
    public final double salePrice;
    public final List<String> manipulationWarnings;

    public GemFlipResult(String gemstone, double profit, double inputCost, double salePrice,
                         List<String> manipulationWarnings) {
        this.gemstone = gemstone;
        this.profit = profit;
        this.inputCost = inputCost;
        this.salePrice = salePrice;
        this.manipulationWarnings = List.copyOf(manipulationWarnings);
    }

    public double getProfit() {
        return profit;
    }

    public boolean hasManipulationRisk() {
        return !manipulationWarnings.isEmpty();
    }
}
