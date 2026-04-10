package antifarm;

import configuration.Configuration;
import core.AntiFarmPlugin;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.util.HashSet;
import java.util.Set;

public class AntiVillageGuard implements Listener {
    private final AntiFarmPlugin plugin;
    private Set<String> disabledWorlds;
    private boolean preventVillageDefense;
    private boolean preventVillageRaids;

    public AntiVillageGuard(AntiFarmPlugin plugin) {
        this.plugin = plugin;
        reloadConf();
    }

    public void reloadConf() {
        Configuration config = plugin.getConfig();
        this.disabledWorlds = new HashSet<>(config.getStringList("settings.disabled-worlds"));
        this.preventVillageDefense = config.getBoolean("villager-settings.prevent-golem-spawning.village-defense", true);
        this.preventVillageRaids = config.getBoolean("villager-settings.prevent-golem-spawning.village-raids", true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() != EntityType.IRON_GOLEM) return;
        SpawnReason reason = event.getSpawnReason();
        if (!((reason == SpawnReason.VILLAGE_DEFENSE && preventVillageDefense) || (reason == SpawnReason.VILLAGE_INVASION && preventVillageRaids))) return;
        if (disabledWorlds.contains(event.getEntity().getWorld().getName())) return;
        event.setCancelled(true);
    }
}