/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Chest
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package me.dante.acs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import me.dante.acs.Database;
import me.dante.acs.GenerateChest;
import me.dante.acs.ItemAdderGui;
import me.dante.acs.LoadChances;
import me.dante.acs.LootEvent;
import me.dante.acs.Main;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class CommandManager
implements CommandExecutor {
    int num = 0;
    ItemAdderGui itg = new ItemAdderGui();
    GenerateChest gc = new GenerateChest();
    LoadChances lc = LoadChances.instance;
    LootEvent le = new LootEvent();

    public ConfigurationSection itdb() {
        return Main.pl.db.data.getConfigurationSection("ItemDatabase");
    }

    public void save() {
        Main.pl.db.saveData();
    }

    public void sendhelp(Player player) {
        player.sendMessage("ACS COMMANDS");
        player.sendMessage("\u00a79ACS\u00a7lCommands:");
        player.sendMessage("\u00a79/acs toggle \u00a76(ENABLES OR DISABLES ACS)");
        player.sendMessage("\u00a79/acs additem \u00a76(Open the item sumbition Gui)");
        player.sendMessage("\u00a79/acs addchest \u00a76(Add fixed chest to collection by right-click on it)");
        player.sendMessage("\u00a79/acs delchest \u00a76(Delete fixed chest from collection by right-click on it)");
        player.sendMessage("\u00a79/acs delall \u00a76(Delete all fixed chests from collection)");
        player.sendMessage("\u00a79/acs killall \u00a76(Clear all random chests from the server)");
        player.sendMessage("\u00a79/acs togglebreak \u00a76(Be able to clear a random chest by breaking it or delete fixed chest)");
        player.sendMessage("\u00a79/acs forcespawn \u00a76(Forcespawn a random chest)");
        player.sendMessage("\u00a79/acs rndtime \u00a76(Randomize time left to kill/update chests)");
        player.sendMessage("\u00a79*-----\u00a74\u00a7lacs\u00a7r\u00a7c*-----*");
    }

    public void sendconsolehelp(ConsoleCommandSender sender) {
        sender.sendMessage("\u00a79*-----\u00a74\u00a7lacs");
        sender.sendMessage("\u00a79\u00a7lConsole Commands:");
        sender.sendMessage("\u00a79/acs delall (Delete all fixed chests from collection)");
        sender.sendMessage("\u00a79/acs killall (Clear all random chests from the server)");
        sender.sendMessage("\u00a79/acs forcespawn (Forcespawn a chest)");
        sender.sendMessage("\u00a79/acs rndtime (Randomize time left to kill (random) or update (fixed) chests)");
        sender.sendMessage("");
    }
public static class hook {
	static int int1 = Main.hooky;
}
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
   
        ConsoleCommandSender player1;
        Player player;
        if (Main.pl.addChestplayers.containsKey((Object)sender)) {
            sender.sendMessage("\u00a7cCanceled \u00a7fo_O");
            Main.pl.addChestplayers.get((Object)sender).cancel();
            Main.pl.addChestplayers.remove((Object)sender);
        }
        if (command.getName().equalsIgnoreCase("acs")) {
            if (sender instanceof Player && !(player = (Player)sender).hasPermission("acs.general")) {
                player.sendMessage("\u00a7cInsufficient permissions.");
                return false;
            }
            if (args.length == 0) {
                if (sender instanceof Player) {
                    player = (Player)sender;
                         this.sendhelp(player);
                } else {
                    player1 = (ConsoleCommandSender)sender;
                    this.sendconsolehelp(player1);
                }
            }
          
                else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("togglebreak")) {
                    if (!(sender instanceof Player)) {
                        this.sendconsolehelp((ConsoleCommandSender)sender);
                        return false;
                    }
                    player = (Player)sender;
                    if (!player.hasPermission("acs.togglebreak")) {
                        player.sendMessage("\u00a7cInsufficient permissions.");
                        return false;
                    }
                    if (Main.pl.abletobreak.contains(player)) {
                    	player.sendMessage("//Debug message// ignore");
                        Main.pl.abletobreak.remove(player);
                        player.sendMessage("\u00a76You toggled ChestBreak \u00a7cOff!");
                    } else {
                        Main.pl.abletobreak.add(player);
                        player.sendMessage("\u00a76You toggled ChestBreak \u00a7aOn!");
                    }
                } else if (args[0].equalsIgnoreCase("killall")) {
                    if (sender instanceof Player && !sender.hasPermission("acs.killall")) {
                        sender.sendMessage("\u00a7cInsufficient permissions.");
                        return false;
                    }
                    this.le.killallchests();
                    sender.sendMessage("\u00a7aChests have been deleted");
                } else if (args[0].equalsIgnoreCase("forcespawn")) {
                    if (!sender.hasPermission("acs.forcespawn")) {
                        sender.sendMessage("\u00a7cInsufficient permissions.");
                        
                       
                    }
                    sender.sendMessage("forcespawn debug");
                    this.gc.forcespawn();
                } else if (args[0].equalsIgnoreCase("rndtime")) {
                    if (!sender.hasPermission("acs.rndtime")) {
                        sender.sendMessage("\u00a7cInsufficient permissions.");
                        return false;
                    }
                    Main.pl.randomizeRandomChestsTimeLeft();
                    Main.pl.randomizeFixedChestsTimeLeft();
                    sender.sendMessage("\u00a7eRandomize time left completed.");
                } else if (args[0].equalsIgnoreCase("addchest")) {
                    if (!sender.hasPermission("acs.fixedchest")) {
                        sender.sendMessage("\u00a7cInsufficient permissions.");
                        return false;
                    }
                    player = (Player)sender;
                    new WaitChooseChest().start(player, "add");
                } else if (args[0].equalsIgnoreCase("delchest")) {
                    if (!sender.hasPermission("acs.fixedchest")) {
                        sender.sendMessage("\u00a7cInsufficient permissions.");
                        return false;
                    }
                    player = (Player)sender;
                    new WaitChooseChest().start(player, "del");
                } else if (args[0].equalsIgnoreCase("delall")) {
                    if (sender instanceof Player && !sender.hasPermission("acs.delall")) {
                        sender.sendMessage("\u00a7cInsufficient permissions.");
                        return false;
                    }
                    for (Map.Entry<Location, Integer> kv : Main.pl.FixedChests.entrySet()) {
                        Location location = kv.getKey();
                        Block block = location.getBlock();
                        if (!block.getType().equals((Object)Material.CHEST)) continue;
                        ((Chest)block.getState()).getInventory().clear();
                    }
                    Main.pl.FixedChests.clear();
                    sender.sendMessage("\u00a7a Fixed chests has been deleted from pool");
                }
            }
        }
        if (args.length >= 1 && args[0].equalsIgnoreCase("additem")) {
            if (!(sender instanceof Player)) {
                player1 = (ConsoleCommandSender)sender;
                this.sendconsolehelp(player1);
                return false;
            }
            player = (Player)sender;
            if (!player.hasPermission("acs.additem")) {
                player.sendMessage("\u00a7cInsufficient permissions.");
                return false;
            }
            this.itg.openGui(player);
        }  else if (args.length == 1) {
       	 if (args[0].equalsIgnoreCase("toggle")) {
             if (!(sender instanceof Player)) {
                 this.sendconsolehelp((ConsoleCommandSender)sender);
                 return false;
             }
             player = (Player)sender;
             if (!player.hasPermission("acs.toggle")) {
                 player.sendMessage("\u00a7cInvalid command");
                 return false;
             }
             if (hook.int1 == 1) {
            	
            	 hook.int1 = 0;
                 player.sendMessage("\u00a76ACS DISABLED");
             }else {
                 hook.int1 = 1;
                 player.sendMessage("\u00a76ACS ENABLED");
    	 }
    }
    }
        return false;
    }

    public class WaitChooseChest
    extends BukkitRunnable {
        int Left;
        Player Player;
        String Command;

        public WaitChooseChest() {
            this.Left = 10;
        }

        public void start(Player player, String command) {
            this.Command = command;
            this.Player = player;
            Main.pl.addChestplayers.put(this.Player, this);
            this.runTaskTimer((Plugin)Main.pl, 20, 20);
        }

        public void run() {
            this.Player.sendMessage(String.format("\u00a7aYou have %d seconds to right click a chest", this.Left));
            if (this.Left-- < 0) {
                this.Player.sendMessage("\u00a7cAddchest event cancelled");
                Main.pl.addChestplayers.remove((Object)this.Player);
                this.cancel();
            }
        }
    }

}

