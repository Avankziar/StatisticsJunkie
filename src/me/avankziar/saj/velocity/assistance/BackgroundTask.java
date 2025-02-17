package me.avankziar.saj.velocity.assistance;

import java.util.concurrent.TimeUnit;

import me.avankziar.saj.velocity.SAJ;

public class BackgroundTask 
{
	private SAJ plugin;
	
	public BackgroundTask(SAJ plugin)
	{
		this.plugin = plugin;
		runTask();
	}
	
	private void runTask()
	{
		plugin.getServer().getScheduler().buildTask(plugin, (task) ->
		{
			//Do something
		}).delay(1L, TimeUnit.MILLISECONDS).repeat(15L, TimeUnit.MILLISECONDS).schedule();
	}
	
	
}
