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
    private final FileConfiguration config;
    private final File file;
    public SpawnCommands(FileConfiguration config, File file) {
        this.config = config;
        this.file = file;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        switch (cmd.getName()) {
            case "setspawn" -> {
                if (!sender.hasPermission("spawn.setspawn")) {
                    sender.sendMessage(color(config.getString("NoPermission")));
                    return true;
                }

                if (!(sender instanceof Player player)) {
                    sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
                    return true;
                }

                Location spawnLocation = player.getLocation();
                setSpawnLocation(spawnLocation);
                sender.sendMessage(color(config.getString("SetSpawnLocation")));
                return true;
            }
            case "spawn" -> {
                if (!sender.hasPermission("spawn.spawn")) {
                    sender.sendMessage(color(config.getString("NoPermission")));
                    return true;
                }

                if (!config.contains("spawn")) {
                    sender.sendMessage(color(config.getString("SpawnNotSet")));
                    return true;
                }

                if (args.length > 2) {
                    sender.sendMessage(color(config.getString("TooManyArgs")));
                    return true;
                }
                if (args.length == 0) {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(ChatColor.RED + "You must be a player to use this command without arguments.");
                        return true;
                    }

                    player.sendMessage(color(config.getString("TpedToSpawn")));
                    //Put a action bar tp message
                    final AtomicInteger task = new AtomicInteger();
                    final AtomicInteger time = new AtomicInteger();
                    task.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(HardcorePlugin.getPlugin(HardcorePlugin.class), () -> {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                new TextComponent(ChatColor.GREEN + "Teleporting in " + (5 - time.getAndIncrement()) + " seconds"));
                        if (time.get() == 5) {
                            player.teleport(config.getLocation("spawn"));
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                    new TextComponent(ChatColor.GREEN + "Teleported!"));
                            Bukkit.getScheduler().cancelTask(task.get());
                        }
                    }, 0, 20));
                } else {
                    if (!sender.hasPermission("spawn.spawn.others")) {
                        sender.sendMessage(color(config.getString("NoPermission")));
                        return true;
                    }

                    Player target = getServer().getPlayer(args[0]);

                    if (target == null || !target.isOnline()) {
                        sender.sendMessage(ChatColor.RED + "Player not found.");
                        return true;
                    }

                    target.teleport(config.getLocation("spawn"));
                    target.sendMessage(color(config.getString("TpedByPlayer").
                            replace("%player's display name%", Objects.requireNonNull(sender).getName())));
                    sender.sendMessage(config.getString("TpedPlayerWorked").
                            replace("%player's display name%", target.getDisplayName()));
                }
                return true;
            }
            case "delspawn" -> {
                if (!sender.hasPermission("spawn.spawn.delete")) {
                    sender.sendMessage(color(config.getString("NoPermission")));
                    return true;
                }

                setSpawnLocation(null);
                sender.sendMessage(color(config.getString("UnsetSpawnLocation")));
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private void setSpawnLocation(Location spawnLocation) {
        config.set("spawn", spawnLocation);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

