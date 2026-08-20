package me.mivael.kirky;

import com.google.gson.JsonObject;
import me.mivael.kirky.gemflip.api.BazaarApiClient;
import me.mivael.kirky.gemflip.command.GemFlipCommands;
import me.mivael.kirky.gemflip.data.GemFlipResult;
import me.mivael.kirky.gemflip.logic.GemFlipCalculator;
import me.mivael.kirky.gemflip.util.ChatUtil;
import net.fabricmc.api.ClientModInitializer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GemFlipMod implements ClientModInitializer {
//help! help! help! im having github problems and i have no idea what im doing! help! help!
    private static final int LEADERBOARD_SIZE = 10;

    @Override
    public void onInitializeClient() {
        GemFlipCommands.register();
    }

    public static void runCheck() {
        ChatUtil.send("§eFetching bazaar data...");

        CompletableFuture.runAsync(() -> {
            try {
                JsonObject bazaar =
                        BazaarApiClient.fetchBazaar()
                                .getAsJsonObject("products");

                List<GemFlipResult> results = GemFlipCalculator.calculate(bazaar);

                if (results.isEmpty()) {
                    ChatUtil.send("§cNo profitable gemstone flips right now.");
                    return;
                }

                ChatUtil.send("§9---------------------------------------");
                ChatUtil.send("§a§lTop Gemstone Flips");

                int rank = 1;
                for (GemFlipResult result : results.stream().limit(LEADERBOARD_SIZE).toList()) {
                    String manipulationLabel = result.hasManipulationRisk()
                                ? " §cLikely manipulated."
                            : "";
                    ChatUtil.send("§e#" + rank++ + " §b" + result.gemstone
                            + " §7→ §6+" + String.format("%,.0f", result.profit) + " §acoins"
                            + manipulationLabel);
                }
                ChatUtil.send("§7Note: Usually higher profit margins mean a higher fill time. Also expect competition.");
                ChatUtil.send("§9---------------------------------------");
            } catch (Exception e) {
                ChatUtil.send("§cFailed to fetch bazaar data.");
                System.err.println("[Kirky] Failed to fetch bazaar data");
                e.printStackTrace(System.err);
            }
        });
    }

}
