package me.avankziar.saj.spigot.handler;

import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Player;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.scheduler.BukkitRunnable;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.avankziar.saj.general.assistance.ChatApiS;
import me.avankziar.saj.general.assistance.TimeHandler;
import me.avankziar.saj.general.objects.AchievementGoal;
import me.avankziar.saj.general.objects.FileAchievementGoal;
import me.avankziar.saj.spigot.SAJ;
import me.avankziar.saj.spigot.assistance.BackgroundTask;
import me.avankziar.saj.spigot.gui.GUIApi;
import me.avankziar.saj.spigot.gui.events.ClickFunction;
import me.avankziar.saj.spigot.gui.objects.ClickFunctionType;
import me.avankziar.saj.spigot.gui.objects.ClickType;
import me.avankziar.saj.spigot.gui.objects.GuiType;
import me.avankziar.saj.spigot.gui.objects.SettingsLevel;

public class GuiHandler
{
	private static SAJ plugin = SAJ.getPlugin();
	public static String PDT_PAGE = "page",
			PDT_OTHER_UUID = "other_uuid",
			PDT_OTHER_NAME = "other_name";
	
	public static void openAchievement(Player player, SettingsLevel settingsLevel, Inventory inv, boolean closeInv, int pagination, UUID other, String othername)
	{
		GuiType gt = GuiType.ACHIEVEMENT;
		GUIApi gui = new GUIApi(SAJ.pluginname, gt.toString(), null, 6, 
				ChatApiS.tlItem(plugin.getYamlHandler().getLang().getString("Gui.Title", "<gold>Achievements of %player%")
						.replace("%player%", othername)));
		openGui(player, gt, gui, closeInv, pagination, other, othername);		
	}
	
	private static void openGui(Player player, GuiType gt, GUIApi gui, boolean closeInv, int pagination, UUID other, String othername)
	{
		boolean fillNotDefineGuiSlots = true;
		Material filler = Material.BLACK_STAINED_GLASS_PANE;
		YamlDocument y = plugin.getYamlHandler().getGui(gt);
		switch(gt)
		{
		case ACHIEVEMENT:
			ArrayList<FileAchievementGoal> list = FileAchievementGoalHandler.getGuiSortedFileAchievement(pagination*45, pagination*45 + 45);
			ArrayList<FileAchievementGoal> notAchieved = BackgroundTask.playerActualReachedAchievementGoal.get(other);
			int j = 0;
			for(FileAchievementGoal favg : list)
			{
				boolean notA = false;
				for(FileAchievementGoal nAfavg : notAchieved)
				{
					if(nAfavg.getAchievementGoalUniqueName().equals(favg.getAchievementGoalUniqueName()))
					{
						notA = true;
						break;
					}
				}
				ItemStack is = null;
				if(notA)
				{
					if(favg.getDisplayItemIfNotAchievedMaterial() == null)
					{
						continue;
					}
					is = new ItemStack(favg.getDisplayItemIfNotAchievedMaterial());
					if(favg.getDisplayItemIfNotAchievedDisplayName() != null)
					{
						ItemMeta im = is.getItemMeta();
						im.setDisplayName(ChatApiS.tlItem(favg.getDisplayItemIfNotAchievedDisplayName().replace("%player%", othername)));
						if(favg.getDisplayItemIfNotAchievedLore() != null && !favg.getDisplayItemIfNotAchievedLore().isEmpty())
						{
							ArrayList<String> l = new ArrayList<>();
							favg.getDisplayItemIfNotAchievedLore().forEach(x -> 
							{
								l.add(ChatApiS.tlItem(x
										.replace("%player%", othername)
										));
							});
							im.setLore(l);
						}
						if(favg.isDisplayItemIfNotAchievedEnchantmentGlintOverride())
						{
							im.setEnchantmentGlintOverride(true);
						}
						is.setItemMeta(im);
					}
					LinkedHashMap<String, Entry<GUIApi.Type, Object>> map = new LinkedHashMap<>();
					gui.add(j, is, SettingsLevel.BASE, true, map);
					j++;
				} else
				{
					AchievementGoal ag = plugin.getMysqlHandler().getData(new AchievementGoal(), 
							"`player_uuid` = ? AND `achievement_goal_uniquename` = ?", 
							other.toString(), favg.getAchievementGoalUniqueName());
					if(favg.getDisplayItemMaterial() == null || ag == null)
					{
						continue;
					}
					is = new ItemStack(favg.getDisplayItemMaterial());
					if(favg.getDisplayItemDisplayName() != null)
					{
						ItemMeta im = is.getItemMeta();
						im.setDisplayName(ChatApiS.tlItem(favg.getDisplayItemDisplayName().replace("%player%", othername)));
						if(favg.getDisplayItemLore() != null && !favg.getDisplayItemLore().isEmpty())
						{
							ArrayList<String> l = new ArrayList<>();
							favg.getDisplayItemLore().forEach(x -> 
							{
								l.add(ChatApiS.tlItem(x
										.replace("%player%", othername)
										.replace("%time%", TimeHandler.getDateTime(ag.getReceivedTime()))
										));
							});
							im.setLore(l);
						}
						if(favg.isDisplayItemEnchantmentGlintOverride())
						{
							im.setEnchantmentGlintOverride(true);
						}
						is.setItemMeta(im);
					}
					LinkedHashMap<String, Entry<GUIApi.Type, Object>> map = new LinkedHashMap<>();
					gui.add(j, is, SettingsLevel.BASE, true, map);
					j++;
				}
			}
			break;
		}
		int allAchiv = FileAchievementGoalHandler.getFileAchievementGoal().size();
		double pagecount = (double) allAchiv / 45.0;
		boolean lastpage = pagecount <= Double.valueOf(pagination+1);
		for(int i = 0; i < 54; i++)
		{
			if(y.get(i+".Material") == null)
			{
				filler(gui, i, filler, fillNotDefineGuiSlots);
				continue;
			}
			ItemStack is = generateItem(y, String.valueOf(i), 0, player);
			switch(gt)
			{
			case ACHIEVEMENT:
				LinkedHashMap<String, Entry<GUIApi.Type, Object>> map = new LinkedHashMap<>();
				ClickFunction[] cf = getClickFunction(y, String.valueOf(i));
				boolean breaks = false;
				for(ClickFunction c : cf)
				{
					if(c.getFunction().equals(ClickFunctionType.PAGE_NEXT.toString()))
					{
						if(lastpage)
						{
							if(pagecount <= 1)
							{
								breaks = true;
								break;
							}
							map.put(PDT_PAGE, new AbstractMap.SimpleEntry<GUIApi.Type, Object>(GUIApi.Type.INTEGER,
									0));
						} else
						{
							map.put(PDT_PAGE, new AbstractMap.SimpleEntry<GUIApi.Type, Object>(GUIApi.Type.INTEGER,
									pagination+1));
						}
					} else if(c.getFunction().equals(ClickFunctionType.PAGE_PAST.toString()))
					{
						if(lastpage)
						{
							if(pagecount <= 1)
							{
								breaks = true;
								break;
							}
							map.put(PDT_PAGE, new AbstractMap.SimpleEntry<GUIApi.Type, Object>(GUIApi.Type.INTEGER,
									pagination-1));
						} else
						{
							map.put(PDT_PAGE, new AbstractMap.SimpleEntry<GUIApi.Type, Object>(GUIApi.Type.INTEGER,
									(int) Math.floor(pagecount)));
						}
					}
					map.put(PDT_OTHER_UUID, new AbstractMap.SimpleEntry<GUIApi.Type, Object>(GUIApi.Type.STRING,
							other.toString()));
					map.put(PDT_OTHER_NAME, new AbstractMap.SimpleEntry<GUIApi.Type, Object>(GUIApi.Type.STRING,
							othername));
				}
				if(breaks)
				{
					filler(gui, i, filler, fillNotDefineGuiSlots);
					break;
				}
				gui.add(i, is, SettingsLevel.BASE, true, map, cf);
			}
		}
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if(closeInv)
				{
					player.closeInventory();
				}
				gui.open(player, gt);
			}
		}.runTask(plugin);
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack generateItem(YamlDocument y, String parentPath, int overrideAmount, Player player)
	{
		if(y.get(parentPath+".Material") == null)
		{
			return null;
		}
		int amount = 1;
		if(y.get(parentPath+".Amount") != null)
		{
			amount = y.getInt(parentPath+".Amount");
		}
		if(overrideAmount > 0)
		{
			amount = overrideAmount;
		}
		Material mat = Material.valueOf(y.getString(parentPath+".Material"));
		ItemStack is = null;
		if(mat == Material.PLAYER_HEAD && y.get(parentPath+".HeadTexture") != null)
		{
			is = getSkull(y.getString(parentPath+".HeadTexture"), amount);
		} else
		{
			is = new ItemStack(mat, amount);
		}
		ItemMeta im = is.getItemMeta();
		if(y.get(parentPath+".Displayname") != null)
		{
			im.setDisplayName(ChatApiS.tlItem(getStringPlaceHolder(player, y.getString(parentPath+".Displayname"), player.getName())));
		}
		if(y.get(parentPath+".CustomModelData") != null)
		{
			im.setCustomModelData(y.getInt(parentPath+".CustomModelData"));
		}
		if(y.get(parentPath+".ItemFlag") != null)
		{
			for(String s : y.getStringList(parentPath+".ItemFlag"))
			{
				try
				{
					im.addItemFlags(ItemFlag.valueOf(s));
				} catch(Exception e)
				{
					continue;
				}
			}
		}
		if(mat == Material.ENCHANTED_BOOK)
		{
			if(im instanceof EnchantmentStorageMeta)
			{
				EnchantmentStorageMeta esm = (EnchantmentStorageMeta) im;
				for(String s : y.getStringList(parentPath+".Enchantment"))
				{
					String[] split = s.split(":");
					if(split.length != 2)
					{
						if(!s.isEmpty() || !s.isBlank())
						{
							SAJ.logger.info("Enchantment Failed! '"+s+"' Lenght != 2 ");
						}
						continue;
					}					
					try
					{
						NamespacedKey nsk = NamespacedKey.minecraft(split[0].toLowerCase());
						Enchantment e = Registry.ENCHANTMENT.get(nsk);
						esm.addStoredEnchant(e, Integer.parseInt(split[1]), true);
					} catch(Exception e)
					{
						SAJ.logger.info("Enchantment Failed! '"+s+"' | "+e.getCause().getClass().getName());
						continue;
					}
				}
				is.setItemMeta(esm);
				im = is.getItemMeta();
			}
		} else
		{
			if(y.get(parentPath+".Enchantment") != null)
			{
				for(String s : y.getStringList(parentPath+".Enchantment"))
				{
					String[] split = s.split(":");
					if(split.length != 2)
					{
						if(!s.isEmpty() || !s.isBlank())
						{
							SAJ.logger.info("Enchantment Failed! '"+s+"' Lenght != 2 ");
						}
						continue;
					}					
					try
					{
						NamespacedKey nsk = NamespacedKey.minecraft(split[0].toLowerCase());
						Enchantment e = Registry.ENCHANTMENT.get(nsk);
						im.addEnchant(e, Integer.parseInt(split[1]), true);
					} catch(Exception e)
					{
						SAJ.logger.info("Enchantment Failed! "+s+" | "+e.getCause().getClass().getName());
						continue;
					}
				}
			}
		}
		if(y.get(parentPath+".Lore") != null)
		{
			ArrayList<String> lo = new ArrayList<>();
			for(String s : y.getStringList(parentPath+".Lore"))
			{
				lo.add(s);
			}
			lo = (ArrayList<String>) getLorePlaceHolder(player, lo, player.getName());
			im.setLore(lo);
		}
		is.setItemMeta(im);
		if(y.get(parentPath+".ArmorMeta.TrimMaterial") != null 
				&& y.get(parentPath+".ArmorMeta.TrimPattern") != null 
				&& im instanceof ArmorMeta)
		{
			ArmorMeta ima = (ArmorMeta) im;
			try
			{
				ima.setTrim(new ArmorTrim(getTrimMaterial(y.getString(parentPath+".ArmorMeta.TrimMaterial")),
						getTrimPattern(y.getString(parentPath+".ArmorMeta.TrimPattern"))));
			} catch(Exception e)
			{
				ima.setTrim(new ArmorTrim(TrimMaterial.IRON, TrimPattern.WILD));
			}
			is.setItemMeta(ima);
			im = is.getItemMeta();
		}
		if(y.get(parentPath+".AxolotlBucket") != null && im instanceof AxolotlBucketMeta)
		{
			AxolotlBucketMeta imm = (AxolotlBucketMeta) im;
			try
			{
				imm.setVariant(Axolotl.Variant.valueOf(y.getString(parentPath+".AxolotlBucket")));
			} catch(Exception e)
			{
				imm.setVariant(Axolotl.Variant.BLUE);
			}
			is.setItemMeta(imm);
			im = is.getItemMeta();
		}
		if(y.get(parentPath+".Banner") != null && im instanceof BannerMeta)
		{
			BannerMeta imm = (BannerMeta) im;
			for(String s : y.getStringList(parentPath+".Banner"))
			{
				String[] split = s.split(";");
				if(split.length != 2)
				{
					continue;
				}
				try
				{
					imm.addPattern(new Pattern(DyeColor.valueOf(split[0]), PatternType.valueOf(split[1])));
				} catch(Exception e)
				{
					continue;
				}
			}
			is.setItemMeta(imm);
			im = is.getItemMeta();
		}
		if(im instanceof BookMeta)
		{
			BookMeta imm = (BookMeta) im;
			try
			{
				if(y.get(parentPath+".Book.Author") != null)
				{
					imm.setAuthor(y.getString(parentPath+".Book.Author"));
				}
				if(y.get(parentPath+".Book.Generation") != null)
				{
					imm.setGeneration(Generation.valueOf(y.getString(parentPath+".Book.Generation")));
				}
				if(y.get(parentPath+".Book.Title") != null)
				{
					imm.setTitle(y.getString(parentPath+".Book.Title"));
				}
				is.setItemMeta(imm);
				im = is.getItemMeta();
			} catch(Exception e){}
		}
		if(y.get(parentPath+".Durability") != null && im instanceof Damageable)
		{
			Damageable imm = (Damageable) im;
			try
			{
				imm.setDamage(getMaxDamage(mat)-y.getInt(parentPath+".Durability"));
			} catch(Exception e)
			{
				imm.setDamage(0);
			}
			is.setItemMeta(imm);
			im = is.getItemMeta();
		}
		if(y.get(parentPath+".LeatherArmor.Color.Red") != null 
				&& y.get(parentPath+".LeatherArmor.Color.Green") != null 
				&& y.get(parentPath+".LeatherArmor.Color.Blue") != null 
				&& im instanceof LeatherArmorMeta)
		{
			LeatherArmorMeta imm = (LeatherArmorMeta) im;
			try
			{
				imm.setColor(Color.fromRGB(
						y.getInt(parentPath+".LeatherArmor.Color.Red"),
						y.getInt(parentPath+".LeatherArmor.Color.Green"),
						y.getInt(parentPath+".LeatherArmor.Color.Blue")));
				is.setItemMeta(imm);
				im = is.getItemMeta();
			} catch(Exception e){}
		}
		if(im instanceof PotionMeta)
		{
			PotionMeta imm = (PotionMeta) im;
			try
			{
				if(y.get(parentPath+".Potion.PotionEffectType") != null 
						&& y.get(parentPath+".Potion.Duration") != null 
						&& y.get(parentPath+".Potion.Amplifier") != null)
				{
					imm.addCustomEffect(new PotionEffect(
							PotionEffectType.getByName(y.getString(parentPath+".Potion.PotionEffectType")),
							y.getInt(parentPath+".Potion.Duration"),
							y.getInt(parentPath+".Potion.Amplifier")), true);
				}
				if(y.get(parentPath+".Potion.Color.Red") != null 
						&& y.get(parentPath+".Potion.Color.Green") != null 
						&& y.get(parentPath+".Potion.Color.Blue") != null)
				{
					imm.setColor(Color.fromRGB(
						y.getInt(parentPath+".Potion.Color.Red"),
						y.getInt(parentPath+".Potion.Color.Green"),
						y.getInt(parentPath+".Potion.Color.Blue")));
				}
				is.setItemMeta(imm);
				im = is.getItemMeta();
			} catch(Exception e){}
		}
		if(y.get(parentPath+".Repairable") != null && im instanceof Repairable)
		{
			Repairable imm = (Repairable) im;
			try
			{
				imm.setRepairCost(y.getInt(parentPath+".Repairable"));
				is.setItemMeta(imm);
				im = is.getItemMeta();
			} catch(Exception e){}
		}
		if(y.get(parentPath+".TropicalFishBucket.BodyColor") != null 
				&& y.get(parentPath+".TropicalFishBucket.Pattern") != null 
				&& y.get(parentPath+".TropicalFishBucket.PatternColor") != null 
				&& im instanceof TropicalFishBucketMeta)
		{
			TropicalFishBucketMeta imm = (TropicalFishBucketMeta) im;
			try
			{
				imm.setBodyColor(DyeColor.valueOf(y.getString(parentPath+".TropicalFishBucket.BodyColor")));
				imm.setPattern(TropicalFish.Pattern.valueOf(y.getString(parentPath+".TropicalFishBucket.Pattern")));
				imm.setPatternColor(DyeColor.valueOf(y.getString(parentPath+".TropicalFishBucket.PatternColor")));
				is.setItemMeta(imm);
				im = is.getItemMeta();
			} catch(Exception e){}
		}
		return is;
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getSkull(String paramString, int amount) 
	{
		ItemStack is = new ItemStack(Material.PLAYER_HEAD, amount);
		SkullMeta paramSkullMeta = (SkullMeta) is.getItemMeta();
	    try 
	    {
	    	UUID uuid = UUID.randomUUID();
	        PlayerProfile playerProfile = Bukkit.createPlayerProfile(uuid, "null");
	        playerProfile.getTextures().setSkin(new URL(paramString));
	        paramSkullMeta.setOwnerProfile(playerProfile);
	    } catch (IllegalArgumentException|SecurityException|java.net.MalformedURLException illegalArgumentException) {
	      illegalArgumentException.printStackTrace();
	    }
	    is.setItemMeta(paramSkullMeta);
	    return is;
	}
	
	public static List<String> getLorePlaceHolder(Player player,
			List<String> lore, String playername)
	{
		List<String> list = new ArrayList<>();
		for(String s : lore)
		{
			String a = getStringPlaceHolder(player, s, playername);
			if(a == null)
			{
				continue;
			}
			a = getStringPlaceHolderVault(player, a, playername);
			if(a == null)
			{
				continue;
			}
			list.add(ChatApiS.tlItem(a));
		}
		return list;
	}
	
	private static String getStringPlaceHolder(Player player,
			String text, String playername)
	{
		String s = text;
		/*if(plot != null)
		{
			if(text.contains(ID))
			{
				s = s.replace(ID, String.valueOf(plot.getID()));
			}
			if(text.contains(OWNER))
			{
				s = s.replace(OWNER, plot.getOwnerUUID() != null ? Bukkit.getOfflinePlayer(plot.getOwnerUUID()).getName() : "/");
			}
			if(text.contains(LENGHT))
			{
				s = s.replace(LENGHT, String.valueOf(plot.getLenght()));
			}
			if(text.contains(WIDTH))
			{
				s = s.replace(WIDTH, String.valueOf(plot.getWidth()));
			}
			if(text.contains(AREA))
			{
				s = s.replace(AREA, String.valueOf(plot.getArea()));
			}
			if(text.contains(PLOT_NAME))
			{
				s = s.replace(PLOT_NAME, String.valueOf(plot.getName()));
			}
		}*/
		return s;
	}
	
	private static String getStringPlaceHolderVault(Player player,
			String text, String playername)
	{
		String s = text;
		/*if(plot != null)
		{
			if(text.contains(BUY_COST))
			{
				s = s.replace(BUY_COST, String.valueOf(plot.getBuyCost()+plugin.getVaultEco().currencyNamePlural()));
			}
			if(text.contains(TAX_COST))
			{
				s = s.replace(TAX_COST, String.valueOf(plot.getTax(PlotHandler.getCostPerBlock())+plugin.getVaultEco().currencyNamePlural()));
			}
		}*/
		return s;
	}
	
	private static int getMaxDamage(Material material)
	{
		int damage = 0;
		switch(material)
		{
		case WOODEN_AXE: //Fallthrough
		case WOODEN_HOE:
		case WOODEN_PICKAXE:
		case WOODEN_SHOVEL:
		case WOODEN_SWORD:
			damage = 60;
			break;
		case LEATHER_BOOTS:
			damage = 65;
			break;
		case LEATHER_CHESTPLATE:
			damage = 80;
			break;
		case LEATHER_HELMET:
			damage = 55;
			break;
		case LEATHER_LEGGINGS:
			damage = 75;
			break;
		case STONE_AXE:
		case STONE_HOE:
		case STONE_PICKAXE:
		case STONE_SHOVEL:
		case STONE_SWORD:
			damage = 132;
			break;
		case CHAINMAIL_BOOTS:
			damage = 196;
			break;
		case CHAINMAIL_CHESTPLATE:
			damage = 241;
			break;
		case CHAINMAIL_HELMET:
			damage = 166;
			break;
		case CHAINMAIL_LEGGINGS:
			damage = 226;
			break;
		case GOLDEN_AXE:
		case GOLDEN_HOE:
		case GOLDEN_PICKAXE:
		case GOLDEN_SHOVEL:
		case GOLDEN_SWORD:
			damage = 33;
			break;
		case GOLDEN_BOOTS:
			damage = 91;
			break;
		case GOLDEN_CHESTPLATE:
			damage = 112;
			break;
		case GOLDEN_HELMET:
			damage = 77;
			break;
		case GOLDEN_LEGGINGS:
			damage = 105;
			break;
		case IRON_AXE:
		case IRON_HOE:
		case IRON_PICKAXE:
		case IRON_SHOVEL:
		case IRON_SWORD:
			damage = 251;
			break;
		case IRON_BOOTS:
			damage = 195;
			break;
		case IRON_CHESTPLATE:
			damage = 40;
			break;
		case IRON_HELMET:
			damage = 165;
			break;
		case IRON_LEGGINGS:
			damage = 225;
			break;
		case DIAMOND_AXE:
		case DIAMOND_HOE:
		case DIAMOND_PICKAXE:
		case DIAMOND_SHOVEL:
		case DIAMOND_SWORD:
			damage = 1562;
			break;
		case DIAMOND_BOOTS:
			damage = 429;
			break;
		case DIAMOND_CHESTPLATE:
			damage = 528;
			break;
		case DIAMOND_HELMET:
			damage = 363;
			break;
		case DIAMOND_LEGGINGS:
			damage = 495;
			break;
		case NETHERITE_AXE:
		case NETHERITE_HOE:
		case NETHERITE_PICKAXE:
		case NETHERITE_SHOVEL:
		case NETHERITE_SWORD:
			damage = 2031;
			break;
		case NETHERITE_BOOTS:
			damage = 482;
			break;
		case NETHERITE_CHESTPLATE:
			damage = 592;
			break;
		case NETHERITE_HELMET:
			damage = 408;
			break;
		case NETHERITE_LEGGINGS:
			damage = 556;
			break;
		case SHIELD:
			damage = 337;
			break;
		case TURTLE_HELMET:
			damage = 276;
			break;
		case TRIDENT:
			damage = 251;
			break;
		case FISHING_ROD:
			damage = 65;
			break;
		case CARROT_ON_A_STICK:
			damage = 26;
			break;
		case WARPED_FUNGUS_ON_A_STICK:
			damage = 100;
			break;
		case ELYTRA:
			damage = 432;
			break;
		case SHEARS:
			damage = 238;
			break;
		case BOW:
			damage = 385;
			break;
		case CROSSBOW:
			damage = 326;
			break;
		case FLINT_AND_STEEL:
			damage = 65;
			break;
		default:
			damage = 0;
			break;
		}
		return damage;
	}
	
	public static TrimMaterial getTrimMaterial(String s)
	{
		switch(s)
		{
		default:
			return TrimMaterial.IRON;
		case "AMETHYST":
			return TrimMaterial.AMETHYST;
		case "COPPER":
			return TrimMaterial.COPPER;
		case "DIAMOND":
			return TrimMaterial.DIAMOND;
		case "EMERALD":
			return TrimMaterial.EMERALD;
		case "GOLD":
			return TrimMaterial.GOLD;
		case "IRON":
			return TrimMaterial.IRON;
		case "LAPIS":
			return TrimMaterial.LAPIS;
		case "NETHERITE":
			return TrimMaterial.NETHERITE;
		case "QUARTZ":
			return TrimMaterial.QUARTZ;
		case "REDSTONE":
			return TrimMaterial.REDSTONE;
		}
	}
	
	public static TrimPattern getTrimPattern(String s)
	{
		switch(s)
		{
		default:
			return TrimPattern.WILD;
		case "COAST":
			return TrimPattern.COAST;
		case "DUNE":
			return TrimPattern.DUNE;
		case "EYE":
			return TrimPattern.EYE;
		case "HOST":
			return TrimPattern.HOST;
		case "RAISER":
			return TrimPattern.RAISER;
		case "RIB":
			return TrimPattern.RIB;
		case "SENTRY":
			return TrimPattern.SENTRY;
		case "SHAPER":
			return TrimPattern.SHAPER;
		case "SILENCE":
			return TrimPattern.SILENCE;
		case "SNOUT":
			return TrimPattern.SNOUT;
		case "SPIRE":
			return TrimPattern.SPIRE;
		case "TIDE":
			return TrimPattern.TIDE;
		case "VEX":
			return TrimPattern.VEX;
		case "WARD":
			return TrimPattern.WARD;
		case "WILD":
			return TrimPattern.WILD;
		}
	}
	
	private static ClickFunction[] getClickFunction(YamlDocument y, String pathBase)
	{
		ArrayList<ClickFunction> ctar = new ArrayList<>();
		List<ClickType> list = new ArrayList<ClickType>(EnumSet.allOf(ClickType.class));
		for(ClickType ct : list)
		{
			if(y.get(pathBase+".ClickFunction."+ct.toString()) == null)
			{
				continue;
			}
			ClickFunctionType cft = null;
			try
			{
				cft = ClickFunctionType.valueOf(y.getString(pathBase+".ClickFunction."+ct.toString()));
			} catch(Exception e)
			{
				continue;
			}
			ctar.add(new ClickFunction(ct, cft));
		}
		return ctar.toArray(new ClickFunction[ctar.size()]);
	}
	
	private static void filler(GUIApi gui, int i, Material mat, boolean fillNotDefineGuiSlots)
	{
		if(fillNotDefineGuiSlots)
		{
			if(gui.isSlotOccupied(i))
			{
				return;
			}
		}
		ItemStack is = new ItemStack(mat, 1);
		ItemMeta im = is.getItemMeta();
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		im.addItemFlags(ItemFlag.HIDE_DESTROYS);
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		im.setDisplayName(ChatApiS.tlItem("<black>."));
		im.setLore(new ArrayList<>());
		is.setItemMeta(im);
		LinkedHashMap<String, Entry<GUIApi.Type, Object>> map = new LinkedHashMap<>();
		gui.add(i, is, SettingsLevel.NOLEVEL, true, map, new ClickFunction[0]);
	}
}