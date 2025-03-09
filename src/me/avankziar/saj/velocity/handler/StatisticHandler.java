package me.avankziar.saj.velocity.handler;

import java.util.UUID;

import me.avankziar.ifh.general.statistic.StatisticType;
import me.avankziar.saj.general.objects.StatisticEntry;
import me.avankziar.saj.velocity.SAJ;

public class StatisticHandler
{	
	public static void incrementStatistic(UUID uuid, StatisticType statisticType, double add)
	{
		StatisticEntry se = SAJ.getPlugin().getMysqlHandler().getData(new StatisticEntry(), 
				"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
				uuid.toString(), statisticType.toString(), "null");
		if(se != null)
		{
			se.setStatisticValue(se.getStatisticValue() + add);
			SAJ.getPlugin().getMysqlHandler().updateData(se,
					"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
					uuid.toString(), statisticType.toString(), "null");
		} else
		{
			se = new StatisticEntry(0, uuid, statisticType, "null", add);
			SAJ.getPlugin().getMysqlHandler().create(se);
		}
	}
}