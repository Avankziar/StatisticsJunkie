package me.avankziar.saj.velocity.listener;

import java.util.UUID;

import com.velocitypowered.api.event.Subscribe;

import me.avankziar.ifh.velocity.event.player.PlayerStatisticIncrementEvent;
import me.avankziar.ifh.general.statistic.StatisticType;
import me.avankziar.saj.velocity.handler.StatisticHandler;

public class PlayerStatisticIncrementListener
{
	@Subscribe
	public void onStatisticIncrement(PlayerStatisticIncrementEvent event)
	{
		final UUID uuid = event.getPlayer().getUniqueId();
		final StatisticType statistic = event.getStatisticType();
		double i = event.getIncrementValue();
		StatisticHandler.incrementStatistic(uuid, statistic, i);
	}
}