package me.avankziar.saj.velocity.listener;

import java.util.UUID;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;

import me.avankziar.ifh.general.statistic.StatisticType;
import me.avankziar.saj.general.objects.PlayerData;
import me.avankziar.saj.velocity.SAJ;
import me.avankziar.saj.velocity.handler.StatisticHandler;

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
		StatisticHandler.incrementStatistic(uuid, statisticType, 1);
	}
}