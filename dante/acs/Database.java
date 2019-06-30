/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.configuration.InvalidConfigurationException
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.plugin.Plugin
 */
package me.dante.acs;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Database {
    static Database instance = new Database();
    FileConfiguration data;
    File dfile;

    public void setup(Plugin p) {
        if (!p.getDataFolder().exists()) {
            p.getDataFolder().mkdir();
        }
        this.dfile = new File(p.getDataFolder(), "database.yml");
        if (!this.dfile.exists()) {
            try {
                this.dfile.createNewFile();
            }
            catch (IOException var3) {
                Bukkit.getServer().getLogger().severe((Object)ChatColor.RED + "Could not create database.yml!");
            }
        }
        this.data = YamlConfiguration.loadConfiguration((File)this.dfile);
    }

    public FileConfiguration getData() {
        return this.data;
    }

    public void saveData() {
        try {
            this.data.save(this.dfile);
        }
        catch (IOException var2) {
            Bukkit.getServer().getLogger().severe((Object)ChatColor.RED + "Could not save database.yml!");
        }
    }

    public void loadData() {
        try {
            this.data.load(this.dfile);
        }
        catch (IOException | InvalidConfigurationException var2) {
            Bukkit.getServer().getLogger().severe((Object)ChatColor.RED + "Could not load database.yml!");
        }
    }

    public void reloadData() {
        this.data = YamlConfiguration.loadConfiguration((File)this.dfile);
    }
}

