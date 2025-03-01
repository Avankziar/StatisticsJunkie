package me.avankziar.saj.spigot.assistance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
	public static ConcurrentHashMap<UUID, ArrayList<FileAchievementGoal>> playerAchievementGoal = new ConcurrentHashMap<>();
	
	public BackgroundTask(SAJ plugin)
	{
		BackgroundTask.plugin = plugin;
		initBackgroundTask();
	}
	
	public boolean initBackgroundTask()
	{
		return true;
	}
	
	public static void checkAchievementGoal(Player player)
	{
		if(player != null)
		{
			if(!playerAchievementGoal.containsKey(player.getUniqueId()))
			{
				ArrayList<FileAchievementGoal> a = new ArrayList<>();
				a.addAll(FileAchievementGoalHandler.getFileAchievementGoal().values());
				playerAchievementGoal.put(player.getUniqueId(), a);
			}
		}
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				if(player == null)
				{
					cancel();
					return;
				}
				final UUID uuid = player.getUniqueId();
				ArrayList<FileAchievementGoal> l = playerAchievementGoal.get(uuid);
				AchievementGoal ag = new AchievementGoal();
				StatisticEntry se = new StatisticEntry();
				Iterator<FileAchievementGoal> iter = l.listIterator();
				while(iter.hasNext())
				{
					FileAchievementGoal favg = iter.next();
					if(SAJ.getPlugin().getMysqlHandler().exist(ag, 
							"`player_uuid` = ? AND `achievement_goal_uniquename` = ?", 
							uuid.toString(), favg.getAchievementGoalUniqueName()))
					{
						iter.remove();;
						continue;
					}
					if(!SAJ.getPlugin().getMysqlHandler().exist(se, 
							"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ? AND `statistic_value` >= ?",
							uuid.toString(), favg.getStatisticType().toString(), favg.getMaterialEntityType(), favg.getStatisticValue()))
					{
						continue;
					}
					AchievementGoal avg = new AchievementGoal(0, uuid, favg.getAchievementGoalUniqueName(), System.currentTimeMillis());
					SAJ.getPlugin().getMysqlHandler().create(avg);
					if(favg.isBroadcast())
					{
						MessageHandler.sendMessage(favg.getBroadcastMessage()
								.toArray(new String[favg.getBroadcastMessage().size()]));
					}
				}
				playerAchievementGoal.put(uuid, l);
			}
		}.runTaskTimerAsynchronously(plugin, 10*20L, 
				20L*60*plugin.getYamlHandler().getConfig().getInt("Task.CheckIfPlayerAchievedSomething", 5));
	}
}
