package me.avankziar.saj.spigot.cmd;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.avankziar.ifh.general.statistic.StatisticType;
import me.avankziar.ifh.general.statistic.StatisticType.SortingType;
import me.avankziar.saj.general.assistance.ChatApiB;
import me.avankziar.saj.general.assistance.ChatApiS;
import me.avankziar.saj.general.assistance.MatchApi;
import me.avankziar.saj.general.cmdtree.ArgumentConstructor;
import me.avankziar.saj.general.cmdtree.CommandConstructor;
import me.avankziar.saj.general.cmdtree.CommandSuggest;
import me.avankziar.saj.general.cmdtree.CommandSuggest.Type;
import me.avankziar.saj.general.objects.PlayerData;
import me.avankziar.saj.general.objects.StatisticEntry;
import me.avankziar.saj.spigot.SAJ;
import me.avankziar.saj.spigot.ModifierValueEntry.Bypass;
import me.avankziar.saj.spigot.cmdtree.ArgumentModule;
import me.avankziar.saj.spigot.handler.MessageHandler;
import net.md_5.bungee.api.chat.ClickEvent;

public class StatisticCommandExecutor implements CommandExecutor
{
	private SAJ plugin;
	private static CommandConstructor cc;
	private boolean km;
	private double cm_to_m = Double.valueOf("100.0");
	private double cm_to_km = Double.valueOf("100000.0");
	private double cm_to_yard = Double.valueOf("91.44");
	private boolean mile;
	private double cm_to_mile = Double.valueOf("160934.0");
	private static ArrayList<String> matORent = new ArrayList<>();
	
	public StatisticCommandExecutor(SAJ plugin, CommandConstructor cc)
	{
		this.plugin = plugin;
		StatisticCommandExecutor.cc = cc;
		km = plugin.getYamlHandler().getConfig().getBoolean("Statistic.UsedDistanceUnits.Kilometer");
		mile = plugin.getYamlHandler().getConfig().getBoolean("Statistic.UsedDistanceUnits.Mile");
		for(Material m : Material.values())
		{
			matORent.add(m.toString());
		}
		for(EntityType e : EntityType.values())
		{
			matORent.add(e.toString());
		}
	}
	
	// /statistic [sortingtype] [{only by substatistic} statistictype] [page] [player]
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) 
	{
		if(cc == null)
		{
			return false;
		}
		if(args.length == 0)
		{
			if (!(sender instanceof Player)) 
			{
				plugin.getLogger().info("Cmd is only for Player!");
				return false;
			}
			Player player = (Player) sender;
			if(!player.hasPermission(cc.getPermission()))
			{
				///Du hast dafür keine Rechte!
				player.spigot().sendMessage(ChatApiS.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
				return false;
			}
			baseCommand(player);
			return true;
		}
		int length = args.length-1;
		ArrayList<ArgumentConstructor> aclist = cc.subcommands;
		for(int i = 0; i <= length; i++)
		{
			for(ArgumentConstructor ac : aclist)
			{
				if(args[i].equalsIgnoreCase(ac.getName()))
				{
					if(length >= ac.minArgsConstructor && length <= ac.maxArgsConstructor)
					{
						if (sender instanceof Player)
						{
							Player player = (Player) sender;
							if(player.hasPermission(ac.getPermission()))
							{
								ArgumentModule am = plugin.getArgumentMap().get(ac.getPath());
								if(am != null)
								{
									try
									{
										am.run(sender, args);
									} catch (IOException e)
									{
										e.printStackTrace();
									}
								} else
								{
									plugin.getLogger().info("ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
											.replace("%ac%", ac.getName()));
									player.spigot().sendMessage(ChatApiS.tl(
											"ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
											.replace("%ac%", ac.getName())));
									return false;
								}
								return false;
							} else
							{
								player.spigot().sendMessage(ChatApiS.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
								return false;
							}
						} else
						{
							ArgumentModule am = plugin.getArgumentMap().get(ac.getPath());
							if(am != null)
							{
								try
								{
									am.run(sender, args);
								} catch (IOException e)
								{
									e.printStackTrace();
								}
							} else
							{
								plugin.getLogger().info("ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
										.replace("%ac%", ac.getName()));
								sender.spigot().sendMessage(ChatApiS.tl(
										"ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
										.replace("%ac%", ac.getName())));
								return false;
							}
							return false;
						}
					} else
					{
						aclist = ac.subargument;
						break;
					}
				}
			}
		}
		sender.spigot().sendMessage(ChatApiB.clickEvent(plugin.getYamlHandler().getLang().getString("InputIsWrong"),
				ClickEvent.Action.RUN_COMMAND, CommandSuggest.getCmdString(CommandSuggest.Type.SAJ)));
		return false;
	}
	
	private void baseCommand(Player player)
	{
		ArrayList<String> msg = new ArrayList<>();
		msg.add(plugin.getYamlHandler().getLang().getString("Statistic.Base.Headline"));
		for(StatisticType.SortingType sortingType : new ArrayList<StatisticType.SortingType>(EnumSet.allOf(StatisticType.SortingType.class)))
		{
			HashSet<StatisticType> st = StatisticType.getStatisticTypes(sortingType);
			if(st.isEmpty())
			{
				continue;
			}
			if(sortingType == SortingType.MOVEMENT)
			{
				StringBuilder sb1 = new StringBuilder();
				ArrayList<Object> ob = new ArrayList<>();
				ob.add(player.getUniqueId().toString());
				sb1.append("(");
				int i = 0;
				for(StatisticType s : st)
				{
					if(StatisticType.JUMP.toString().equals(s.toString()))
					{
						continue;
					}
					sb1.append("`statistic_type` = ?");
					ob.add(s.toString());
					if(i + 1 < st.size())
					{
						sb1.append(" OR ");
					}
					i++;
				}
				sb1.append(")");
				double mov = plugin.getMysqlHandler().getSum(new StatisticEntry(), "`statistic_value`",
						"`player_uuid` = ? AND "+sb1.toString(), ob.toArray(new Object[ob.size()]));
				StatisticEntry se = plugin.getMysqlHandler().getData(new StatisticEntry(),
						"`player_uuid` = ? AND `statistic_type` = ?", StatisticType.JUMP.toString());
				mov += se != null ? se.getStatisticValue() * 100.0 : 0.0;
				msg.add(plugin.getYamlHandler().getLang().getString("Statistic.Base."+sortingType.toString())
						.replace("%statisticcmd%", CommandSuggest.getCmdString(Type.STATISTIC))
						.replace("%value%", formatDouble(convertFromCentiMeter(mov))+getUnit(mov)));
			} else
			{
				StringBuilder sb1 = new StringBuilder();
				sb1.append("`player_uuid` = ? AND ");
				ArrayList<Object> ob = new ArrayList<>();
				ob.add(player.getUniqueId().toString());
				sb1.append("(");
				int i = 0;
				for(StatisticType s : st)
				{
					sb1.append("`statistic_type` = ?");
					ob.add(s.toString());
					if(i + 1 < st.size())
					{
						sb1.append(" OR ");
					}
				}
				sb1.append(")");
				double mov = plugin.getMysqlHandler().getSum(new StatisticEntry(), "`statistic_value`",
						sb1.toString(), st.toString());
				msg.add(plugin.getYamlHandler().getLang().getString("Statistic.Base."+sortingType.toString())
						.replace("%statisticcmd%", CommandSuggest.getCmdString(Type.STATISTIC))
						.replace("%value%", formatDouble(mov)));
			}
		}
		msg.forEach(x -> MessageHandler.sendMessage(player, x));
	}
	
	public static void withSubStatistic(Player player, String[] args)
	{
		SortingType sortingType = SortingType.WITH_SUBSTATISTIC;
		ArrayList<String> msg = new ArrayList<>();
		SAJ plugin = SAJ.getPlugin();
		if(args.length == 1)
		{
			HashSet<StatisticType> hst = StatisticType.getStatisticTypes(sortingType);
			if(hst.isEmpty())
			{
				MessageHandler.sendMessage(player, plugin.getYamlHandler().getLang().getString("Statistic.NoEntry"));
				return;
			}
			msg.add(plugin.getYamlHandler().getLang().getString("Statistic.Headline")
					.replace("%statistic%", sortingType.toString()));
			for(StatisticType st : hst)
			{
				StatisticEntry se = plugin.getMysqlHandler().getData(new StatisticEntry(),
						"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
						player.getUniqueId().toString(), st.toString(), "null");
				msg.add(plugin.getYamlHandler().getLang().getString("Statistic.Substatistic.Info")
						.replace("%statistic%", st.toString())
						.replace("%value%", String.valueOf(se.getStatisticValue())));
			}
			msg.forEach(x -> MessageHandler.sendMessage(player, x));
			return;
		}
		StatisticType st = StatisticType.MINE_BLOCK;
		if(args.length >= 2)
		{
			st = StatisticType.valueOf(args[1]);
		}
		int page = 0;
		if(args.length >= 3)
		{
			if(MatchApi.isInteger(args[2]))
			{
				page = Integer.valueOf(args[2]);
			}
		}
		UUID uuid = player.getUniqueId();
		if(args.length >= 4)
		{
			if(!player.hasPermission(Bypass.get(Bypass.Permission.STATISTIC_OTHER)))
			{
				player.spigot().sendMessage(ChatApiS.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
				return;
			}
			PlayerData pd = plugin.getMysqlHandler().getData(new PlayerData(), "`player_name` = ?", args[3]);
			if(pd == null)
			{
				MessageHandler.sendMessage(player, plugin.getYamlHandler().getLang().getString("NoPlayerExist"));
				return;
			}
			uuid = pd.getUUID();
		}
		ArrayList<StatisticEntry> ase = new ArrayList<>();
		if(page == 0)
		{
			StatisticEntry sen = plugin.getMysqlHandler().getData(new StatisticEntry(),
					"`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` = ?", 
					uuid.toString(), st.toString(), "null");
			ase.add(sen);
			ArrayList<StatisticEntry> se = plugin.getMysqlHandler().getList(new StatisticEntry(),
					"`id` ASC", page*19, 19, "`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` != ?", 
					uuid.toString(), st.toString(), "null");
			ase.addAll(se);
		} else
		{
			ArrayList<StatisticEntry> se = plugin.getMysqlHandler().getList(new StatisticEntry(),
					"`id` ASC", page*20, 20, "`player_uuid` = ? AND `statistic_type` = ? AND `material_or_entitytype` != ?", 
					uuid.toString(), st.toString(), "null");
			ase.addAll(se);
		}
		msg.add(plugin.getYamlHandler().getLang().getString("Statistic.Headline")
				.replace("%statistic%", st.toString()));
		for(StatisticEntry se : ase)
		{
			msg.add(plugin.getYamlHandler().getLang().getString("Statistic.Substatistic.Info")
					.replace("%statistic%", se.getMaterialEntityType())
					.replace("%value%", String.valueOf(se.getStatisticValue())));
		}
		msg.forEach(x -> MessageHandler.sendMessage(player, x));
	}
	
	public static void args(Player player, String[] args, SortingType sortingType)
	{
		SAJ plugin = SAJ.getPlugin();
		ArrayList<String> msg = new ArrayList<>();
		int page = 0;
		if(args.length >= 2)
		{
			if(MatchApi.isInteger(args[1]))
			{
				page = Integer.valueOf(args[1]);
			}
		}
		UUID uuid = player.getUniqueId();
		if(args.length >= 3)
		{
			if(!player.hasPermission(Bypass.get(Bypass.Permission.STATISTIC_OTHER)))
			{
				player.spigot().sendMessage(ChatApiS.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
				return;
			}
			PlayerData pd = plugin.getMysqlHandler().getData(new PlayerData(), "`player_name` = ?", args[2]);
			if(pd == null)
			{
				MessageHandler.sendMessage(player, plugin.getYamlHandler().getLang().getString("NoPlayerExist"));
				return;
			}
			uuid = pd.getUUID();
		}
		HashSet<StatisticType> hst = StatisticType.getStatisticTypes(sortingType);
		if(hst.isEmpty())
		{
			MessageHandler.sendMessage(player, plugin.getYamlHandler().getLang().getString("Statistic.NoEntry"));
			return;
		}
		StringBuilder sb1 = new StringBuilder();
		sb1.append("`player_uuid` = ? AND ");
		ArrayList<Object> ob = new ArrayList<>();
		ob.add(uuid.toString());
		sb1.append("(");
		int i = 0;
		for(StatisticType s : hst)
		{
			if(page*20 >= i)
			{
				break;
			}
			sb1.append("`statistic_type` = ?");
			ob.add(s.toString());
			if(i + 1 < hst.size())
			{
				sb1.append(" OR ");
			}
		}
		sb1.append(")");
		ArrayList<StatisticEntry> ase = plugin.getMysqlHandler().getList(new StatisticEntry(),
				"`id` ASC", page*20, 20, sb1.toString(), 
				ob.toArray(new Object[ob.size()]));
		for(StatisticEntry se : ase)
		{
			msg.add(plugin.getYamlHandler().getLang().getString("Statistic.Statistic.Info")
					.replace("%statistic%", se.getStatisticType().toString())
					.replace("%value%", String.valueOf(se.getStatisticValue())));
		}
		msg.forEach(x -> MessageHandler.sendMessage(player, x));
		return;
	}
	
	private double convertFromCentiMeter(double v)
	{
		if(km)
		{
			if(v < cm_to_km)
			{
				return v / cm_to_m;
			}
			return v / cm_to_km;
		} else if(mile)
		{
			if(v < cm_to_mile)
			{
				return v / cm_to_yard;
			}
			return v / cm_to_mile;
		} else
		{
			if(v < cm_to_km)
			{
				return v / cm_to_m;
			}
			return cm_to_km / v;
		}
	}
	
	private String getUnit(double v)
	{
		if(km)
		{
			if(v < cm_to_km)
			{
				return plugin.getYamlHandler().getLang().getString("Statistic.Unit.Meter");
			}
			return plugin.getYamlHandler().getLang().getString("Statistic.Unit.Kilometer");
		} else if(mile)
		{
			if(v < cm_to_mile)
			{
				return plugin.getYamlHandler().getLang().getString("Statistic.Unit.Yard");
			}
			return plugin.getYamlHandler().getLang().getString("Statistic.Unit.Mile");
		} else
		{
			if(v < cm_to_km)
			{
				return plugin.getYamlHandler().getLang().getString("Statistic.Unit.Meter");
			}
			return plugin.getYamlHandler().getLang().getString("Statistic.Unit.Kilometer");
		}
	}
	
	private String formatDouble(double d)
	{
		Locale l = null;
		switch(plugin.getYamlManager().getLanguageType())
		{
		default:
		case ENG: l = Locale.US; break;
		case GER: l = Locale.GERMAN; break;
		case FRE: l = Locale.FRENCH; break;
		case ITA: l = Locale.ITALIAN; break;
		case CHI: l = Locale.CHINESE; break;
		case JPN: l = Locale.JAPANESE; break;
		case KOR: l = Locale.KOREAN; break;
		}
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(l);
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
		formatter.setDecimalFormatSymbols(symbols);
		return formatter.format(d);
	}
}