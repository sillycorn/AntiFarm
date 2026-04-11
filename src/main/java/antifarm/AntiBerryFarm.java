package antifarm;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;

import configuration.Configuration;
import core.AntiFarmPlugin;

public class AntiBerryFarm implements Listener {
    private final AntiFarmPlugin plugin;
    private boolean preventBerryFarms;
    private Set<String> disabledWorlds;
    private static final ItemStack BERRY_TEMPLATE = new ItemStack(Material.SWEET_BERRIES, 1);

    public AntiBerryFarm(AntiFarmPlugin plugin) {
        this.plugin = plugin;
        reloadConf();
    }

    public void reloadConf() {
        Configuration config = plugin.getConfig();
        this.preventBerryFarms = config.getBoolean("farms-settings.prevent-berry-farms", true);
        this.disabledWorlds = new HashSet<>(config.getStringList("settings.disabled-worlds"));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (!preventBerryFarms) return;
        if (event.getEntityType() != EntityType.FOX) return;

        Block block = event.getBlock();
        if (block.getType() != Material.SWEET_BERRY_BUSH) return;

        World world = block.getWorld();
        if (!disabledWorlds.isEmpty() && disabledWorlds.contains(world.getName())) return;
        event.setCancelled(true);
        LivingEntity fox = (LivingEntity) event.getEntity();
        Ageable sweetBerryBush = (Ageable) block.getBlockData();
        sweetBerryBush.setAge(1);
        block.setBlockData(sweetBerryBush);
        if (fox.getEquipment() != null && fox.getEquipment().getItemInMainHand().getType() == Material.AIR) {
            fox.getEquipment().setItemInMainHand(BERRY_TEMPLATE.clone());
            world.playSound(fox.getLocation(), Sound.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, 1.0f, 1.0f);
        }
    }
}