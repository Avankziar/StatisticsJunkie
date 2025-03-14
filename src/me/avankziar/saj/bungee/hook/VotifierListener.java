package me.avankziar.saj.bungee.hook;

import com.vexsoftware.votifier.bungee.events.VotifierEvent;

import me.avankziar.ifh.general.statistic.StatisticType;
import me.avankziar.saj.bungee.SAJ;
import me.avankziar.saj.general.objects.PlayerData;
import me.avankziar.saj.general.objects.StatisticEntry;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class VotifierListener implements Listener
{
	@EventHandler
	public void onVote(VotifierEvent event)
	{
		if(event.getVote() == null || event.getVote().getUsername() == null)
		{
			return;
		}
		final String name = event.getVote().getUsername();
		final String website = event.getVote().getServiceName();
		processVote(name, website);
	}
	
	private void processVote(String playername, String website)
	{
		PlayerData pd = SAJ.getPlugin().getMysqlHandler().getData(new PlayerData(), "`player_name` = ?", playername);
		if(pd == null)
		{
			return;
		}
		StatisticType statisticType = StatisticType.VOTE;
		StatisticEntry se = SAJ.getPlugin().getMysqlHandler().getData(new StatisticEntry(), 
				"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
				pd.getUUID().toString(), statisticType.toString(), "null");
		if(se != null)
		{
			se.setStatisticValue(se.getStatisticValue() + 1);
			SAJ.getPlugin().getMysqlHandler().updateData(se,
					"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
					pd.getUUID().toString(), statisticType.toString(), "null");
		} else
		{
			se = new StatisticEntry(0, pd.getUUID(), statisticType, "null", 1);
			SAJ.getPlugin().getMysqlHandler().create(se);
		}
		se = SAJ.getPlugin().getMysqlHandler().getData(new StatisticEntry(), 
				"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
				pd.getUUID().toString(), statisticType.toString(), website);
		if(se != null)
		{
			se.setStatisticValue(se.getStatisticValue() + 1);
			SAJ.getPlugin().getMysqlHandler().updateData(se,
					"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
					pd.getUUID().toString(), statisticType.toString(), website);
		} else
		{
			se = new StatisticEntry(0, pd.getUUID(), statisticType, website, 1);
			SAJ.getPlugin().getMysqlHandler().create(se);
		}
	}
}