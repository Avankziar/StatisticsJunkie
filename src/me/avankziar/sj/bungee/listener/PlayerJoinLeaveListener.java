package me.avankziar.sj.bungee.listener;

import java.util.UUID;

import me.avankziar.sj.bungee.SJ;
import me.avankziar.sj.general.objects.PlayerData;
import me.avankziar.sj.general.objects.StatisticEntry;
import me.avankziar.sj.general.objects.StatisticType;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerJoinLeaveListener implements Listener
{
	private SJ plugin;
	
	public PlayerJoinLeaveListener(SJ plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PostLoginEvent event)
	{
		final UUID uuid = event.getPlayer().getUniqueId();
		final String name = event.getPlayer().getName();
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
		StatisticEntry se = SJ.getPlugin().getMysqlHandler().getData(new StatisticEntry(), 
				"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
				uuid.toString(), statisticType.toString(), "null");
		if(se != null)
		{
			se.setStatisticValue(se.getStatisticValue() + 1);
			SJ.getPlugin().getMysqlHandler().updateData(se,
					"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
					uuid.toString(), statisticType.toString(), "null");
		} else
		{
			se = new StatisticEntry(0, uuid, statisticType, "null", 1);
			SJ.getPlugin().getMysqlHandler().create(se);
		}
	}
}