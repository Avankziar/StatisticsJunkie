package me.avankziar.saj.spigot.gui.listener;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.avankziar.saj.spigot.SAJ;
import me.avankziar.saj.spigot.gui.events.UpperGuiClickEvent;
import me.avankziar.saj.spigot.gui.objects.ClickFunctionType;
import me.avankziar.saj.spigot.gui.objects.ClickType;
import me.avankziar.saj.spigot.gui.objects.GuiType;
import me.avankziar.saj.spigot.handler.GuiFunctionHandler;
import me.avankziar.saj.spigot.handler.GuiHandler;

public class UpperListener implements Listener
{
	//private SJ plugin;
	
	public UpperListener(SAJ plugin)
	{
		//this.plugin = plugin;
	}
	
	@EventHandler
	public void onUpperGui(UpperGuiClickEvent event) throws IOException
	{
		if(!event.getPluginName().equals(SAJ.pluginname))
		{
			return;
		}
		if(!(event.getEvent().getWhoClicked() instanceof Player))
		{
			return;
		}
		Player player = (Player) event.getEvent().getWhoClicked();
		GuiType gt = null;
		try
		{
			gt = GuiType.valueOf(event.getInventoryIdentifier());
		} catch(Exception e)
		{
			return;
		}
		if(!event.getValuesInteger().containsKey(GuiHandler.PDT_PAGE)
				|| !event.getValuesString().containsKey(GuiHandler.PDT_OTHER_UUID)
				|| !event.getValuesString().containsKey(GuiHandler.PDT_OTHER_NAME))
		{
			return;
		}
		int page = event.getValuesInteger().get(GuiHandler.PDT_PAGE);
		UUID otheruuid = UUID.fromString(event.getValuesString().get(GuiHandler.PDT_OTHER_UUID));
		String othername = event.getValuesString().get(GuiHandler.PDT_OTHER_NAME);
		ClickType ct = getClickFunctionType(event.getEvent().getClick(), event.getEvent().getHotbarButton());
		if(ct == null)
		{
			return;
		}
		ClickFunctionType cft = null;
		try
		{
			cft = ClickFunctionType.valueOf(event.getFunction(ct));
		} catch(Exception e)
		{
			return;
		}
		if(cft == null)
		{
			return;
		}
		switch(gt)
		{
		case ACHIEVEMENT:
			GuiFunctionHandler.doClickFunktion(gt, cft, player, event.getEvent().getClickedInventory(),
					page, otheruuid, othername);
		}
	}
	
	private ClickType getClickFunctionType(org.bukkit.event.inventory.ClickType ct, int hotbarButton)
	{
		switch(ct)
		{
		default: return null;
		case LEFT: return ClickType.LEFT;
		case RIGHT: return ClickType.RIGHT;
		case DROP: return ClickType.DROP;
		case SHIFT_LEFT: return ClickType.SHIFT_LEFT;
		case SHIFT_RIGHT: return ClickType.SHIFT_RIGHT;
		case CONTROL_DROP: return ClickType.CTRL_DROP;
		case NUMBER_KEY:
			if(hotbarButton < 0)
			{
				return null;
			}
			int i = hotbarButton+1;
			switch(i)
			{
			default: return null;
			case 1: return ClickType.NUMPAD_1;
			case 2: return ClickType.NUMPAD_2;
			case 3: return ClickType.NUMPAD_3;
			case 4: return ClickType.NUMPAD_4;
			case 5: return ClickType.NUMPAD_5;
			case 6: return ClickType.NUMPAD_6;
			case 7: return ClickType.NUMPAD_7;
			case 8: return ClickType.NUMPAD_8;
			case 9: return ClickType.NUMPAD_9;
			}
		}
	}
}