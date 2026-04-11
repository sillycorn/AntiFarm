# AntiFarmReloaded

**AntiFarmReloaded** is a powerful Spigot/Paper Minecraft server plugin designed for Survival servers. It automatically **detects and blocks** automated farms that produce near-infinite amounts of items without active player effort. 

Server owners have **full control** via the configuration file, allowing you to tailor the anti-farming rules exactly to your server's needs, including per-world toggles.

---

## Features

### Universal AFK Farm Prevention
* **Detects & Blocks AFK Methods:** Covers crop farms, mob grinders, spawner farms, and redstone-based automation.
* **Active Gameplay Required:** Prevents players from gaining resources passively without actually playing.

### Mob Farm & Spawner Control
* **Damage Thresholds:** Require players to deal a configurable percentage of damage to mobs for them to drop loot and XP.
* **Spawner Management:** Disable spawners entirely, block breaking (with admin sneak-bypass), and block spawn egg conversions.
* **Armadillo XP:** Disable armadillo infection to prevent automatic XP farming.
* **Mob Whitelisting/Blacklisting:** Choose exactly which mobs are protected by the plugin.
* **Custom Death Causes:** Allow drops from custom death causes to ensure compatibility with other plugins.

### Automatic Crop & Block Farm Prevention
* **Cactus & Bamboo:** Block cactus farms by preventing growth next to adjacent blocks.
* **Piston Farms:** Stop pistons from automatically breaking or moving farm blocks.
* **Water Harvesting:** Prevent water from automatically breaking and collecting crops.
* **Growth Control:** Prevent the automatic growth of configurable blocks (cactus, bamboo, kelp, dripstone, etc.).
* **Infinite Lava:** Block infinite lava generation via pointed dripstone.

### Complete Villager Control
* **Farming & Breeding:** Prevent villagers from harvesting crops, breeding, or changing professions.
* **Trading:** Disable trading for both standard Villagers and Wandering Traders.
* **Iron Golems:** Stop Iron Golem spawning to prevent iron farms.
* **Cure Mechanics:** Adjust zombie villager cure chances independently of server difficulty.

### Anti-AFK Fish Farms
* **Chunk Limits:** Limits fishing by tracking the number of catches per chunk.
* **Cooldowns:** Set a configurable cooldown per chunk before the fish limit resets.
* **Custom Warnings:** Warn players with fully customizable messages when they hit the chunk limit.

### Animal & Creature Production Limits
* **Hand-Feeding Required:** Limit froglight (frogs), egg (chickens), and milk (cows) production. Players must hand-feed the animals to continue production.
* **Randomized Yields:** Production limits are randomized between a configurable minimum and maximum per feeding cycle.
* **Foxes & Snow Golems:** Prevent foxes from automatically dropping sweet berries, and disable snow golem trails to prevent snowball farms.
* **Piglin Bartering:** Prevent dispensers from feeding gold to Piglins. Optionally require players to hand gold directly to them by disabling ground pickup.

### Anti-Dispenser & Raid Farms
* **Dispenser Blacklist:** Block dispensers from interacting with farm blocks using a fully configurable item blacklist (bone meal, buckets, shears, etc.).
* **Automated Shearing:** Prevent automatic sheep shearing via dispensers.
* **Raid Farms:** Prevent automated raid farming by enforcing a configurable cooldown between raids.

---

## Installation

1. Download the latest `.jar` file from the [Releases](../../releases/latest) page.
2. Drop the `.jar` into your Minecraft server's `plugins/` folder.
3. Restart your server.
4. Open `plugins/AntiFarmReloaded/config.yml` to customize your settings!

---

## Reloaded Improvements
**AntiFarmReloaded** is a reimagined, actively maintained version of the original AntiFarm plugin. 
It features **massive bug fixes, modern optimizations, codebase improvements, and entirely new features** to support newer versions of Minecraft.

## Credits
* **Original Plugin:** [AntiFarm by bagdydad](https://github.com/bagdydad/AntiFarm)
* **Config:** [Config-Updater by tchristofferson](https://github.com/tchristofferson/Config-Updater)
