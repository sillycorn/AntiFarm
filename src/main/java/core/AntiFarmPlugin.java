package core;

import antifarm.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import configuration.Configuration;

public class AntiFarmPlugin extends JavaPlugin implements Listener {

    private Configuration config;
    private Configuration spawners;
    private AntiFishFarm antiFishFarm;
    private AntiDispenser antiDispenser;
    private AntiCactusFarm antiCactusFarm;
    private AntiBarteringFarm antiBarteringFarm;
    private AntiGrowth antiGrowth;
    private AntiSnowballFarm antiSnowballFarm;
    private AntiArmadilloFarm antiArmadilloFarm;
    private AntiVillagerTransform antiVillagerTransform;
    private AntiVillagerBreed antiVillagerBreed;
    private AntiVillagerFarm antiVillagerFarm;
    private AntiVillagerCareer antiVillagerCareer;
    private AntiVillagerTarget antiVillagerTarget;
    private AntiVillagerTrade antiVillagerTrade;
    private AntiVillageGuard antiVillageGuard;
    private AntiMobFarm antiMobFarm;
    private AntiBerryFarm antiBerryFarm;
    private AntiLavaFarm antiLavaFarm;
    private AntiWaterFarm antiWaterFarm;

    @Override
    public void onEnable() {

        config = new Configuration("config", this);
        spawners = new Configuration("spawners", this);
        antiFishFarm = new AntiFishFarm(this);
        antiDispenser = new AntiDispenser(this);
        antiCactusFarm = new AntiCactusFarm(this);
        antiBarteringFarm = new AntiBarteringFarm(this);
        antiGrowth = new AntiGrowth(this);
        antiSnowballFarm = new AntiSnowballFarm(this);
        antiVillageGuard = new AntiVillageGuard(this);
        antiVillagerBreed = new AntiVillagerBreed(this);
        antiVillagerFarm = new AntiVillagerFarm(this);
        antiVillagerCareer = new AntiVillagerCareer(this);
        antiVillagerTarget = new AntiVillagerTarget(this);
        antiVillagerTrade = new AntiVillagerTrade(this);
        antiVillagerTransform = new AntiVillagerTransform(this);
        antiMobFarm = new AntiMobFarm(this);
        antiLavaFarm = new AntiLavaFarm(this);
        antiBerryFarm = new AntiBerryFarm(this);
        antiWaterFarm = new AntiWaterFarm(this);

        registerEvents(this, new AntiPistonFarm(this), antiVillagerFarm, antiWaterFarm, antiCactusFarm,
                new AntiEndermanFarm(this), antiVillagerBreed, antiMobFarm,
                antiDispenser, antiFishFarm, new AntiMobSpawner(this),
                antiVillagerTransform, antiVillagerTarget, antiVillageGuard, antiSnowballFarm,
                new AntiRaidFarm(this), antiBerryFarm, antiGrowth,
                new AntiFroglightFarm(this), antiVillagerCareer, antiVillagerTrade,
                new AntiChickenEggFarm(this), new AntiCowMilk(this), antiLavaFarm, antiBarteringFarm);
            if (armadillocheck()) {
                antiArmadilloFarm = new AntiArmadilloFarm(this);
                Bukkit.getPluginManager().registerEvents(antiArmadilloFarm, this);
            }
        getCommand("antifarmreloaded").setExecutor(new Commands(this));
        getCommand("antifarm").setExecutor(new Commands(this));
    }

    @Override
    public void onDisable() {
    }

    private static void registerEvents(Plugin plugin, Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, plugin);
        }
    }

    @Override
    public Configuration getConfig() {
        return config;
    }

    public Configuration getSpawners() {
        return spawners;
    }

    public void reloadListeners() {
        antiFishFarm.reloadConf();
        antiDispenser.reloadConf();
        antiCactusFarm.reloadConf();
        antiBarteringFarm.reloadConf();
        antiGrowth.reloadConf();
        antiSnowballFarm.reloadConf();
        antiVillagerBreed.reloadConf();
        antiVillagerFarm.reloadConf();
        antiVillagerCareer.reloadConf();
        antiVillagerTarget.reloadConf();
        antiVillagerTrade.reloadConf();
        antiVillagerTransform.reloadConf();
        antiVillageGuard.reloadConf();
        antiMobFarm.reloadConf();
        antiLavaFarm.reloadConf();
        antiBerryFarm.reloadConf();
        antiWaterFarm.reloadConf();
        
        if (antiArmadilloFarm != null) {
            antiArmadilloFarm.reloadConf();
        }
    }

    private boolean armadillocheck() {
        try {
            EntityType.valueOf("ARMADILLO");
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}