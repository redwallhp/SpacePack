package de.freestylecrafter.aio.jetpacks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffectType;

public class Jetpack
{
	private String name;
	private String displayName;
	private Material item;
	private ShapedRecipe recipe;
	private HashMap<Enchantment, Integer> enchantments;
	private boolean infiniteFuel;
	private Material fuel;
	private int ticksPerFuel;
	private double normalSpeed;
	private double fastSpeed;
	private double slowSpeed;
	private HashMap<PotionEffectType, Integer> potionEffects;
	
	public Jetpack(ConfigurationSection section) throws IllegalArgumentException
	{
		if (section != null)
		{
			if (!section.getName().isEmpty())
			{
				boolean needToSaveConfig = false;
				
				if (!section.isString("displayName"))
				{
					section.set("displayName", "Unnamed");
					AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " is missing 'displayName', setting to default.");
					needToSaveConfig = true;
				}
				if (!section.isString("item"))
				{
					section.set("item", "IRON_CHESTPLATE");
					AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " is missing 'item', setting to default.");
					needToSaveConfig = true;
				}
				if (!section.isList("recipe"))
				{
					section.set("recipe", new ArrayList<String>());
					AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " is missing 'recipe', setting to default.");
					needToSaveConfig = true;
				}
				if (!section.isConfigurationSection("enchantments"))
				{
					section.createSection("enchantments");
					AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " is missing section 'enchantments', creating default.");
					needToSaveConfig = true;
				}
				if (!section.isBoolean("infiniteFuel"))
				{
					section.set("infiniteFuel", false);
					AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " is missing 'infiniteFuel', setting to default.");
					needToSaveConfig = true;
				}
				if (!section.getBoolean("infiniteFuel"))
				{
					if (!section.isString("fuel"))
					{
						section.set("fuel", "COAL");
						AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " is missing 'fuel', setting to default.");
						needToSaveConfig = true;
					}
					if (!section.isInt("ticksPerFuel"))
					{
						section.set("ticksPerFuel", 640);
						AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " is missing 'ticksPerFuel', setting to default.");
						needToSaveConfig = true;
					}
				}
				if (!section.isDouble("normalSpeed"))
				{
					section.set("normalSpeed", 1.0);
					AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " is missing 'normalSpeed', setting to default.");
					needToSaveConfig = true;
				}
				if (!section.isDouble("fastSpeed"))
				{
					section.set("fastSpeed", 1.5);
					AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " is missing 'fastSpeed', setting to default.");
					needToSaveConfig = true;
				}
				if (!section.isDouble("slowSpeed"))
				{
					section.set("slowSpeed", 0.5);
					AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " is missing 'slowSpeed', setting to default.");
					needToSaveConfig = true;
				}
				if (!section.isConfigurationSection("effects"))
				{
					section.createSection("effects");
					AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " is missing section 'effects', creating default.");
					needToSaveConfig = true;
				}
				
				this.name = section.getName();
				this.displayName = section.getString("displayName");
				
				this.item = Material.getMaterial(section.getString("item"));
				if (this.item == null)
				{
					AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " has invalid 'item', material not found.");
					throw new IllegalArgumentException();
				}
				
				this.infiniteFuel = section.getBoolean("infiniteFuel");
				
				if (!infiniteFuel)
				{
					this.fuel = Material.getMaterial(section.getString("fuel"));
					if (this.fuel == null)
					{
						AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " has invalid 'fuel', material not found.");
						throw new IllegalArgumentException();
					}
					
					this.ticksPerFuel = (section.getInt("ticksPerFuel") >= 0 ? section.getInt("ticksPerFuel") : 0);
					if (this.ticksPerFuel <= 0)
					{
						AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " has invalid 'ticksPerFuel', must be greater than 0.");
						throw new IllegalArgumentException();
					}
				}
				else
				{
					this.fuel = Material.AIR;
					this.ticksPerFuel = 0;
				}
				
				this.normalSpeed = section.getDouble("normalSpeed");
				if (this.normalSpeed <= 0.0)
				{
					AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " has invalid 'normalSpeed', must be greater than 0.0.");
					throw new IllegalArgumentException();
				}
				this.fastSpeed = section.getDouble("fastSpeed");
				if (this.fastSpeed <= 0.0)
				{
					AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " has invalid 'fastSpeed', must be greater than 0.0.");
					throw new IllegalArgumentException();
				}
				this.slowSpeed = section.getDouble("slowSpeed");
				if (this.slowSpeed <= 0.0)
				{
					AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " has invalid 'slowSpeed', must be greater than 0.0.");
					throw new IllegalArgumentException();
				}
				
				
				this.potionEffects = new HashMap<PotionEffectType, Integer>();
				ConfigurationSection effectSection = section.getConfigurationSection("effects");
				if (!effectSection.getKeys(false).isEmpty())
				{
					for (String key : effectSection.getKeys(false))
					{
						PotionEffectType effectType = PotionEffectType.getByName(key);
						
						if (effectType != null)
						{
							if (effectSection.isInt(key))
							{
								int i = effectSection.getInt(key);
								if (i < 0)
								{
									AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " has invalid potion strength in 'effects', potion strenght cannot be 0.");
									throw new IllegalArgumentException();
								}
								this.potionEffects.put(effectType, i);
							}
						}
						else
						{
							AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " has invalid potion type in 'effects', potion type not found.");
							throw new IllegalArgumentException();
						}
					}
				}
				
				this.enchantments = new HashMap<Enchantment, Integer>();
				ConfigurationSection enchantmentSection = section.getConfigurationSection("enchantments");
				if (!enchantmentSection.getKeys(false).isEmpty())
				{
					for (String key : enchantmentSection.getKeys(false))
					{
						Enchantment enchantType = Enchantment.getByName(key);
						
						if (enchantType != null)
						{
							int i = enchantmentSection.getInt(key);
							if (i < 0)
							{
								AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " has enchantment level in 'in enchantments', enchantment level must be greater than -1.");
								throw new IllegalArgumentException();
							}
							this.enchantments.put(enchantType, i);
						}
						else
						{
							AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " has invalid enchantment type in 'enchantments', enchantment type not found.");
							throw new IllegalArgumentException();
						}
					}
				}
				
				// Do this at the end because we need a nearly completed Profile
				@SuppressWarnings("unchecked")
				List<String> recipeList = (List<String>) section.getList("recipe");
				if (recipeList.size() == 3)
				{
					String[] row1 = recipeList.get(0).split(" ");
					String[] row2 = recipeList.get(1).split(" ");
					String[] row3 = recipeList.get(2).split(" ");
					if (row1.length != 3 || row2.length != 3 || row3.length != 3)
					{
						AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " has invalid crafting recipe in 'recipe', too much or too few items in a row.");
						throw new IllegalArgumentException();
					}
					
					this.recipe = new ShapedRecipe(JetpackItem.getNewJetpackItem(this).getItem()).shape("123", "456", "789");
					
					Material m1 = Material.getMaterial(row1[0]);
					if (m1 != null)
						recipe.setIngredient('1', m1);
					
					Material m2 = Material.getMaterial(row1[1]);
					if (m2 != null)
						recipe.setIngredient('2', m2);
					
					Material m3 = Material.getMaterial(row1[2]);
					if (m3 != null)
						recipe.setIngredient('3', m3);
					
					
					Material m4 = Material.getMaterial(row2[0]);
					if (m4 != null)
						recipe.setIngredient('4', m4);
					
					Material m5 = Material.getMaterial(row2[1]);
					if (m5 != null)
						recipe.setIngredient('5', m5);
					
					Material m6 = Material.getMaterial(row2[2]);
					if (m6 != null)
						recipe.setIngredient('6', m6);
					
					
					Material m7 = Material.getMaterial(row3[0]);
					if (m7 != null)
						recipe.setIngredient('7', m7);
					
					Material m8 = Material.getMaterial(row3[1]);
					if (m8 != null)
						recipe.setIngredient('8', m8);
					
					Material m9 = Material.getMaterial(row3[2]);
					if (m9 != null)
						recipe.setIngredient('9', m9);
					
					AIOPlugin.getInstance().getServer().addRecipe(this.recipe);
				}
				else if (recipeList.size() == 0)
				{
					// This is okay, do nothing
				}
				else
				{
					AIOPlugin.getInstance().getLogger().info("Jetpack profile #" + section.getName() + " has invalid crafting recipe in 'recipe', too much or too few rows.");
					throw new IllegalArgumentException();
				}
				
				if (needToSaveConfig)
				{
					try
					{
						AIOPlugin.getInstance().getConfigManager().saveConfiguration();
					} catch (IOException e)
					{
						AIOPlugin.getInstance().getLogger().warning(e.toString());
					}
				}
			}
			else
			{
				AIOPlugin.getInstance().getLogger().info("Invalid call. No or invalid ConfigurationSection given.");
				throw new NullPointerException();
			}
		}
		else
		{
			AIOPlugin.getInstance().getLogger().info("Invalid call. No or invalid ConfigurationSection given.");
			throw new NullPointerException();
		}
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getDisplayName()
	{
		return this.displayName;
	}
	
	public Material getItem()
	{
		return this.item;
	}
	
	public ShapedRecipe getRecipe()
	{
		return this.recipe;
	}
	
	public HashMap<Enchantment, Integer> getEnchantments()
	{
		return this.enchantments;
	}
	
	public boolean isInfiniteFuel()
	{
		return this.infiniteFuel;
	}
	
	public Material getFuel()
	{
		return this.fuel;
	}
	
	public int getTicksPerFuel()
	{
		return this.ticksPerFuel;
	}
	
	public double getNormalSpeed()
	{
		return this.normalSpeed;
	}
	
	public double getFastSpeed()
	{
		return this.fastSpeed;
	}
	
	public double getSlowSpeed()
	{
		return this.slowSpeed;
	}
	
	public HashMap<PotionEffectType, Integer> getPotionEffects()
	{
		return this.potionEffects;
	}
}
