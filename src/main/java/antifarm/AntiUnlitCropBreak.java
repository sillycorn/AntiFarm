package antifarm;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import configuration.Configuration;
import core.AntiFarmPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AntiUnlitCropBreak implements Listener {
    private final AntiFarmPlugin plugin;
    private final Set<Material> lightSensitiveCrops;
    private boolean enableCropPrevention;
    private Set<String> disabledWorlds;

    public AntiUnlitCropBreak(AntiFarmPlugin plugin) {
        this.plugin = plugin;
        this.lightSensitiveCrops = EnumSet.noneOf(Material.class);
        reloadConf();
    }

    public void reloadConf() {
        Configuration config = plugin.getConfig();
        this.enableCropPrevention = config.getBoolean("crop-farms.no-light-break.enabled", true);
        this.disabledWorlds = new HashSet<>(config.getStringList("settings.disabled-worlds"));
        this.lightSensitiveCrops.clear();
        List<String> cropsFromConfig = config.getStringList("crop-farms.no-light-break.no-light-break-crops");

        for (String cropName : cropsFromConfig) {
            Material mat = Material.getMaterial(cropName.toUpperCase());
            if (mat != null) {
                this.lightSensitiveCrops.add(mat);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCropDestroy(BlockDestroyEvent event) {
        if (!enableCropPrevention) return;
        Block block = event.getBlock();
        if (!lightSensitiveCrops.contains(block.getType())) return;
        if (block.getRelative(BlockFace.DOWN).getType() == Material.FARMLAND) {
            if (!disabledWorlds.isEmpty() && disabledWorlds.contains(block.getWorld().getName())) return;
            event.setCancelled(true);
        }
    }
}