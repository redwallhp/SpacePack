package de.freestylecrafter.aio.jetpacks;

import java.io.File;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
public class LocalizationManager
{
	private File configFile;
	private static final String CONFIG_FILE_NAME = "languages.yml";
	private static final String HEADER = "Language configuration file";
	private YamlConfiguration languageConfig;
	
	public LocalizationManager() throws IOException
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
		this.languageConfig = YamlConfiguration.loadConfiguration(this.configFile);
		
		if (this.languageConfig.options() == null || this.languageConfig.options().header() == null || !this.languageConfig.options().header().equals(HEADER))
		{
			this.languageConfig.options().header(HEADER);
			changedConfig = true;
		}
		
		if (!this.languageConfig.isString("message-craft-deny"))
		{
			this.languageConfig.set("message-craft-deny", ChatColor.AQUA + "[Jetpacks] " + ChatColor.RED + "You don't have permission to craft this jetpack.");
			changedConfig = true;
		}
		
		if (!this.languageConfig.isString("message-use-deny"))
		{
			this.languageConfig.set("message-use-deny", ChatColor.AQUA + "[Jetpacks] " + ChatColor.RED + "You don't have permission to use this jetpack.");
			changedConfig = true;
		}
		
		if (!this.languageConfig.isString("message-reload-success"))
		{
			this.languageConfig.set("message-reload-success", ChatColor.GREEN + "Reloaded AIO-Jetpacks successfully!");
			changedConfig = true;
		}
		if (!this.languageConfig.isString("message-reload-fail"))
		{
			this.languageConfig.set("message-reload-fail", ChatColor.RED + "AIO-Jetpacks reload failed! Aborted.");
			changedConfig = true;
		}
		if (!this.languageConfig.isString("message-unknown-command"))
		{
			this.languageConfig.set("message-unknown-command", ChatColor.RED + "Invalid command.");
			changedConfig = true;
		}
		if (!this.languageConfig.isString("message-help-header"))
		{
			this.languageConfig.set("message-help-header", ChatColor.GREEN + "== AIO Jetpacks help ==");
			changedConfig = true;
		}
		if (!this.languageConfig.isString("message-help-deny"))
		{
			this.languageConfig.set("message-help-deny", ChatColor.RED + "You are not allowed to read the help because you don't have the permissions needed to execute a jetpack command.");
			changedConfig = true;
		}
		if (!this.languageConfig.isString("message-help-cheat"))
		{
			this.languageConfig.set("message-help-cheat", "/jetpacks cheat <jetpack> [<player>] (allows you to manually cheat jetpacks)");
			changedConfig = true;
		}
		if (!this.languageConfig.isString("message-help-reload"))
		{
			this.languageConfig.set("message-help-reload", "/jetpacks reload <jetpack> [<player>] (allows you to reload the configuration files)");
			changedConfig = true;
		}
		if (!this.languageConfig.isString("message-give-success"))
		{
			this.languageConfig.set("message-give-success", ChatColor.GREEN + "Gave player %playername% a jetpack by name \"%profilename%\".");
			changedConfig = true;
		}
		if (!this.languageConfig.isString("message-give-fail"))
		{
			this.languageConfig.set("message-give-fail", ChatColor.RED + "%playername% doesn't have enough space in his inventory to cheat him a jetpack.");
			changedConfig = true;
		}
		if (!this.languageConfig.isString("message-give-nopermissions"))
		{
			this.languageConfig.set("message-give-nopermissions", ChatColor.RED + "You do not have permission to cheat a jetpack.");
			changedConfig = true;
		}
		if (!this.languageConfig.isString("message-give-invalidname"))
		{
			this.languageConfig.set("message-give-invalidname", ChatColor.RED + "Couldn't find jetpack by name #%jetpackname%.");
			changedConfig = true;
		}
		if (!this.languageConfig.isString("message-give-offline"))
		{
			this.languageConfig.set("message-give-offline", ChatColor.RED + "The player %playername% is not online.");
			changedConfig = true;
		}
		if (!this.languageConfig.isString("message-reload-nopermissions"))
		{
			this.languageConfig.set("message-reload-nopermissions", ChatColor.RED + "You don't have permission to reload the config!");
			changedConfig = true;
		}
		if (!this.languageConfig.isString("message-cheat-usage"))
		{
			this.languageConfig.set("message-cheat-usage", ChatColor.YELLOW + "Usage: /jetpacks cheat <jetpack> [<player>]");
			changedConfig = true;
		}
		if (!this.languageConfig.isString("message-cheat-console"))
		{
			this.languageConfig.set("message-cheat-console", ChatColor.RED + "You cannot cheat yourself a jetpack from the console, but you can give a jetpack to someone else!");
			changedConfig = true;
		}
		if (!this.languageConfig.isString("message-cheat-nopermissions"))
		{
			this.languageConfig.set("message-cheat-nopermissions", ChatColor.RED + "You don't have permission to cheat yourself a jetpack!");
			changedConfig = true;
		}
		if (!this.languageConfig.isString("message-general-nopermissions"))
		{
			this.languageConfig.set("message-general-nopermissions", ChatColor.RED + "You don't have permission to use this command!");
			changedConfig = true;
		}
		
		if (changedConfig)
		{
			this.saveConfiguration();
		}
	}
	
	public YamlConfiguration getConfiguration()
	{
		return this.languageConfig;
	}
	
	public void saveConfiguration() throws IOException
	{
		this.languageConfig.options().copyHeader(true);
		this.languageConfig.save(this.configFile);
	}
}
