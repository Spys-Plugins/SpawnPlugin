package dev.cosmics.hardcoreplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitHandler implements Listener {
    private final FileConfiguration config;
    public BukkitHandler(FileConfiguration config) {
        this.config = config;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location spawn = config.getLocation("spawn");
        if (!player.hasPlayedBefore()) {
            if (spawn != null) {
                player.teleport(spawn);
            }
            if (config.getBoolean("FirstJoin")) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', config.getString("FirstJoinMessage").replace("%player's display name%", player.getDisplayName())));
            }
        } else {
            if (config.getBoolean("JoinMessage")) {
                event.setJoinMessage("");
            }
            if (config.getBoolean("TPJoin")) {
                if (spawn != null) {
                    player.teleport(spawn);
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (config.getBoolean("QuitMessage")) {
            event.setQuitMessage("");
        }
    }

    @EventHandler
    public void onDie(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location spawn = config.getLocation("spawn");
        if (!config.getBoolean("TPDeath")) return;
        if (spawn != null) {
            player.teleport(spawn);
        }
    }
}
