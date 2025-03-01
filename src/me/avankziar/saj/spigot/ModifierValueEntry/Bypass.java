package me.avankziar.saj.spigot.ModifierValueEntry;

import java.util.LinkedHashMap;

import me.avankziar.saj.spigot.SAJ;

public class Bypass
{
	public enum Permission
	{
		//Here Condition and BypassPermission.
		ACHIEVEMENT_INFO_OTHER,
		STATISTIC_OTHER;
		
		public String getValueLable()
		{
			return SAJ.pluginname.toLowerCase()+"-"+this.toString().toLowerCase();
		}
	}
	
	private static LinkedHashMap<Bypass.Permission, String> mapPerm = new LinkedHashMap<>();
	
	public static void set(Bypass.Permission bypass, String perm)
	{
		mapPerm.put(bypass, perm);
	}
	
	public static String get(Bypass.Permission bypass)
	{
		return mapPerm.get(bypass);
	}
}