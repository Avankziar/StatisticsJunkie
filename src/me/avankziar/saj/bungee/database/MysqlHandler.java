package me.avankziar.saj.bungee.database;

import me.avankziar.saj.bungee.SAJ;
import me.avankziar.saj.general.database.MysqlBaseHandler;

public class MysqlHandler extends MysqlBaseHandler
{	
	public MysqlHandler(SAJ plugin)
	{
		super(plugin.getLogger(), plugin.getMysqlSetup());
	}
}