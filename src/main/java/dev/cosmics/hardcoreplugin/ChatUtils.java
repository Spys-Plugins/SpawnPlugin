package dev.cosmics.hardcoreplugin;

import org.bukkit.ChatColor;

public class ChatUtils {
    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
