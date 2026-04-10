package antifarm;

import configuration.Configuration;
import core.AntiFarmPlugin;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.util.HashSet;
import java.util.Set;

public class AntiVillagerBreed implements Listener {
    private final AntiFarmPlugin plugin;
    private Set<String> disabledWorlds;
    private boolean preventVillagersBreed;

    public AntiVillagerBreed(AntiFarmPlugin plugin) {
        this.plugin = plugin;
        reloadConf();
    }

    public void reloadConf() {
        Configuration config = plugin.getConfig();
        this.disabledWorlds = new HashSet<>(config.getStringList("settings.disabled-worlds"));
        this.preventVillagersBreed = config.getBoolean("villager-settings.prevent-villagers-breed", true);
    }

    private boolean isBlockedWorld(org.bukkit.entity.Entity entity) {
        return disabledWorlds.contains(entity.getWorld().getName());
    }

    private void resetBreed(org.bukkit.entity.Entity entity) {
        if (!(entity instanceof Villager villager)) return;
        if (isBlockedWorld(entity)) return;
        villager.setBreed(false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVillagerPickup(EntityPickupItemEvent event) {
        if (!preventVillagersBreed) return;
        resetBreed(event.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVillagerDrop(EntityDropItemEvent event) {
        if (!preventVillagersBreed) return;
        resetBreed(event.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVillagerBreed(EntityBreedEvent event) {
        if (!preventVillagersBreed) return;
        if (!(event.getEntity() instanceof Villager)) return;
        if (isBlockedWorld(event.getEntity())) return;
        event.setCancelled(true);
        ((Villager) event.getMother()).setBreed(false);
        ((Villager) event.getFather()).setBreed(false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVillagerSpawn(CreatureSpawnEvent event) {
        if (!preventVillagersBreed) return;
        if (event.getSpawnReason() != SpawnReason.BREEDING) return;
        if (event.getEntityType() != EntityType.VILLAGER) return;
        if (isBlockedWorld(event.getEntity())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityEnterLoveMode(EntityEnterLoveModeEvent event) {
        if (!preventVillagersBreed) return;
        resetBreed(event.getEntity());
    }
}