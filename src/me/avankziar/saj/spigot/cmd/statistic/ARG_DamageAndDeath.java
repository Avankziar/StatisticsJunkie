package me.avankziar.saj.spigot.cmd.statistic;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ifh.general.statistic.StatisticType.SortingType;
import me.avankziar.saj.general.cmdtree.ArgumentConstructor;
import me.avankziar.saj.spigot.SAJ;
import me.avankziar.saj.spigot.cmd.StatisticCommandExecutor;
import me.avankziar.saj.spigot.cmdtree.ArgumentModule;

public class ARG_DamageAndDeath extends ArgumentModule
{
	private SAJ plugin;
	
	public ARG_DamageAndDeath(ArgumentConstructor argumentConstructor) 
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
				StatisticCommandExecutor.args(player, args, SortingType.DAMAGE_AND_DEATH);
			}
		}.runTaskAsynchronously(plugin);
	}
}