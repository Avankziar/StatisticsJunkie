package me.avankziar.saj.spigot.cmd.achievement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.saj.general.cmdtree.ArgumentConstructor;
import me.avankziar.saj.general.objects.AchievementGoal;
import me.avankziar.saj.general.objects.PlayerData;
import me.avankziar.saj.spigot.SAJ;
import me.avankziar.saj.spigot.ModifierValueEntry.Bypass;
import me.avankziar.saj.spigot.cmdtree.ArgumentModule;
import me.avankziar.saj.spigot.handler.FileAchievementGoalHandler;
import me.avankziar.saj.spigot.handler.MessageHandler;

public class ARG_Info extends ArgumentModule
{
	private SAJ plugin;
	
	public ARG_Info(ArgumentConstructor argumentConstructor) 
	{
		super(argumentConstructor);
		this.plugin = SAJ.getPlugin();
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException 
	{
		Player player = (Player) sender;
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				task(player, args);
			}
		}.runTaskAsynchronously(plugin);
	}
	
	private void task(Player player, String[] args)
	{
		String othername = player.getName();
		UUID other = player.getUniqueId();
		if(args.length >= 2)
		{
			if(!player.hasPermission(Bypass.get(Bypass.Permission.ACHIEVEMENT_INFO_OTHER)))
			{
				MessageHandler.sendMessage(player, plugin.getYamlHandler().getLang().getString("NoPermission"));
				return;
			}
			othername = args[1];
			PlayerData otherpd = plugin.getMysqlHandler().getData(new PlayerData(), "`player_name` = ?", othername);
			if(otherpd == null)
			{
				MessageHandler.sendMessage(player, plugin.getYamlHandler().getLang().getString("NoPlayerExist"));
				return;
			}
		}
		int allAchievements = FileAchievementGoalHandler.getFileAchievementGoal().size();
		int receivedAchievements = plugin.getMysqlHandler().getCount(new AchievementGoal(),
				"`player_uuid` = ?", other.toString());
		ArrayList<Integer> l = plugin.getMysqlHandler()
				.getMostCountWhereColumnAreSame(new AchievementGoal(), "player_uuid");
		int place = 0;
		int count = 0;
		int i = 1;
		for(Integer c : l)
		{
			if(place == 0 && receivedAchievements == c)
			{
				place = i;
			}
			count += c;
			i++;
		}
		int average = l.size() > 0 ? count/l.size() : 0;
		ArrayList<String> msg = new ArrayList<>();
		msg.add(plugin.getYamlHandler().getLang().getString("Achievement.Info.Headline").replace("%player%", othername));
		msg.add(plugin.getYamlHandler().getLang().getString("Achievement.Info.AchievedVersusTotal")
				.replace("%total%", String.valueOf(allAchievements))
				.replace("%achieved%", String.valueOf(receivedAchievements))
				.replace("%place%", place > 0 ? String.valueOf(place) : "/"));
		msg.add(plugin.getYamlHandler().getLang().getString("Achievement.Info.PlayerCount")
				.replace("%achievementtotal%", String.valueOf(count))
				.replace("%playercount%", String.valueOf(l.size())));
		msg.add(plugin.getYamlHandler().getLang().getString("Achievement.Info.AverageAndMedian")
				.replace("%average%", String.valueOf(average)));
		MessageHandler.sendMessage(player, msg.toArray(new String[msg.size()]));
	}
}