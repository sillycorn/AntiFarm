package antifarm;

import configuration.Configuration;
import core.AntiFarmPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class AntiWaterFarm implements Listener {
    private final AntiFarmPlugin plugin;
    private boolean preventWaterHarvesting;
    private boolean breakBlocks;
    private Set<String> disabledWorlds;
    private Set<Material> farmBlocks;

    public AntiWaterFarm(AntiFarmPlugin plugin) {
        this.plugin = plugin;
        reloadConf();
    }

    public void reloadConf() {
        Configuration config = plugin.getConfig();
        this.preventWaterHarvesting = config.getBoolean("farms-settings.prevent-water-harvesting-farms", true);
        this.breakBlocks = config.getBoolean("settings.break-blocks", true);
        this.disabledWorlds = new HashSet<>(config.getStringList("settings.disabled-worlds"));
        this.farmBlocks = EnumSet.noneOf(Material.class);
        for (String matStr : config.getStringList("farm-blocks")) {
            try {
                this.farmBlocks.add(Material.valueOf(matStr.toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (!preventWaterHarvesting) return;
        if (event.getBlock().getType() != Material.WATER) return;

        Block toBlock = event.getToBlock();
        if (!farmBlocks.contains(toBlock.getType())) return;
        if (!disabledWorlds.isEmpty() && disabledWorlds.contains(toBlock.getWorld().getName())) return;
        event.setCancelled(true);
        if (breakBlocks) {
            toBlock.setType(Material.AIR);
        }
    }
}