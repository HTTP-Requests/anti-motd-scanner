package xyz.akjr.antiMotdScanner;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Config {
    private final AntiMotdScanner plugin;
    private File configFile;
    private YamlConfiguration config;

    public Config(JavaPlugin plugin) {
        this.plugin = (AntiMotdScanner) plugin;
    }

    // ############################ INIT CONFIG FILE ############################

    public void createConfig() {
        this.configFile = new File(this.plugin.getDataFolder(), "config.yml");
        if (!this.configFile.exists()) {
            this.configFile.getParentFile().mkdirs();
            this.plugin.saveResource("config.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(this.configFile);

        this.config.addDefault("block-unknown-pings", true);
        this.config.addDefault("enable-ipcache", true);
        this.config.addDefault("log-all-motd-pings", true);
        this.config.addDefault("log-blocked-motd-pings", true);
        this.config.addDefault("stack-same-ips", true);

        this.config.options().copyDefaults(true);
        try {
            this.config.save(this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public YamlConfiguration getConfig() {
        return this.config;
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = (YamlConfiguration) plugin.getConfig();
    }
}
