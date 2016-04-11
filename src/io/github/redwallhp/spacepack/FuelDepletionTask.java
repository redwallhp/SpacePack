package io.github.redwallhp.spacepack;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
        if (item.getProfile().isInfiniteFuel()) return;
        if (item.getFuel() <= 0) {
            item.reFuel(p.getInventory());
            if (item.getFuel() <= 0) {
                item.setEnabled(false);
                p.setFlying(false);
                p.setAllowFlight(false);
                return;
            }
        }
        if (item.isEnabled()) {
            if (p.isFlying()) {
                handleOutOfFuelMessage(p, item, 20);
                item.useFuel(20);
            } else {
                handleOutOfFuelMessage(p, item, 1);
                item.useFuel(1);
            }
        }
    }


    private void handleOutOfFuelMessage(Player p, JetpackItem item, int subtraction) {
        if (item.getFuel() > 0 && (item.getFuel() - subtraction) <= 0) {
            p.sendMessage(plugin.getLocalizationManager().getConfiguration().getString("message-nofuel"));
        }
    }


}
