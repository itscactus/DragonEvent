package its.cactusdev.DragonEvent.managers;

import its.cactusdev.DragonEvent.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class PlaceholderManager extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "dragonevent";
    }

    @Override
    public @NotNull String getAuthor() {
        return "cactusdev";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if(params.equalsIgnoreCase("timer")) {
            String nextEvent = Main.getSqlManager().getData("next");
            LocalDateTime nextEventp = LocalDateTime.parse(nextEvent);
            LocalDateTime now = LocalDateTime.now();
            long secs = ChronoUnit.SECONDS.between(now, nextEventp);
            return Main.formattedTime(secs);
        }
        return null;
    }
}
