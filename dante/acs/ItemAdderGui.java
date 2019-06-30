package me.dante.acs;

import org.bukkit.configuration.*;
import org.bukkit.entity.*;
import java.util.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;

public class ItemAdderGui implements Listener
{
    LoadChances lc;
    
    public ItemAdderGui() {
        this.lc = new LoadChances();
    }
    
    public ConfigurationSection itdb() {
        return Main.pl.db.data.getConfigurationSection("ItemDatabase");
    }
    
    public void addOnMap(final ItemStack item) {
        for (int i = 0; i < 100000; ++i) {
            if (Main.pl.itemstoadd.get(i) == null) {
                Main.pl.itemstoadd.put(i, item);
                Main.pl.itemstoadd.put(i, item);
                break;
            }
        }
    }
    
    public void loadItems() {
        Main.pl.db.loadData();
        for (int i = 0; i < 10000 && this.itdb().isConfigurationSection(String.valueOf(i)); ++i) {
            final ItemStack item = this.itdb().getConfigurationSection(String.valueOf(i)).getItemStack("item");
            final int chance = this.itdb().getConfigurationSection(String.valueOf(i)).getInt("chance");
            Main.pl.itemstoadd.put(i, item);
            Main.pl.chances.put(i, chance);
        }
    }
    
    public boolean isFull(final Inventory inv) {
        for (int i = 0; i < 45; ++i) {
            if (inv.getItem(i) == null) {
                return false;
            }
        }
        return true;
    }
    
    public void saveItems() {
        Main.pl.db.loadData();
        int toset = 0;
        Main.pl.db.data.set("ItemDatabase", (Object)null);
        Main.pl.db.data.createSection("ItemDatabase");
        for (int i = 0; i < Main.pl.itemstoadd.size(); ++i) {
            if (Main.pl.itemstoadd.get(i) != null) {
                this.itdb().createSection(String.valueOf(toset));
                this.itdb().getConfigurationSection(String.valueOf(toset)).set("item", (Object)Main.pl.itemstoadd.get(i));
                if (Main.pl.chances.get(i) != null && Main.pl.chances.get(i) != 0) {
                    this.itdb().getConfigurationSection(String.valueOf(toset)).set("chance", (Object)Main.pl.chances.get(i));
                }
                else {
                    this.itdb().getConfigurationSection(String.valueOf(toset)).set("chance", (Object)50);
                }
            }
            else {
                --toset;
            }
            ++toset;
        }
        Main.pl.db.saveData();
        this.loadItems();
        this.lc.loaditems();
    }
    
    public void addchance(final int id, final int chancetoadd) {
        final int currentchance = Main.pl.chances.get(id);
        Main.pl.chances.put(id, currentchance + chancetoadd);
    }
    
    public void remove(final int id, final int chancetoremove) {
        final int currentchance = Main.pl.chances.get(id);
        Main.pl.chances.put(id, currentchance - chancetoremove);
    }
    
    public void additems(final Player player, final int page) {
        final Inventory inv = player.getOpenInventory().getTopInventory();
        final ItemStack arrow = new ItemStack(Material.ARROW);
        final ItemMeta arrowmeta = arrow.getItemMeta();
        arrowmeta.setDisplayName("§cBack");
        arrow.setItemMeta(arrowmeta);
        inv.setItem(45, arrow);
        arrowmeta.setDisplayName("§cNext");
        arrow.setItemMeta(arrowmeta);
        inv.setItem(53, arrow);
        final ItemStack paper = new ItemStack(Material.PAPER);
        final ItemMeta papermeta = paper.getItemMeta();
        papermeta.setDisplayName("§6Info:");
        final ArrayList lore = new ArrayList();
        lore.add("§aTo add items to the chests");
        lore.add("§ajust drop them in here.");
        lore.add("§aTo edit the chance of an item");
        lore.add("§aright click it and a chance editor");
        lore.add("§agui will open.");
        lore.add("§cIf you don't edit the chance it will");
        lore.add("§cautomatically be set to 50.");
        papermeta.setLore((List)lore);
        paper.setItemMeta(papermeta);
        inv.setItem(49, paper);
        int firstnumber;
        int maxnumber;
        if (page == 1) {
            firstnumber = page * 45 - 45;
            maxnumber = page * 45;
        }
        else {
            firstnumber = page * 45 - 45;
            maxnumber = page * 45;
        }
        int slotcounter = 0;
        for (int glass = firstnumber; glass < maxnumber; ++glass) {
            if (Main.pl.itemstoadd.get(glass) != null) {
                inv.setItem(slotcounter, (ItemStack)Main.pl.itemstoadd.get(glass));
            }
            ++slotcounter;
        }
        final ItemStack var14 = new ItemStack(Material.STAINED_GLASS_PANE);
        final ItemMeta glassmeta = var14.getItemMeta();
        glassmeta.setDisplayName(" ");
        var14.setItemMeta(glassmeta);
        var14.setDurability((short)7);
        inv.setItem(47, var14);
        inv.setItem(48, var14);
        inv.setItem(46, var14);
        inv.setItem(51, var14);
        inv.setItem(52, var14);
        inv.setItem(50, var14);
    }
    
    public void openPage(final Player player, final int page) {
        final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, "§8Page: " + page + "/5");
        Main.pl.additem.add(player);
        Main.pl.currentpage.put(player, page);
        player.openInventory(inv);
        this.additems(player, page);
    }
    
    public void openGui(final Player player) {
        this.loadItems();
        this.openPage(player, 1);
    }
    
    public ItemStack item(final ItemStack item, final String name, final int durability) {
        final ItemMeta itemmeta = item.getItemMeta();
        itemmeta.setDisplayName(name);
        item.setItemMeta(itemmeta);
        item.setDurability((short)durability);
        return item;
    }
    
    public int chancetoaddorremove(final ItemStack item) {
        final String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        final String name2 = name.replace("Add", "");
        final String name3 = name2.replace("Remove", "");
        final String name4 = name3.replace(" ", "");
        return Integer.parseInt(name4);
    }
    
    @EventHandler
    public void onClick(final InventoryClickEvent e) {
        final Player player = (Player)e.getWhoClicked();
        if (Main.pl.additem.contains(player)) {
            if (e.getCurrentItem() == null) {
                return;
            }
            if (e.getSlot() == 53) {
                e.setCancelled(true);
                if (Main.pl.currentpage.get(player) == 5) {
                    return;
                }
                if (this.isFull(e.getInventory())) {
                    final int inv = Main.pl.currentpage.get(player) + 1;
                    player.getOpenInventory().close();
                    this.openPage(player, inv);
                }
                else {
                    player.sendMessage("§cPlease fill this page before moving to the next one.");
                }
            }
            else if (e.getSlot() == 45) {
                e.setCancelled(true);
                if (Main.pl.currentpage.get(player) == 1) {
                    return;
                }
                final int inv = Main.pl.currentpage.get(player) - 1;
                player.getOpenInventory().close();
                this.openPage(player, inv);
            }
            else {
                if (e.getSlot() > 45 && e.getSlot() < 54) {
                    e.setCancelled(true);
                }
                if (e.getClick().equals((Object)ClickType.RIGHT) || e.getClick().equals((Object)ClickType.SHIFT_RIGHT)) {
                    this.save(e.getInventory(), Main.pl.currentpage.get(player));
                    if (!e.getCurrentItem().getType().toString().equalsIgnoreCase("AIR") && e.getSlot() < 45) {
                        e.setCancelled(true);
                        int inv;
                        if (Main.pl.currentpage.get(player) == 1) {
                            inv = e.getSlot();
                        }
                        else {
                            inv = Main.pl.currentpage.get(player) * 45 - 45 + e.getSlot();
                        }
                        this.openChanceEditor(player, inv, Main.pl.currentpage.get(player));
                    }
                }
            }
        }
        else if (Main.pl.idediting.containsKey(player)) {
            final Inventory inv2 = e.getInventory();
            e.setCancelled(true);
            final int id = Main.pl.idediting.get(player);
            final int currentchance = Main.pl.chances.get(id);
            final int lastpage = Main.pl.lastpageno.get(player);
            if (e.getCurrentItem().getType().equals((Object)Material.ARROW)) {
                player.closeInventory();
                this.openPage(player, lastpage);
                return;
            }
            if (e.getCurrentItem().getType().equals((Object)Material.STAINED_GLASS_PANE)) {
                if (e.getCurrentItem().getDurability() == 13) {
                    final int chancetoremove = this.chancetoaddorremove(e.getCurrentItem());
                    if (currentchance + chancetoremove > 100) {
                        player.sendMessage("§cThe chance can't be bigger than 100");
                    }
                    else {
                        this.addchance(id, chancetoremove);
                        inv2.setItem(13, this.item(new ItemStack(Material.DIAMOND), "§6Current Chance: §c" + Main.pl.chances.get(id), 0));
                    }
                }
                else if (e.getCurrentItem().getDurability() == 14) {
                    final int chancetoremove = this.chancetoaddorremove(e.getCurrentItem());
                    if (currentchance - chancetoremove < 1) {
                        player.sendMessage("§cThe chance can't be less than 1");
                    }
                    else {
                        this.remove(id, chancetoremove);
                        inv2.setItem(13, this.item(new ItemStack(Material.DIAMOND), "§6Current Chance: §c" + Main.pl.chances.get(id), 0));
                    }
                }
            }
        }
    }
    
    public void save(final Inventory inv, final int page) {
        int maplast;
        int mapfirst;
        if (page == 1) {
            maplast = page * 45;
            mapfirst = page * 45 - 45;
        }
        else {
            mapfirst = page * 45 - 45;
            maplast = page * 45;
        }
        int slotcounter = 0;
        for (int i = mapfirst; i < maplast; ++i) {
            Main.pl.itemstoadd.remove(i);
            if (inv.getItem(slotcounter) != null) {
                this.addOnMap(inv.getItem(slotcounter));
            }
            ++slotcounter;
        }
        this.saveItems();
    }
    
    public void openChanceEditor(final Player player, final int itemid, final int currentpage) {
        Main.pl.idediting.put(player, itemid);
        Main.pl.lastpageno.put(player, currentpage);
        final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, "§aEdit the chance");
        inv.setItem(10, this.item(new ItemStack(Material.STAINED_GLASS_PANE), "§aAdd 50", 13));
        inv.setItem(11, this.item(new ItemStack(Material.STAINED_GLASS_PANE), "§aAdd 10", 13));
        inv.setItem(12, this.item(new ItemStack(Material.STAINED_GLASS_PANE), "§aAdd 1", 13));
        inv.setItem(13, this.item(new ItemStack(Material.DIAMOND), "§6Current Chance: §c" + Main.pl.chances.get(itemid), 0));
        inv.setItem(14, this.item(new ItemStack(Material.STAINED_GLASS_PANE), "§aRemove 1", 14));
        inv.setItem(15, this.item(new ItemStack(Material.STAINED_GLASS_PANE), "§aRemove 10", 14));
        inv.setItem(16, this.item(new ItemStack(Material.STAINED_GLASS_PANE), "§aRemove 50", 14));
        inv.setItem(18, this.item(new ItemStack(Material.ARROW), "§cBack", 0));
        for (int i = 0; i < 26; ++i) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, this.item(new ItemStack(Material.STAINED_GLASS_PANE), " ", 8));
            }
        }
        player.openInventory(inv);
    }
    
    @EventHandler
    public void onInvClose(final InventoryCloseEvent e) {
        if (Main.pl.additem.contains(e.getPlayer())) {
            final Player player = (Player)e.getPlayer();
            this.save(e.getInventory(), Main.pl.currentpage.get(player));
            e.getPlayer().sendMessage("ADMIRAL'S CRATE SERVICE CONTENT UPDATED");
            Main.pl.additem.remove(player);
            Main.pl.currentpage.remove(player);
        }
        else if (Main.pl.idediting.containsKey(e.getPlayer())) {
            Main.pl.idediting.remove(e.getPlayer());
            Main.pl.lastpageno.remove(e.getPlayer());
            this.saveItems();
        }
    }
    
    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        if (Main.pl.additem.contains(e.getPlayer())) {
            Main.pl.additem.remove(e.getPlayer());
            Main.pl.currentpage.remove(e.getPlayer());
        }
    }
}
