package me.avankziar.saj.spigot.database;

import me.avankziar.saj.general.database.MysqlBaseHandler;
import me.avankziar.saj.spigot.SAJ;

public class MysqlHandler extends MysqlBaseHandler
{	
	public MysqlHandler(SAJ plugin)
	{
		super(plugin.getLogger(), plugin.getMysqlSetup());
	}
}
