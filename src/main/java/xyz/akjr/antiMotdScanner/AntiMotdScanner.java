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
        if (! directory.exists()){
            directory.mkdir();
        }
        try{
            File file = new File(directory + "/ipcache.txt");
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("");
            bw.close();
        }
        catch (IOException e){ e.printStackTrace(); }
        try{
            File file = new File(directory + "/motd-ping-logs.txt");
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("# Logs from all motd pings (blocked or allowed), data generated here is controlled by 'log-all-motd-pings'\n");
            bw.close();
        }
        catch (IOException e){ e.printStackTrace(); }
        try{
            File file = new File(directory + "/blocked-motd-logs.txt");
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("# Logs all blocked motd pings, data generated here is controlled by 'log-blocked-motd-pings'\n");
            bw.close();
        }
        catch (IOException e){ e.printStackTrace(); }
    }

    @Override
    public void onDisable() {
        // None yet
    }
}