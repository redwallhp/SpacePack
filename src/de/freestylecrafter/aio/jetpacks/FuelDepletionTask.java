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
            JetpackItem item = entry.getValue();
            if (item.isEnabled()) {
                // this is where we're going to move fuel depletion to, so it's continuous
            }
        }
    }


}
