package me.avankziar.sj.spigot.listener;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.sj.general.objects.StatisticEntry;
import me.avankziar.sj.general.objects.StatisticType;
import me.avankziar.sj.spigot.SJ;
import me.avankziar.sj.spigot.hook.WorldGuardHook;

public class StatisticIncrementListener implements Listener
{
	@EventHandler
	public void onPlayerStatisticIncrement(PlayerStatisticIncrementEvent event)
	{
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE
				|| event.getPlayer().getGameMode() == GameMode.SPECTATOR
				|| (SJ.getWorldGuard() ? 
						WorldGuardHook.accecptStatisticChange(event.getPlayer(), event.getPlayer().getLocation())
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
			statisticIncrement(uuid, statistic, mat, ent, i);
		} else
		{
			new BukkitRunnable() 
			{
				@Override
				public void run() 
				{
					statisticIncrement(uuid, statistic, mat, ent, i);
				}
			}.runTaskAsynchronously(SJ.getPlugin());
		}
	}
	
	private void statisticIncrement(UUID uuid, Statistic statistic, Material mat, EntityType ent, int add)
	{
		StatisticType statisticType;
		switch(statistic)
		{
		case PLAY_ONE_MINUTE:
		case TOTAL_WORLD_TIME:
			return;
		default:
			statisticType = StatisticType.getStatisticType(statistic);
		}
		StatisticEntry se = SJ.getPlugin().getMysqlHandler().getData(new StatisticEntry(), 
				"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
				uuid.toString(), statisticType.toString(),
				mat != null ? mat.toString() : (ent != null ? ent.toString() : "null"));
		if(se != null)
		{
			se.setStatisticValue(se.getStatisticValue() + (long) add);
			SJ.getPlugin().getMysqlHandler().updateData(se,
					"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
					uuid.toString(), statisticType.toString(),
					mat != null ? mat.toString() : (ent != null ? ent.toString() : "null"));
		} else
		{
			se = new StatisticEntry(0, uuid, statisticType,
					mat != null ? mat.toString() : (ent != null ? ent.toString() : "null"), add);
			SJ.getPlugin().getMysqlHandler().create(se);
		}
		switch(statisticType)
		{
		default:
			return;
		case DROP_ITEM:
		case PICKUP_ITEM:
		case MINE_BLOCK:
		case USE_ITEM:
		case BREAK_ITEM:
		case CRAFT_ITEM:
		case KILL_ENTITY:
		case ENTITY_KILLED_BY:
			se = SJ.getPlugin().getMysqlHandler().getData(new StatisticEntry(), 
					"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
					uuid.toString(), statisticType.toString(), "null");
			if(se != null)
			{
				se.setStatisticValue(se.getStatisticValue() + (long) add);
				SJ.getPlugin().getMysqlHandler().updateData(se,
						"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
						uuid.toString(), statisticType.toString(), "null");
			} else
			{
				se = new StatisticEntry(0, uuid, statisticType, "null", add);
				SJ.getPlugin().getMysqlHandler().create(se);
			}
		}
	}
}