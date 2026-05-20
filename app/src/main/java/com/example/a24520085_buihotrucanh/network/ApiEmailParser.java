package com.example.a24520085_buihotrucanh.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;

public final class ApiEmailParser {
    private ApiEmailParser() {}

    public static List<String> parseEmailStrings(JsonElement root) {
        List<String> out = new ArrayList<>();
        if (root == null || root.isJsonNull()) return out;

        if (root.isJsonArray()) {
            JsonArray arr = root.getAsJsonArray();
            for (JsonElement e : arr) {
                if (e == null || e.isJsonNull()) continue;
                if (e.isJsonPrimitive()) {
                    JsonPrimitive p = e.getAsJsonPrimitive();
                    if (p.isString()) out.add(p.getAsString());
                } else if (e.isJsonObject()) {
                    String em = extractEmailFromObject(e.getAsJsonObject());
                    if (em != null && !em.isEmpty()) out.add(em);
                }
            }
            return out;
        }

        if (root.isJsonObject()) {
            JsonObject o = root.getAsJsonObject();
            for (String key : new String[]{"data", "emails", "items", "results"}) {
                if (o.has(key) && !o.get(key).isJsonNull()) {
                    out.addAll(parseEmailStrings(o.get(key)));
                    if (!out.isEmpty()) return out;
                }
            }
        }

        return out;
    }

    private static String extractEmailFromObject(JsonObject o) {
        for (String key : new String[]{"email", "user_email", "mail", "address"}) {
            if (o.has(key) && o.get(key).isJsonPrimitive() && o.get(key).getAsJsonPrimitive().isString()) {
                return o.get(key).getAsString();
            }
        }
        return null;
    }
}
