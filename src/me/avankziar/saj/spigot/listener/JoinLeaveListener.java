package me.avankziar.saj.spigot.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ifh.general.statistic.StatisticType;
import me.avankziar.saj.general.objects.PlayerData;
import me.avankziar.saj.general.objects.StatisticEntry;
import me.avankziar.saj.spigot.SAJ;
import me.avankziar.saj.spigot.assistance.BackgroundTask;

public class JoinLeaveListener implements Listener
{
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		final UUID uuid = event.getPlayer().getUniqueId();
		final String name = event.getPlayer().getName();
		if(event.isAsynchronous())
		{
			join(uuid, name);
		} else
		{
			new BukkitRunnable() 
			{
				@Override
				public void run() 
				{
					join(uuid, name);
				}
			}.runTaskAsynchronously(SAJ.getPlugin());
		}
	}
	
	private void join(UUID uuid, String name)
	{
		BackgroundTask.checkAchievementGoal(Bukkit.getPlayer(uuid));
		if(SAJ.getPlugin().getYamlHandler().getConfig().getBoolean("IsInstalledOnProxy"))
		{
			return;
		}
		SAJ pl = SAJ.getPlugin();
		PlayerData pd = pl.getMysqlHandler().getData(new PlayerData(), "`player_uuid` = ?", uuid.toString());
		if(pd != null)
		{
			if(!pd.getName().equals(name))
			{
				pd.setName(name);
				pl.getMysqlHandler().updateData(pd, "`player_uuid` = ?", uuid.toString());
			}
		} else
		{
			pd = new PlayerData(0, uuid, name);
			pl.getMysqlHandler().create(pd);
		}
		StatisticType statisticType = StatisticType.LOGIN;
		StatisticEntry se = SAJ.getPlugin().getMysqlHandler().getData(new StatisticEntry(), 
				"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
				uuid.toString(), statisticType.toString(), "null");
		if(se != null)
		{
			se.setStatisticValue(se.getStatisticValue() + 1);
			SAJ.getPlugin().getMysqlHandler().updateData(se,
					"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
					uuid.toString(), statisticType.toString(), "null");
		} else
		{
			se = new StatisticEntry(0, uuid, statisticType, "null", 1);
			SAJ.getPlugin().getMysqlHandler().create(se);
		}
	}
}