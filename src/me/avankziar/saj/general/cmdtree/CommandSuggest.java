package me.avankziar.saj.general.cmdtree;

import java.util.LinkedHashMap;

public class CommandSuggest
{
	/**
	 * All Commands and their following arguments
	 */
	public enum Type 
	{
		SAJ,
		ACHIEVEMENT,
		ACHIEVEMENT_INFO,
		STATISTIC,
		STATISTIC_CHATANDCOMMAND,
		STATISTIC_DAMAGEANDDEATH,
		STATISTIC_ECONOMY,
		STATISTIC_INTERACTIONWITHBLOCKS,
		STATISTIC_MISCELLANEOUS,
		STATISTIC_MOVEMENT,
		STATISTIC_PLUGINS,
		STATISTIC_SKILL,
		STATISTIC_SPECIAL,
		STATISTIC_TIME,
		STATISTIC_WITHSUBSTATISTIC;
	}
	
	public static LinkedHashMap<CommandSuggest.Type, BaseConstructor> map = new LinkedHashMap<>();
	
	public static void set(CommandSuggest.Type cst, BaseConstructor bc)
	{
		map.put(cst, bc);
	}
	
	public static BaseConstructor get(CommandSuggest.Type ces)
	{
		return map.get(ces);
	}
	
	public static String getCmdString(CommandSuggest.Type ces)
	{
		BaseConstructor bc = map.get(ces);
		return bc != null ? bc.getCommandString() : null;
	}
	
	
}
