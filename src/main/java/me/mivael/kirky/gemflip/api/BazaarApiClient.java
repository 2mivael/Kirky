package me.mivael.kirky.gemflip.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BazaarApiClient {
    public static JsonObject fetchBazaar() throws IOException {
        URL url = new URL("https://api.hypixel.net/v2/skyblock/bazaar");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        return JsonParser.parseReader(
                new InputStreamReader(conn.getInputStream())
        ).getAsJsonObject();
    }

}
