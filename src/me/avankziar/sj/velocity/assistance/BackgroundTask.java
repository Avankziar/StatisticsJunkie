package me.avankziar.sj.velocity.assistance;

import java.util.concurrent.TimeUnit;

import me.avankziar.sj.velocity.SJ;

public class BackgroundTask 
{
	private SJ plugin;
	
	public BackgroundTask(SJ plugin)
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
