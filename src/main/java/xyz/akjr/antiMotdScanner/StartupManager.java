package xyz.akjr.antiMotdScanner;

import org.bukkit.scheduler.BukkitRunnable;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import java.util.Objects;

public class StartupManager {
    private final AntiMotdScanner plugin;
    private final Config config;

    public StartupManager(AntiMotdScanner plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void initialize() {
        initializeConfig();
        validateDependencies();
        registerCommands();
        printStartupMessages();
    }


    private void registerCommands() {
        Commands commandExecutor = new Commands(plugin, config);
        Objects.requireNonNull(plugin.getCommand("ams")).setExecutor(commandExecutor);
        Objects.requireNonNull(plugin.getCommand("ams-reload")).setExecutor(commandExecutor);
        Objects.requireNonNull(plugin.getCommand("ams-purge-cache")).setExecutor(commandExecutor);
    }

    private void initializeConfig() {
        config.createConfig();
    }

    private void validateDependencies() {
        if (plugin.getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
            plugin.getLogger().severe("ProtocolLib not found! Disabling plugin.");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        if (protocolManager == null) {
            plugin.getLogger().severe("Failed to initialize ProtocolLib's ProtocolManager! Disabling plugin.");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }


    private void printStartupMessages() {
        (new BukkitRunnable() {
            public void run() {
                System.out.println("Server protected by Anti-MOTD-Scanner (AMS).");
                }
        }).runTaskLater(plugin, 225L);
    }
}