package me.avankziar.saj.bungee.listener;

import java.util.UUID;

import me.avankziar.ifh.general.statistic.StatisticType;
import me.avankziar.saj.bungee.handler.StatisticHandler;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerChatAndCommandListener implements Listener
{
	@EventHandler(priority = 5)
	public void onChatAndCommand(ChatEvent event)
	{
		if(event.isCancelled())
		{
			return;
		}
		if(!(event.getSender() instanceof ProxiedPlayer))
		{
			return;
		}
		ProxiedPlayer player = (ProxiedPlayer) event.getSender();
		final UUID uuid = player.getUniqueId();
		if(event.isCommand() || event.isProxyCommand())
		{
			StatisticHandler.incrementStatistic(uuid, StatisticType.COMMAND_EXECUTED, 1);
		} else
		{
			final int c = event.getMessage().length();
			final int w = event.getMessage().split(" ").length;
			StatisticHandler.incrementStatistic(uuid, StatisticType.CHAT_CHARACTER, c);
			StatisticHandler.incrementStatistic(uuid, StatisticType.CHAT_WORD, w);
		}
	}
}