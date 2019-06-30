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
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.BlockState
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.material.Chest
 *  org.bukkit.material.MaterialData
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 */
package me.dante.acs;

import java.util.HashMap;

import me.dante.acs.CommandManager.hook;
import me.dante.acs.Database;
import me.dante.acs.FindAvaliableLocation;
import me.dante.acs.LootEvent.sthooks;
import me.dante.acs.Main;
import me.dante.acs.RandomChestInfo;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.material.Chest;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class GenerateChest {
	static int minplayer;
	static int playercount;
    static FindAvaliableLocation fal = new FindAvaliableLocation();
    Database data = Database.instance;
    static BlockFace[] chestFaces = new BlockFace[]{BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
public void init2() {
	
	
	 minplayer = Main.pl.getConfig().getInt("Minplayer");
 	
 	playercount = Bukkit.getServer().getOnlinePlayers().size();
	
}
    public ConfigurationSection data() {
        return this.data.data;
    }

    public void GenerateChest(int time) {
        Main.pl.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)Main.pl, new Runnable(){

            @Override
            public void run() {
                GenerateChest.this.spawnchest();
            }
        }, (long)time * 20, (long)time * 20);
    }

    public void spawnchest() {
  

        Location loc = fal.FindLocation();
        if (hook.int1 == 1){
        
        	
        	if (playercount >= minplayer) {
        		
        	if (sthooks.sthook == 1) {
        if (loc != null) {
            this.saveChest(loc);
            loc.getBlock().setType(Material.CHEST);
            BlockState state = loc.getBlock().getState();
            state.setData((MaterialData)new Chest(chestFaces[FindAvaliableLocation.getRandom(0, 3)]));
            state.update();
            sthooks.sthook = 0;
            if (Main.pl.MessageOnSpawn != null) {
                String x = String.valueOf(loc.getBlockX());
                String y = String.valueOf(loc.getBlockY());
                String z = String.valueOf(loc.getBlockZ());
                String string = Main.pl.MessageOnSpawn.replace("&", "\u00a7");
                String string2 = string.replace("{X}", x);
                String string3 = string2.replace("{Y}", y);
                String string4 = string3.replace("{Z}", z);
                Main.pl.getServer().broadcastMessage(string4);
            }
        }
    }
  
    }
    }
    } 

    void saveChest(Location location) {
        MaterialData md = location.getBlock().getState().getData();
        String mdText = String.format("%s:%d", new Object[]{md.getItemType(), Byte.valueOf(md.getData())});
        Main.pl.RandomChests.put(location, new RandomChestInfo(Main.pl.getConfig().getInt("KillChestAfterTime"), mdText));
        if (Main.pl.RandomChestSound != null) {
            location.getWorld().playSound(location, Main.pl.RandomChestSound, 1.0f, 1.0f);
        }
        if (Main.pl.RandomChestEffect != null) {
            location.getWorld().playEffect(location, Main.pl.RandomChestEffect, 1);
        }
    }

public void forcespawn() {
    Location loc = fal.FindLocation();
  
    if (loc != null) {
        this.saveChest(loc);
        loc.getBlock().setType(Material.CHEST);
        BlockState state = loc.getBlock().getState();
        state.setData((MaterialData)new Chest(chestFaces[FindAvaliableLocation.getRandom(0, 3)]));
        state.update();
        sthooks.sthook = 0;
        if (Main.pl.MessageOnSpawn != null) {
            String x = String.valueOf(loc.getBlockX());
            String y = String.valueOf(loc.getBlockY());
            String z = String.valueOf(loc.getBlockZ());
            String string = Main.pl.MessageOnSpawn.replace("&", "\u00a7");
            String string2 = string.replace("{X}", x);
            String string3 = string2.replace("{Y}", y);
            String string4 = string3.replace("{Z}", z);
            Main.pl.getServer().broadcastMessage(string4);
        }
    }
}
}



