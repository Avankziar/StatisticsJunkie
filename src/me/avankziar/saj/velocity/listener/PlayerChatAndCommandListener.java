package me.avankziar.saj.velocity.listener;

import java.util.UUID;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.PostCommandInvocationEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;

import me.avankziar.ifh.general.statistic.StatisticType;
import me.avankziar.saj.velocity.handler.StatisticHandler;

public class PlayerChatAndCommandListener
{	
	@Subscribe( priority = 5)
	public void onChat(PlayerChatEvent event)
	{
		final UUID uuid = event.getPlayer().getUniqueId();
		final int c = event.getMessage().length();
		final int w = event.getMessage().split(" ").length;
		StatisticHandler.incrementStatistic(uuid, StatisticType.CHAT_CHARACTER, c);
		StatisticHandler.incrementStatistic(uuid, StatisticType.CHAT_WORD, w);
	}
	
	@Subscribe( priority = 5)
	public void onCommand(PostCommandInvocationEvent event)
	{
		if(!(event.getCommandSource() instanceof Player))
		{
			return;
		}
		Player player = (Player) event.getCommandSource();
		final UUID uuid = player.getUniqueId();
		StatisticHandler.incrementStatistic(uuid, StatisticType.COMMAND_EXECUTED, 1);
	}
}