package its.cactusdev.DragonEvent.managers;

import its.cactusdev.DragonEvent.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class DragonManager {
    private final EnderDragon dragon;
    public DragonManager(Location loc) {
        World world = loc.getWorld();
        this.dragon = (EnderDragon) world.spawnEntity(loc, EntityType.ENDER_DRAGON);

        for(Player p : Main.getInstance().getServer().getOnlinePlayers()) {
            p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&eDragon Event"),
                    ChatColor.translateAlternateColorCodes('&', "&fEvent has started"));
        }

        Main.getInstance().addNBT2Entity(dragon, "type", "eventDragon");
        String name = Main.getInstance().getConfig().getString("Dragon.name");
        double health = Main.getInstance().getConfig().getDouble("Dragon.health");
        dragon.setCustomName(ChatColor.translateAlternateColorCodes('&', name));
        dragon.setCustomNameVisible(true);
        dragon.setAI(true);
        dragon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health);
        dragon.setHealth(health);
    }

    public EnderDragon getDragon() {
        return dragon;
    }
}
