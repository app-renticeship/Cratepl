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
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.material.MaterialData
 */
package me.dante.acs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.dante.acs.CommandManager;
import me.dante.acs.Database;
import me.dante.acs.FindAvaliableLocation;
import me.dante.acs.Main;
import me.dante.acs.OpenLootInventory;
import me.dante.acs.RandomChestInfo;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Effect.Type;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.material.MaterialData;

public class LootEvent
implements Listener {
    Database data = Database.instance;
    OpenLootInventory olv = OpenLootInventory.instance;

    public boolean isChest(Location loc) {
        return Main.pl.RandomChests.containsKey((Object)loc);
    }

    public void deleteChest(Location loc) {
        RandomChestInfo value = Main.pl.RandomChests.get((Object)loc);
        Main.pl.RandomChests.remove((Object)loc);
        MaterialData md = this.parseMaterialData(value.Block);
        Block block = loc.getBlock();
        BlockState blockState = block.getState();
        blockState.setType(md.getItemType());
        blockState.setData(md);
        blockState.update(true);
        sthooks.sthook = 1;
        
    }

    MaterialData parseMaterialData(String s) {
        String[] p;
        Material material;
        if (s != null && (material = Material.matchMaterial((String)(p = s.split(":"))[0])) != null) {
            MaterialData md = new MaterialData(material);
            if (p.length > 1) {
                try {
                    int d = Integer.parseInt(p[1]);
                    md.setData((byte)d);
                }
                catch (Exception d) {
                    // empty catch block
                }
                return md;
            }
        }
        return new MaterialData(Material.AIR);
    }

    public void killallchests() {
    	 for (final Entry<Location, RandomChestInfo> e : Main.pl.RandomChests.entrySet()) {
             e.getKey().getBlock().setType(Material.AIR);
         }
         Main.pl.RandomChests.clear();
     }
public static class sthooks{
	static int sthook = Main.sthook1;
}
    @EventHandler
    public void onBlockClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (e.getAction().equals((Object)Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals((Object)Material.CHEST)) {
            Location location = e.getClickedBlock().getLocation();
            if (Main.pl.addChestplayers.containsKey((Object)player)) {
                CommandManager.WaitChooseChest wcc = Main.pl.addChestplayers.get((Object)player);
                wcc.cancel();
                Main.pl.addChestplayers.remove((Object)player);
                if (!this.isChest(location)) {
                    switch (wcc.Command) {
                        case "add": {
                            if (!Main.pl.FixedChests.containsKey((Object)location)) {
                                Inventory inv;
                                BlockState blockState = location.getBlock().getState();
                                if (blockState instanceof Chest) {
                                    inv = ((Chest)blockState).getInventory();
                                    inv.clear();
                                    OpenLootInventory.fillInvenory(inv);
                                }
                                Main.pl.FixedChests.put(location, FindAvaliableLocation.getRandom(1, FindAvaliableLocation.getRandom(1, Integer.max(FindAvaliableLocation.getRandom(Main.pl.FixedChestUpdateTimeMin, Main.pl.FixedChestUpdateTimeMax), 0))));
                                inv = ((Chest)blockState).getInventory();
                                inv.clear();
                                OpenLootInventory.fillInvenory(inv);
                                if (Main.pl.FixedChestSound != null) {
                                    location.getWorld().playSound(location, Main.pl.FixedChestSound, 1.0f, 1.0f);
                                }
                                if (Main.pl.FixedChestEffect != null) {
                                    location.getWorld().spigot().playEffect(location.clone().add(0.5, 0.5, 0.5), Main.pl.FixedChestEffect, 0, 0, 0.1f, 0.1f, 0.1f, 0.05f, 50, 30);
                                }
                                player.sendMessage("\u00a7aFixed chest has been added to collection \u00a7f=)");
                                break;
                            }
                            player.sendMessage("\u00a7cOops... This chest is already added \u00a7f=\\");
                            break;
                        }
                        case "del": {
                            if (Main.pl.FixedChests.containsKey((Object)location)) {
                                Main.pl.FixedChests.remove((Object)location);
                                Chest chest = (Chest) location.getBlock().getState();
                                Block block = location.getBlock();
                                
                                if (block.getType().equals((Object)Material.CHEST)) {
                                	Inventory inventory = chest.getInventory();
                                    inventory.clear();
                                    if (Main.pl.FixedChestSound != null) {
                                        location.getWorld().playSound(location, Main.pl.FixedChestSound, 1.0f, 1.0f);
                                    }
                                    if (Main.pl.FixedChestEffect != null) {
                                        location.getWorld().spigot().playEffect(location.clone().add(0.5, 0.5, 0.5), Main.pl.FixedChestEffect, 0, 0, 0.1f, 0.1f, 0.1f, 0.05f, 50, 30);
                                    }
                                }
                                player.sendMessage("\u00a7aFixed chest has been removed from collection \u00a7f=)");
                                break;
                            }
                            player.sendMessage("\u00a7cOops... This chest is not our \u00a7f=\\");
                        }
                    }
                }
                e.setCancelled(true);
            } else if (this.isChest(e.getClickedBlock().getLocation())) {
            	Firework firework = player.getWorld().spawn(location, Firework.class);
            	FireworkMeta data = (FireworkMeta) firework.getFireworkMeta();
            	data.addEffects(FireworkEffect.builder().withColor(Color.RED).withFade(Color.RED).with(FireworkEffect.Type.BURST).trail(false).flicker(true).build());
            	data.setPower(2);
            	firework.setFireworkMeta(data);
            	 
                location.getWorld().playSound(location, Main.pl.RandomChestOpenSound, 1.0f, 1.0f);
                this.olv.openInvenory(player);
                if (Main.pl.commands != null) {
                    for (String loc : Main.pl.commands) {
                        Main.pl.getServer().dispatchCommand((CommandSender)Main.pl.getServer().getConsoleSender(), loc.replace("{player}", player.getName()));
                    }
                }//*HA GOTEEEM
                sthooks.sthook = 1;
                this.deleteChest(location);
                e.setCancelled(true);
                if (Main.pl.MessageOnLoot != null) {
                    Location loc1 = e.getClickedBlock().getLocation();
                    String x1 = String.valueOf(loc1.getBlockX());
                    String y = String.valueOf(loc1.getBlockY());
                    String z = String.valueOf(loc1.getBlockZ());
                    String string = Main.pl.MessageOnLoot.replace("&", "\u00a7");
                    String string2 = string.replace("{X}", x1);
                    String string3 = string2.replace("{Y}", y);
                    String string4 = string3.replace("{Z}", z);
                    String string5 = string4.replace("{Player}", player.getName());
                    Main.pl.getServer().broadcastMessage(string5);
                }
            }
        }
    }
   
    @EventHandler
    public void onChestBreak(BlockBreakEvent e) {
        if (e.getBlock().getType().equals((Object)Material.CHEST)) {
            Location location = e.getBlock().getLocation();
            if (this.isChest(location)) {
            	 if (!Main.pl.abletobreak.contains(e.getPlayer())) {
                     e.setCancelled(true);
                     e.getPlayer().sendMessage(Main.pl.getConfig().getString("NotAbleToBreakAchest").replaceAll("&", "§"));
                } else {
                    
                    this.deleteChest(e.getBlock().getLocation());
                    e.getPlayer().sendMessage(Main.pl.getConfig().getString("PlayerOnBreakChest").replaceAll("&", "§"));
                }
            } else if (Main.pl.FixedChests.containsKey((Object)location)) {
                if (!Main.pl.abletobreak.contains((Object)e.getPlayer())) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(Main.pl.getConfig().getString("NotAbleToBreakAchest").replaceAll("&", "\u00a7"));
                } else {
                    e.setCancelled(true);
                    Main.pl.FixedChests.remove((Object)location);
                    Chest chest = (Chest) location.getBlock().getState();
                    Block block = location.getBlock();
                    if (block.getType().equals((Object)Material.CHEST)) {
                    	Inventory inventory = chest.getInventory();
                        inventory.clear();
                    }
                    e.getPlayer().sendMessage("\u00a7aFixed chest has been removed from collection");
                }
            }
        }
   }
}




