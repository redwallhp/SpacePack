package de.freestylecrafter.aio.jetpacks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class CmdExecutor implements CommandExecutor, TabCompleter
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("jetpacks"))
		{
			if (sender instanceof Player)
			{
				if (!PermissionsHelper.canCommandBase((Player) sender))
				{
					sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
					return true;
				}
			}
			
			if (args.length >= 1 && args[0].equalsIgnoreCase("cheat"))
			{
				if (sender instanceof Player)
				{
					if (!PermissionsHelper.canCommandCheat((Player) sender))
					{
						sender.sendMessage(ChatColor.RED + "You don't have permission to cheat jetpacks by command!");
						return true;
					}
				}
				
				if (args.length == 2)
				{
					if (sender instanceof Player)
					{
						Player p = (Player) sender;
						this.executeCheat(sender, p, p, args[1]);
					}
					else
					{
						sender.sendMessage("You are not a player, but you can cheat someone else a jetpack!");
						sender.sendMessage("Usage: /jetpacks cheat <jetpack> <player>");
						return true;
					}
				}
				else if (args.length == 3)
				{
					Player from = (sender instanceof Player ? (Player) sender : null);
					@SuppressWarnings("deprecation")
					Player to = AIOPlugin.getInstance().getServer().getPlayer(args[2]);
					
					this.executeCheat(sender, from, to, args[1]);
				}
				else
				{
					sender.sendMessage("Usage: /jetpacks cheat <jetpack> [<player>]");
				}
			}
			else if (args.length >= 1 && args[0].equalsIgnoreCase("reload"))
			{
				if (sender instanceof Player)
				{
					if (!PermissionsHelper.canCommandReload((Player) sender))
					{
						sender.sendMessage(ChatColor.RED + "You don't have permission to reload the config!");
						return true;
					}
				}
				
				try
				{
					AIOPlugin.getInstance().getConfigManager().reloadConfig();
					if (!AIOPlugin.getInstance().getServer().getOnlinePlayers().isEmpty() && AIOPlugin.getInstance().getJetpackManager() != null)
					{
						for (Player p : AIOPlugin.getInstance().getServer().getOnlinePlayers())
						{
							AIOPlugin.getInstance().getJetpackManager().removeJetpackItemForPlayer(p);
						}
					}
					AIOPlugin.getInstance().getJetpackManager().reloadProfiles(AIOPlugin.getInstance().getConfigManager().getConfiguration());
					if (!AIOPlugin.getInstance().getServer().getOnlinePlayers().isEmpty())
					{
						for (Player p : AIOPlugin.getInstance().getServer().getOnlinePlayers())
						{
							AIOPlugin.getInstance().getJetpackManager().checkJetpackItemForPlayer(p);
						}
					}
					sender.sendMessage(ChatColor.GREEN + "Reloaded AIO-Jetpacks successfully!");
					return true;
				} catch (IOException e)
				{
					AIOPlugin.getInstance().getLogger().warning(e.toString());
					sender.sendMessage(ChatColor.RED + "Reloading failed. Please check console.");
					return true;
				}
			}
			else
			{
				if (args.length >= 1)
					sender.sendMessage(ChatColor.RED + "Invalid command.");
				
				sender.sendMessage(ChatColor.GREEN + "== AIO Jetpacks help ==");
				
				boolean canCommandCheat = (sender instanceof Player ? PermissionsHelper.canCommandCheat((Player) sender) : true);
				boolean canCommandReload = (sender instanceof Player ? PermissionsHelper.canCommandReload((Player) sender) : true);
				if (!canCommandCheat && !canCommandReload)
				{
					sender.sendMessage("There is no help available, because you are not allowed to use any commands.");
				}
				else
				{
					if (canCommandCheat)
						sender.sendMessage("/jetpacks cheat <jetpack> [<player>] Allows you to manually cheat jetpacks");
					
					if (canCommandReload)
						sender.sendMessage("/jetpacks reload Reload the plugin and it's configuration");
				}
				return true;
			}
			return true;
		}
		
		return false;
	}
	
	private void executeCheat(CommandSender sender, Player from, Player to, String profArg)
	{
		if (sender == null || (to == null && from == null))
			return;
		
		if (to != null && to.isOnline())
		{
			Jetpack profile = AIOPlugin.getInstance().getJetpackManager().getProfileByName(profArg);
			if (profile != null)
			{
				if (!(sender instanceof Player) || from == null || PermissionsHelper.canCheatJetpack(profile, from))
				{
					if (to.getInventory().firstEmpty() != -1)
					{
						JetpackItem i = JetpackItem.getNewJetpackItem(profile);
						if (i != null)
						{
							i.setCraftTimestamp();
							to.getInventory().addItem(i.getItem());
							sender.sendMessage("Gave player " + to.getName() + " a jetpack by name #" + profile.getName());
							return;
						}
					}
					else
					{
						sender.sendMessage(to.getName() + " doesn't have enough space in his inventory to cheat him a jetpack by name #" + profile.getName());
						return;
					}
				}
				else
				{
					sender.sendMessage("You don't have permission to cheat someone a jetpack by name #" + profile.getName());
					return;
				}
			}
			else
			{
				sender.sendMessage("Couldn't find jetpack by name #" + profArg);
				return;
			}
		}
		else
		{
			sender.sendMessage("The player " + to.getName() + " is not online!");
			return;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (cmd.getName().equals("jetpacks"))
		{
			if (args.length >= 1 && args[0].equalsIgnoreCase("cheat"))
			{
				if (sender instanceof Player)
				{
					if (!PermissionsHelper.canCommandCheat((Player) sender))
					{
						return null;
					}
				}
				
				if (args.length == 2)
				{
					List<String> list = new ArrayList<String>();
					boolean needCheckInput = !(args[1].isEmpty());
					for (Jetpack j : AIOPlugin.getInstance().getJetpackManager().getProfiles())
					{
						if (sender instanceof Player)
							if (!PermissionsHelper.canCheatJetpack(j, (Player) sender))
								continue;
						
						if (!args[1].isEmpty() && j.getName().startsWith(args[1]))
						{
							list.clear();
							list.add(j.getName());
							needCheckInput = false;
							break;
						}
						
						list.add(j.getName());
					}
					
					if (needCheckInput)
						list.clear();
					
					return list;
				}
				else if (args.length == 3)
				{
					List<String> list = new ArrayList<String>();
					boolean needCheckInput = !(args[2].isEmpty());
					for (Player p : AIOPlugin.getInstance().getServer().getOnlinePlayers())
					{
						if (!args[2].isEmpty() && p.getName().startsWith(args[1].toLowerCase()))
						{
							list.clear();
							list.add(p.getName());
							needCheckInput = false;
							break;
						}
						
						list.add(p.getName());
					}
					
					if (needCheckInput)
						list.clear();
					
					return list;
				}
				
				return null;
			}
			else if (args.length >= 1 && args[0].equalsIgnoreCase("reload"))
			{
				return new ArrayList<String>();
			}
			else if (args.length >= 1 && !args[0].isEmpty() && "reload".startsWith(args[0].toLowerCase()))
			{
				List<String> list = new ArrayList<String>();
				
				boolean canCommandReload = (sender instanceof Player ? PermissionsHelper.canCommandReload((Player) sender) : true);
				
				if (canCommandReload)
					list.add("reload");
				
				return list;
			}
			else if (args.length >= 1 && !args[0].isEmpty() && "cheat".startsWith(args[0].toLowerCase()))
			{
				List<String> list = new ArrayList<String>();
				
				boolean canCommandCheat = (sender instanceof Player ? PermissionsHelper.canCommandCheat((Player) sender) : true);
				
				if (canCommandCheat)
					list.add("cheat");
				
				return list;
			}
			else if (args.length > 1)
			{
				return new ArrayList<String>();
			}
			else
			{
				List<String> list = new ArrayList<String>();
				
				boolean canCommandCheat = (sender instanceof Player ? PermissionsHelper.canCommandCheat((Player) sender) : true);
				boolean canCommandReload = (sender instanceof Player ? PermissionsHelper.canCommandReload((Player) sender) : true);
				
				if (canCommandCheat)
					list.add("cheat");
				
				if (canCommandReload)
					list.add("reload");
				
				return list;
			}
		}
		return null;
	}
}
