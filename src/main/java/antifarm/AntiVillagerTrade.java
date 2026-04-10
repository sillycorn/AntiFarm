package antifarm;

import configuration.Configuration;
import core.AntiFarmPlugin;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.HashSet;
import java.util.Set;

public class AntiVillagerTrade implements Listener {
    private final AntiFarmPlugin plugin;
    private Set<String> disabledWorlds;
    private boolean preventVillagerTrade;
    private boolean preventWanderingTraderTrade;

    public AntiVillagerTrade(AntiFarmPlugin plugin) {
        this.plugin = plugin;
        reloadConf();
    }

    public void reloadConf() {
        Configuration config = plugin.getConfig();
        this.disabledWorlds = new HashSet<>(config.getStringList("settings.disabled-worlds"));
        this.preventVillagerTrade = config.getBoolean("villager-settings.prevent-villager-trade", false);
        this.preventWanderingTraderTrade = config.getBoolean("villager-settings.prevent-wandering-trader-trade", false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractAtEntity(PlayerInteractEntityEvent event) {
        if (!preventVillagerTrade && !preventWanderingTraderTrade) return;
        EntityType clickedType = event.getRightClicked().getType();
        if (!((clickedType == EntityType.VILLAGER && preventVillagerTrade) || (clickedType == EntityType.WANDERING_TRADER && preventWanderingTraderTrade))) return;
        if (disabledWorlds.contains(event.getPlayer().getWorld().getName())) return;
        event.setCancelled(true);
    }
}