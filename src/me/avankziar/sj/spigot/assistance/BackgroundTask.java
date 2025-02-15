package me.avankziar.sj.spigot.assistance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.sj.general.objects.AchievementGoal;
import me.avankziar.sj.general.objects.FileAchievementGoal;
import me.avankziar.sj.spigot.SJ;
import me.avankziar.sj.spigot.handler.FileAchievementGoalHandler;

public class BackgroundTask
{
	private static SJ plugin;
	private static HashMap<UUID, ArrayList<FileAchievementGoal>> playerAchievementGoal = new HashMap<>();
	
	public BackgroundTask(SJ plugin)
	{
		BackgroundTask.plugin = plugin;
		initBackgroundTask();
	}
	
	public boolean initBackgroundTask()
	{
		return true;
	}
	
	private void checkAchievementGoal(Player player)
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
				for(Iterator<FileAchievementGoal> iter = l.iterator(); 
						iter.hasNext();)
				{
					FileAchievementGoal favg = iter.next();
					if(SJ.getPlugin().getMysqlHandler().exist(ag, "`player_uuid` = ? AND `achievement_goal_uniquename` = ?", 
							uuid.toString(), favg.getAchievementGoalUniqueName()))
					{
						l.remove(favg);
						continue;
					}
					
				}
				playerAchievementGoal.put(uuid, l);
			}
		}.runTaskTimerAsynchronously(plugin, 10*20L, 
				20L*60*plugin.getYamlHandler().getConfig().getInt("Task.CheckIfPlayerAchievedSomething"));
	}
}
