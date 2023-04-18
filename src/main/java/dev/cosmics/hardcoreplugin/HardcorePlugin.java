package dev.cosmics.hardcoreplugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class HardcorePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        SpawnCommands spawnCommands = new SpawnCommands(getConfig(), new File(getDataFolder(), "config.yml"));
        getCommand("setspawn").setExecutor(spawnCommands);
        getCommand("spawn").setExecutor(spawnCommands);
        getCommand("delspawn").setExecutor(spawnCommands);
        getServer().getPluginManager().registerEvents(new BukkitHandler(getConfig()), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
