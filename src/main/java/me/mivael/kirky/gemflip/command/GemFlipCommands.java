package me.mivael.kirky.gemflip.command;

import me.mivael.kirky.GemFlipMod;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class GemFlipCommands {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommands.literal("kirky_calc")
                    .executes(context -> {
                        GemFlipMod.runCheck();
                        return 1;
                    }));
        });
    }
}
