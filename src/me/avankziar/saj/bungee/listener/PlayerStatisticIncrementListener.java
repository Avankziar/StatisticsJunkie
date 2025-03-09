package me.avankziar.saj.bungee.listener;

import java.util.UUID;

import me.avankziar.ifh.bungee.event.player.PlayerStatisticIncrementEvent;
import me.avankziar.ifh.general.statistic.StatisticType;
import me.avankziar.saj.bungee.handler.StatisticHandler;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerStatisticIncrementListener implements Listener
{
	@EventHandler
	public void onStatisticIncrement(PlayerStatisticIncrementEvent event)
	{
		final UUID uuid = event.getPlayer().getUniqueId();
		final StatisticType statistic = event.getStatisticType();
		double i = event.getIncrementValue();
		StatisticHandler.incrementStatistic(uuid, statistic, i);
	}
}
