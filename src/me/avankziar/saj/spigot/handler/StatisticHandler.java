package me.avankziar.saj.spigot.handler;

import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ifh.general.statistic.StatisticType;
import me.avankziar.ifh.general.statistic.StatisticType.SortingType;
import me.avankziar.saj.general.objects.StatisticEntry;
import me.avankziar.saj.spigot.SAJ;

public class StatisticHandler
{
	private static ConcurrentHashMap<UUID, ConcurrentHashMap<StatisticType, ConcurrentHashMap<String, Double>>> increments = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<UUID, ConcurrentHashMap<StatisticType, ConcurrentHashMap<String, Double>>> incrementsBackUp = new ConcurrentHashMap<>();
	private static boolean doTask = false;
	
	public static void statisticIncrement(UUID uuid, Statistic st, Material mat, EntityType ent, double add)
	{
		StatisticType statisticType = StatisticType.getStatisticType(st);
		if(statisticType == null)
		{
			return;
		}
		statisticIncrement(uuid, statisticType, mat, ent, add);
	}
	
	public static void statisticIncrement(UUID uuid, StatisticType statisticType, Material mat, EntityType ent, double add)
	{
		if(doTask)
		{
			ConcurrentHashMap<StatisticType, ConcurrentHashMap<String, Double>> mapI = new ConcurrentHashMap<>();
			if(incrementsBackUp.containsKey(uuid))
			{
				mapI = incrementsBackUp.get(uuid);
			}
			ConcurrentHashMap<String, Double> mapII = new ConcurrentHashMap<>();
			if(mapI.containsKey(statisticType))
			{
				mapII = mapI.get(statisticType);
			}
			double l = 0.0;
			if(mapII.containsKey(mat != null ? mat.toString() : (ent != null ? ent.toString() : "null")))
			{
				l = mapII.get(mat != null ? mat.toString() : (ent != null ? ent.toString() : "null"));
			}
			l += add;
			mapII.put(mat != null ? mat.toString() : (ent != null ? ent.toString() : "null"), l);
			mapI.put(statisticType, mapII);
			incrementsBackUp.put(uuid, mapI);
			if(statisticType.getSortingType() == SortingType.WITH_SUBSTATISTIC)
			{
				ConcurrentHashMap<StatisticType, ConcurrentHashMap<String, Double>> mapNI = new ConcurrentHashMap<>();
				if(incrementsBackUp.containsKey(uuid))
				{
					mapNI = incrementsBackUp.get(uuid);
				}
				ConcurrentHashMap<String, Double> mapNII = new ConcurrentHashMap<>();
				if(mapNI.containsKey(statisticType))
				{
					mapNII = mapNI.get(statisticType);
				}
				double ll = 0.0;
				if(mapNII.containsKey("null"))
				{
					ll = mapNII.get("null");
				}
				ll += add;
				mapNII.put("null", ll);
				mapNI.put(statisticType, mapNII);
				incrementsBackUp.put(uuid, mapNI);
			}
		} else
		{
			ConcurrentHashMap<StatisticType, ConcurrentHashMap<String, Double>> mapI = new ConcurrentHashMap<>();
			if(increments.containsKey(uuid))
			{
				mapI = increments.get(uuid);
			}
			ConcurrentHashMap<String, Double> mapII = new ConcurrentHashMap<>();
			if(mapI.containsKey(statisticType))
			{
				mapII = mapI.get(statisticType);
			}
			double l = 0;
			if(mapII.containsKey(mat != null ? mat.toString() : (ent != null ? ent.toString() : "null")))
			{
				l = mapII.get(mat != null ? mat.toString() : (ent != null ? ent.toString() : "null"));
			}
			l += add;
			mapII.put(mat != null ? mat.toString() : (ent != null ? ent.toString() : "null"), l);
			mapI.put(statisticType, mapII);
			increments.put(uuid, mapI);
			if(statisticType.getSortingType() == SortingType.WITH_SUBSTATISTIC)
			{
				ConcurrentHashMap<StatisticType, ConcurrentHashMap<String, Double>> mapNI = new ConcurrentHashMap<>();
				if(increments.containsKey(uuid))
				{
					mapNI = increments.get(uuid);
				}
				ConcurrentHashMap<String, Double> mapNII = new ConcurrentHashMap<>();
				if(mapNI.containsKey(statisticType))
				{
					mapNII = mapNI.get(statisticType);
				}
				double ll = 0;
				if(mapNII.containsKey("null"))
				{
					ll = mapNII.get("null");
				}
				ll += add;
				mapNII.put("null", ll);
				mapNI.put(statisticType, mapNII);
				increments.put(uuid, mapNI);
			}
		}
	}
	
	public static void processStatistic()
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				doTask = true;
				final ConcurrentHashMap<UUID, ConcurrentHashMap<StatisticType, ConcurrentHashMap<String, Double>>> copy = deepClone(increments);
				increments.clear();
				doTask = false;
				for(Entry<UUID, ConcurrentHashMap<StatisticType, ConcurrentHashMap<String, Double>>> e : copy.entrySet())
				{
					UUID uuid = e.getKey();
					ConcurrentHashMap<StatisticType, ConcurrentHashMap<String, Double>> v = e.getValue();
					for(Entry<StatisticType, ConcurrentHashMap<String, Double>> ee : v.entrySet())
					{
						StatisticType st = ee.getKey();
						ConcurrentHashMap<String, Double> vv = ee.getValue();
						for(Entry<String, Double> eee : vv.entrySet())
						{
							String matORent = eee.getKey();
							double add = eee.getValue();
							StatisticEntry se = SAJ.getPlugin().getMysqlHandler().getData(new StatisticEntry(), 
									"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
									uuid.toString(), st.toString(),	matORent);
							if(se != null)
							{
								se.setStatisticValue(se.getStatisticValue() + add);
								SAJ.getPlugin().getMysqlHandler().updateData(se,
										"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
										uuid.toString(), st.toString(), matORent);
							} else
							{
								se = new StatisticEntry(0, uuid, st, matORent, add);
								SAJ.getPlugin().getMysqlHandler().create(se);
							}
						}
					}
				}
				for(Entry<UUID, ConcurrentHashMap<StatisticType, ConcurrentHashMap<String, Double>>> e : incrementsBackUp.entrySet())
				{
					UUID uuid = e.getKey();
					ConcurrentHashMap<StatisticType, ConcurrentHashMap<String, Double>> v = e.getValue();
					for(Entry<StatisticType, ConcurrentHashMap<String, Double>> ee : v.entrySet())
					{
						StatisticType st = ee.getKey();
						ConcurrentHashMap<String, Double> vv = ee.getValue();
						for(Entry<String, Double> eee : vv.entrySet())
						{
							String matORent = eee.getKey();
							double add = eee.getValue();
							StatisticEntry se = SAJ.getPlugin().getMysqlHandler().getData(new StatisticEntry(), 
									"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
									uuid.toString(), st.toString(),	matORent);
							if(se != null)
							{
								se.setStatisticValue(se.getStatisticValue() + add);
								SAJ.getPlugin().getMysqlHandler().updateData(se,
										"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
										uuid.toString(), st.toString(), matORent);
							} else
							{
								se = new StatisticEntry(0, uuid, st, matORent, add);
								SAJ.getPlugin().getMysqlHandler().create(se);
							}
						}
					}
				}
				incrementsBackUp.clear();
			}
		}.runTaskTimerAsynchronously(SAJ.getPlugin(),
				20L*10, 20L*60*SAJ.getPlugin().getYamlHandler().getConfig().getLong("Task.UpdateStatisticIncrementToDatabase", 2L));
	}
	
	public static ConcurrentHashMap<UUID, ConcurrentHashMap<StatisticType, ConcurrentHashMap<String, Double>>> deepClone(
			ConcurrentHashMap<UUID, ConcurrentHashMap<StatisticType, ConcurrentHashMap<String, Double>>> map)
	{
		ConcurrentHashMap<UUID, ConcurrentHashMap<StatisticType, ConcurrentHashMap<String, Double>>> mapI = new ConcurrentHashMap<>();
		//ConcurrentHashMap<StatisticType, ConcurrentHashMap<String, Long>> mapII = new ConcurrentHashMap<>();
		//ConcurrentHashMap<String, Long> mapIII = new ConcurrentHashMap<>();
		for(Entry<UUID, ConcurrentHashMap<StatisticType, ConcurrentHashMap<String, Double>>> e : map.entrySet())
		{
			UUID uuid = e.getKey();
			ConcurrentHashMap<StatisticType, ConcurrentHashMap<String, Double>> mapII = new ConcurrentHashMap<>();
			for(Entry<StatisticType, ConcurrentHashMap<String, Double>> ee : e.getValue().entrySet())
			{
				StatisticType st = ee.getKey();
				ConcurrentHashMap<String, Double> mapIII = new ConcurrentHashMap<>();
				mapIII.putAll(ee.getValue());
				mapII.put(st, mapIII);
			}
			mapI.put(uuid, mapII);
		}
		return mapI;
	}
}