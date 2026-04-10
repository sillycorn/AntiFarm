package antifarm;

import configuration.Configuration;
import core.AntiFarmPlugin;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.EntityTransformEvent.TransformReason;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class AntiVillagerTransform implements Listener {
    private final AntiFarmPlugin plugin;
    private Set<String> disabledWorlds;
    private boolean preventCureEnable;
    private int curePercent;
    private int infectionPercent;

    public AntiVillagerTransform(AntiFarmPlugin plugin) {
        this.plugin = plugin;
        reloadConf();
    }

    public void reloadConf() {
        Configuration config = plugin.getConfig();
        this.disabledWorlds = new HashSet<>(config.getStringList("settings.disabled-worlds"));
        this.preventCureEnable = config.getBoolean("villager-settings.prevent-zombie-villagers-cure.enable", true);
        this.curePercent = config.getInt("villager-settings.prevent-zombie-villagers-cure.cure-percent", 0);
        this.infectionPercent = config.getInt("villager-settings.prevent-villagers-infection.infection-percent", 30);
    }

    private boolean isGoldenApple(ItemStack item) {
        Material m = item.getType();
        return m == Material.GOLDEN_APPLE || m == Material.ENCHANTED_GOLDEN_APPLE;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!preventCureEnable || curePercent != 0) return;
        if (event.getRightClicked().getType() != EntityType.ZOMBIE_VILLAGER) return;
        PlayerInventory inv = event.getPlayer().getInventory();
        if (!isGoldenApple(inv.getItemInMainHand()) && !isGoldenApple(inv.getItemInOffHand())) return;
        if (disabledWorlds.contains(event.getPlayer().getWorld().getName())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTransform(EntityTransformEvent event) {
        EntityType type = event.getEntityType();
        TransformReason reason = event.getTransformReason();
        boolean isInfection = type == EntityType.VILLAGER && reason == TransformReason.INFECTION;
        boolean isCure = type == EntityType.ZOMBIE_VILLAGER && reason == TransformReason.CURED;
        if (!isInfection && !isCure) return;
        if (disabledWorlds.contains(event.getEntity().getWorld().getName())) return;
        int percent = isInfection ? infectionPercent : curePercent;
        if (ThreadLocalRandom.current().nextInt(100) < percent) return;
        event.setCancelled(true);
    }
}