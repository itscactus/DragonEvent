package its.cactusdev.DragonEvent.listeners;

import its.cactusdev.DragonEvent.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class DeathListener implements Listener {
    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if(event.getEntity() instanceof EnderDragon){
            String entityType = Main.getInstance().getNBTfromEntity(event.getEntity(), "type");
            if(entityType.equalsIgnoreCase("eventDragon")) {
                Main.getInstance().getServer().broadcastMessage(
                        ChatColor.translateAlternateColorCodes('&', "&aDragon Event &cThe Dragon is killed by &e" + event.getDamageSource().getDirectEntity().getName())
                );
                Main.getSqlManager().deleteData("next");
            }
        }
    }
}
