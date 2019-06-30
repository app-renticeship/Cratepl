package me.dante.acs;

import org.bukkit.*;
import java.util.*;

public class FindAvaliableLocation
{
    static GenerateChest gc;
    static int biggestx;
    static int smallestx;
    static int biggestz;
    static int smallestz;
    static int biggesty;
    static int smallesty;
    static World world;
    static int worldMaxY;
    
    static {
        FindAvaliableLocation.gc = new GenerateChest();
    }
    
    public void init() {
        FindAvaliableLocation.world = Main.pl.getServer().getWorld(Main.pl.getConfig().getString("World"));
        FindAvaliableLocation.worldMaxY = FindAvaliableLocation.world.getMaxHeight() - 1;
        FindAvaliableLocation.biggestx = Main.pl.getConfig().getInt("LargestDistance_X");
        FindAvaliableLocation.smallestx = Main.pl.getConfig().getInt("SmallestDistance_X");
        FindAvaliableLocation.biggestz = Main.pl.getConfig().getInt("LargestDistance_Z");
        FindAvaliableLocation.smallestz = Main.pl.getConfig().getInt("SmallestDistance_Z");
        FindAvaliableLocation.biggesty = Integer.min(Main.pl.getConfig().getInt("LargestDistance_Y"), FindAvaliableLocation.worldMaxY);
        FindAvaliableLocation.smallesty = Integer.min(Main.pl.getConfig().getInt("SmallestDistance_Y"), FindAvaliableLocation.worldMaxY);
        if (FindAvaliableLocation.smallesty > FindAvaliableLocation.biggesty) {
            final int a = FindAvaliableLocation.smallesty;
            FindAvaliableLocation.smallesty = FindAvaliableLocation.biggesty;
            FindAvaliableLocation.biggesty = a;
        }
    }
    
    public static int getRandom(final int no1, final int no2) {
        int max;
        int min;
        if (no1 > no2) {
            max = no1;
            min = no2;
        }
        else {
            max = no2;
            min = no1;
        }
        final Random rand = new Random();
        final int randomNum = rand.nextInt(max - min + 1) + min;
        return randomNum;
    }
    
    public Location FindLocation() {
        if (FindAvaliableLocation.biggesty < 0 || FindAvaliableLocation.smallesty > FindAvaliableLocation.worldMaxY) {
            return null;
        }
        for (int k = 0; k < 100; ++k) {
            final int randomX = getRandom(FindAvaliableLocation.smallestx, FindAvaliableLocation.biggestx);
            final int randomZ = getRandom(FindAvaliableLocation.smallestz, FindAvaliableLocation.biggestz);
            FindAvaliableLocation.world.getHighestBlockYAt(randomX, randomZ);
            final int randomY = getRandom(FindAvaliableLocation.smallesty, FindAvaliableLocation.biggesty);
            final Location loc1 = new Location(FindAvaliableLocation.world, (double)randomX, (double)randomY, (double)randomZ);
            final Location loc2 = loc1.clone();
            final boolean isEmpty2;
            final boolean isEmpty1 = isEmpty2 = this.IsEmptyBlock(loc1);
            for (int n = Math.max(FindAvaliableLocation.biggesty - randomY, randomY - FindAvaliableLocation.smallesty), i = 1; i <= n; ++i) {
                if (loc1.getBlockY() >= FindAvaliableLocation.smallesty) {
                    if (Main.pl.canSpawnChest(loc1)) {
                        return loc1;
                    }
                    loc1.add(0.0, -1.0, 0.0);
                }
                if (loc2.getBlockY() < FindAvaliableLocation.biggesty) {
                    loc2.add(0.0, 1.0, 0.0);
                    if (Main.pl.canSpawnChest(loc2)) {
                        return loc2;
                    }
                }
            }
        }
        return null;
    }
    
    private boolean IsEmptyBlock(final Location location) {
        final Material material = location.getBlock().getType();
        return material.isTransparent();
    }
    
    private boolean CanSpawnChestHere(final Location location, final int deltaZ) {
        final Location loc = location.clone();
        if (deltaZ != 0) {
            loc.add(0.0, (double)deltaZ, 0.0);
        }
        final Material material = loc.getBlock().getType();
        final HashSet filter = new HashSet();
        final boolean filterToExclude = true;
        if (filter != null && filter.size() > 0) {
            if (filterToExclude) {
                if (!filter.contains(material)) {
                    return loc.add(1.0, 0.0, 0.0).getBlock().getType() == Material.CHEST || loc.add(-1.0, 0.0, 1.0).getBlock().getType() == Material.CHEST || loc.add(-1.0, 0.0, -1.0).getBlock().getType() == Material.CHEST || loc.add(1.0, 0.0, -1.0).getBlock().getType() == Material.CHEST;
                }
            }
            else if (filter.contains(material)) {
                return loc.add(1.0, 0.0, 0.0).getBlock().getType() == Material.CHEST || loc.add(-1.0, 0.0, 1.0).getBlock().getType() == Material.CHEST || loc.add(-1.0, 0.0, -1.0).getBlock().getType() == Material.CHEST || loc.add(1.0, 0.0, -1.0).getBlock().getType() == Material.CHEST;
            }
            return false;
        }
        return loc.add(1.0, 0.0, 0.0).getBlock().getType() == Material.CHEST || loc.add(-1.0, 0.0, 1.0).getBlock().getType() == Material.CHEST || loc.add(-1.0, 0.0, -1.0).getBlock().getType() == Material.CHEST || loc.add(1.0, 0.0, -1.0).getBlock().getType() == Material.CHEST;
    }
}
