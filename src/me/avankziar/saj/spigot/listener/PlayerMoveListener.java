package me.avankziar.saj.spigot.listener;

import java.util.UUID;

import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ifh.general.statistic.StatisticType;
import me.avankziar.saj.spigot.SAJ;
import me.avankziar.saj.spigot.handler.StatisticHandler;

public class PlayerMoveListener implements Listener
{	
	@EventHandler
	public void onMove(PlayerMoveEvent event)
	{
		if(event.getFrom().getX() == event.getTo().getX() 
				 && event.getFrom().getY() == event.getTo().getY() 
				 && event.getFrom().getZ() == event.getTo().getZ())
		{
			return;
		}
		addStatistic(event.getPlayer().getUniqueId(), event.getFrom().getX(), event.getTo().getX(),
				event.getFrom().getY(), event.getTo().getY(), event.getFrom().getZ(), event.getTo().getZ(),
				event.getPlayer().isInWater(), event.getPlayer().isSwimming(), event.getPlayer().getPose(),
				event.getPlayer().isClimbing(), event.getPlayer().isFlying(), event.getPlayer().isGliding(),
				event.getPlayer().isInsideVehicle(), event.getPlayer().getVehicle(), event.getPlayer().isSneaking(), event.getPlayer().isSprinting());
	}
	
	private double f2(double n)
	{
		return n * n;
	}
	
	private void addStatistic(UUID uuid, double x1, double x2, double y1, double y2, double z1, double z2,
			boolean isInWater, boolean isSwimming, Pose pose, boolean isClimbing, boolean isFlying, boolean isGliding,
			boolean isInsideVehicle, Entity vehicle, boolean isSneaking, boolean isSprinting)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				double v = Math.sqrt(f2(x1 - x2) + f2(y1 - y2) + f2(z1 - z2));
				if(isInWater && isSwimming)
				{
					StatisticHandler.statisticIncrement(uuid, StatisticType.SWIM_ONE_CM, null, null, (int) (v*100));
				} else if(!isInWater && pose == Pose.SWIMMING)
				{
					StatisticHandler.statisticIncrement(uuid, StatisticType.CRAWLING_ONE_CM, null, null, (int) (v*100));
				} else if(isClimbing)
				{
					StatisticHandler.statisticIncrement(uuid, StatisticType.CLIMB_ONE_CM, null, null, (int) (v*100));
				} else if(isFlying)
				{
					StatisticHandler.statisticIncrement(uuid, StatisticType.FLY_ONE_CM, null, null, (int) (v*100));
				} else if(isGliding)
				{
					StatisticHandler.statisticIncrement(uuid, StatisticType.AVIATE_ONE_CM, null, null, (int) (v*100));
				} else if(isInsideVehicle)
				{
					if(vehicle instanceof Boat)
					{
						StatisticHandler.statisticIncrement(uuid, StatisticType.BOAT_ONE_CM, null, null, (int) (v*100));
					} else if(vehicle instanceof Horse)
					{
						StatisticHandler.statisticIncrement(uuid, StatisticType.HORSE_ONE_CM, null, null, (int) (v*100));
					} else
					{
						switch(vehicle.getType())
						{
						default: break;
						case PIG:
							StatisticHandler.statisticIncrement(uuid, StatisticType.PIG_ONE_CM, null, null, (int) (v*100));
							break;
						case MINECART:
							StatisticHandler.statisticIncrement(uuid, StatisticType.MINECART_ONE_CM, null, null, (int) (v*100));
							break;
						case STRIDER:
							StatisticHandler.statisticIncrement(uuid, StatisticType.STRIDER_ONE_CM, null, null, (int) (v*100));
							break;
						}
					}					
				} else if(isSneaking)
				{
					StatisticHandler.statisticIncrement(uuid, StatisticType.CROUCH_ONE_CM, null, null, (int) (v*100));
				} else if(isSprinting)
				{
					StatisticHandler.statisticIncrement(uuid, StatisticType.SPRINT_ONE_CM, null, null, (int) (v*100));
				} else
				{
					StatisticHandler.statisticIncrement(uuid, StatisticType.WALK_ONE_CM, null, null, (int) (v*100));
				}
			}
		}.runTaskAsynchronously(SAJ.getPlugin());
	}
}