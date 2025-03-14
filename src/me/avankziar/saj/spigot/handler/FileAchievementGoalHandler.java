package me.avankziar.saj.spigot.handler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.avankziar.ifh.general.statistic.StatisticType;
import me.avankziar.saj.general.objects.FileAchievementGoal;
import me.avankziar.saj.spigot.SAJ;

public class FileAchievementGoalHandler 
{
	private static HashMap<String, FileAchievementGoal> fileAchievementGoalMap = new HashMap<>();
	public static HashMap<String, FileAchievementGoal> getFileAchievementGoal()
	{
		return fileAchievementGoalMap;
	}
	
	public static List<Entry<String, FileAchievementGoal>> getGuiSortedFileAchievement()
	{
		List<Map.Entry<String, FileAchievementGoal>> sortedEntries = new ArrayList<>(fileAchievementGoalMap.entrySet());
        sortedEntries.sort(Comparator.comparingInt(entry -> entry.getValue().getGuiSlot()));
        return sortedEntries;
	}
	
	public static ArrayList<FileAchievementGoal> getGuiSortedFileAchievement(int start, int end)
	{
		List<Map.Entry<String, FileAchievementGoal>> sortedEntries = getGuiSortedFileAchievement();
		ArrayList<FileAchievementGoal> sublist = new ArrayList<>();
		for(int i = 0; i < sortedEntries.size(); i++)
		{
			if(i >= start && i < end)
			{
				sublist.add(sortedEntries.get(i).getValue());
			}
		}
		return sublist;
	}
	
	public static void init(boolean reload)
	{
		if(reload)
		{
			fileAchievementGoalMap = new HashMap<>();
		}
		SAJ.logger.info(SAJ.getPlugin().getYamlHandler().getFileAchievementGoal().size()+" AchievementGoals loading...");
		for(YamlDocument y : SAJ.getPlugin().getYamlHandler().getFileAchievementGoal())
		{
			if(y.get("Uniquename") == null
					|| y.get("Displayname") == null
					|| y.get("StatisticType") == null
					|| y.get("MaterialOrEntityType") == null
					|| y.get("GoalValue") == null
					|| y.get("Gui.SlotNumber") == null)
			{
				SAJ.logger.warning("Error at AchievementGoal "+y.getFile().getName()+"! Has not the minium Values!");
			}
			try
			{
				String uniquename = y.getString("Uniquename");
				String displayname = y.getString("Displayname");
				StatisticType statisticType = StatisticType.valueOf(y.getString("StatisticType"));
				String materialEntityType = y.getString("MaterialOrEntityType");
				long statisticValue = y.getLong("GoalValue");
				List<String> executeCommand = List.of();
				if(y.get("Reward.ExecuteCommandAsConsole") != null)
				{
					executeCommand = y.getStringList("Reward.ExecuteCommandAsConsole");
				}
				boolean broadcast = y.getBoolean("Reward.Broadcast", false);
				List<String> broadcastMessage = List.of();
				if(y.get("Reward.BroadcastMessage") != null)
				{
					broadcastMessage = y.getStringList("Reward.BroadcastMessage");
				}
				int guiSlot = y.getInt("Gui.SlotNumber");
				Material displayItemMaterial = Material.valueOf(y.getString("Gui.Item.Material"));
				String displayItemDisplayname = y.getString("Gui.Item.Displayname", null);
				List<String> displayItemLore = List.of();
				if(y.get("Gui.Item.Lore") != null)
				{
					displayItemLore = y.getStringList("Gui.Item.Lore");
				}
				boolean displayItemEnchantmentGlintOverride = y.getBoolean("Gui.Item.EnchantmentGlintOverride", false);
				
				Material displayItemIfNotAchievedMaterial = 
						Material.valueOf(y.getString("Gui.ItemIfNotAchieved.Material"));
				String displayItemIfNotAchievedDisplayname = y.getString("Gui.ItemIfNotAchieved.Displayname", null);
				List<String> displayItemIfNotAchievedLore = List.of();
				if(y.get("Gui.ItemIfNotAchieved.Lore") != null)
				{
					displayItemIfNotAchievedLore = y.getStringList("Gui.ItemIfNotAchieved.Lore");
				}
				boolean displayItemIfNotAchievedEnchantmentGlintOverride = 
						y.getBoolean("Gui.ItemIfNotAchieved.EnchantmentGlintOverride", false);
				FileAchievementGoal favg = new FileAchievementGoal(uniquename, displayname,
						statisticType, materialEntityType, statisticValue, 
						executeCommand, broadcast, broadcastMessage, guiSlot,
						displayItemMaterial,
						displayItemDisplayname,
						displayItemLore,
						displayItemEnchantmentGlintOverride,
						displayItemIfNotAchievedMaterial,
						displayItemIfNotAchievedDisplayname,
						displayItemIfNotAchievedLore,
						displayItemIfNotAchievedEnchantmentGlintOverride);
				fileAchievementGoalMap.put(uniquename, favg);
			} catch(Exception e)
			{
				SAJ.logger.warning("Error at AchievementGoal "+y.getFile().getName()+"!");
			}
		}
		SAJ.logger.info(fileAchievementGoalMap.size()+" AchievementGoals loaded!");
	}
}