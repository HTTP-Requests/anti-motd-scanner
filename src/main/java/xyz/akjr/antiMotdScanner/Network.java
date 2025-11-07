package xyz.akjr.antiMotdScanner;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

public class Network implements Listener {

    private final Config configManager;
    private final AntiMotdScanner plugin;
    private ProtocolManager protocolManager;
    private Set<String> cachedIPs;
    private File ipCacheFile;

    public Network(Config configManager, AntiMotdScanner plugin) {
        this.configManager = configManager;
        this.plugin = plugin;
        this.ipCacheFile = new File(plugin.getDataFolder(), "/ip-data/ipcache.txt");
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.cachedIPs = new HashSet<>();
        initialize();
    }

    private void initialize() {
        loadCachedIPs();
        blockStatusRequests();
        plugin.getLogger().info("ServerListPing initialized - Only cached player IPs can ping server");
    }

    private void loadCachedIPs() {
        cachedIPs.clear();

        if (!ipCacheFile.exists()) {
            plugin.getLogger().info("IP cache file doesn't exist yet - will be created when players join");
            return;
        }

        try {
            cachedIPs.addAll(Files.readAllLines(ipCacheFile.toPath()));
            cachedIPs.removeIf(ip -> ip.trim().isEmpty());
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not load /ip-data/ipcache.txt", e);
        }
    }

    private void blockStatusRequests() {
        // Block handshake packets that are for STATUS requests only (nextState = 1)
        // Allow login handshakes (nextState = 2) to pass through
        protocolManager.addPacketListener(new PacketAdapter(plugin,
                ListenerPriority.HIGHEST,
                PacketType.Handshake.Client.SET_PROTOCOL) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.isCancelled()) return;
                boolean smartDisable = configManager.getConfig().getBoolean("block-unknown-pings", true);
                if (!smartDisable) return;
                try {
                    PacketContainer packet = event.getPacket();
                    // 1 = Status request (server list ping)
                    // 2 = Login request (actual player joining)
                    int nextState = packet.getIntegers().read(1);

                    if (nextState == 1) { // status request (server list ping)
                        reloadCachedIPs();
                        String clientIP = getClientIP(event);
                        if (!isCachedIP(clientIP)) {
                            event.setCancelled(true);
                            log("Blocked ServerListPing from " + clientIP);
                        }
                    }

                } catch (Exception e) {
                    plugin.getLogger().log(Level.WARNING, "Error processing handshake packet", e);
                }
            }
        });

        // Block the status start packet (secondary protection)
        protocolManager.addPacketListener(new PacketAdapter(plugin,
                ListenerPriority.HIGHEST,
                PacketType.Status.Client.START) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
                boolean smartDisable = configManager.getConfig().getBoolean("block-unknown-pings", true);
                if (!smartDisable) return;
                reloadCachedIPs();
                String clientIP = getClientIP(event);
                if (!isCachedIP(clientIP)) {
                    event.setCancelled(true);
                    log("Blocked StatusRequest from " + clientIP);
                }
            }
        });

        // Block ping packets (final protection)
        protocolManager.addPacketListener(new PacketAdapter(plugin,
                ListenerPriority.HIGHEST,
                PacketType.Status.Client.PING) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
                boolean smartDisable = configManager.getConfig().getBoolean("block-unknown-pings", true);
                if (!smartDisable) return;
                reloadCachedIPs();
                String clientIP = getClientIP(event);
                if (!isCachedIP(clientIP)) {
                    event.setCancelled(true);
                    log("Blocked PingPacket from " + clientIP);
                    return; // Don't disconnect, just cancel
                }
            }
        });
    }

    private String getClientIP(PacketEvent event) {
        try {
            if (event.getPlayer() != null && event.getPlayer().getAddress() != null) {
                return ((InetSocketAddress) event.getPlayer().getAddress()).getAddress().getHostAddress();
            }
        } catch (Exception e) {}
        return "0.0.0.0";
    }

    private boolean isCachedIP(String ip) {
        return cachedIPs.contains(ip);
    }

    public void reloadCachedIPs() {
        loadCachedIPs();
    }

    private void log(String message) {
        String lastMessage = null;
        int repeatCount = 1;
        try {
            File logFile = new File(plugin.getDataFolder(), "/ip-data/blocked-motd-logs.txt");
            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            FileWriter fw = new FileWriter(logFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);

            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

            out.println("[" + timestamp + "] - " + message);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        String playerIp = event.getAddress().getHostAddress();
        boolean logMotds = this.configManager.getConfig().getBoolean("log-all-motd-pings", true);
        if (logMotds) {
            logMotdPing(playerIp);
        }
    }

    private void logMotdPing(String ip) {
        try {
            File logFile = new File(plugin.getDataFolder(), "/ip-data/motd-ping-logs.txt");
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            FileWriter fw = new FileWriter(logFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            out.println("[" + timestamp + "] IP: " + ip);
            out.close();
        } catch (IOException e) {e.printStackTrace();}
    }


    // ################# PLAYER #################
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean cacheOn = this.configManager.getConfig().getBoolean("enable-ipcache", true);
        if (cacheOn) {
            String filePath = "/ip-data/ipcache.txt";
            String playerIP = Objects.requireNonNull(event.getPlayer().getAddress()).getAddress().getHostAddress();
            try {
                File file = new File(plugin.getDataFolder(), filePath);
                if (!file.exists()) {
                    file.createNewFile();
                }
                Set<String> existingLines = new HashSet<>(Files.readAllLines(file.toPath()));
                if (!existingLines.contains(playerIP)) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                        writer.write(playerIP);
                        writer.newLine();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
