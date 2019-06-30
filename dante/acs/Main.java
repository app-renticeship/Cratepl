/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Effect
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.Sound
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.configuration.Configuration
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.FileConfigurationOptions
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginLoader
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.dante.acs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.dante.acs.CommandManager;
import me.dante.acs.Database;
import me.dante.acs.FindAvaliableLocation;
import me.dante.acs.GenerateChest;
import me.dante.acs.ItemAdderGui;
import me.dante.acs.LoadChances;
import me.dante.acs.LootEvent;
import me.dante.acs.RandomChestInfo;
import me.dante.acs.Timer;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main
extends JavaPlugin
implements Listener {
	FileConfiguration config = getConfig();
    public static final int fy = 0;
    public static final int hooky = 0;
	public static final int sthook1 = 1;
	public File cFile;
	FileConfiguration config1;
	static Main pl;
    static GenerateChest gc;
    Database db;
    LoadChances lc;
    Timer timer;
    GenerateChest genc;
    FindAvaliableLocation fal;
    ArrayList<String> commands;
    ArrayList<Player> abletobreak;
    ArrayList<Player> additem;
    static HashMap<Integer, ItemStack> items;
    HashMap<Integer, ItemStack> itemstoadd;
    HashMap<Integer, Integer> chances;
    HashMap<Player, Integer> currentpage;
    HashMap<Player, Integer> idediting;
    HashMap<Player, Integer> lastpageno;
    HashMap<Player, CommandManager.WaitChooseChest> addChestplayers = new HashMap();
    HashMap<Location, Integer> FixedChests = new HashMap();
    
    private int SpawnChestPerTime;
    private int KillChestAfterTime;
    int FixedChestUpdateTimeMin;
    int FixedChestUpdateTimeMax;
    Effect RandomChestEffect;
    Sound RandomChestSound;
    Sound RandomChestOpenSound;
    Effect FixedChestEffect;
    Sound FixedChestSound;
    String MessageOnSpawn;
    String MessageOnLoot;
    String MessageOnKill;
    boolean PluginenabledEnabled;
    MaterialCondition SpawnBlockCondition_Positive;
    MaterialCondition SpawnBlockCondition_Negative;
    MaterialCondition UnderBlockCondition_Positive;
    MaterialCondition UnderBlockCondition_Negative;
    MaterialCondition SideBlockCondition_Positive;
    MaterialCondition SideBlockCondition_Negative;
    HashMap<Location, RandomChestInfo> RandomChests;

    public Main() {
        this.SpawnBlockCondition_Positive = new MaterialCondition(this);
        this.SpawnBlockCondition_Negative = new MaterialCondition(this);
        this.UnderBlockCondition_Positive = new MaterialCondition(this);
        this.UnderBlockCondition_Negative = new MaterialCondition(this);
        this.SideBlockCondition_Positive = new MaterialCondition(this);
        this.SideBlockCondition_Negative = new MaterialCondition(this);
        this.RandomChests = new HashMap();
        this.
        pl = this;
        this.db = Database.instance;
        this.lc = LoadChances.instance;
        this.timer = Timer.instance;
        this.genc = new GenerateChest();
        this.fal = new FindAvaliableLocation();
        this.commands = new ArrayList();
        this.abletobreak = new ArrayList();
        this.additem = new ArrayList();
        this.itemstoadd = new HashMap();
        this.chances = new HashMap();
        this.currentpage = new HashMap();
        this.idediting = new HashMap();
        this.lastpageno = new HashMap();
    }

    private Sound findSound(String str, Sound defaultValue, boolean showError) {
        if (str == null || str.trim().isEmpty()) {
            return defaultValue;
        }
        for (String s : str.split("[ \\t|;,]+")) {
            if (s.equalsIgnoreCase("NONE")) {
                return null;
            }
            try {
                return Sound.valueOf((String)s.trim().toUpperCase());
            }
            catch (Exception exception) {
            }
        }
        if (showError) {
            this.getLogger().log(Level.WARNING, "No sound was found '{0}'", str);
        }
        return defaultValue;
    }

    private Effect findEffect(String str, Effect defaultValue, boolean showError) {
        if (str == null || str.trim().isEmpty()) {
            return defaultValue;
        }
        for (String s : str.split("[ \\s|;,]+")) {
            if (s.equalsIgnoreCase("NONE")) {
                return null;
            }
            try {
                return Effect.valueOf((String)s.toUpperCase());
            }
            catch (Exception exception) {
            }
        }
        if (showError) {
            this.getLogger().log(Level.WARNING, "No effect was found '{0}'", str);
        }
        return defaultValue;
    }

    private void backConfig() {
        try {
            Files.copy(new File(this.getDataFolder(), "config.yml").toPath(), new File(this.getDataFolder().getPath(), "config_" + System.currentTimeMillis() + ".yml").toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private boolean checkConfig() {
        block5 : {
            block4 : {
                FileConfiguration cfg = this.getConfig();
                Configuration org = cfg.getDefaults();
                Set cfgKeys = cfg.getKeys(true);
                Set orgKeys = org.getKeys(true);
                if (orgKeys.stream().anyMatch(x -> !cfgKeys.contains(x))) break block4;
                if (!cfgKeys.stream().anyMatch(x -> !orgKeys.contains(x))) break block5;
            }
            InputStream in = this.getResource("config.yml");
            File outFile = new File(this.getDataFolder(), "config_example.yml");
            FileOutputStream out = null;
            try {
                int len;
                out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while saving default config file", e);
            }
            return false;
        }
        return true;
    }

    private void pause(int seconds) {
        this.getServer().getConsoleSender().sendMessage(String.format("\u00a7fPause %d seconds ...", seconds));
        try {
            TimeUnit.SECONDS.sleep(seconds);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    public void onEnable() {
  cFile = new File(getDataFolder(), "config.yml");
       /* this.saveDefaultConfig();
        if (!this.checkConfig()) {
            this.getPluginLoader().disablePlugin((Plugin)this);
            this.getServer().getConsoleSender().sendMessage("§cACS ACS ACS ACS ACS CONFIG ERROR");
            this.getServer().getConsoleSender().sendMessage("§cACS ACS ACS ACS ACS CONFIG ERROR");
            this.getServer().getConsoleSender().sendMessage("§cACS ACS ACS ACS ACS CONFIG ERROR");
            this.getServer().getConsoleSender().sendMessage("§cACS ACS ACS ACS ACS CONFIG ERROR");
            this.getServer().getConsoleSender().sendMessage("§cCONSULT TO DEVELOPER");
            this.pause(5);
            return;
        }*/
        if (!this.getConfig().getBoolean("EnablePlugin")) {
            this.getPluginLoader().disablePlugin((Plugin)this);
            this.getServer().getConsoleSender().sendMessage("§cACS FIRST TIME STARTUP");
            this.getServer().getConsoleSender().sendMessage("§cENABLE PLUGIN IN CONFIG BEFORE START");
            this.getServer().getConsoleSender().sendMessage("§cACS FIRST TIME STARTUP");
            this.getServer().getConsoleSender().sendMessage("§cENABLE PLUGIN IN CONFIG BEFORE START");
            this.getServer().getConsoleSender().sendMessage("§cACS FIRST TIME STARTUP");
            this.getServer().getConsoleSender().sendMessage("§cENABLE PLUGIN IN CONFIG BEFORE START");
            this.pause(5);
            return;
        }
        this.SpawnBlockCondition_Positive = new MaterialCondition(this, this.getConfig().getString("SpawnBlockCondition"), false);
        this.SpawnBlockCondition_Negative = new MaterialCondition(this, this.getConfig().getString("SpawnBlockCondition"), true);
        this.UnderBlockCondition_Positive = new MaterialCondition(this, this.getConfig().getString("UnderBlockCondition"), false);
        this.UnderBlockCondition_Negative = new MaterialCondition(this, this.getConfig().getString("UnderBlockCondition"), true);
        this.SideBlockCondition_Positive = new MaterialCondition(this, this.getConfig().getString("SideBlockCondition"), false);
        this.SideBlockCondition_Negative = new MaterialCondition(this, this.getConfig().getString("SideBlockCondition"), true);
        this.MessageOnSpawn = this.getConfig().getString("MessageOnSpawn");
        this.MessageOnLoot = this.getConfig().getString("MessageOnLoot");
        this.MessageOnKill = this.getConfig().getString("MessageOnKill");
        if (this.MessageOnSpawn != null && this.MessageOnSpawn.trim().isEmpty()) {
            this.MessageOnSpawn = null;
        }
        if (this.MessageOnLoot != null && this.MessageOnLoot.trim().isEmpty()) {
            this.MessageOnLoot = null;
        }
        if (this.MessageOnKill != null && this.MessageOnKill.trim().isEmpty()) {
            this.MessageOnKill = null;
        }
        this.RandomChestEffect = this.findEffect("MOBSPAWNER_FLAMES", null, false);
        this.RandomChestSound = this.findSound("NONE", null, false);
        this.RandomChestOpenSound = this.findSound("CHEST_OPEN|BLOCK_CHEST_OPEN", null, false);
        this.FixedChestEffect = this.findEffect("EXPLOSION", null, false);
        this.FixedChestSound = this.findSound("DIG_GRASS|BLOCK_GRASS_BREAK", null, false);
        this.RandomChestEffect = this.findEffect(this.getConfig().getString("RandomChestEffect"), this.RandomChestEffect, true);
        this.RandomChestSound = this.findSound(this.getConfig().getString("RandomChestSound"), this.RandomChestSound, true);
        this.RandomChestOpenSound = this.findSound(this.getConfig().getString("RandomChestOpenSound"), this.RandomChestSound, true);
        this.FixedChestEffect = this.findEffect(this.getConfig().getString("FixedChestEffect"), this.FixedChestEffect, true);
        this.FixedChestSound = this.findSound(this.getConfig().getString("FixedChestSound"), this.FixedChestSound, true);
        this.db.setup((Plugin)this);
        this.db.data.options().copyDefaults(true);
        this.SpawnChestPerTime = this.getConfig().getInt("SpawnChestPerTime");
        this.FixedChestUpdateTimeMin = this.getConfig().getInt("FixedChestUpdateTimeMin", 3600);
        this.FixedChestUpdateTimeMax = this.getConfig().getInt("FixedChestUpdateTimeMax", 3600);
        
        this.KillChestAfterTime = pl.getConfig().getInt("KillChestAfterTime");
        gc.GenerateChest(this.SpawnChestPerTime);
        if (!this.db.data.isConfigurationSection("Chests")) {
            this.db.data.createSection("Chests");
            this.db.saveData();
        }
        if (!this.db.data.isConfigurationSection("ItemDatabase")) {
            this.db.data.createSection("ItemDatabase");
            this.db.saveData();
        }
        if (!this.db.data.isConfigurationSection("LocationDatabase")) {
            this.db.data.createSection("LocationDatabase");
            this.db.saveData();
        }
        this.getCommand("acs").setExecutor((CommandExecutor)new CommandManager());
        this.getServer().getPluginManager().registerEvents((Listener)new LootEvent(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new ItemAdderGui(), (Plugin)this);
        this.lc.loaditems();
        this.timer.loadChests();
        this.fal.init();
        this.genc.init2();
        if (this.getConfig().getList("CommandsToExecuteOnLoot") != null) {
            this.commands = (ArrayList)this.getConfig().getStringList("CommandsToExecuteOnLoot");
        }
        if (this.RandomChestEffect != null) {
            this.Particles();
        }
        if (this.getConfig().getBoolean("KillChest")) {
            this.timer.decrease();
        }
        this.PluginenabledEnabled = true;
    }

    public void onDisable() {
        if (!this.PluginenabledEnabled) {
            return;
        }
        this.timer.saveChests();
    }

    private void Particles() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this, () -> {
            for (Map.Entry<Location, RandomChestInfo> e : this.RandomChests.entrySet()) {
                Location location = e.getKey();
                if (this.RandomChestEffect == null) continue;
                location.getWorld().playEffect(location, this.RandomChestEffect, 1);
            }
        }
        , 20, 20);
    }

    void randomizeRandomChestsTimeLeft() {
        int t = Integer.max(pl.getConfig().getInt("KillChestAfterTime"), 0);
        for (Map.Entry<Location, RandomChestInfo> e : this.RandomChests.entrySet()) {
            e.getValue().Time = FindAvaliableLocation.getRandom(0, t);
        }
    }

    void randomizeFixedChestsTimeLeft() {
        int t = Integer.max(this.FixedChestUpdateTimeMax, Integer.max(this.FixedChestUpdateTimeMax, 0));
        for (Map.Entry<Location, Integer> e : this.FixedChests.entrySet()) {
            e.setValue(FindAvaliableLocation.getRandom(0, t));
        }
    }

    boolean checkSide(Material material) {
        return this.SideBlockCondition_Positive.isMatch(material) && this.SideBlockCondition_Negative.isMatch(material) && material != Material.CHEST;
    }

    boolean canSpawnChest(Location location) {
        Block block = location.getBlock();
        if (block instanceof BlockState) {
            return false;
        }
        Material material = block.getType();
        if (!this.SpawnBlockCondition_Positive.isMatch(material)) {
            return false;
        }
        if (!this.SpawnBlockCondition_Negative.isMatch(material)) {
            return false;
        }
        Block block0 = (location = location.clone().add(0.0, -1.0, 0.0)).getBlock();
        material = block0.getType();
        if (!this.UnderBlockCondition_Positive.isMatch(material)) {
            return false;
        }
        if (!this.UnderBlockCondition_Negative.isMatch(material)) {
            return false;
        }
        boolean r = this.checkSide(location.add(1.0, 1.0, 0.0).getBlock().getType()) && this.checkSide(location.add(-1.0, 0.0, 1.0).getBlock().getType()) && this.checkSide(location.add(-1.0, 0.0, -1.0).getBlock().getType()) && this.checkSide(location.add(1.0, 0.0, -1.0).getBlock().getType());
        return r;
    }
 public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
	 
	 
	 if (cmd.getName().equalsIgnoreCase("acsreload")) {
		 reloadConfig();
		 sender.sendMessage(ChatColor.RED + "ADMIRAL'S CRATE SERVICE SUCCESSFULLY RELOADED");
	 }
	 
	 
	return true;
	 
	 
 }
    static {
        gc = new GenerateChest();
        items = new HashMap();
    }

    private class MaterialCondition {
        private boolean empty;
        private boolean negative;
        private boolean Fuel;
        private boolean Record;
        private boolean Occluding;
        private boolean Transparent;
        private boolean Block;
        private boolean Burnable;
        private boolean Edible;
        private boolean Flammable;
        private boolean Solid;
        private boolean Gravity;
        public HashSet<Material> Materials;
        final /* synthetic */ Main this$0;

        MaterialCondition(Main main) {
            this.this$0 = main;
            this.empty = true;
            this.Materials = new HashSet();
        }

        MaterialCondition(Main main, String txt, boolean negative) {
            this.this$0 = main;
            this.empty = true;
            this.Materials = new HashSet();
            if (txt == null) {
                return;
            }
            this.negative = negative;
            HashMap<String, Material> allMaterial = new HashMap<String, Material>(Arrays.stream(Material.values()).collect(Collectors.toMap(x -> x.name().toUpperCase(), x -> x)));
            block24 : for (String word : txt.split("[ \\t;,]+")) {
                String upperWord;
                boolean isWordNegative = word.startsWith("!");
                if (isWordNegative != negative) continue;
                this.empty = false;
                switch (upperWord = (isWordNegative ? word.substring(1) : word).toUpperCase()) {
                    case "_FUEL_": {
                        this.Fuel = true;
                        continue block24;
                    }
                    case "_RECORD_": {
                        this.Record = true;
                        continue block24;
                    }
                    case "_OCCLUDING_": {
                        this.Occluding = true;
                        continue block24;
                    }
                    case "_TRANSPARENT_": {
                        this.Transparent = true;
                        continue block24;
                    }
                    case "_BLOCK_": {
                        this.Block = true;
                        continue block24;
                    }
                    case "_BURNABLE_": {
                        this.Burnable = true;
                        continue block24;
                    }
                    case "_EDIBLE_": {
                        this.Edible = true;
                        continue block24;
                    }
                    case "_FLAMMABLE_": {
                        this.Flammable = true;
                        continue block24;
                    }
                    case "_SOLID_": {
                        this.Solid = true;
                        continue block24;
                    }
                    case "_GRAVITY_": {
                        this.Gravity = true;
                        continue block24;
                    }
                    default: {
                        Material m = allMaterial.get(upperWord);
                        if (m != null) {
                            this.Materials.add(m);
                            continue block24;
                        }
                        Main.pl.getServer().getConsoleSender().sendMessage(String.format("\u00a7cWarning: Unknown material '\u00a7e%s\u00a7c' in condition will be skipped. Look your config.yml", upperWord));
                    }
                }
            }
        }

        boolean isMatch(Material material) {
            return this.empty || this.negative != (this.Fuel && material.isFuel() || this.Record && material.isRecord() || this.Occluding && material.isOccluding() || this.Transparent && material.isTransparent() || this.Block && material.isBlock() || this.Burnable && material.isBurnable() || this.Edible && material.isEdible() || this.Flammable && material.isFlammable() || this.Solid && material.isSolid() || this.Gravity && material.hasGravity() || this.Materials.contains((Object)material));
        }
    }

}

