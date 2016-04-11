package io.github.redwallhp.spacepack;

import com.sk89q.worldedit.util.YAMLConfiguration;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;


public class WorldGuardHelper {


    private boolean wg = false;


    public WorldGuardHelper() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            wg = true;
        }
    }


    public boolean playerInNoFlyRegion(Player player) {
        if (!wg) return false;
        RegionManager regions = WGBukkit.getRegionManager(player.getWorld());
        LocalPlayer wgPlayer = WGBukkit.getPlugin().wrapPlayer(player);
        List<String> noflyRegions = AIOPlugin.getInstance().getConfigManager().getConfiguration().getStringList("nofly_regions");
        if (regions != null) {
            ApplicableRegionSet applicable = regions.getApplicableRegions(player.getLocation());
            for (ProtectedRegion r : applicable.getRegions()) {
                if (noflyRegions.contains(r.getId())) {
                    return true;
                }
            }
        }
        return false;
    }


}
