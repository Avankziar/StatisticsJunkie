package me.avankziar.sj.velocity.database;

import me.avankziar.sj.general.database.MysqlBaseHandler;
import me.avankziar.sj.velocity.SJ;

public class MysqlHandler extends MysqlBaseHandler
{	
	public MysqlHandler(SJ plugin)
	{
		super(plugin.getLogger(), plugin.getMysqlSetup());
	}
}
