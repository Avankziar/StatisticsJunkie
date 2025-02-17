package me.avankziar.saj.spigot.handler;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.avankziar.saj.spigot.gui.objects.ClickFunctionType;
import me.avankziar.saj.spigot.gui.objects.GuiType;
import me.avankziar.saj.spigot.gui.objects.SettingsLevel;

public class GuiFunctionHandler
{
	public static void doClickFunktion(GuiType guiType, ClickFunctionType cft, Player player,
			Inventory openInv, int page, UUID other, String othername)
	{
		switch(cft)
		{
		case PAGE_NEXT:
		case PAGE_PAST:
			lastNextPage(player, openInv, page, other, othername);
		}
	}
	
	private static void lastNextPage(Player player, Inventory inv, int page, UUID other, String othername)
	{
		GuiHandler.openAchievement(player, SettingsLevel.BASE, inv, false, page, other, othername);
	}
}