package me.avankziar.base.bungee.database;

import me.avankziar.base.bungee.BM;
import me.avankziar.base.general.database.MysqlBaseHandler;

public class MysqlHandler extends MysqlBaseHandler
{	
	public MysqlHandler(BM plugin)
	{
		super(plugin.getLogger(), plugin.getMysqlSetup());
	}
}