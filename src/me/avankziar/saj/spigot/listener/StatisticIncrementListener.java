package me.avankziar.saj.spigot.listener;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ifh.general.statistic.StatisticType;
import me.avankziar.saj.spigot.SAJ;
import me.avankziar.saj.spigot.handler.StatisticHandler;
import me.avankziar.saj.spigot.hook.WorldGuardHook;

public class StatisticIncrementListener implements Listener
{
	@EventHandler
	public void onPlayerStatisticIncrement(PlayerStatisticIncrementEvent event)
	{
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE
				|| event.getPlayer().getGameMode() == GameMode.SPECTATOR
				|| (SAJ.getWorldGuard() ? 
						!WorldGuardHook.accecptStatisticChange(event.getPlayer(), event.getPlayer().getLocation())
						: false)
				)
		{
			return;
		}
		final UUID uuid = event.getPlayer().getUniqueId();
		final Statistic statistic = event.getStatistic();
		final EntityType ent = event.getEntityType();
		final Material mat = event.getMaterial();
		int i = event.getNewValue()-event.getPreviousValue();
		if(event.isAsynchronous())
		{
			StatisticHandler.statisticIncrement(uuid, statistic, mat, ent, i);
		} else
		{
			new BukkitRunnable() 
			{
				@Override
				public void run() 
				{
					StatisticHandler.statisticIncrement(uuid, statistic, mat, ent, i);
				}
			}.runTaskAsynchronously(SAJ.getPlugin());
		}
	}
	
	@EventHandler
	public void onPlayerStatisticIncrementIFH(me.avankziar.ifh.spigot.event.player.PlayerStatisticIncrementEvent event)
	{
		final UUID uuid = event.getPlayer().getUniqueId();
		final StatisticType statistic = event.getStatisticType();
		final EntityType ent = event.getEntityType();
		final Material mat = event.getMaterial();
		long i = event.getIncrementValue();
		if(event.isAsynchronous())
		{
			StatisticHandler.statisticIncrement(uuid, statistic, mat, ent, i);
		} else
		{
			new BukkitRunnable() 
			{
				@Override
				public void run() 
				{
					StatisticHandler.statisticIncrement(uuid, statistic, mat, ent, i);
				}
			}.runTaskAsynchronously(SAJ.getPlugin());
		}
	}
}