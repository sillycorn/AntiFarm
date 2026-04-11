package antifarm;

import configuration.Configuration;
import core.AntiFarmPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent.ChangeReason;

import java.util.HashSet;
import java.util.Set;

public class AntiLavaFarm implements Listener {

    private final AntiFarmPlugin plugin;
    private boolean preventInfinityLava;
    private Set<String> disabledWorlds;

    public AntiLavaFarm(AntiFarmPlugin plugin) {
        this.plugin = plugin;
        reloadConf();
    }

    public void reloadConf() {
        Configuration config = plugin.getConfig();
        this.preventInfinityLava = config.getBoolean("farms-settings.prevent-infinity-lava", true);
        this.disabledWorlds = new HashSet<>(config.getStringList("settings.disabled-worlds"));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCauldronLevelChange(CauldronLevelChangeEvent event) {
        if (!preventInfinityLava) return;
        if (event.getReason() != ChangeReason.NATURAL_FILL) return;
        if (event.getNewState().getType() != Material.LAVA_CAULDRON) return;
        Block block = event.getBlock();
        if (!disabledWorlds.isEmpty() && disabledWorlds.contains(block.getWorld().getName())) return;
        for (int i = 2; i <= 15; i++) {
            Block current = block.getRelative(0, i, 0);
            if (current.getType() == Material.LAVA) {
                current.setType(Material.AIR);
                break;
            }
        }
    }
}