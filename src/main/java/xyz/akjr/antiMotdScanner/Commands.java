package xyz.akjr.antiMotdScanner;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Commands implements CommandExecutor {
    private AntiMotdScanner plugin;
    private Config config;
    private StartupManager startupManager;
    public Commands(AntiMotdScanner plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
    }
    @SuppressWarnings({"deprecation", "experimental"})
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ams-reload")) {
            if (sender.isOp()){
                config.reload();
                sender.sendMessage(ChatColor.GREEN + "Reloaded AntiMotdScanner config.");
            }
            else{
                sender.sendMessage(ChatColor.RED + "Operator status is required for this command.");
            }
            return true;
        }
        else if (command.getName().equalsIgnoreCase("ams-purge-cache")) {
            if (sender.isOp()){
                PrintWriter pw = null;
                try {
                    File directory = new File(plugin.getDataFolder() + "/ip-data/");
                    File file = new File(directory + "/ipcache.txt");
                    pw = new PrintWriter(file);
                } catch (FileNotFoundException e) {
                    sender.sendMessage(ChatColor.GREEN + "Failed, ipache file not found. Did someone delete it? Restart the server and it should automatically re-generate.");
                }
                pw.close();
                sender.sendMessage(ChatColor.GREEN + "Purged all IP Cache.");
            }
            else{
                sender.sendMessage(ChatColor.RED + "Operator status is required for this command.");
            }
            return true;
        }
        else if (command.getName().equalsIgnoreCase("ams")) {
            if (sender.isOp()){
                config.reload();
                sender.sendMessage(ChatColor.GREEN + "AntiMOTD Scanner, by Akjr");
                sender.sendMessage(ChatColor.GRAY + "This plugin prevents ServerListPing events from people who have never joined the server, " +
                        "it prevents less specialized automatic scanners from finding your server. It can't prevent more advanced scanners from joining with an" +
                        "account as this plugin does not interfere with the join process. This is a newer plugin, all suggestions are welcome!");
            }
            else{
                sender.sendMessage(ChatColor.RED + "Operator status is required for this command.");
            }
            return true;
        }
        return false;
    }
    public void setStartupManager(StartupManager startupManager) {
        this.startupManager = startupManager;
    }
}