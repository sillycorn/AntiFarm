package antifarm;

import configuration.Configuration;
import core.AntiFarmPlugin;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.HashSet;
import java.util.Set;

public class AntiVillagerTarget implements Listener {
    private final AntiFarmPlugin plugin;
    private Set<String> disabledWorlds;
    private boolean preventTargeting;

    public AntiVillagerTarget(AntiFarmPlugin plugin) {
        this.plugin = plugin;
        reloadConf();
    }

    public void reloadConf() {
        Configuration config = plugin.getConfig();
        this.disabledWorlds = new HashSet<>(config.getStringList("settings.disabled-worlds"));
        this.preventTargeting = config.getBoolean("villager-settings.prevent-targeting-villager", true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        if (!preventTargeting) return;
        if (event.getTarget() == null) return;
        if (event.getTarget().getType() != EntityType.VILLAGER) return;
        if (disabledWorlds.contains(event.getEntity().getWorld().getName())) return;
        event.setCancelled(true);
    }
}