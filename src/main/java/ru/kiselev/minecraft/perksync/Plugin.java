package ru.kiselev.minecraft.perksync;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiselev.minecraft.perksync.command.PerkSyncCommand;
import ru.kiselev.minecraft.perksync.listener.GameListener;
import ru.kiselev.minecraft.perksync.util.PerkConfigUtil;

import java.io.File;
import java.util.HashMap;

public class Plugin extends JavaPlugin {

    public static Plugin instance;

    public File dataFile;
    public FileConfiguration dataConfiguration;

    public HashMap<String, ItemStack> playerSticks = new HashMap<>();
    public HashMap<String, ItemStack> playerBlocks = new HashMap<>();
    public HashMap<String, ItemStack> playerPickaxes = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        final File configFile = new File(getDataFolder() + File.separator + "config.yml");
        if (!configFile.exists()) {
            getLogger().info("Creating file config.yml..");

            getConfig().options().copyDefaults(true);
            saveDefaultConfig();

            getLogger().info("File \"config.yml\" has been successfully created and copied!");
        }

        playerSticks = PerkConfigUtil.loadCustomMap("sticks");
        playerBlocks = PerkConfigUtil.loadCustomMap("blocks");
        playerPickaxes = PerkConfigUtil.loadCustomMap("pickaxes");

        dataFile = new File(getDataFolder() + File.separator + "data.yml");
        dataConfiguration = YamlConfiguration.loadConfiguration(dataFile);
        if (!dataFile.exists()) {
            getLogger().info("Creating file data.yml..");

            getConfig().options().copyDefaults(true);
            dataConfiguration.options().copyDefaults(true);
            saveResource("data.yml", false);

            getLogger().info("File \"data.yml\" has been successfully created!");
        }

        Bukkit.getPluginManager().registerEvents(new GameListener(), this);
        getCommand("perksync").setExecutor(new PerkSyncCommand());
    }

    @Override
    public void onDisable() {
        instance = null;

        PerkConfigUtil.saveCustomMap(playerSticks, "sticks");
        PerkConfigUtil.saveCustomMap(playerBlocks, "blocks");
        PerkConfigUtil.saveCustomMap(playerPickaxes, "pickaxes");
    }

    public static Plugin getInstance() {
        return instance;
    }

    public File getDataFile() {
        return dataFile;
    }

    public FileConfiguration getDataConfiguration() {
        return dataConfiguration;
    }

    public HashMap<String, ItemStack> getPlayerSticks() {
        return playerSticks;
    }

    public HashMap<String, ItemStack> getPlayerBlocks() {
        return playerBlocks;
    }

    public HashMap<String, ItemStack> getPlayerPickaxes() {
        return playerPickaxes;
    }
}