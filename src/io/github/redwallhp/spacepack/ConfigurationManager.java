package io.github.redwallhp.spacepack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigurationManager
{
	private static final String CONFIG_FILE_NAME = "config.yml";
	private static final String HEADER = "SpacePack configuration\nuse-permissions: Enable or disable permissions system\nmessage-equip: Message to be shown on equip, %name% -> Jetpacks name\nmessage-unequip: Message to be shown on unequip, %name% -> Jetpacks name\n" +
		"jetpacks: You can add your own jetpacks here\n<name>: The jetpacks profile name\ndisplayName: Name the jetpack item should have\n" +
		"item: The item that should be used as jetpack\nrecipe: To disable recipe, type []\n" +
		"  - First recipe row, to leave a field empty, write NULL\n  - Second recipe row, to leave a field empty, write NULL\n  - Third recipe row, to leave a field empty, write NULL\n" +
		"enchantments: Here you can add enchantments to the item, write {} to leave empty.\n  <ENCHANTMENT>: Level\n" +
		"infiniteFuel: Set if fuel is infinite\n" +
		"fuel: The fuel item to use\nticksPerFuel: How many ticks you can fly with one piece of fuel\n" +
		"normalSpeed: Normal speed of the jetpack\nfastSpeed: Speed when pressing sprint key + W\nslowSpeed: Speed when sneaking\n" +
		"effects: Here you can add potion effects, write {} to leave empty.\n  <EFFECT>: Effect strength\n";
	
	private File configFile;
	private YamlConfiguration config;
	
	public ConfigurationManager() throws IOException
	{
		this.configFile = new File(AIOPlugin.getInstance().getDataFolder(), CONFIG_FILE_NAME);
		this.reloadConfig();
	}
	
	public void reloadConfig() throws IOException
	{
		if (!this.configFile.exists() || !this.configFile.isFile())
		{
			this.configFile.getParentFile().mkdirs();
			this.configFile.createNewFile();
		}
		
		boolean changedConfig = false;
		this.config = YamlConfiguration.loadConfiguration(this.configFile);
		
		if (this.config.options() == null || this.config.options().header() == null || !this.config.options().header().equals(HEADER))
		{
			this.config.options().header(HEADER);
			
			changedConfig = true;
		}
		if (!this.config.isBoolean("use-permissions"))
		{
			this.config.set("use-permissions", true);
			
			changedConfig = true;
		}

		if (!this.config.isBoolean("sounds")) {
			this.config.set("sounds", true);
			changedConfig = true;
		}
		if (!this.config.isBoolean("trails")) {
			this.config.set("trails", true);
			changedConfig = true;
		}
		if (!this.config.isBoolean("nerf_nofly_fall")) {
			this.config.set("nerf_nofly_fall", false);
			changedConfig = true;
		}
		
		if (!this.config.isConfigurationSection("jetpacks"))
		{
			this.config.set("jetpacks.example.displayName", "Example Jetpack");
			this.config.set("jetpacks.example.item", "IRON_CHESTPLATE");
			List<String> l = new ArrayList<String>();
			l.add("NULL REDSTONE NULL");
			l.add("REDSTONE IRON_CHESTPLATE REDSTONE");
			l.add("FEATHER BLAZE_ROD FEATHER");
			this.config.set("jetpacks.example.recipe", l);
			this.config.set("jetpacks.example.enchantments.PROTECTION_ENVIRONMENTAL", 1);
			this.config.set("jetpacks.example.infiniteFuel", false);
			this.config.set("jetpacks.example.fuel", "COAL");
			this.config.set("jetpacks.example.ticksPerFuel", 640);
			this.config.set("jetpacks.example.normalSpeed", 0.1);
			this.config.set("jetpacks.example.fastSpeed", 0.1);
			this.config.set("jetpacks.example.effects", Collections.emptyMap());
			
			changedConfig = true;
		}

		if (!this.config.isList("nofly_regions")) {
			this.config.set("nofly_regions", Collections.emptyList());
			changedConfig = true;
		}
		
		if (changedConfig)
		{
			this.saveConfiguration();
		}
	}
	
	public YamlConfiguration getConfiguration()
	{
		return this.config;
	}
	
	public void saveConfiguration() throws IOException
	{
		this.config.options().copyHeader(true);
		this.config.save(this.configFile);
	}
}
