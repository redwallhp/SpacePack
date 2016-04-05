package de.freestylecrafter.aio.jetpacks;


import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;

public class FuelDepletionTask extends BukkitRunnable {


    private AIOPlugin plugin;


    public FuelDepletionTask(AIOPlugin plugin) {
        this.plugin = plugin;
    }


    public void run() {
        for (Map.Entry<UUID, JetpackItem> entry : plugin.getJetpackManager().getActiveJetpackItems().entrySet()) {
            Player player = plugin.getServer().getPlayer(entry.getKey());
            handleFuelDepletion(player, entry.getValue());
        }
    }


    private void handleFuelDepletion(Player p, JetpackItem item) {
        if (item.isEnabled()) {
            if (item.getProfile().isInfiniteFuel()) return;
            if (item.getFuel() <= 0) {
                item.reFuel(p.getInventory());
                if (item.getFuel() <= 0) {
                    item.setEnabled(false);
                    p.setFlying(false);
                    return;
                }
            }
            item.useFuel(1);
        }
    }


}
