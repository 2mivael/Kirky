package me.mivael.kirky.gemflip.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class ChatUtil {

    private ChatUtil() {
        // Prevent instantiation
    }

    public static void send(String message) {
        Minecraft client = Minecraft.getInstance();

        if (client.player == null) return;

        client.execute(() ->
                client.player.sendSystemMessage(Component.literal(message))
        );
    }

}

