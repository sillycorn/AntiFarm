package antifarm;

import configuration.Configuration;
import core.AntiFarmPlugin;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.event.entity.VillagerCareerChangeEvent.ChangeReason;

import java.util.HashSet;
import java.util.Set;

public class AntiVillagerCareer implements Listener {
    private final AntiFarmPlugin plugin;
    private Set<String> disabledWorlds;
    private boolean preventProfessionChange;

    public AntiVillagerCareer(AntiFarmPlugin plugin) {
        this.plugin = plugin;
        reloadConf();
    }

    public void reloadConf() {
        Configuration config = plugin.getConfig();
        this.disabledWorlds = new HashSet<>(config.getStringList("settings.disabled-worlds"));
        this.preventProfessionChange = config.getBoolean("villager-settings.prevent-villagers-profession-change", true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVillagerCareerChange(VillagerCareerChangeEvent event) {
        if (!preventProfessionChange) return;
        if (event.getReason() != ChangeReason.EMPLOYED) return;
        if (disabledWorlds.contains(event.getEntity().getWorld().getName())) return;
        Villager villager = event.getEntity();
        villager.setVillagerExperience(villager.getVillagerExperience() + 1);
    }
}