package antifarm;

import configuration.Configuration;
import core.AntiFarmPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class AntiMobFarm implements Listener {
    private static final Attribute maxmobHealth;
    private static final Set<Enchantment> FIRE_ENCHANTS = Set.of(
            Enchantment.FIRE_ASPECT,
            Enchantment.FLAME
    );

    static {
        Attribute maxhealth = null;
        if (maxhealth == null) {
            maxhealth = Registry.ATTRIBUTE.get(NamespacedKey.minecraft("max_health"));
        }
        if (maxhealth == null) {
            maxhealth = Registry.ATTRIBUTE.get(NamespacedKey.minecraft("generic.max_health"));
        }
        maxmobHealth = maxhealth;
    }

    private final AntiFarmPlugin plugin;
    private final NamespacedKey damageTakenKey;
    private final NamespacedKey lastPlayerHitKey;
    private Set<String> disabledWorlds;
    private boolean enablePrevention;
    private boolean allowCustomDeathDrops;
    private boolean isBlacklist;
    private Set<EntityType> mobList;
    private double damagePercentage;
    private boolean blockDropXp;
    private boolean blockDropItem;

    public AntiMobFarm(AntiFarmPlugin plugin) {
        this.plugin = plugin;
        this.damageTakenKey = new NamespacedKey(plugin, "damageTaken");
        this.lastPlayerHitKey = new NamespacedKey(plugin, "lastPlayerHit");
        reloadConf();
    }

    public void reloadConf() {
        Configuration config = plugin.getConfig();
        this.disabledWorlds = new HashSet<>(config.getStringList("settings.disabled-worlds"));
        this.enablePrevention = config.getBoolean("prevent-mob-farms.enable", true);
        this.allowCustomDeathDrops = config.getBoolean("prevent-mob-farms.allow-custom-death-drops", false);
        this.isBlacklist = config.getBoolean("prevent-mob-farms.blacklist", true);
        this.damagePercentage = config.getDouble("prevent-mob-farms.required-damage-percent-for-loot") / 100.0;
        this.blockDropXp = config.getBoolean("prevent-mob-farms.block-drop-xp", true);
        this.blockDropItem = config.getBoolean("prevent-mob-farms.block-drop-item", true);

        this.mobList = EnumSet.noneOf(EntityType.class);
        for (String mob : config.getStringList("prevent-mob-farms.moblist")) {
            try {
                this.mobList.add(EntityType.valueOf(mob.toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private boolean isIgnoredEntity(Entity entity) {
        return entity instanceof Player || entity instanceof ArmorStand || entity instanceof AbstractVillager || entity instanceof ChestedHorse;
    }

    private boolean isListExempt(EntityType type) {
        boolean inList = mobList.contains(type);
        return isBlacklist != inList;
    }

    private boolean hasFireEnchant(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasEnchants()) return false;
        return meta.getEnchants().keySet().stream().anyMatch(FIRE_ENCHANTS::contains);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!enablePrevention) return;
        DamageCause cause = event.getCause();
        if (cause != DamageCause.ENTITY_ATTACK && cause != DamageCause.ENTITY_SWEEP_ATTACK && cause != DamageCause.PROJECTILE && cause != DamageCause.FIRE_TICK)
            return;
        Entity entity = event.getEntity();
        if (isIgnoredEntity(entity)) return;
        if (isListExempt(entity.getType())) return;

        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        Player player = null;

        if (event instanceof EntityDamageByEntityEvent damageEvent) {
            Entity damager = damageEvent.getDamager();
            if (cause == DamageCause.PROJECTILE && damager instanceof Projectile proj && proj.getShooter() instanceof Player p) {
                player = p;
            } else if ((cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.ENTITY_SWEEP_ATTACK) && damager instanceof Player p) {
                player = p;
            }
        } else if (cause == DamageCause.FIRE_TICK) {
            long lastHit = pdc.getOrDefault(lastPlayerHitKey, PersistentDataType.LONG, 0L);
            if (System.currentTimeMillis() - lastHit > 5000L) return;
        }

        if (player != null && hasFireEnchant(player.getInventory().getItemInMainHand())) {
            pdc.set(lastPlayerHitKey, PersistentDataType.LONG, System.currentTimeMillis());
        }

        if (disabledWorlds.contains(entity.getWorld().getName())) return;
        double damageTaken = pdc.getOrDefault(damageTakenKey, PersistentDataType.DOUBLE, 0.0);
        pdc.set(damageTakenKey, PersistentDataType.DOUBLE, damageTaken + event.getFinalDamage());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!enablePrevention) return;
        Entity entity = event.getEntity();
        if (isIgnoredEntity(entity)) return;
        EntityDamageEvent lastDamage = entity.getLastDamageCause();
        if (lastDamage != null && lastDamage.getCause() == DamageCause.CUSTOM && allowCustomDeathDrops) return;
        if (isListExempt(entity.getType())) return;
        AttributeInstance healthAttr = event.getEntity().getAttribute(maxmobHealth);
        if (healthAttr == null) return;
        double maxHealth = healthAttr.getValue();
        double damageTaken = entity.getPersistentDataContainer().getOrDefault(damageTakenKey, PersistentDataType.DOUBLE, 0.0);
        if ((maxHealth * damagePercentage) <= damageTaken) return;
        if (disabledWorlds.contains(entity.getWorld().getName())) return;
        if (blockDropXp) event.setDroppedExp(0);
        if (blockDropItem) event.getDrops().clear();
    }
}