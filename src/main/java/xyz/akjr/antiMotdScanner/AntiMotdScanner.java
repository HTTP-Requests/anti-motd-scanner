package xyz.akjr.antiMotdScanner;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class AntiMotdScanner extends JavaPlugin {

    private Config configManager;
    private Commands commands;
    private StartupManager startupManager;
    private Network network;

    @Override
    public void onEnable() {
        this.configManager = new Config(this);
        this.startupManager = new StartupManager(this, configManager);
        this.commands = new Commands(this, configManager);
        this.commands.setStartupManager(this.startupManager);
        this.startupManager.initialize();
        this.network = new Network(configManager, this);
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(network, this);
        setup();
    }

    public void setup(){
        File directory = new File(getDataFolder() + "/ip-data/");
        if (!directory.exists()){
            directory.mkdir();
        }

        createIfAbsent(directory, "ipcache.txt", "");
        createIfAbsent(directory, "motd-ping-logs.txt",
                "# Logs for all motd pings (blocked or allowed), data generated here is controlled by 'log-all-motd-pings'\n");
        createIfAbsent(directory, "ip-join-logs.txt",
                "# Logs for all accepted player joins, data generated here is controlled by 'log-join-ips'\n");
        createIfAbsent(directory, "blocked-motd-logs.txt",
                "# Logs all blocked motd pings, data generated here is controlled by 'log-blocked-motd-pings'\n");
    }

    private void createIfAbsent(File directory, String filename, String initialContent) {
        File file = new File(directory, filename);
        if (!file.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write(initialContent);
            } catch (IOException e) {
                getLogger().severe("Can't create file " + filename + ":\n" + e);
            }
        }
    }

    @Override
    public void onDisable() {}
}