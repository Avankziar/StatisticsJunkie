package me.avankziar.sj.spigot.assistance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.sj.general.objects.AchievementGoal;
import me.avankziar.sj.general.objects.FileAchievementGoal;
import me.avankziar.sj.general.objects.StatisticEntry;
import me.avankziar.sj.spigot.SJ;
import me.avankziar.sj.spigot.handler.FileAchievementGoalHandler;
import me.avankziar.sj.spigot.handler.MessageHandler;

public class BackgroundTask
{
	private static SJ plugin;
	public static HashMap<UUID, ArrayList<FileAchievementGoal>> playerAchievementGoal = new HashMap<>();
	
	public BackgroundTask(SJ plugin)
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
				for(Iterator<FileAchievementGoal> iter = l.iterator(); 
						iter.hasNext();)
				{
					FileAchievementGoal favg = iter.next();
					if(SJ.getPlugin().getMysqlHandler().exist(ag, 
							"`player_uuid` = ? AND `achievement_goal_uniquename` = ?", 
							uuid.toString(), favg.getAchievementGoalUniqueName()))
					{
						l.remove(favg);
						continue;
					}
					if(!SJ.getPlugin().getMysqlHandler().exist(se, 
							"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ? AND `statistic_value` >= ?",
							uuid.toString(), favg.getStatisticType().toString(), favg.getMaterialEntityType(), favg.getStatisticValue()))
					{
						continue;
					}
					AchievementGoal avg = new AchievementGoal(0, uuid, favg.getAchievementGoalUniqueName(), System.currentTimeMillis());
					SJ.getPlugin().getMysqlHandler().create(avg);
					if(favg.isBroadcast())
					{
						MessageHandler.sendMessage(favg.getBroadcastMessage()
								.toArray(new String[favg.getBroadcastMessage().size()]));
					}
				}
				playerAchievementGoal.put(uuid, l);
			}
		}.runTaskTimerAsynchronously(plugin, 10*20L, 
				20L*60*plugin.getYamlHandler().getConfig().getInt("Task.CheckIfPlayerAchievedSomething"));
	}
}
