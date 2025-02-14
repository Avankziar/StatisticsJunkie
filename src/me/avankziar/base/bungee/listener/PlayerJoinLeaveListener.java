package me.avankziar.base.bungee.listener;

import me.avankziar.base.bungee.BM;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerJoinLeaveListener implements Listener
{
	private BM plugin;
	
	public PlayerJoinLeaveListener(BM plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent event)
	{
		
	}
}