package its.cactusdev.DragonEvent.commands;

import its.cactusdev.DragonEvent.Main;
import its.cactusdev.DragonEvent.managers.DragonManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DragonCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!commandSender.hasPermission("dragon.admin")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have enough permissions for this."));
        }

        if(args.length == 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /dragon <reload/start>"));
            return true;
        }

        if(args[0].equalsIgnoreCase("reload")) {
            Main.getInstance().reloadConfig();
            Main.getInstance().loadDragonSettings();
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aConfiguration reloaded"));
            return true;
        }

        if(args[0].equalsIgnoreCase("start")) {
            Player player = (Player) commandSender;
            World world = Main.getInstance().getServer().getWorld(Main.getInstance().getConfig().getString("Settings.world"));
            Location location = new Location(world,0, 80,0);
            new DragonManager(location);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&aDragon Event has started."));
        }
        return false;
    }
}
