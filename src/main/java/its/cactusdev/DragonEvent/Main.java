package its.cactusdev.DragonEvent;

import its.cactusdev.DragonEvent.commands.DragonCommand;
import its.cactusdev.DragonEvent.listeners.DamageListener;
import its.cactusdev.DragonEvent.listeners.DeathListener;
import its.cactusdev.DragonEvent.listeners.PlayerTeleportListener;
import its.cactusdev.DragonEvent.managers.DragonManager;
import its.cactusdev.DragonEvent.managers.PlaceholderManager;
import its.cactusdev.DragonEvent.managers.SQLManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public final class Main extends JavaPlugin {
    private static Main instance;
    private static SQLManager sqlManager;
    private static LocalDateTime nextEvent;
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        try {
            sqlManager = new SQLManager();
        } catch (SQLException e) {
            getLogger().severe("Couldn't connect to database.db");
            e.printStackTrace();
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                String nextData = sqlManager.getData("next");
                LocalDateTime now = LocalDateTime.now();
                if (nextData == null || nextData.isEmpty() || nextData.equals("0")) {
                    nextEvent = now.plusMinutes(getConfig().getInt("Settings.timer"));
                } else {
                    try {
                        nextEvent = LocalDateTime.parse(nextData);
                        if (nextEvent.isBefore(now)) {
                            nextEvent = now.plusMinutes(getConfig().getInt("Settings.timer"));
                            World world = getServer().getWorld(getConfig().getString("Settings.world"));
                            Location location = new Location(world,0, 80,0);
                            new DragonManager(location);
                            getServer().broadcastMessage("Süre doldu");
                        }
                    } catch (DateTimeParseException e) {
                        nextEvent = now.plusMinutes(getConfig().getInt("Settings.timer"));
                    }
                }
                sqlManager.setData("next", nextEvent.toString());
                sqlManager.setData("now", now.toString());
                getLogger().info(nextEvent.toString());
            }
        }.runTaskTimer(this, 0L, 20L * 3);
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderManager().register();
        } else {
            getLogger().warning("Could not find PlaceholderAPI! U can't use custom Placeholders.");
        }
        getServer().getPluginManager().registerEvents(new DamageListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerTeleportListener(), this);
        getCommand("dragon").setExecutor(new DragonCommand());
        loadDragonSettings();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public void addNBT2Entity(Entity entity, String key, String value) {
        NamespacedKey namespacedKey = new NamespacedKey(this, key);
        PersistentDataContainer container = entity.getPersistentDataContainer();
        container.set(namespacedKey, PersistentDataType.STRING, value);
    }
    public String getNBTfromEntity(Entity entity, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(this, key);
        PersistentDataContainer container = entity.getPersistentDataContainer();
        return container.getOrDefault(namespacedKey, PersistentDataType.STRING, "None");
    }
    public static Main getInstance() {
        return instance;
    }
    public static SQLManager getSqlManager() {
        return sqlManager;
    }
    public void loadDragonSettings() {
        FileConfiguration config = getConfig(); // Access the default config

        String dragonName = config.getString("Dragon.name", "Fallback Dragon");
        double health = config.getDouble("Dragon.health", 200.0);

        getLogger().info("Loaded event dragon settings:");
        getLogger().info("Name: " + dragonName);
        getLogger().info("Health: " + health);
    }
    public static String formattedTime(long seconds) {
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        String formattedDays = String.format("%02d", days);
        String formattedHours = String.format("%02d", hours);
        String formattedMinutes = String.format("%02d", minutes);
        String formattedSeconds = String.format("%02d", remainingSeconds);

        StringBuilder result = new StringBuilder();

        if (!formattedDays.equals("00")) {
            // Gün varsa tam format
            result.append(formattedDays).append(" " + getInstance().getConfig().getString("Settings.Placeholders.days") + " ")
                    .append(formattedHours).append(" " + getInstance().getConfig().getString("Settings.Placeholders.hours") + " ")
                    .append(formattedMinutes).append(" " + getInstance().getConfig().getString("Settings.Placeholders.minutes") + " ");
        } else if (!formattedHours.equals("00")) {
            // Saat varsa saat ve dakika
            result.append(formattedHours).append(" " + getInstance().getConfig().getString("Settings.Placeholders.hours") + " ")
                    .append(formattedMinutes).append(" " + getInstance().getConfig().getString("Settings.Placeholders.minutes") + " ");
        } else if (!formattedMinutes.equals("00")) {
            // Dakika varsa dakika
            result.append(formattedMinutes).append(" " + getInstance().getConfig().getString("Settings.Placeholders.minutes") + " ")
                    .append(formattedSeconds).append(" " + getInstance().getConfig().getString("Settings.Placeholders.seconds") + " ");
        } else {
            // Sadece saniye
            result.append(formattedSeconds).append(" " + getInstance().getConfig().getString("Settings.Placeholders.seconds") + " ");
        }

        return result.toString().trim();
    }

}
