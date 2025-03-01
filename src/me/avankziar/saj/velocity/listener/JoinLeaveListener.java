package me.avankziar.saj.velocity.listener;

import java.util.UUID;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;

import me.avankziar.ifh.general.statistic.StatisticType;
import me.avankziar.saj.general.objects.PlayerData;
import me.avankziar.saj.general.objects.StatisticEntry;
import me.avankziar.saj.velocity.SAJ;

public class JoinLeaveListener
{
	private SAJ plugin;
	
	public JoinLeaveListener(SAJ plugin)
	{
		this.plugin = plugin;
	}
	
	@Subscribe
	public void onPlayerJoin(PlayerChooseInitialServerEvent event)
	{
		final UUID uuid = event.getPlayer().getUniqueId();
		final String name = event.getPlayer().getUsername();
		join(uuid, name);
	}
	
	private void join(UUID uuid, String name)
	{
		PlayerData pd = plugin.getMysqlHandler().getData(new PlayerData(), "`player_uuid` = ?", uuid.toString());
		if(pd != null)
		{
			if(!pd.getName().equals(name))
			{
				pd.setName(name);
				plugin.getMysqlHandler().updateData(pd, "`player_uuid` = ?", uuid.toString());
			}
		} else
		{
			pd = new PlayerData(0, uuid, name);
			plugin.getMysqlHandler().create(pd);
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