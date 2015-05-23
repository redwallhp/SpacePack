package de.freestylecrafter.aio.jetpacks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class JetpackItem
{
	private static final String LORE_PLUGIN_IDENTIFIER = "§r§a> Jetpack";
	private static final String LORE_PROFILE_NAME = "§r§7#%var%";
	private static final String LORE_FUEL_LEVEL = "§r§eFuel: %var%";
	private static final String LORE_FUEL_LEVEL_COSMETIC_MAX = "/%var%";
	private static final String LORE_COSMETIC_FUEL_TYPE = "§r§eFuel type: %var%";
	private static final String LORE_ADMINISTRATIONAL_TIMESTAMP_CRAFTED = "§r§8C: %var%";
	
	private Jetpack jetpackProfile;
	private ItemStack item;
	
	private int fuel = 0;
	private boolean enabled = false;
	private long timestamp_crafted = 0;
	
	private JetpackItem(Jetpack jetpackProfile, ItemStack item)
	{
		this.jetpackProfile = jetpackProfile;
		if (item == null)
		{
			ItemStack s = new ItemStack(jetpackProfile.getItem(), 1);
			ItemMeta m = s.getItemMeta();
			m.setDisplayName(jetpackProfile.getDisplayName());
			s.setItemMeta(m);
			
			if (jetpackProfile.getEnchantments() != null && !jetpackProfile.getEnchantments().isEmpty())
			{
				for (Entry<Enchantment, Integer> kv : jetpackProfile.getEnchantments().entrySet())
				{
					s.addUnsafeEnchantment(kv.getKey(), kv.getValue());
				}
			}
			
			this.item = s;
			
			this.fuel = 0;
			this.timestamp_crafted = 0;
			this.refreshLore();
		}
		else
		{
			this.item = item;
			
			if (this.item.getAmount() != 1)
				this.item.setAmount(1);
			
			this.refreshVariables();
		}
	}
	
	private void refreshVariables()
	{
		String tmp_LORE_FUEL_LEVEL = ChatColor.stripColor(LORE_FUEL_LEVEL);
		String tmp_LORE_ADMINISTRATIONAL_TIMESTAMP_CRAFTED = ChatColor.stripColor(LORE_ADMINISTRATIONAL_TIMESTAMP_CRAFTED);
		for (String lore : this.item.getItemMeta().getLore())
		{
			String strippedLore = ChatColor.stripColor(lore);
			if (strippedLore.startsWith(tmp_LORE_FUEL_LEVEL.replace("%var%", "")) && !this.getProfile().isInfiniteFuel())
			{
				String fuelLore = strippedLore.split("/")[0];
				try {
					this.fuel = Integer.parseInt(fuelLore.split(": ")[1]);
				}
				catch (NumberFormatException e)
				{
					this.fuel = 0;
				}
			}
			else if (strippedLore.startsWith(tmp_LORE_ADMINISTRATIONAL_TIMESTAMP_CRAFTED.replace("%var%", "")))
			{
				try {
					this.timestamp_crafted = Long.parseLong(strippedLore.split(": ")[1]);
					
					if (this.timestamp_crafted == -1)
						this.setCraftTimestamp();
				}
				catch (NumberFormatException e)
				{
					this.timestamp_crafted = 0;
				}
			}
		}
		
		if (this.getProfile().isInfiniteFuel())
			this.fuel = 0;
	}
	
	private void refreshLore()
	{
		List<String> lores = new ArrayList<String>();
		lores.add(LORE_PLUGIN_IDENTIFIER);
		lores.add(LORE_PROFILE_NAME.replace("%var%", this.getProfile().getName()));
		
		if (!this.getProfile().isInfiniteFuel())
		{
			lores.add(LORE_FUEL_LEVEL.replace("%var%", "" + this.fuel) + LORE_FUEL_LEVEL_COSMETIC_MAX.replace("%var%", "" + this.getProfile().getTicksPerFuel()));
			lores.add(LORE_COSMETIC_FUEL_TYPE.replace("%var%", this.getProfile().getFuel().name()));
		}
		
		if (this.timestamp_crafted > 0 || this.timestamp_crafted == -1)
		{
			lores.add(LORE_ADMINISTRATIONAL_TIMESTAMP_CRAFTED.replace("%var%", "" + this.timestamp_crafted));
		}
		
		ItemMeta m = this.getItem().getItemMeta();
		m.setLore(lores);
		this.getItem().setItemMeta(m);
	}
	
	public void setCraftTimestamp()
	{
		this.timestamp_crafted = System.currentTimeMillis();
		this.refreshLore();
	}
	
	public Jetpack getProfile()
	{
		return this.jetpackProfile;
	}
	
	public ItemStack getItem()
	{
		return this.item;
	}
	
	public void setItem(ItemStack item)
	{
		this.item = item;
		this.refreshVariables();
	}
	
	public void setFuel(int fuelTicks)
	{
		this.fuel = (fuelTicks >= 0 ? fuelTicks : 0);
		this.refreshLore();
	}
	
	public int getFuel()
	{
		return this.fuel;
	}
	
	public void useFuel(int amount)
	{
		this.setFuel(this.getFuel() - (amount >= 0 ? amount : 0));
	}
	
	public void reFuel(int amount)
	{
		this.setFuel(this.getFuel() + (amount >= 0 ? amount : 0));
	}
	
	public void reFuel(Inventory i)
	{
		if (this.getProfile().isInfiniteFuel())
			return;
		
		if (i == null || i.getContents() == null || i.getContents().length == 0)
			return;
		
		ItemStack smallestStack = null;
		for (ItemStack item : i.getContents())
		{
			if (item == null || item.getType() == null)
				continue;
			
			if (item.getType().equals(this.getProfile().getFuel()))
			{
				if ((smallestStack == null || smallestStack.getAmount() > item.getAmount()) && (JetpackItem.getJetpackItem(item) == null))
					smallestStack = item;
				
				if (smallestStack != null && smallestStack.getAmount() <= 1)
					break;
			}
		}
		
		if (smallestStack != null)
		{
			int amount = smallestStack.getAmount();
			if (amount - 1 <= 0)
				i.remove(smallestStack);
			else
				smallestStack.setAmount(amount - 1);
			
			this.reFuel(this.getProfile().getTicksPerFuel());
		}
	}
	
	public boolean isEnabled()
	{
		return this.enabled;
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	// Check if item is a valid jetpack with the given profile
	private static boolean isJetpackCorrect(Jetpack jetpackProfile, ItemStack item)
	{
		if (jetpackProfile == null || item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore())
			return false;
		
		boolean hasIdentifier = false;
		boolean hasName = false;
		boolean hasFuel = jetpackProfile.isInfiniteFuel();
		
		String tmp_LORE_PLUGIN_IDENTIFIER = ChatColor.stripColor(LORE_PLUGIN_IDENTIFIER);
		String tmp_LORE_PROFILE_NAME = ChatColor.stripColor(LORE_PROFILE_NAME);
		String tmp_LORE_FUEL_LEVEL = ChatColor.stripColor(LORE_FUEL_LEVEL);
		
		for (String lore : item.getItemMeta().getLore())
		{
			String strippedLore = ChatColor.stripColor(lore);
			
			if (strippedLore.equals(tmp_LORE_PLUGIN_IDENTIFIER))
				hasIdentifier = true;
			
			if (strippedLore.equals(tmp_LORE_PROFILE_NAME.replace("%var%", jetpackProfile.getName())))
				hasName = true;
			
			if (strippedLore.startsWith(tmp_LORE_FUEL_LEVEL.replace("%var%", "")))
				hasFuel = true;
		}
		
		return (hasIdentifier && hasName && hasFuel);
	}
	
	public static boolean isJetpack_recipeCheck(ItemStack item)
	{
		if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore())
			return false;
		
		boolean hasIdentifier = false;
		boolean hasName = false;
		
		String tmp_LORE_PLUGIN_IDENTIFIER = ChatColor.stripColor(LORE_PLUGIN_IDENTIFIER);
		String tmp_LORE_PROFILE_NAME = ChatColor.stripColor(LORE_PROFILE_NAME);
		
		for (String lore : item.getItemMeta().getLore())
		{
			String strippedLore = ChatColor.stripColor(lore);
			
			if (strippedLore.equals(tmp_LORE_PLUGIN_IDENTIFIER))
				hasIdentifier = true;
			
			if (strippedLore.startsWith(tmp_LORE_PROFILE_NAME.replace("%var%", "")))
				hasName = true;
		}
		
		return (hasIdentifier && hasName);
	}
	
	// INSTANCE CREATORS
	
	public static JetpackItem getNewJetpackItem(Jetpack unfinishedProfile)
	{
		if (unfinishedProfile == null)
			return null;
		
		return new JetpackItem(unfinishedProfile, null);
	}
	
	public static JetpackItem getJetpackItem(ItemStack item)
	{
		for (Jetpack jetpackProfile : AIOPlugin.getInstance().getJetpackManager().getProfiles())
		{
			if (JetpackItem.isJetpackCorrect(jetpackProfile, item))
				return new JetpackItem(jetpackProfile, item);
		}
		return null;
	}
}
