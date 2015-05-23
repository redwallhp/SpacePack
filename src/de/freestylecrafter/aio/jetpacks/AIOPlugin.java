package de.freestylecrafter.aio.jetpacks;

import java.io.IOException;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class AIOPlugin extends JavaPlugin implements Listener {
	
	private static AIOPlugin instance;
	
	private ConfigurationManager configManager;
	private JetpackManager jetpackManager;
	
	private CmdExecutor cmdExecutor;
	
	@Override
	public void onEnable()
	{
		AIOPlugin.instance = this;
		
		try
		{
			this.configManager = new ConfigurationManager();
		} catch (IOException e)
		{
			this.getLogger().warning(e.toString());
			return;
		}
		
		this.jetpackManager = new JetpackManager(this.getConfigManager().getConfiguration());
		
		this.getServer().getPluginManager().registerEvents(this, this);
		
		this.cmdExecutor = new CmdExecutor();
		
		this.getCommand("jetpacks").setExecutor(this.cmdExecutor);
		this.getCommand("jetpacks").setTabCompleter(this.cmdExecutor);
		
		// Run this after 20 ticks because some plugins are updating variables
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run()
			{
				if (!AIOPlugin.getInstance().getServer().getOnlinePlayers().isEmpty())
				{
					for (Player p : AIOPlugin.getInstance().getServer().getOnlinePlayers())
					{
						AIOPlugin.getInstance().getJetpackManager().checkJetpackItemForPlayer(p);
					}
				}
			}
		}, 20);
	}
	
	@Override
	public void onDisable()
	{
		if (!this.getServer().getOnlinePlayers().isEmpty() && this.getJetpackManager() != null)
		{
			for (Player p : this.getServer().getOnlinePlayers())
			{
				this.getJetpackManager().removeJetpackItemForPlayer(p);
			}
		}
		
		this.getJetpackManager().unregisterOldRecipes();
	}
	
	public ConfigurationManager getConfigManager()
	{
		return this.configManager;
	}
	
	public static AIOPlugin getInstance()
	{
		return AIOPlugin.instance;
	}
	
	public JetpackManager getJetpackManager()
	{
		return this.jetpackManager;
	}
	
	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent e)
	{		
		// Run this after 20 ticks because some plugins are updating variables
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run()
			{
				AIOPlugin.getInstance().getJetpackManager().checkJetpackItemForPlayer(e.getPlayer());
			}
		}, 20);
	}
	
	@EventHandler
	public void onPlayerQuit(final PlayerQuitEvent e)
	{
		this.getJetpackManager().removeJetpackItemForPlayer(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerChangeGameMode(final PlayerGameModeChangeEvent e)
	{
		// Run this next Tick because bukkit and some plugins are changing variables after calling that event
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run()
			{
				AIOPlugin.getInstance().getJetpackManager().checkJetpackItemForPlayer(e.getPlayer());
			}
		});
	}
	
	@EventHandler
	public void onPlayerDeath(final PlayerDeathEvent e)
	{
		// Run this next Tick because bukkit and some plugins are changing variables after calling that event
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run()
			{
				AIOPlugin.getInstance().getJetpackManager().checkJetpackItemForPlayer(e.getEntity());
			}
		});
	}
	
	@EventHandler
	public void onPlayerRespawn(final PlayerRespawnEvent e)
	{
		// Run this after 20 ticks because some plugins are updating variables
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run()
			{
				AIOPlugin.getInstance().getJetpackManager().checkJetpackItemForPlayer(e.getPlayer());
			}
		}, 20);
	}
	
	@EventHandler
	public void onPlayerDamage(final EntityDamageEvent e)
	{
		if (!(e.getEntity() instanceof Player))
			return;
		
		// Run this next Tick because bukkit and some plugins are changing variables after calling that event
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run()
			{
				AIOPlugin.getInstance().getJetpackManager().checkJetpackItemForPlayer((Player) e.getEntity());
			}
		});
	}
	
	@EventHandler
	public void onPlayerChangedWorld(final PlayerChangedWorldEvent e)
	{
		AIOPlugin.getInstance().getJetpackManager().removeJetpackItemForPlayer(e.getPlayer());
		// Run this after 20 ticks because some plugins are updating variables
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run()
			{
				AIOPlugin.getInstance().getJetpackManager().checkJetpackItemForPlayer(e.getPlayer());
			}
		}, 20);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInventoryClick(final InventoryClickEvent e)
	{
		if (!(e.getWhoClicked() instanceof Player))
			return;
		
		JetpackItem currentItem = JetpackItem.getJetpackItem(e.getCurrentItem());
		JetpackItem cursorItem = JetpackItem.getJetpackItem(e.getCursor());
		Player p = (Player) e.getWhoClicked();
		
		if ((e.getAction().equals(InventoryAction.PLACE_ALL) || e.getAction().equals(InventoryAction.PLACE_ONE) || e.getAction().equals(InventoryAction.PLACE_SOME)) && (cursorItem != null && currentItem != null) && !(e.getSlotType().equals(SlotType.ARMOR) && e.getRawSlot() == 6))
		{
			if (cursorItem.getItem().equals(currentItem.getItem()))
			{
				e.setCancelled(true);
			}
		}
		
		if ((e.getClick().equals(ClickType.LEFT) || e.getClick().equals(ClickType.RIGHT)) && cursorItem != null && (e.getSlotType().equals(SlotType.ARMOR) && e.getRawSlot() == 6))
		{
			if (PermissionsHelper.canUseJetpack(cursorItem, p))
			{
				this.getJetpackManager().removeJetpackItemForPlayer(p);
				
				ItemStack currentItemCopy = e.getCurrentItem().clone();
				ItemStack cursorItemCopy = e.getCursor().clone();
				
				e.setCursor(currentItemCopy);
				e.setCurrentItem(cursorItemCopy);
			}
			else
			{
				p.sendMessage(PermissionsHelper.MESSAGE_NOT_ALLOWED_USE);
			}
			e.setCancelled(true);
		}
		
		if (e.getView().getType().equals(InventoryType.CRAFTING))
		{
			if ((e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT)) && currentItem != null && !(e.getSlotType().equals(SlotType.ARMOR) || e.getRawSlot() == 6))
			{
				if (PermissionsHelper.canUseJetpack(currentItem, p))
				{
					this.getJetpackManager().removeJetpackItemForPlayer(p);
					
					ItemStack currentItemCopy = (e.getCurrentItem() != null ? e.getCurrentItem().clone() : null);
					ItemStack chestplateItemCopy = (((Player) e.getWhoClicked()).getInventory().getChestplate() != null ? ((Player) e.getWhoClicked()).getInventory().getChestplate().clone() : null);
					
					e.setCurrentItem(chestplateItemCopy);
					p.getInventory().setChestplate(currentItemCopy);
				}
				else
				{
					p.sendMessage(PermissionsHelper.MESSAGE_NOT_ALLOWED_USE);
				}
				e.setCancelled(true);
			}
		}
		
		if (e.getClick().equals(ClickType.NUMBER_KEY) && (e.getSlotType().equals(SlotType.ARMOR) && e.getRawSlot() == 6))
		{
			JetpackItem hotbarItem = JetpackItem.getJetpackItem(e.getView().getBottomInventory().getItem(e.getHotbarButton()));
			if (hotbarItem != null)
			{
				if (PermissionsHelper.canUseJetpack(hotbarItem, p))
				{
					this.getJetpackManager().removeJetpackItemForPlayer(p);
					
					ItemStack currentItemCopy = e.getCurrentItem().clone();
					ItemStack hotbarItemCopy = e.getView().getBottomInventory().getItem(e.getHotbarButton());
					
					e.getView().getBottomInventory().setItem(e.getHotbarButton(), currentItemCopy);
					e.setCurrentItem(hotbarItemCopy);
				}
				else
				{
					p.sendMessage(PermissionsHelper.MESSAGE_NOT_ALLOWED_USE);
				}
				e.setCancelled(true);
			}
		}
		
		if ((e.getSlotType().equals(SlotType.ARMOR) && e.getRawSlot() == 6 && (!e.getClick().equals(ClickType.MIDDLE))) || currentItem != null)
		{
			// Run this next Tick because bukkit and some plugins are changing variables after calling that event
			this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				@Override
				public void run()
				{
					AIOPlugin.getInstance().getJetpackManager().checkJetpackItemForPlayer((Player) e.getWhoClicked());
				}
			});
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerCraftItem(CraftItemEvent e)
	{
		if (!(e.getWhoClicked() instanceof Player) || !(e.getView().getTopInventory() instanceof CraftingInventory))
			return;
		
		if (!e.getView().getType().equals(InventoryType.CRAFTING) && !e.getView().getType().equals(InventoryType.WORKBENCH))
			return;
		
		JetpackItem currentItem = JetpackItem.getJetpackItem(e.getCurrentItem());
		JetpackItem cursorItem = JetpackItem.getJetpackItem(e.getCursor());
		
		if (currentItem != null && !PermissionsHelper.canCraftJetpack(currentItem, (Player) e.getWhoClicked()))
		{
			((Player) e.getWhoClicked()).sendMessage(PermissionsHelper.MESSAGE_NOT_ALLOWED_CRAFT);
			e.setCancelled(true);
			return;
		}
		
		if ((currentItem != null && cursorItem != null) || (currentItem != null && e.isShiftClick()))
		{
			e.setCancelled(true);
			return;
		}
		
		if (currentItem != null && (e.getCursor() == null || e.getCursor().getType().equals(Material.AIR)))
		{
			JetpackItem currentItemCopy = JetpackItem.getJetpackItem(e.getCurrentItem().clone());
			if (currentItemCopy != null)
			{
				currentItemCopy.setCraftTimestamp();
				e.setCursor(currentItemCopy.getItem());
				CraftingInventory inv = (CraftingInventory) e.getView().getTopInventory();
				
				int maxSlot = (e.getView().getType().equals(InventoryType.CRAFTING) ? 4 : 9);
				for (int i = 1; i <= maxSlot; i++)
				{
					ItemStack is = inv.getItem(i);
					if (is != null && !is.getType().equals(Material.AIR))
					{
						if (is.getAmount() - 1 <= 0)
							inv.remove(is);
						else
							is.setAmount(is.getAmount() - 1);
					}
				}
				((Player) e.getWhoClicked()).updateInventory();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		final Player p = e.getPlayer();
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			JetpackItem inHand = JetpackItem.getJetpackItem(p.getItemInHand());
			if (inHand != null)
			{
				if (!p.getGameMode().equals(GameMode.CREATIVE))
				{
					if (PermissionsHelper.canUseJetpack(inHand, p))
					{
						this.getJetpackManager().removeJetpackItemForPlayer(p);
						
						ItemStack oldChestplate = (p.getInventory().getChestplate() == null ? null : p.getInventory().getChestplate().clone());
						p.getInventory().setChestplate(p.getItemInHand());
						p.setItemInHand(oldChestplate);
						
						p.updateInventory();
						
						// Run this next Tick because bukkit and some plugins are changing variables after calling that event
						this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
							@Override
							public void run()
							{
								AIOPlugin.getInstance().getJetpackManager().checkJetpackItemForPlayer(p);
							}
						});
					}
					else
					{
						p.sendMessage(PermissionsHelper.MESSAGE_NOT_ALLOWED_USE);
					}
				}
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent e)
	{
		this.getJetpackManager().checkJetpackItemForPlayer(e.getPlayer());
		JetpackItem item = this.getJetpackManager().getJetpackItemForPlayer(e.getPlayer());
		if (item != null)
		{
			if (item.isEnabled())
			{
				item.setEnabled(false);
			}
			else
			{
				item.setEnabled(true);
			}
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{
		Player p = e.getPlayer();
		
		this.getJetpackManager().checkJetpackItemForPlayer(p);
		JetpackItem item = this.getJetpackManager().getJetpackItemForPlayer(e.getPlayer());
		if (item != null && item.isEnabled())
		{
			if (!item.getProfile().isInfiniteFuel())
			{
				if (item.getFuel() <= 0)
				{
					item.reFuel(p.getInventory());
					if (item.getFuel() <= 0)
					{
						item.setEnabled(false);
						return;
					}
				}
				item.useFuel(1);
			}
			
			if (p.isSprinting())
				p.setVelocity(p.getLocation().getDirection().multiply(item.getProfile().getFastSpeed()));
			else if (p.isSneaking())
				p.setVelocity(p.getLocation().getDirection().multiply(item.getProfile().getSlowSpeed()));
			else
				p.setVelocity(p.getLocation().getDirection().multiply(item.getProfile().getNormalSpeed()));
		}
	}
}
