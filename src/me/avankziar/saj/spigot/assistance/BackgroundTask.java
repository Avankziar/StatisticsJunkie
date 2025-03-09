package me.avankziar.saj.spigot.assistance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.saj.general.objects.AchievementGoal;
import me.avankziar.saj.general.objects.FileAchievementGoal;
import me.avankziar.saj.general.objects.StatisticEntry;
import me.avankziar.saj.spigot.SAJ;
import me.avankziar.saj.spigot.handler.FileAchievementGoalHandler;
import me.avankziar.saj.spigot.handler.MessageHandler;

public class BackgroundTask
{
	private static SAJ plugin;
	public static ConcurrentHashMap<UUID, ArrayList<FileAchievementGoal>> playerActualReachedAchievementGoal = new ConcurrentHashMap<>();
	
	public BackgroundTask(SAJ plugin)
	{
		BackgroundTask.plugin = plugin;
		initBackgroundTask();
	}
	
	public boolean initBackgroundTask()
	{
		checkAchievement();
		return true;
	}
	
	public static void joinAchievementGoal(Player player)
	{
		if(player != null)
		{
			if(!playerActualReachedAchievementGoal.containsKey(player.getUniqueId()))
			{
				ArrayList<FileAchievementGoal> a = new ArrayList<>();
				a.addAll(FileAchievementGoalHandler.getFileAchievementGoal().values());
				final UUID uuid = player.getUniqueId();
				ArrayList<AchievementGoal> ag = SAJ.getPlugin().getMysqlHandler().getFullList(new AchievementGoal(),
						"`id` ASC", "`player_uuid` = ?", uuid.toString());
				Iterator<FileAchievementGoal> iter = a.listIterator();
				while(iter.hasNext())
				{
					FileAchievementGoal favg = iter.next();
					Iterator<AchievementGoal> iag = ag.listIterator();
					while(iag.hasNext())
					{
						AchievementGoal achg = iag.next();
						if(achg.getAchievementGoalUniqueName().equals(favg.getAchievementGoalUniqueName()))
						{
							iter.remove();
							iag.remove();
							break;
						}
					}
				}
				playerActualReachedAchievementGoal.put(uuid, a);
			}
		}
	}
	
	private static void checkAchievement()
	{
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				for(Player player : Bukkit.getOnlinePlayers())
				{
					final UUID uuid = player.getUniqueId();
					final String name = player.getName();
					checkAchievement(uuid, name);
				}
			}
		}.runTaskTimerAsynchronously(plugin, 10*20L, 
				20L*60*plugin.getYamlHandler().getConfig().getInt("Task.CheckIfPlayerAchievedSomething", 5));
	}
	
	private static void checkAchievement(final UUID uuid, final String name)
	{
		if (!playerActualReachedAchievementGoal.keySet().stream().anyMatch(key -> key.equals(uuid)))
		{
			return;
		}
		ArrayList<FileAchievementGoal> l = playerActualReachedAchievementGoal.get(uuid);
		StatisticEntry se = new StatisticEntry();
		Iterator<FileAchievementGoal> iter = l.listIterator();
		while(iter.hasNext())
		{
			FileAchievementGoal favg = iter.next();
			if(!SAJ.getPlugin().getMysqlHandler().exist(se, 
					"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ? AND `statistic_value` >= ?",
					uuid.toString(), favg.getStatisticType().toString(), favg.getMaterialEntityType(), favg.getStatisticValue()))
			{
				continue;
			}
			AchievementGoal avg = new AchievementGoal(0, uuid, favg.getAchievementGoalUniqueName(), System.currentTimeMillis());
			SAJ.getPlugin().getMysqlHandler().create(avg);
			iter.remove();
			if(favg.isBroadcast())
			{
				ArrayList<String> arr = new ArrayList<>();
				favg.getBroadcastMessage().forEach(x -> arr.add(x.replace("%player%", name)));
				MessageHandler.sendMessage(arr.toArray(new String[arr.size()]));
			}
		}
		playerActualReachedAchievementGoal.put(uuid, l);
	}
}
