package org.fracturedsmp.finality.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.fracturedsmp.finality.data.PlayerData;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class NameGenerator {
    private static final int MAX_LEN = 16;
    private static final int MAX_TRIES = 200;

    private static List<String> ADJ = null;
    private static List<String> NOUN = null;

    public static String generateUniquePersonaName(Map<String, PlayerData> existing) {
        ensureLoaded();

        Set<String> taken = new HashSet<>();
        for (PlayerData d : existing.values()) {
            String pn = d.getPersonaName();
            if (pn != null && !pn.isBlank()) taken.add(pn.toLowerCase());
        }

        // avoid real usernames currently online
        for (Player p : Bukkit.getOnlinePlayers()) taken.add(p.getName().toLowerCase());

        ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (int i = 0; i < MAX_TRIES; i++) {
            String a = pick(ADJ, rng);
            String n = pick(NOUN, rng);

            String suffix = String.valueOf(rng.nextInt(0, 1000)); // 0-999

            String candidate = toNameCase(a) + toNameCase(n) + suffix;

            candidate = clamp(candidate, MAX_LEN);

            if (!taken.contains(candidate.toLowerCase())) return candidate;
        }

        // last resort
        return "Persona" + (System.currentTimeMillis() % 100000);
    }

    public static void initWordlists(ClassLoader cl) {
        ADJ = loadLines(cl, "wordlists/adjectives.txt");
        NOUN = loadLines(cl, "wordlists/nouns.txt");
        if (ADJ.isEmpty() || NOUN.isEmpty()) {
            throw new IllegalStateException("Wordlists missing/empty: put resources at wordlists/adjectives.txt and wordlists/nouns.txt");
        }
    }

    private static void ensureLoaded() {
        if (ADJ == null || NOUN == null) initWordlists(NameGenerator.class.getClassLoader());
    }


    private static List<String> loadLines(ClassLoader cl, String path) {
        try (InputStream in = cl.getResourceAsStream(path)) {
            if (in == null) return List.of();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            List<String> out = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                // keep only letters for safety
                line = line.replaceAll("[^A-Za-z]", "");
                if (!line.isEmpty()) out.add(line);
            }
            return out;
        } catch (Exception e) {
            return List.of();
        }
    }

    private static String pick(List<String> list, ThreadLocalRandom rng) {
        return list.get(rng.nextInt(list.size()));
    }

    private static String toNameCase(String s) {
        if (s.isEmpty()) return s;
        s = s.toLowerCase(Locale.ROOT);
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String clamp(String s, int max) {
        if (s.length() <= max) return s;
        return s.substring(0, max);
    }
}
