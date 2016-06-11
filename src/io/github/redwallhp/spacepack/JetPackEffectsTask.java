package io.github.redwallhp.spacepack;


import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;

public class JetPackEffectsTask extends BukkitRunnable {

    private AIOPlugin plugin;
    private boolean sounds;
    private boolean trails;

    public JetPackEffectsTask(AIOPlugin plugin) {
        this.plugin = plugin;
        this.sounds = plugin.getConfig().getBoolean("sounds", true);
        this.trails = plugin.getConfig().getBoolean("trails", true);
    }

    public void run() {
        for (Map.Entry<UUID, JetpackItem> entry : plugin.getJetpackManager().getActiveJetpackItems().entrySet()) {
            Player player = plugin.getServer().getPlayer(entry.getKey());
            JetpackItem item = entry.getValue();
            if (item.isEnabled() && player.isFlying()) {
                Location loc = player.getLocation().subtract(new Vector(0, 1, 0));
                if (trails) player.playEffect(loc, Effect.MOBSPAWNER_FLAMES, null);
                if (sounds) player.playSound(loc, Sound.ENTITY_TNT_PRIMED, 0.1f, 0.5f);
            }
        }
    }

}
