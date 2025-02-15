package me.avankziar.sj.spigot.gui.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import me.avankziar.sj.spigot.SJ;
import me.avankziar.sj.spigot.gui.GUIApi;
import me.avankziar.sj.spigot.gui.events.BottomGuiClickEvent;
import me.avankziar.sj.spigot.gui.objects.GuiType;

public class BottomListener implements Listener
{
	private SJ plugin;
	
	public BottomListener(SJ plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onBottomGui(BottomGuiClickEvent event)
	{
		if(!event.getPluginName().equals(SJ.pluginname))
		{
			return;
		}
		if(event.getEvent().getCurrentItem() == null || event.getEvent().getCurrentItem().getType() == Material.AIR)
		{
			return;
		}
		final ItemStack is = event.getEvent().getCurrentItem().clone();
		is.setAmount(1);
		if(!(event.getEvent().getWhoClicked() instanceof Player))
		{
			return;
		}
		Player player = (Player) event.getEvent().getWhoClicked();
		if(!GUIApi.isInGui(player.getUniqueId()))
		{
			return;
		}
		GuiType gt = GUIApi.getGuiType(player.getUniqueId());
		switch(gt)
		{
		default:
			break;
		}
	}
}