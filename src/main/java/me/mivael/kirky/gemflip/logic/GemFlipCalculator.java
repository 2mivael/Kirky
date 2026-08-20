package me.mivael.kirky.gemflip.logic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.mivael.kirky.gemflip.data.GemFlipResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GemFlipCalculator {

    private static final String[] GEMS = {
            "JADE","AMBER","TOPAZ","SAPPHIRE","AMETHYST",
            "JASPER","RUBY","OPAL","ONYX",
            "AQUAMARINE","CITRINE","PERIDOT"
    };

    private static final int REQUIRED_FLAWED_GEMS = 6400;
    private static final int LOW_ORDER_WARNING_THRESHOLD = 12;

    public static List<GemFlipResult> calculate(JsonObject products) {
        List<GemFlipResult> results = new ArrayList<>();

        for (String gem : GEMS) {
            String flawed = "FLAWED_" + gem + "_GEM";
            String flawless = "FLAWLESS_" + gem + "_GEM";

            if (!products.has(flawed) || !products.has(flawless)) continue;

            JsonObject flawedProduct = products.getAsJsonObject(flawed);
            JsonObject flawlessProduct = products.getAsJsonObject(flawless);

            if (flawedProduct == null || flawlessProduct == null) continue;

            // Note: Hypixel Bazaar API exposes summaries of current orders.
            // We use counts as a rough manipulation/illiquidity signal.
            JsonArray flawedSellOrders = flawedProduct.getAsJsonArray("sell_summary"); // cheapest first
            JsonArray flawlessBuyOrders = flawlessProduct.getAsJsonArray("buy_summary"); // highest first
            JsonArray flawlessSellOrders = flawlessProduct.getAsJsonArray("sell_summary");
            JsonArray flawedBuyOrders = flawedProduct.getAsJsonArray("buy_summary");

            if (flawedSellOrders == null || flawlessBuyOrders == null ||
                    flawlessSellOrders == null || flawedBuyOrders == null) continue;

            double totalFlawedCost = 0;
            int remaining = REQUIRED_FLAWED_GEMS;

            for (int i = 0; i < flawedSellOrders.size() && remaining > 0; i++) {
                JsonObject order = flawedSellOrders.get(i).getAsJsonObject();
                int amount = order.get("amount").getAsInt();
                double pricePerUnit = order.get("pricePerUnit").getAsDouble();

                int take = Math.min(amount, remaining);
                totalFlawedCost += pricePerUnit * take;
                remaining -= take;
            }

            if (remaining > 0) continue; // not enough supply

            // --- 2. Get top flawless buy order ---
            if (flawlessBuyOrders.isEmpty()) continue;

            double flawlessTopBuy = flawlessBuyOrders.get(0)
                    .getAsJsonObject()
                    .get("pricePerUnit")
                    .getAsDouble();

            // --- 3. Profit ---
            double profit = flawlessTopBuy - totalFlawedCost;

            if (profit > 0) {
                results.add(new GemFlipResult(
                        gem,
                        profit,
                        totalFlawedCost,
                        flawlessTopBuy,
                        getManipulationWarnings(flawlessSellOrders, flawedBuyOrders)
                ));
            }
        }

        return results.stream()
                .sorted(Comparator.comparingDouble(GemFlipResult::getProfit).reversed())
                .toList();
    }

    private static List<String> getManipulationWarnings(JsonArray flawlessSellOrders, JsonArray flawedBuyOrders) {
        List<String> warnings = new ArrayList<>();

        if (flawlessSellOrders.size() < LOW_ORDER_WARNING_THRESHOLD) {
            warnings.add("only " + flawlessSellOrders.size() + " flawless sell orders are available");
        }
        if (flawedBuyOrders.size() < LOW_ORDER_WARNING_THRESHOLD) {
            warnings.add("only " + flawedBuyOrders.size() + " flawed buy orders are available");
        }
        return warnings;
    }
}
