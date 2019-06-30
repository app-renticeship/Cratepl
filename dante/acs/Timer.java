/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  org.bukkit.Effect
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.Sound
 *  org.bukkit.World
 *  org.bukkit.World$Spigot
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Chest
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 */
package me.dante.acs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.dante.acs.Database;
import me.dante.acs.FindAvaliableLocation;
import me.dante.acs.LootEvent;
import me.dante.acs.Main;
import me.dante.acs.OpenLootInventory;
import me.dante.acs.RandomChestInfo;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

class Timer {
    static Timer instance = new Timer();
    private Database data = Database.instance;
    private LootEvent le = new LootEvent();

    private Timer() {
    }

    private ConfigurationSection chests() {
        return this.data.data.getConfigurationSection("Chests");
    }

    void loadChests() {
        for (String s : this.chests().getKeys(true)) {
            if (s.contains(".")) continue;
            World world = Main.pl.getServer().getWorld(this.chests().getConfigurationSection((String)s).getString("World"));
            int x2 = this.chests().getConfigurationSection((String)s).getInt("X");
            int y = this.chests().getConfigurationSection((String)s).getInt("Y");
            int z = this.chests().getConfigurationSection((String)s).getInt("Z");
            Location loc = new Location(world, (double)x2, (double)y, (double)z);
            int currenttime = this.chests().getConfigurationSection((String)s).getInt("TimeToDelete");
            String block = this.chests().getConfigurationSection((String)s).getString("Block", "AIR");
            Main.pl.RandomChests.put(loc, new RandomChestInfo(currenttime, block));
            this.chests().set((String)s, (Object)null);
        }
        ConfigurationSection section = this.data.data.getConfigurationSection("FixedChests");
        if (section != null) {
            for (String key : section.getKeys(true).stream().filter(x -> !x.contains(".")).collect(Collectors.toSet())) {
                ConfigurationSection data = section.getConfigurationSection(key);
                World world = Main.pl.getServer().getWorld(data.getString("World"));
                int x3 = data.getInt("X");
                int y = data.getInt("Y");
                int z = data.getInt("Z");
                int time = data.getInt("TimeLeft");
                Location location = new Location(world, (double)x3, (double)y, (double)z);
                if (location.getBlock().getType().equals((Object)Material.CHEST)) {
                    Main.pl.FixedChests.put(location, time);
                    continue;
                }
                section.set(key, (Object)null);
            }
        }
        this.data.saveData();
    }

    void saveChests() {
        int counter = 0;
        for (Map.Entry<Location, RandomChestInfo> e : Main.pl.RandomChests.entrySet()) {
            Location loc = e.getKey();
            this.chests().createSection("Chest" + counter);
            this.chests().getConfigurationSection("Chest" + counter).set("World", (Object)loc.getWorld().getName());
            this.chests().getConfigurationSection("Chest" + counter).set("X", (Object)loc.getBlockX());
            this.chests().getConfigurationSection("Chest" + counter).set("Y", (Object)loc.getBlockY());
            this.chests().getConfigurationSection("Chest" + counter).set("Z", (Object)loc.getBlockZ());
            this.chests().getConfigurationSection("Chest" + counter).set("TimeToDelete", (Object)e.getValue().Time);
            this.chests().getConfigurationSection("Chest" + counter).set("Block", (Object)e.getValue().Block);
            ++counter;
        }
        this.data.data.set("FixedChests", (Object)null);
        ConfigurationSection section = this.data.data.createSection("FixedChests");
        counter = 0;
        for (Map.Entry<Location, Integer> item : Main.pl.FixedChests.entrySet()) {
            Location location = item.getKey();
            Object[] arrobject = new Object[]{++counter};
            ConfigurationSection data = section.createSection(String.format("%d", arrobject));
            data.set("World", (Object)location.getWorld().getName());
            data.set("X", (Object)location.getBlockX());
            data.set("Y", (Object)location.getBlockY());
            data.set("Z", (Object)location.getBlockZ());
            data.set("TimeLeft", (Object)item.getValue());
        }
        this.data.saveData();
    }

    void decrease() {
        Main.pl.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)Main.pl, () -> {
            for (Map.Entry<Location, RandomChestInfo> e : new ArrayList<Map.Entry<Location, RandomChestInfo>>(Main.pl.RandomChests.entrySet())) {
                RandomChestInfo value = e.getValue();
                int timeToDelete = value.Time;
                if (timeToDelete > 0) {
                    value.Time = timeToDelete - 1;
                    continue;
                }
                Location loc1 = e.getKey();
                this.le.deleteChest(loc1);
                if (Main.pl.MessageOnKill == null) continue;
                String string = Main.pl.MessageOnKill.replace("&", "\u00a7");
                String x1 = String.valueOf(loc1.getBlockX());
                String y1 = String.valueOf(loc1.getBlockY());
                String z1 = String.valueOf(loc1.getBlockZ());
                String string2 = string.replace("{X}", x1);
                String string3 = string2.replace("{Y}", y1);
                String string4 = string3.replace("{Z}", z1);
                Main.pl.getServer().broadcastMessage(string4);
            }
            for (Map.Entry kv : Main.pl.FixedChests.entrySet()) {
                int k = (Integer)kv.getValue() - 1;
                if (k > 0) {
                    kv.setValue(k);
                    continue;
                }
                kv.setValue(Integer.max(FindAvaliableLocation.getRandom(Main.pl.FixedChestUpdateTimeMin, Main.pl.FixedChestUpdateTimeMax), 0));
                Location location = (Location)kv.getKey();
                Block block = location.getBlock();
                if (!block.getType().equals((Object)Material.CHEST)) continue;
                Inventory inv = ((Chest)block.getState()).getInventory();
                inv.clear();
                OpenLootInventory.fillInvenory(inv);
                if (Main.pl.FixedChestSound != null) {
                    location.getWorld().playSound(location, Main.pl.FixedChestSound, 1.0f, 1.0f);
                }
                if (Main.pl.FixedChestEffect == null) continue;
                location.getWorld().spigot().playEffect(location.clone().add(0.5, 0.5, 0.5), Main.pl.FixedChestEffect, 0, 0, 0.1f, 0.1f, 0.1f, 0.05f, 50, 30);
            }
        }
        , 20, 20);
    }
}

