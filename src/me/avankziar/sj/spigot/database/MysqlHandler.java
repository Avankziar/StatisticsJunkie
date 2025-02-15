package me.avankziar.sj.spigot.database;

import me.avankziar.sj.general.database.MysqlBaseHandler;
import me.avankziar.sj.spigot.SJ;

public class MysqlHandler extends MysqlBaseHandler
{	
	public MysqlHandler(SJ plugin)
	{
		super(plugin.getLogger(), plugin.getMysqlSetup());
	}
}
