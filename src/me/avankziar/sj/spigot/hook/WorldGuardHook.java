package me.avankziar.sj.spigot.hook;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public class WorldGuardHook
{
	public static StateFlag SJ_ACCEPT_STATISTIC_CHANGE;
	
	public static boolean init()
	{
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		try 
		{
			StateFlag sj_asc = new StateFlag("sj_accept_statistic_change", true);
	        registry.register(sj_asc);
	        SJ_ACCEPT_STATISTIC_CHANGE = sj_asc;
	    } catch (FlagConflictException e) 
		{
	        return false;
	    }
		return true;
	}
	
	public static boolean accecptStatisticChange(Player player, Location pointOne)
	{
		RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        return query.testState(BukkitAdapter.adapt(pointOne), 
        		WorldGuardPlugin.inst().wrapPlayer(player), SJ_ACCEPT_STATISTIC_CHANGE);
	}
}