package antifarm;

import configuration.Configuration;
import core.AntiFarmPlugin;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.HashSet;
import java.util.Set;

public class AntiVillagerFarm implements Listener {
    private final AntiFarmPlugin plugin;
    private Set<String> disabledWorlds;
    private boolean preventHarvesting;
    private boolean preventTrampling;

    public AntiVillagerFarm(AntiFarmPlugin plugin) {
        this.plugin = plugin;
        reloadConf();
    }

    public void reloadConf() {
        Configuration config = plugin.getConfig();
        this.disabledWorlds = new HashSet<>(config.getStringList("settings.disabled-worlds"));
        this.preventHarvesting = config.getBoolean("villager-settings.prevent-villagers-harvesting-farms", true);
        this.preventTrampling = config.getBoolean("villager-settings.prevent-villagers-trampling-farmland", false);
    }

    private boolean isCropBlock(Material mat) {
        return Tag.CROPS.isTagged(mat);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (!preventHarvesting && !preventTrampling) return;
        if (event.getEntityType() != EntityType.VILLAGER) return;
        Material from = event.getBlock().getType();
        boolean isCropAction = isCropBlock(from);
        boolean isTrampling = from == Material.FARMLAND;
        if (isCropAction && !preventHarvesting) return;
        if (isTrampling && !preventTrampling) return;
        if (!isCropAction && !isTrampling) return;
        if (disabledWorlds.contains(event.getEntity().getWorld().getName())) return;
        event.setCancelled(true);
    }
}