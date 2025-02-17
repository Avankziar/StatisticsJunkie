package me.avankziar.saj.spigot.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.avankziar.saj.general.assistance.ChatApiS;
import me.avankziar.saj.general.database.YamlManager;
import me.avankziar.saj.spigot.SAJ;

public class MessageHandler 
{
	public static void sendMessage(CommandSender sender, String...array)
	{
		Arrays.asList(array).stream().forEach(x -> sender.spigot().sendMessage(ChatApiS.tl(x)));
	}
	
	public static void sendMessage(UUID uuid, String...array)
	{
		Player player = Bukkit.getPlayer(uuid);
		if(player != null)
		{
			Arrays.asList(array).stream().forEach(x -> player.spigot().sendMessage(ChatApiS.tl(x)));
			return;
		}
		if(SAJ.getPlugin().getMtV() != null)
		{
			SAJ.getPlugin().getMtV().sendMessage(uuid, array);
		} else if(SAJ.getPlugin().getMtB() == null)
		{
			ArrayList<String> l = new ArrayList<>();
			for(String s : array)
			{
				l.add(YamlManager.convertMiniMessageToBungee(s));
			}
			SAJ.getPlugin().getMtB().sendMessage(uuid, l.toArray(new String[l.size()]));
		}
	}
	
	public static void sendMessage(Collection<UUID> uuids, String...array)
	{
		uuids.stream().forEach(x -> sendMessage(x, array));
	}
	
	public static void sendMessage(String...array)
	{
		if(SAJ.getPlugin().getMtV() != null)
		{
			SAJ.getPlugin().getMtV().sendMessage(array);
			return;
		} else if(SAJ.getPlugin().getMtV() != null)
		{
			ArrayList<String> l = new ArrayList<>();
			for(String s : array)
			{
				l.add(YamlManager.convertMiniMessageToBungee(s));
			}
			SAJ.getPlugin().getMtB().sendMessage(l.toArray(new String[l.size()]));
		} else
		{
			Bukkit.getOnlinePlayers().stream().forEach(x -> sendMessage(x.getUniqueId(), array));
			return;
		}
	}
}