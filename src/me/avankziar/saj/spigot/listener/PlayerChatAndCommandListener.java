package me.avankziar.saj.spigot.listener;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ifh.general.statistic.StatisticType;
import me.avankziar.saj.spigot.SAJ;
import me.avankziar.saj.spigot.handler.StatisticHandler;

public class PlayerChatAndCommandListener implements Listener
{
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent event)
	{
		if(event.isCancelled())
		{
			return;
		}
		final UUID uuid = event.getPlayer().getUniqueId();
		final long c = event.getMessage().length();
		final long w = event.getMessage().split(" ").length;
		if(event.isAsynchronous())
		{
			StatisticHandler.statisticIncrement(uuid, StatisticType.CHAT_CHARACTER, null, null, c);
			StatisticHandler.statisticIncrement(uuid, StatisticType.CHAT_WORD, null, null, w);
		} else
		{
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					StatisticHandler.statisticIncrement(uuid, StatisticType.CHAT_CHARACTER, null, null, c);
					StatisticHandler.statisticIncrement(uuid, StatisticType.CHAT_WORD, null, null, w);
				}
			}.runTaskAsynchronously(SAJ.getPlugin());
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onCommand(PlayerCommandPreprocessEvent event)
	{
		if(event.isCancelled())
		{
			return;
		}
		final UUID uuid = event.getPlayer().getUniqueId();
		if(event.isAsynchronous())
		{
			StatisticHandler.statisticIncrement(uuid, StatisticType.COMMAND_EXECUTED, null, null, 1);
		} else
		{
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					StatisticHandler.statisticIncrement(uuid, StatisticType.COMMAND_EXECUTED, null, null, 1);
				}
			}.runTaskAsynchronously(SAJ.getPlugin());
		}
	}
}