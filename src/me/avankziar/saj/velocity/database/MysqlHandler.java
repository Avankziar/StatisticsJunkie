package me.avankziar.saj.velocity.database;

import me.avankziar.saj.general.database.MysqlBaseHandler;
import me.avankziar.saj.velocity.SAJ;

public class MysqlHandler extends MysqlBaseHandler
{	
	public MysqlHandler(SAJ plugin)
	{
		super(plugin.getLogger(), plugin.getMysqlSetup());
	}
}
