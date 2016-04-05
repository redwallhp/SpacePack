package de.freestylecrafter.aio.jetpacks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PermissionsHelper
{
	private static final String BASE_PERMISSION = "aio.jetpacks";
	private static final String JETPACKS_PERMISSION_COMMAND_BASE = BASE_PERMISSION + ".command.base";
	private static final String JETPACKS_PERMISSION_COMMAND_CHEAT = BASE_PERMISSION + ".command.cheat";
	private static final String JETPACKS_PERMISSION_COMMAND_RELOAD = BASE_PERMISSION + ".command.reload";
	private static final String JETPACKS_PERMISSION_USE_ALL = BASE_PERMISSION + ".jetpack.use.*";
	private static final String JETPACKS_PERMISSION_USE_VAR = BASE_PERMISSION + ".jetpack.use.%var%";
	private static final String JETPACKS_PERMISSION_CRAFT_ALL = BASE_PERMISSION + ".jetpack.craft.*";
	private static final String JETPACKS_PERMISSION_CRAFT_VAR = BASE_PERMISSION + ".jetpack.craft.%var%";
	private static final String JETPACKS_PERMISSION_CHEAT_ALL = BASE_PERMISSION + ".jetpack.cheat.*";
	private static final String JETPACKS_PERMISSION_CHEAT_VAR = BASE_PERMISSION + ".jetpack.cheat.%var%";
	
	public static final String MESSAGE_NOT_ALLOWED_USE = ChatColor.RED + "You don't have permission to use this jetpack.";
	public static final String MESSAGE_NOT_ALLOWED_CRAFT = ChatColor.RED + "You don't have permission to craft this jetpack.";
	
	public static boolean canUseJetpack(JetpackItem i, Player p)
	{
		if (i == null || p == null)
			return false;
		
		return canUseJetpack(i.getProfile(), p);
	}
	
	public static boolean canUseJetpack(Jetpack j, Player p)
	{
		if (j == null || p == null)
			return false;
		
		// If we don't use permissions, allow to use all jetpacks
		if (!AIOPlugin.getInstance().getConfigManager().getConfiguration().getBoolean("use-permissions"))
			return true;
		
		if (p.hasPermission(JETPACKS_PERMISSION_USE_ALL) || p.hasPermission(JETPACKS_PERMISSION_USE_VAR.replace("%var%", j.getName())))
			return true;
		
		return false;
	}
	
	public static boolean canCraftJetpack(JetpackItem i, Player p)
	{
		if (i == null || p == null)
			return false;
		
		return canCraftJetpack(i.getProfile(), p);
	}
	
	public static boolean canCraftJetpack(Jetpack j, Player p)
	{
		if (j == null || p == null)
			return false;
		
		// If we don't use permissions, allow to craft all jetpacks
		if (!AIOPlugin.getInstance().getConfigManager().getConfiguration().getBoolean("use-permissions"))
			return true;
		
		if (p.hasPermission(JETPACKS_PERMISSION_CRAFT_ALL) || p.hasPermission(JETPACKS_PERMISSION_CRAFT_VAR.replace("%var%", j.getName())))
			return true;
		
		return false;
	}
	
	public static boolean canCheatJetpack(Jetpack j, Player p)
	{
		if (j == null || p == null)
			return false;
		
		// If we don't use permissions, check if player is op
		if (!AIOPlugin.getInstance().getConfigManager().getConfiguration().getBoolean("use-permissions"))
			return p.isOp();
		
		if (p.hasPermission(JETPACKS_PERMISSION_CHEAT_ALL) || p.hasPermission(JETPACKS_PERMISSION_CHEAT_VAR.replace("%var%", j.getName())))
			return true;
		
		return false;
	}
	
	
	public static boolean canCommandBase(Player p)
	{
		if (p == null)
			return false;
		
		// If we don't use permissions, check if player is op
		if (!AIOPlugin.getInstance().getConfigManager().getConfiguration().getBoolean("use-permissions"))
			return p.isOp();
		
		if (p.hasPermission(JETPACKS_PERMISSION_COMMAND_BASE))
			return true;
		
		return false;
	}
	
	public static boolean canCommandCheat(Player p)
	{
		if (p == null)
			return false;
		
		// If we don't use permissions, check if player is op
		if (!AIOPlugin.getInstance().getConfigManager().getConfiguration().getBoolean("use-permissions"))
			return p.isOp();
		
		if (p.hasPermission(JETPACKS_PERMISSION_COMMAND_CHEAT))
			return true;
		
		return false;
	}
	
	public static boolean canCommandReload(Player p)
	{
		if (p == null)
			return false;
		
		// If we don't use permissions, check if player is op
		if (!AIOPlugin.getInstance().getConfigManager().getConfiguration().getBoolean("use-permissions"))
			return p.isOp();
		
		if (p.hasPermission(JETPACKS_PERMISSION_COMMAND_RELOAD))
			return true;
		
		return false;
	}
}
