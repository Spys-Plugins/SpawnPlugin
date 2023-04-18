package dev.cosmics.hardcoreplugin;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.cosmics.hardcoreplugin.ChatUtils.color;
import static org.bukkit.Bukkit.getServer;

public class SpawnCommands implements CommandExecutor {
    private FileConfiguration config;
    private File file;
    public SpawnCommands(FileConfiguration config, File file) {
        this.config = config;
        this.file = file;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setspawn")) {
            if (!sender.hasPermission("spawn.setspawn")) {
                sender.sendMessage(color(config.getString("NoPermission")));
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
                return true;
            }

            Player player = (Player) sender;
            Location spawnLocation = player.getLocation();
            config.set("spawn", spawnLocation);
            try {
                config.save(file);
            } catch (IOException e) {
                player.sendMessage(ChatColor.RED + "Error: " + e.getMessage());
            }
            sender.sendMessage(color(config.getString("SetSpawnLocation")));
            return true;
        } else if (cmd.getName().equalsIgnoreCase("spawn")) {
            if (!sender.hasPermission("spawn.spawn")) {
                sender.sendMessage(color(config.getString("NoPermission")));
                return true;
            }

            if (!config.contains("spawn")) {
                sender.sendMessage(color(config.getString("SpawnNotSet")));
                return true;
            }

            if (args.length == 0) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You must be a player to use this command without arguments.");
                    return true;
                }

                Player player = (Player) sender;
                player.sendMessage(color(config.getString("TpedToSpawn")));
                //Play a sound in increasing pitch once every second for 5 seconds
                final AtomicInteger pitch = new AtomicInteger(0);
                final AtomicInteger task = new AtomicInteger();
                task.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(HardcorePlugin.getPlugin(HardcorePlugin.class), () -> {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, pitch.getAndIncrement() * 0.2f + 1);
                    //Set action bar message
                    if (pitch.get() > 5) {
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
                        Bukkit.getScheduler().cancelTask(task.get());
                        player.teleport(config.getLocation("spawn"));
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Teleported!"));
                        return;
                    }
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Teleporting in " + (5 - pitch.get() + 1) + " seconds"));
                }, 0, 20));
            } else {
                if (!sender.hasPermission("spawn.spawn.others")) {
                    sender.sendMessage(color(config.getString("NoPermission")));
                    return true;
                }

                Player target = getServer().getPlayer(args[0]);

                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                    return true;
                }

                target.teleport(config.getLocation("spawn"));
                target.sendMessage(color(config.getString("TpedByPlayer").replace("%player's display name%", Objects.requireNonNull(sender).getName())));
                sender.sendMessage(config.getString("TpedPlayerWorked").replace("%player's display name%", target.getDisplayName()));
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("delspawn")) {
            if (!sender.hasPermission("spawn.spawn.delete")) {
                sender.sendMessage(color(config.getString("NoPermission")));
                return true;
            }

            config.set("spawn", null);
            try {
                config.save(file);
            } catch (IOException e) {
                sender.sendMessage(ChatColor.RED + "Error: " + e.getMessage());
            }
            sender.sendMessage(color(config.getString("UnsetSpawnLocation")));
            return true;
        }

        return false;
    }

}

