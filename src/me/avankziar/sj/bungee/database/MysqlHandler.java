package me.avankziar.sj.bungee.database;

import me.avankziar.sj.bungee.SJ;
import me.avankziar.sj.general.database.MysqlBaseHandler;

public class MysqlHandler extends MysqlBaseHandler
{	
	public MysqlHandler(SJ plugin)
	{
		super(plugin.getLogger(), plugin.getMysqlSetup());
	}
}