package me.avankziar.saj.general.ifh;

import java.util.UUID;

import me.avankziar.ifh.general.statistic.Statistic;
import me.avankziar.ifh.general.statistic.StatisticType;
import me.avankziar.saj.general.database.MysqlBaseHandler;
import me.avankziar.saj.general.objects.StatisticEntry;
import me.avankziar.saj.spigot.SAJ;

public class StatisticProvider implements Statistic
{
	private MysqlBaseHandler mysql;
	
	public StatisticProvider(MysqlBaseHandler mysql)
	{
		this.mysql = mysql;
	}

	@Override
	public Double getStatistic(UUID uuid, StatisticType statisticType)
	{
		StatisticEntry se = mysql.getData(new StatisticEntry(), 
				"`player_uuid` = ? AND `statistic_type` = ?",
				uuid.toString(), statisticType.toString());
		return se != null ? se.getStatisticValue() : null;
	}

	@Override
	public Double getStatistic(UUID uuid, StatisticType statisticType, String materialOrEntityType)
	{
		if(materialOrEntityType == null)
		{
			return getStatistic(uuid, statisticType);
		}
		StatisticEntry se = mysql.getData(new StatisticEntry(), 
				"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?",
				uuid.toString(), statisticType.toString(), materialOrEntityType);
		return se != null ? se.getStatisticValue() : null;
	}

	@Override
	public void addStatisticValue(UUID uuid, StatisticType statisticType, String materialOrEntityType, double value)
	{
		StatisticEntry se = SAJ.getPlugin().getMysqlHandler().getData(new StatisticEntry(), 
				"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
				uuid.toString(), statisticType.toString(), materialOrEntityType);
		if(se != null)
		{
			se.setStatisticValue(se.getStatisticValue() + value);
			SAJ.getPlugin().getMysqlHandler().updateData(se,
					"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
					uuid.toString(), statisticType.toString(), materialOrEntityType);
		} else
		{
			se = new StatisticEntry(0, uuid, statisticType, materialOrEntityType, value);
			SAJ.getPlugin().getMysqlHandler().create(se);
		}
	}
}