package io.github.redwallhp.spacepack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class JetpackManager
{
	private List<Jetpack> jetpackProfiles;
	private HashMap<UUID, JetpackItem> activeJetpacks;

	public JetpackManager(YamlConfiguration config)
	{
		this.reloadProfiles(config);
	}

	public void reloadProfiles(YamlConfiguration config)
	{
		if (config == null)
			return;

		// Unregister old recipes
		this.unregisterOldRecipes();

		ArrayList<Jetpack> jetpackProfiles_tmp = new ArrayList<Jetpack>();

		ConfigurationSection jetpackSection = config.getConfigurationSection("jetpacks");

		Set<String> keysUnsorted = jetpackSection.getKeys(false);
		List<String> keys = new ArrayList<String>(keysUnsorted);
		Collections.sort(keys);

		if (!keys.isEmpty())
		{
			for (String subSection : keys)
			{
				try {
					jetpackProfiles_tmp.add(new Jetpack(jetpackSection.getConfigurationSection(subSection)));
					AIOPlugin.getInstance().getLogger().info("Registered jetpack #" + subSection);
				}
				catch (IllegalArgumentException e)
				{
					AIOPlugin.getInstance().getLogger().warning(e.toString());
					AIOPlugin.getInstance().getLogger().info("Skipping registration of jetpack #" + subSection);
				}
				catch (NullPointerException e)
				{
					AIOPlugin.getInstance().getLogger().warning(e.toString());
					AIOPlugin.getInstance().getLogger().info("Skipping registration of jetpack #" + subSection);
				}
			}
		}

		this.activeJetpacks = new HashMap<UUID, JetpackItem>();
		this.jetpackProfiles = jetpackProfiles_tmp;
	}

	public void unregisterOldRecipes()
	{
		Iterator<Recipe> i = AIOPlugin.getInstance().getServer().recipeIterator();
		while (i.hasNext())
		{
			Recipe r = i.next();
			if (JetpackItem.isJetpack_recipeCheck(r.getResult()))
			{
				i.remove();
			}
		}
	}

	public List<Jetpack> getProfiles()
	{
		return this.jetpackProfiles;
	}

	public Jetpack getProfileByName(String name)
	{
		for (Jetpack jetpackProfile : AIOPlugin.getInstance().getJetpackManager().getProfiles())
		{
			if (jetpackProfile.getName().equals(name))
				return jetpackProfile;
		}
		return null;
	}

	public HashMap<UUID, JetpackItem> getActiveJetpackItems()
	{
		return this.activeJetpacks;
	}

	public void checkJetpackItemForPlayer(Player p)
	{
		if (p == null)
			return;

		if (p.getGameMode() != GameMode.CREATIVE && p.getInventory().getChestplate() != null && !p.isDead())
		{
			JetpackItem jetpack = JetpackItem.getJetpackItem(p.getInventory().getChestplate());
			if (jetpack == null || !PermissionsHelper.canUseJetpack(jetpack, p))
			{
				this.removeJetpackItemForPlayer(p);
				return;
			}
			this.addJetpackItemForPlayer(p, jetpack);
		}
		else
		{
			this.removeJetpackItemForPlayer(p);
			return;
		}
	}

	public JetpackItem getJetpackItemForPlayer(Player p)
	{
		if (p != null && this.activeJetpacks.containsKey(p.getUniqueId()))
			return this.activeJetpacks.get(p.getUniqueId());

		return null;
	}

	public void removeJetpackItemForPlayer(Player p)
	{
		if (this.activeJetpacks.containsKey(p.getUniqueId()))
		{
			Jetpack profile = this.activeJetpacks.get(p.getUniqueId()).getProfile();
			this.activeJetpacks.get(p.getUniqueId()).setEnabled(false);
			this.activeJetpacks.remove(p.getUniqueId());

			if (p.getGameMode() != GameMode.CREATIVE && p.getAllowFlight()) {
				p.setAllowFlight(false);
			}

			if (profile.getPotionEffects() != null && !profile.getPotionEffects().isEmpty())
			{
				for (Entry<PotionEffectType, Integer> kv : profile.getPotionEffects().entrySet())
				{
					p.removePotionEffect(kv.getKey());
				}
			}

			String name = (profile == null || profile.getName() == null ? "unknown" :  profile.getName());
			p.sendMessage(AIOPlugin.getInstance().getLocalizationManager().getConfiguration().getString("message-unequip", "").replace("%name%", "#" + name));
		}
	}

	private void addJetpackItemForPlayer(Player p, JetpackItem i)
	{
		if (p == null || i == null)
			return;

		if (this.activeJetpacks.containsKey(p.getUniqueId()))
		{
			JetpackItem current = this.activeJetpacks.get(p.getUniqueId());

			if (current != null && current.getItem().equals(i.getItem()))
			{
				return;
			}
			else {
				this.removeJetpackItemForPlayer(p);
			}
		}

		if (PermissionsHelper.canUseJetpack(i, p))
		{
			this.activeJetpacks.put(p.getUniqueId(), i);

			if (i.getProfile().getPotionEffects() != null && !i.getProfile().getPotionEffects().isEmpty())
			{
				for (Entry<PotionEffectType, Integer> kv : i.getProfile().getPotionEffects().entrySet())
				{
					p.addPotionEffect(new PotionEffect(kv.getKey(), Integer.MAX_VALUE, kv.getValue()), true);
				}
			}

			String name = (i.getProfile() == null || i.getProfile().getName() == null ? "unknown" :  i.getProfile().getName());
			p.sendMessage(AIOPlugin.getInstance().getLocalizationManager().getConfiguration().getString("message-equip", "").replace("%name%", "#" + name));
		}
	}
}