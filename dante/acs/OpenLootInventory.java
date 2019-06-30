/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 */
package me.dante.acs;

import java.util.HashMap;
import java.util.Random;
import me.dante.acs.Main;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class OpenLootInventory {
    static OpenLootInventory instance = new OpenLootInventory();

    public static ItemStack getrandomitem() {
        int all = Main.items.size();
        if (all != 0) {
            Random air1 = new Random();
            int ra = air1.nextInt(all) + 0;
            ItemStack item = Main.items.get(ra);
            return item;
        }
        ItemStack air = new ItemStack(Material.AIR);
        return air;
    }

    public static int findavaliablerandomSlot(Inventory inv) {
        int size = inv.getSize();
        int current = 1000;
        for (int i = 0; i < size; ++i) {
            Random random = new Random();
            int ra = random.nextInt(size) + 0;
            if (inv.getItem(ra) != null) continue;
            current = ra;
            break;
        }
        return current;
    }

    public void openInvenory(Player player) {
        String invname = Main.pl.getConfig().getString("Inventory_Name").replaceAll("&", "\u00a7");
        int slots = Main.pl.getConfig().getInt("Inventory_Slots");
        Inventory inv = Main.pl.getServer().createInventory((InventoryHolder)null, slots, invname);
        OpenLootInventory.fillInvenory(inv);
        player.openInventory(inv);
    }

    public static void fillInvenory(Inventory inv) {
        int itemamount = Main.pl.getConfig().getInt("ItemAmountToAdd");
        for (int i = 0; i < itemamount; ++i) {
            if (OpenLootInventory.findavaliablerandomSlot(inv) == 1000) continue;
            int slot = OpenLootInventory.findavaliablerandomSlot(inv);
            inv.setItem(slot, OpenLootInventory.getrandomitem());
        }
    }
}

