package me.avankziar.saj.general.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Material;

import me.avankziar.ifh.general.statistic.StatisticType;
import me.avankziar.ifh.general.statistic.StatisticType.SortingType;
import me.avankziar.saj.general.database.Language.ISO639_2B;
import me.avankziar.saj.spigot.ModifierValueEntry.Bypass;
import me.avankziar.saj.spigot.gui.objects.ClickFunctionType;
import me.avankziar.saj.spigot.gui.objects.ClickType;
import me.avankziar.saj.spigot.gui.objects.GuiType;

public class YamlManager
{	
	public enum Type
	{
		BUNGEE, SPIGOT, VELO;
	}
	
	private ISO639_2B languageType = ISO639_2B.GER;
	//The default language of your plugin. Mine is german.
	private ISO639_2B defaultLanguageType = ISO639_2B.GER;
	private static Type type;
	
	//Per Flatfile a linkedhashmap.
	private static LinkedHashMap<String, Language> configKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> commandsKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> languageKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> mvelanguageKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, LinkedHashMap<String, Language>> achievementgoalKeys = new LinkedHashMap<>();
	/*
	 * Here are mutiplefiles in one "double" map. The first String key is the filename
	 * So all filename muss be predefine. For example in the config.
	 */
	private static LinkedHashMap<GuiType, LinkedHashMap<String, Language>> guisKeys = new LinkedHashMap<>();
	
	public YamlManager(Type type)
	{
		YamlManager.type = type;
		initConfig();
		if(type == Type.SPIGOT)
		{
			initCommands();
			initLanguage();
			initFileAchievementGoal();
			initGui();
		}
	}
	
	public ISO639_2B getLanguageType()
	{
		return languageType;
	}

	public void setLanguageType(ISO639_2B languageType)
	{
		this.languageType = languageType;
	}
	
	public ISO639_2B getDefaultLanguageType()
	{
		return defaultLanguageType;
	}
	
	public LinkedHashMap<String, Language> getConfigKey()
	{
		return configKeys;
	}
	
	public LinkedHashMap<String, Language> getCommandsKey()
	{
		return commandsKeys;
	}
	
	public LinkedHashMap<String, Language> getLanguageKey()
	{
		return languageKeys;
	}
	
	public LinkedHashMap<String, Language> getModifierValueEntryLanguageKey()
	{
		return mvelanguageKeys;
	}
	
	public LinkedHashMap<String, LinkedHashMap<String, Language>> getAchievementGoalKeys()
	{
		return achievementgoalKeys;
	}
	
	public LinkedHashMap<GuiType, LinkedHashMap<String, Language>> getGUIKey()
	{
		return guisKeys;
	}
	
	/*
	 * The main methode to set all paths in the yamls.
	 */
	public void setFileInputBukkit(org.bukkit.configuration.file.YamlConfiguration yml,
			LinkedHashMap<String, Language> keyMap, String key, ISO639_2B languageType)
	{
		if(!keyMap.containsKey(key))
		{
			return;
		}
		if(key.startsWith("#"))
		{
			//Comments
			String k = key.replace("#", "");
			if(yml.get(k) == null)
			{
				//return because no aktual key are present
				return;
			}
			if(yml.getComments(k) != null && !yml.getComments(k).isEmpty())
			{
				//Return, because the comments are already present, and there could be modified. F.e. could be comments from a admin.
				return;
			}
			if(keyMap.get(key).languageValues.get(languageType).length == 1)
			{
				if(keyMap.get(key).languageValues.get(languageType)[0] instanceof String)
				{
					String s = ((String) keyMap.get(key).languageValues.get(languageType)[0]).replace("\r\n", "");
					yml.setComments(k, Arrays.asList(s));
				}
			} else
			{
				List<Object> list = Arrays.asList(keyMap.get(key).languageValues.get(languageType));
				ArrayList<String> stringList = new ArrayList<>();
				if(list instanceof List<?>)
				{
					for(Object o : list)
					{
						if(o instanceof String)
						{
							stringList.add(((String) o).replace("\r\n", ""));
						}
					}
				}
				yml.setComments(k, (List<String>) stringList);
			}
			return;
		}
		if(yml.get(key) != null)
		{
			return;
		}
		if(keyMap.get(key).languageValues.get(languageType).length == 1)
		{
			if(keyMap.get(key).languageValues.get(languageType)[0] instanceof String)
			{
				yml.set(key, ((String) keyMap.get(key).languageValues.get(languageType)[0]).replace("\r\n", ""));
			} else
			{
				yml.set(key, keyMap.get(key).languageValues.get(languageType)[0]);
			}
		} else
		{
			List<Object> list = Arrays.asList(keyMap.get(key).languageValues.get(languageType));
			ArrayList<String> stringList = new ArrayList<>();
			if(list instanceof List<?>)
			{
				for(Object o : list)
				{
					if(o instanceof String)
					{
						stringList.add(((String) o).replace("\r\n", ""));
					} else
					{
						stringList.add(o.toString().replace("\r\n", ""));
					}
				}
			}
			yml.set(key, (List<String>) stringList);
		}
	}
	
	public void setFileInputBungee(net.md_5.bungee.config.Configuration yml,
			LinkedHashMap<String, Language> keyMap, String key, ISO639_2B languageType)
	{
		if(!keyMap.containsKey(key))
		{
			return;
		}
		if(key.startsWith("#"))
		{
			//Comments cannot funktion on bungee
			return;
		}
		if(yml.get(key) != null)
		{
			return;
		}
		if(keyMap.get(key).languageValues.get(languageType).length == 1)
		{
			if(keyMap.get(key).languageValues.get(languageType)[0] instanceof String)
			{
				yml.set(key, ((String) keyMap.get(key).languageValues.get(languageType)[0]).replace("\r\n", ""));
			} else
			{
				yml.set(key, keyMap.get(key).languageValues.get(languageType)[0]);
			}
		} else
		{
			List<Object> list = Arrays.asList(keyMap.get(key).languageValues.get(languageType));
			ArrayList<String> stringList = new ArrayList<>();
			if(list instanceof List<?>)
			{
				for(Object o : list)
				{
					if(o instanceof String)
					{
						stringList.add(((String) o).replace("\r\n", ""));
					} else
					{
						stringList.add(o.toString().replace("\r\n", ""));
					}
				}
			}
			yml.set(key, (List<String>) stringList);
		}
	}
	
	public void setFileInput(dev.dejvokep.boostedyaml.YamlDocument yml,
			LinkedHashMap<String, Language> keyMap, String key, ISO639_2B languageType) throws org.spongepowered.configurate.serialize.SerializationException
	{
		if(!keyMap.containsKey(key))
		{
			return;
		}
		if(yml.get(key) != null)
		{
			return;
		}
		if(key.startsWith("#"))
		{
			//Comments
			String k = key.replace("#", "");
			if(yml.get(k) == null)
			{
				//return because no actual key are present
				return;
			}
			if(yml.getBlock(k) == null)
			{
				return;
			}
			if(yml.getBlock(k).getComments() != null && !yml.getBlock(k).getComments().isEmpty())
			{
				//Return, because the comments are already present, and there could be modified. F.e. could be comments from a admin.
				return;
			}
			if(keyMap.get(key).languageValues.get(languageType).length == 1)
			{
				if(keyMap.get(key).languageValues.get(languageType)[0] instanceof String)
				{
					String s = ((String) keyMap.get(key).languageValues.get(languageType)[0]).replace("\r\n", "");
					yml.getBlock(k).setComments(Arrays.asList(s));
				}
			} else
			{
				List<Object> list = Arrays.asList(keyMap.get(key).languageValues.get(languageType));
				ArrayList<String> stringList = new ArrayList<>();
				if(list instanceof List<?>)
				{
					for(Object o : list)
					{
						if(o instanceof String)
						{
							stringList.add(((String) o).replace("\r\n", ""));
						}
					}
				}
				yml.getBlock(k).setComments((List<String>) stringList);
			}
			return;
		}
		if(keyMap.get(key).languageValues.get(languageType).length == 1)
		{
			if(keyMap.get(key).languageValues.get(languageType)[0] instanceof String)
			{
				yml.set(key, convertMiniMessageToBungee(((String) keyMap.get(key).languageValues.get(languageType)[0]).replace("\r\n", "")));
			} else
			{
				yml.set(key, keyMap.get(key).languageValues.get(languageType)[0]);
			}
		} else
		{
			List<Object> list = Arrays.asList(keyMap.get(key).languageValues.get(languageType));
			ArrayList<String> stringList = new ArrayList<>();
			if(list instanceof List<?>)
			{
				for(Object o : list)
				{
					if(o instanceof String)
					{
						stringList.add(convertMiniMessageToBungee(((String) o).replace("\r\n", "")));
					} else
					{
						stringList.add(o.toString().replace("\r\n", ""));
					}
				}
			}
			yml.set(key, (List<String>) stringList);
		}
	}
	
	public static String convertMiniMessageToBungee(String s)
	{
		if(type == null || type != Type.BUNGEE)
		{
			//If Server is not Bungee, there is no need to convert.
			return s;
		}
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if(c == '<' && i+1 < s.length())
			{
				char cc = s.charAt(i+1);
				if(cc == '#' && i+8 < s.length())
				{
					//Hexcolors
					//     i12345678
					//f.e. <#00FF00>
					String rc = s.substring(i, i+8);
					b.append(rc.replace("<#", "&#").replace(">", ""));
					i += 8;
				} else
				{
					//Normal Colors
					String r = null;
					StringBuilder sub = new StringBuilder();
					sub.append(c).append(cc);
					i++;
					for(int j = i+1; j < s.length(); j++)
					{
						i++;
						char jc = s.charAt(j);
						if(jc == '>')
						{
							sub.append(jc);
							switch(sub.toString())
							{
							case "</color>":
							case "</black>":
							case "</dark_blue>":
							case "</dark_green>":
							case "</dark_aqua>":
							case "</dark_red>":
							case "</dark_purple>":
							case "</gold>":
							case "</gray>":
							case "</dark_gray>":
							case "</blue>":
							case "</green>":
							case "</aqua>":
							case "</red>":
							case "</light_purple>":
							case "</yellow>":
							case "</white>":
							case "</obf>":
							case "</obfuscated>":
							case "</b>":
							case "</bold>":
							case "</st>":
							case "</strikethrough>":
							case "</u>":
							case "</underlined>":
							case "</i>":
							case "</em>":
							case "</italic>":
								r = "";
								break;
							case "<black>":
								r = "&0";
								break;
							case "<dark_blue>":
								r = "&1";
								break;
							case "<dark_green>":
								r = "&2";
								break;
							case "<dark_aqua>":
								r = "&3";
								break;
							case "<dark_red>":
								r = "&4";
								break;
							case "<dark_purple>":
								r = "&5";
								break;
							case "<gold>":
								r = "&6";
								break;
							case "<gray>":
								r = "&7";
								break;
							case "<dark_gray>":
								r = "&8";
								break;
							case "<blue>":
								r = "&9";
								break;
							case "<green>":
								r = "&a";
								break;
							case "<aqua>":
								r = "&b";
								break;
							case "<red>":
								r = "&c";
								break;
							case "<light_purple>":
								r = "&d";
								break;
							case "<yellow>":
								r = "&e";
								break;
							case "<white>":
								r = "&f";
								break;
							case "<obf>":
							case "<obfuscated>":
								r = "&k";
								break;
							case "<b>":
							case "<bold>":
								r = "&l";
								break;
							case "<st>":
							case "<strikethrough>":
								r = "&m";
								break;
							case "<u>":
							case "<underlined>":
								r = "&n";
								break;
							case "<i>":
							case "<em>":
							case "<italic>":
								r = "&o";
								break;
							case "<reset>":
								r = "&r";
								break;
							case "<newline>":
								r = "~!~";
								break;
							}
							b.append(r);
							break;
						} else
						{
							//Search for the color.
							sub.append(jc);
						}
					}
				}
			} else
			{
				b.append(c);
			}
		}
		return b.toString();
	}
	
	private void addComments(LinkedHashMap<String, Language> mapKeys, String path, Object[] o)
	{
		mapKeys.put(path, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, o));
	}
	
	private void addConfig(String path, Object[] c, Object[] o)
	{
		configKeys.put(path, new Language(new ISO639_2B[] {ISO639_2B.GER}, c));
		addComments(configKeys, "#"+path, o);
	}
	
	public void initConfig() //INFO:Config
	{
		addConfig("useIFHAdministration",
				new Object[] {
				true},
				new Object[] {
				"Boolean um auf das IFH Interface Administration zugreifen soll.",
				"Wenn 'true' eingegeben ist, aber IFH Administration ist nicht vorhanden, so werden automatisch die eigenen Configwerte genommen.",
				"Boolean to access the IFH Interface Administration.",
				"If 'true' is entered, but IFH Administration is not available, the own config values are automatically used."});
		addConfig("IFHAdministrationPath", 
				new Object[] {
				"saj"},
				new Object[] {
				"",
				"Diese Funktion sorgt dafür, dass das Plugin auf das IFH Interface Administration zugreifen kann.",
				"Das IFH Interface Administration ist eine Zentrale für die Daten von Sprache, Servername und Mysqldaten.",
				"Diese Zentralisierung erlaubt für einfache Änderung/Anpassungen genau dieser Daten.",
				"Sollte das Plugin darauf zugreifen, werden die Werte in der eigenen Config dafür ignoriert.",
				"",
				"This function ensures that the plugin can access the IFH Interface Administration.",
				"The IFH Interface Administration is a central point for the language, server name and mysql data.",
				"This centralization allows for simple changes/adjustments to precisely this data.",
				"If the plugin accesses it, the values in its own config are ignored."});
		addConfig("ServerName",
				new Object[] {
				"hub"},
				new Object[] {
				"",
				"Der Server steht für den Namen des Spigotservers, wie er in BungeeCord/Waterfall/Velocity config.yml unter dem Pfad 'servers' angegeben ist.",
				"Sollte kein BungeeCord/Waterfall oder andere Proxys vorhanden sein oder du nutzt IFH Administration, so kannst du diesen Bereich ignorieren.",
				"",
				"The server stands for the name of the spigot server as specified in BungeeCord/Waterfall/Velocity config.yml under the path 'servers'.",
				"If no BungeeCord/Waterfall or other proxies are available or you are using IFH Administration, you can ignore this area."});
		addConfig("Language",
				new Object[] {
				"ENG"},
				new Object[] {
				"",
				"Die eingestellte Sprache. Von Haus aus sind 'ENG=Englisch' und 'GER=Deutsch' mit dabei.",
				"Falls andere Sprachen gewünsch sind, kann man unter den folgenden Links nachschauen, welchs Kürzel für welche Sprache gedacht ist.",
				"Siehe hier nach, sowie den Link, welche dort auch für Wikipedia steht.",
				"https://github.com/Avankziar/RootAdministration/blob/main/src/main/java/me/avankziar/roota/general/Language.java",
				"",
				"The set language. By default, ENG=English and GER=German are included.",
				"If other languages are required, you can check the following links to see which abbreviation is intended for which language.",
				"See here, as well as the link, which is also there for Wikipedia.",
				"https://github.com/Avankziar/RootAdministration/blob/main/src/main/java/me/avankziar/roota/general/Language.java"});
		addConfig("Mysql.Status",
				new Object[] {
				false},
				new Object[] {
				"",
				"'Status' ist ein simple Sicherheitsfunktion, damit nicht unnötige Fehler in der Konsole geworfen werden.",
				"Stelle diesen Wert auf 'true', wenn alle Daten korrekt eingetragen wurden.",
				"",
				"'Status' is a simple security function so that unnecessary errors are not thrown in the console.",
				"Set this value to 'true' if all data has been entered correctly."});
		addComments(configKeys, "#Mysql", 
				new Object[] {
				"",
				"Mysql ist ein relationales Open-Source-SQL-Databaseverwaltungssystem, das von Oracle entwickelt und unterstützt wird.",
				"'My' ist ein Namenkürzel und 'SQL' steht für Structured Query Language. Eine Programmsprache mit der man Daten auf einer relationalen Datenbank zugreifen und diese verwalten kann.",
				"Link https://www.mysql.com/de/",
				"Wenn du IFH Administration nutzt, kann du diesen Bereich ignorieren.",
				"",
				"Mysql is an open source relational SQL database management system developed and supported by Oracle.",
				"'My' is a name abbreviation and 'SQL' stands for Structured Query Language. A program language that can be used to access and manage data in a relational database.",
				"Link https://www.mysql.com",
				"If you use IFH Administration, you can ignore this section."});
		addConfig("Mysql.Host",
				new Object[] {
				"127.0.0.1"},
				new Object[] {
				"",
				"Der Host, oder auch die IP. Sie kann aus einer Zahlenkombination oder aus einer Adresse bestehen.",
				"Für den Lokalhost, ist es möglich entweder 127.0.0.1 oder 'localhost' einzugeben. Bedenke, manchmal kann es vorkommen,",
				"das bei gehosteten Server die ServerIp oder Lokalhost möglich ist.",
				"",
				"The host, or IP. It can consist of a number combination or an address.",
				"For the local host, it is possible to enter either 127.0.0.1 or >localhost<.",
				"Please note that sometimes the serverIp or localhost is possible for hosted servers."});
		addConfig("Mysql.Port",
				new Object[] {
				3306},
				new Object[] {
				"",
				"Ein Port oder eine Portnummer ist in Rechnernetzen eine Netzwerkadresse,",
				"mit der das Betriebssystem die Datenpakete eines Transportprotokolls zu einem Prozess zuordnet.",
				"Ein Port für Mysql ist standart gemäß 3306.",
				"",
				"In computer networks, a port or port number ",
				"is a network address with which the operating system assigns the data packets of a transport protocol to a process.",
				"A port for Mysql is standard according to 3306."});
		addConfig("Mysql.DatabaseName",
				new Object[] {
				"mydatabase"},
				new Object[] {
				"",
				"Name der Datenbank in Mysql.",
				"",
				"Name of the database in Mysql."});
		addConfig("Mysql.SSLEnabled",
				new Object[] {
				false},
				new Object[] {
				"",
				"SSL ist einer der drei Möglichkeiten, welcher, solang man nicht weiß, was es ist, es so lassen sollte wie es ist.",
				"",
				"SSL is one of the three options which, as long as you don't know what it is, you should leave it as it is."});
		addConfig("Mysql.AutoReconnect",
				new Object[] {
				true},
				new Object[] {
				"",
				"AutoReconnect ist einer der drei Möglichkeiten, welcher, solang man nicht weiß, was es ist, es so lassen sollte wie es ist.",
				"",
				"AutoReconnect is one of the three options which, as long as you don't know what it is, you should leave it as it is."});
		addConfig("Mysql.VerifyServerCertificate",
				new Object[] {
				false},
				new Object[] {
				"",
				"VerifyServerCertificate ist einer der drei Möglichkeiten, welcher, solang man nicht weiß, was es ist, es so lassen sollte wie es ist.",
				"",
				"VerifyServerCertificate is one of the three options which, as long as you don't know what it is, you should leave it as it is."});
		addConfig("Mysql.User",
				new Object[] {
				"admin"},
				new Object[] {
				"",
				"Der User, welcher auf die Mysql zugreifen soll.",
				"",
				"The user who should access the Mysql."});
		addConfig("Mysql.Password",
				new Object[] {
				"not_0123456789"},
				new Object[] {
				"",
				"Das Passwort des Users, womit er Zugang zu Mysql bekommt.",
				"",
				"The user's password, with which he gets access to Mysql."});
		
		if(type != Type.SPIGOT)
		{
			return;
		}
		//Only for spigot configs.
		addConfig("IsInstalledOnProxy",
				new Object[] {
				false},
				new Object[] {
				"",
				"Legt fest, ob StatisticsJunkie auf den Proxy (Bungeecord/Velocity) installiert ist.",
				"",
				""});
		addConfig("UsePlayerMoveEvent",
				new Object[] {
				true},
				new Object[] {
				"",
				"Legt fest, ob die Erhebung für folgende Statistiken erfolgt:",
				"SWIM_ONE_CM, CRAWLING_ONE_CM, CLIMB_ONE_CM, FLY_ONE_CM, AVIATE_ONE_CM,",
				"BOAT_ONE_CM, HORSE_ONE_CM, PIG_ONE_CM, MINECART_ONE_CM, STRIDER_ONE_CM,",
				"CROUCH_ONE_CM, SPRINT_ONE_CM, WALK_ONE_CM",
				"",
				"Determines whether the collection is carried out for the following statistics:",
				"SWIM_ONE_CM, CRAWLING_ONE_CM, CLIMB_ONE_CM, FLY_ONE_CM, AVIATE_ONE_CM,",
				"BOAT_ONE_CM, HORSE_ONE_CM, PIG_ONE_CM, MINECART_ONE_CM, STRIDER_ONE_CM,",
				"CROUCH_ONE_CM, SPRINT_ONE_CM, WALK_ONE_CM"});
		addConfig("Task.CheckIfPlayerAchievedSomething",
				new Object[] {
				15},
				new Object[] {
				"",
				"Ein Asynchroner Task in Minuten, der checkt ob Spieler irgendwelche AchievementGoals erreicht haben.",
				"",
				""});
		addConfig("Task.UpdateStatisticIncrementToDatabase",
				new Object[] {
				2},
				new Object[] {
				"",
				"Ein Asynchroner Task in Minuten, der die normalen Minecraft Statistischen Inkrements in die Datenbank einfügt.",
				"",
				""});
		addConfig("Statistic.UsedDistanceUnits.Kilometer",
				new Object[] {
				true},
				new Object[] {
				"",
				"Definiert für die Distanze Statistik ob man in Kilometer rechnen möchte.",
				"KiloMeter wird immer priorisiert vor der Meile, falls beide aktiv sein sollten.",
				"",
				""});
		addConfig("Statistic.UsedDistanceUnits.Mile",
				new Object[] {
				false},
				new Object[] {
				"",
				"",
				"",
				""});
	}
	
	@SuppressWarnings("unused") //INFO:Commands
	public void initCommands()
	{
		comBypass();
		String path = "";
		commandsInput("saj", "saj", "saj.cmd", 
				"/saj [pagenumber]", "/saj ", false,
				"<red>/saj [Seitenzahl] <white>| Infoseite für alle Befehle.",
				"<red>/saj [pagenumber] <white>| Info page for all commands.",
				"<aque>Befehlsrecht für <white>/saj",
				"<aque>Commandright for <white>/saj",
				"<yellow>Basisbefehl für das StatisticalAchievementJunkie Plugin.",
				"<yellow>Groundcommand for the StatisticalAchievementJunkie Plugin.");
		commandsInput("achievement", "achievement", "achievement.cmd", 
				"/achievement [Playername]", "/achievement ", false,
				"<red>/achievement [Spielername] <white>| Öffnet das Achievement Gui.",
				"<red>/achievement [Playername] <white>| Opens the Achievement Gui.",
				"<aque>Befehlsrecht für <white>/achievement",
				"<aque>Commandright for <white>/achievement",
				"<yellow>Öffnet das Achievement Gui.",
				"<yellow>Opens the Achievement Gui.");
		String basePermission = "achievement";
		argumentInput("achievement_info", "info", basePermission,
				"/achievement info <playername>", "/achievement info ", false,
				"<red>/achievement info <Spielername> <white>| Zeigt Informationen über die Anzahl der Achievements des Spielers an.",
				"<red>/achievement info <playername> <white>| Displays information about the number of achievements the player has.",
				"<aque>Befehlsrecht für <white>/achievement info",
				"<aque>Commandright for <white>/achievement info",
				"<yellow>Zeigt Informationen über die Anzahl der Achievements des Spielers an.",
				"<yellow>Displays information about the number of achievements the player has.");
		commandsInput("statistic", "statistic", "statistic.cmd", 
				"/statistic", "/statistic ", false,
				"<red>/statistic <white>| Zeigt eine Liste klickbarere Nachichten an, welche Statistikkategorien es gibt. Mit zusammengezählten Zahlen pro Kategorie.",
				"<red>/statistic <white>| Shows a list of clickable messages, which statistics categories there are. With total numbers per category.",
				"<aque>Befehlsrecht für <white>/statistic",
				"<aque>Commandright for <white>/statistic",
				"<yellow>Zeigt eine Liste klickbarere Nachichten an, welche Statistikkategorien es gibt. Mit zusammengezählten Zahlen pro Kategorie.",
				"<yellow>Shows a list of clickable messages, which statistics categories there are. With total numbers per category.");
		basePermission = "statistic";
		argumentInput("statistic_chatandcommand", "CHAT_AND_COMMAND", basePermission,
				"/statistic CHAT_AND_COMMAND [page] [playername]", "/statistic CHAT_AND_COMMAND ", false,
				"<red>/statistic CHAT_AND_COMMAND [Seite] [Spielername] <white>| Zeigt alle Statistiken zu Chat und Befehlen an.",
				"<red>/statistic CHAT_AND_COMMAND [page] [playername] <white>| Displays all chat and commands statistics.",
				"<aque>Befehlsrecht für <white>/statistic CHAT_AND_COMMAND",
				"<aque>Commandright for <white>/statistic CHAT_AND_COMMAND",
				"<yellow>Zeigt alle Statistiken zu Chat und Befehlen an.",
				"<yellow>Displays all chat and command statistics.");
		argumentInput("statistic_damageanddeath", "DAMAGE_AND_DEATH", basePermission,
				"/statistic DAMAGE_AND_DEATH [page] [playername]", "/statistic DAMAGE_AND_DEATH ", false,
				"<red>/statistic DAMAGE_AND_DEATH [Seite] [Spielername] <white>| Zeigt alle Statistiken zu Schaden und Toden an.",
				"<red>/statistic DAMAGE_AND_DEATH [page] [playername] <white>| Displays all damage and death statistics.",
				"<aque>Befehlsrecht für <white>/statistic DAMAGE_AND_DEATH",
				"<aque>Commandright for <white>/statistic DAMAGE_AND_DEATH",
				"<yellow>Zeigt alle Statistiken zu Schaden und Toden an.",
				"<yellow>Displays all damage and death statistics.");
		argumentInput("statistic_economy", "ECONOMY", basePermission,
				"/statistic ECONOMY [page] [playername]", "/statistic ECONOMY ", false,
				"<red>/statistic ECONOMY [Seite] [Spielername] <white>| Zeigt alle Statistiken zu Geldwerten an.",
				"<red>/statistic ECONOMY [page] [playername] <white>| Displays all statistics related to monetary values.",
				"<aque>Befehlsrecht für <white>/statistic ECONOMY",
				"<aque>Commandright for <white>/statistic ECONOMY",
				"<yellow>Zeigt alle Statistiken zu Geldwerten an.",
				"<yellow>Displays all statistics related to monetary values.");
		argumentInput("statistic_interactionwithblocks", "INTERACTION_WITH_BLOCKS", basePermission,
				"/statistic INTERACTION_WITH_BLOCKS [page] [playername]", "/statistic INTERACTION_WITH_BLOCKS ", false,
				"<red>/statistic INTERACTION_WITH_BLOCKS [Seite] [Spielername] <white>| Zeigt alle Statistiken zu Interaktionen mit Blöcken an.",
				"<red>/statistic INTERACTION_WITH_BLOCKS [page] [playername] <white>| Displays all statistics about block interactions.",
				"<aque>Befehlsrecht für <white>/statistic INTERACTION_WITH_BLOCKS",
				"<aque>Commandright for <white>/statistic INTERACTION_WITH_BLOCKS",
				"<yellow>Zeigt alle Statistiken zu Interaktionen mit Blöcken an.",
				"<yellow>Displays all statistics about block interactions.");
		argumentInput("statistic_miscellaneous", "MISCELLANEOUS", basePermission,
				"/statistic MISCELLANEOUS [page] [playername]", "/statistic MISCELLANEOUS ", false,
				"<red>/statistic MISCELLANEOUS [Seite] [Spielername] <white>| Zeigt alle Statistiken zu Sonstigem an.",
				"<red>/statistic MISCELLANEOUS [page] [playername] <white>| Displays all statistics about block interactions.",
				"<aque>Befehlsrecht für <white>/statistic MISCELLANEOUS",
				"<aque>Commandright for <white>/statistic MISCELLANEOUS",
				"<yellow>Zeigt alle Statistiken zu Sonstigem an.",
				"<yellow>Displays all statistics about block interactions.");
		argumentInput("statistic_movement", "MOVEMENT", basePermission,
				"/statistic MOVEMENT [page] [playername]", "/statistic MOVEMENT ", false,
				"<red>/statistic MOVEMENT [Seite] [Spielername] <white>| Zeigt alle Statistiken zu Bewegung an.",
				"<red>/statistic MOVEMENT [page] [playername] <white>| Displays all MOVEMENT statistics.",
				"<aque>Befehlsrecht für <white>/statistic MOVEMENT",
				"<aque>Commandright for <white>/statistic MOVEMENT",
				"<yellow>Zeigt alle Statistiken zu Bewegung an.",
				"<yellow>Displays all MOVEMENT statistics.");
		argumentInput("statistic_plugins", "PLUGINS", basePermission,
				"/statistic PLUGINS [page] [playername]", "/statistic PLUGINS ", false,
				"<red>/statistic PLUGINS [Seite] [Spielername] <white>| Zeigt alle Statistiken zu custom Plugins an.",
				"<red>/statistic PLUGINS [page] [playername] <white>| Shows all statistics about custom PLUGINS.",
				"<aque>Befehlsrecht für <white>/statistic PLUGINS",
				"<aque>Commandright for <white>/statistic PLUGINS",
				"<yellow>Zeigt alle Statistiken zu custom Plugins an.",
				"<yellow>Shows all statistics about custom PLUGINS.");
		argumentInput("statistic_skill", "SKILL", basePermission,
				"/statistic SKILL [page] [playername]", "/statistic SKILL ", false,
				"<red>/statistic SKILL [Seite] [Spielername] <white>| Zeigt alle Statistiken zu Skillwerten an.",
				"<red>/statistic SKILL [page] [playername] <white>| Shows all statistics about SKILL.",
				"<aque>Befehlsrecht für <white>/statistic SKILL",
				"<aque>Commandright for <white>/statistic SKILL",
				"<yellow>Zeigt alle Statistiken zu Skillwerten an.",
				"<yellow>Shows all statistics about SKILL.");
		argumentInput("statistic_special", "SPECIAL", basePermission,
				"/statistic SPECIAL [page] [playername]", "/statistic SPECIAL ", false,
				"<red>/statistic SPECIAL [Seite] [Spielername] <white>| Zeigt alle Statistiken zu Spezialwerten an.",
				"<red>/statistic SPECIAL [page] [playername] <white>| Displays all statistics on SPECIAL values.",
				"<aque>Befehlsrecht für <white>/statistic SPECIAL",
				"<aque>Commandright for <white>/statistic SPECIAL",
				"<yellow>Zeigt alle Statistiken zu Spezialwerten an.",
				"<yellow>Displays all statistics on SPECIAL values.");
		argumentInput("statistic_time", "TIME", basePermission,
				"/statistic TIME [page] [playername]", "/statistic TIME ", false,
				"<red>/statistic TIME [Seite] [Spielername] <white>| Zeigt alle Statistiken zu Zeitwerten an.",
				"<red>/statistic TIME [page] [playername] <white>| Displays all statistics on  values.",
				"<aque>Befehlsrecht für <white>/statistic TIME",
				"<aque>Commandright for <white>/statistic TIME",
				"<yellow>Zeigt alle Statistiken zu Zeitwerten an.",
				"<yellow>Displays all statistics on  values.");
		argumentInput("statistic_withsubstatistic", "WITH_SUBSTATISTIC", basePermission,
				"/statistic WITH_SUBSTATISTIC <substatistic> [page] [playername]", "/statistic WITH_SUBSTATISTIC ", false,
				"<red>/statistic WITH_SUBSTATISTIC [Substatistik] [Seite] [Spielername] <white>| Zeigt alle Statistiken mit Substatistiken an. Wahlweise im Detail oder zusammengefasst.",
				"<red>/statistic WITH_SUBSTATISTIC [substatistic] [page] [playername] <white>| Shows all statistics with substatistics. Either in detail or summarized.",
				"<aque>Befehlsrecht für <white>/statistic WITH_SUBSTATISTIC",
				"<aque>Commandright for <white>/statistic WITH_SUBSTATISTIC",
				"<yellow>Zeigt alle Statistiken mit Substatistiken an. Wahlweise im Detail oder zusammengefasst.",
				"<yellow>Shows all statistics with substatistics. Either in detail or summarized.");
	}
	
	private void comBypass() //INFO:ComBypass
	{
		List<Bypass.Permission> list = new ArrayList<Bypass.Permission>(EnumSet.allOf(Bypass.Permission.class));
		for(Bypass.Permission ept : list)
		{
			commandsKeys.put("Bypass."+ept.toString().replace("_", ".")
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					"saj."+ept.toString().toLowerCase().replace("_", ".")}));
		}
	}
	
	private void commandsInput(String path, String name, String basePermission, 
			String suggestion, String commandString, boolean putUpCmdPermToBonusMalusSystem,
			String helpInfoGerman, String helpInfoEnglish,
			String dnGerman, String dnEnglish,
			String exGerman, String exEnglish)
	{
		commandsKeys.put(path+".Name"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				name}));
		commandsKeys.put(path+".Permission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				basePermission}));
		commandsKeys.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				suggestion}));
		commandsKeys.put(path+".PutUpCommandPermToBonusMalusSystem"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				putUpCmdPermToBonusMalusSystem}));
		commandsKeys.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				commandString}));
		commandsKeys.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				helpInfoGerman,
				helpInfoEnglish}));
		commandsKeys.put(path+".Displayname"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				dnGerman,
				dnEnglish}));
		commandsKeys.put(path+".Explanation"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				exGerman,
				exEnglish}));
	}
	
	private void argumentInput(String path, String argument, String basePermission, 
			String suggestion, String commandString, boolean putUpCmdPermToBonusMalusSystem,
			String helpInfoGerman, String helpInfoEnglish,
			String dnGerman, String dnEnglish,
			String exGerman, String exEnglish)
	{
		commandsKeys.put(path+".Argument"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				argument}));
		commandsKeys.put(path+".Permission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				basePermission+"."+argument}));
		commandsKeys.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				suggestion}));
		commandsKeys.put(path+".PutUpCommandPermToBonusMalusSystem"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				putUpCmdPermToBonusMalusSystem}));
		commandsKeys.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				commandString}));
		commandsKeys.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				helpInfoGerman,
				helpInfoEnglish}));
		commandsKeys.put(path+".Displayname"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				dnGerman,
				dnEnglish}));
		commandsKeys.put(path+".Explanation"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				exGerman,
				exEnglish}));
	}
	
	public void initLanguage() //INFO:Languages
	{
		languageKeys.put("InputIsWrong",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>Deine Eingabe ist fehlerhaft! Klicke hier auf den Text, um weitere Infos zu bekommen!",
						"<red>Your input is incorrect! Click here on the text to get more information!"}));
		languageKeys.put("NoPermission",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>Du hast dafür keine Rechte!",
						"<red>You dont not have the rights!"}));
		languageKeys.put("NoPlayerExist",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>Der Spieler existiert nicht!",
						"<red>The player does not exist!"}));
		languageKeys.put("NoNumber",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>Das Argument <white>%value% <red>muss eine ganze Zahl sein.",
						"<red>The argument <white>%value% &must be an integer."}));
		languageKeys.put("NoDouble",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>Das Argument <white>%value% <red>muss eine Gleitpunktzahl sein!",
						"<red>The argument <white>%value% <red>must be a floating point number!"}));
		languageKeys.put("IsNegativ",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>Das Argument <white>%value% <red>muss eine positive Zahl sein!",
						"<red>The argument <white>%value% <red>must be a positive number!"}));
		languageKeys.put("GeneralHover",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<yellow>Klick mich!",
						"<yellow>Click me!"}));
		languageKeys.put("Headline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<yellow>=====<gray>[<gold>StatisticalAchievementJunkie<gray>]<yellow>=====",
						"<yellow>=====<gray>[<gold>StatisticalAchievementJunkie<gray>]<yellow>====="}));
		languageKeys.put("Next", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<yellow><understrike>nächste Seite <yellow>==>",
						"<yellow><understrike>next page <yellow>==>"}));
		languageKeys.put("Past", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<yellow><== <understrike>vorherige Seite",
						"<yellow><== <understrike>previous page"}));
		languageKeys.put("IsTrue", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<green>✔",
						"<green>✔"}));
		languageKeys.put("IsFalse", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>✖",
						"<red>✖"}));
		languageKeys.put("Gui.Title", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<gold>Achievements von %player%",
						"<gold>Achievements of %player%"}));
		languageKeys.put("Achievement.Info.Headline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<gray>========<gold>Achievement Info <white>%player%<gray>========",
						"<gray>========<gold>Achievement Info <white>%player%<gray>========"}));
		languageKeys.put("Achievement.Info.AchievedVersusTotal", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<white>Platz <gold>%place%<gray>. <white>mit <green>%achieved%<white>/<gray>%total% <white>Achievements erreicht.",
						"<white>Place <gold>%place%<gray>. <white>with <green>%achieved%<white>/<gray>%total% <white>Achievements achieved."}));
		languageKeys.put("Achievement.Info.PlayerCount", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<white>Insgesamt haben <gold>%playercount% <white>Spieler <gold>%achievementtotal% <white>Achievements erreicht.",
						"<white>In total, <gold>%player count% <white>players have achieved <gold>%achievement total% <white>achievements."}));
		languageKeys.put("Achievement.Info.AverageAndMedian", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<white>Der Durchschnitt liegt dabei bei <gold>%average%<white>/Spieler und der Median bei <gold>%median%<white>.",
						"<white>The average is <gold>%average%<white>/player and the median is <gold>%median%<white>."}));
		initCmdStatistic();
	}
	
	private void initCmdStatistic()
	{
		languageKeys.put("Statistic.NoEntry", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>Es gibt keine Statistik in diesem Sortierungswert.",
						"<red>There are no statistics in this sorting value."}));
		languageKeys.put("Statistic.Unit.Meter", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<white>m",
						"<white>m"}));
		languageKeys.put("Statistic.Unit.Kilometer", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<white>km",
						"<white>km"}));
		languageKeys.put("Statistic.Unit.Yard", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<white>yd",
						"<white>yd"}));
		languageKeys.put("Statistic.TimeScale.UnderDays", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<white>HH<gray>h <white>mm<dark_gray>min",
						"<white>HH<gray>h <white>mm<dark_gray>mins"}));
		languageKeys.put("Statistic.TimeScale.UnderMonths", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<white>dd<yellow>Tage <white>HH<gray>h <white>mm<dark_gray>min",
						"<white>dd<yellow>Days <white>HH<gray>h <white>mm<dark_gray>mins"}));
		languageKeys.put("Statistic.TimeScale.UnderYears", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<white>MM<#FF4D00>Monate <white>dd<yellow>Tage <white>HH<gray>h <white>mm<dark_gray>min",
						"<white>MM<#FF4D00>Months <white>dd<yellow>Days <white>HH<gray>h <white>mm<dark_gray>mins"}));
		languageKeys.put("Statistic.TimeScale.OverYears", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<white>yyyy<red>Jahre <white>MM<#FF4D00>Monate <white>dd<yellow>Tage <white>HH<gray>h <white>mm<dark_gray>min",
						"<white>yyyy<red>Years <white>MM<#FF4D00>Months <white>dd<yellow>Days <white>HH<gray>h <white>mm<dark_gray>mins"}));
		languageKeys.put("Statistic.Base.Headline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<gray>========<gold>Statistik <white>%player%<gray>========",
						"<gray>========<gold>Statistic <white>%player%<gray>========"}));
		languageKeys.put("Statistic.Base."+SortingType.MOVEMENT.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<hover:show_text:'<yellow>Klicke hier für die Bewegungsstatistik!'><click:run_command:'%statisticcmd%'><red>Insgesamte Bewegung: <white>%value%</click></hover>",
						"<hover:show_text:'<yellow>Click here for the movement statistics!'><click:run_command:'%statisticcmd%'><red>Total movement: <white>%value%</click></hover>"}));
		languageKeys.put("Statistic.Base."+SortingType.CHAT_AND_COMMAND.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<hover:show_text:'<yellow>Klicke hier für die Chat und Command Statistik!'><click:run_command:'%statisticcmd%'><red>Insgesamter Chat und Commands: <white>%value%</click></hover>",
						"<hover:show_text:'<yellow>Click here for the chat and command statistics!'><click:run_command:'%statisticcmd%'><red>Total chat and commands: <white>%value%</click></hover>"}));
		languageKeys.put("Statistic.Base."+SortingType.DAMAGE_AND_DEATH.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<hover:show_text:'<yellow>Klicke hier für die Schaden- und Todesstatistik!'><click:run_command:'%statisticcmd%'><red>Insgesamter Schaden und Tode: <white>%value%</click></hover>",
						"<hover:show_text:'<yellow>Click here for the damage and death statistics!'><click:run_command:'%statisticcmd%'><red>Total damage and deaths: <white>%value%</click></hover>"}));
		languageKeys.put("Statistic.Base."+SortingType.ECONOMY.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<hover:show_text:'<yellow>Klicke hier für die Wirtschaftsstatistik!'><click:run_command:'%statisticcmd%'><red>Insgesamte Vermögenswerte: <white>%value%</click></hover>",
						"<hover:show_text:'<yellow>Click here for economy statistics!'><click:run_command:'%statisticcmd%'><red>Total assets: <white>%value%</click></hover>"}));
		languageKeys.put("Statistic.Base."+SortingType.INTERACTION_WITH_BLOCKS.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<hover:show_text:'<yellow>Klicke hier für die Interaktion mit Blöcken Statistik!'><click:run_command:'%statisticcmd%'><red>Insgesamte Interaktion mit Blöcken: <white>%value%</click></hover>",
						"<hover:show_text:'<yellow>Click here for interaction with blocks statistics!'><click:run_command:'%statisticcmd%'><red>Total interaction with blocks: <white>%value%</click></hover>"}));
		languageKeys.put("Statistic.Base."+SortingType.MISCELLANEOUS.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<hover:show_text:'<yellow>Klicke hier für die Sonstiges Statistik!'><click:run_command:'%statisticcmd%'><red>Insgesamtes Sonstiges: <white>%value%</click></hover>",
						"<hover:show_text:'<yellow>Click here for the misc statistics!'><click:run_command:'%statisticcmd%'><red>Total miscellaneous: <white>%value%</click></hover>"}));
		languageKeys.put("Statistic.Base."+SortingType.PLUGINS.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<hover:show_text:'<yellow>Klicke hier für die Pluginstatistik!'><click:run_command:'%statisticcmd%'><red>Insgesamte Custom Pluginswerte: <white>%value%</click></hover>",
						"<hover:show_text:'<yellow>Click here for the plugin statistics!'><click:run_command:'%statisticcmd%'><red>Total custom plugin values: <white>%value%</click></hover>"}));
		languageKeys.put("Statistic.Base."+SortingType.SKILL.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<hover:show_text:'<yellow>Klicke hier für die Skillstatistik!'><click:run_command:'%statisticcmd%'><red>Insgesamter Skillwerte: <white>%value%</click></hover>",
						"<hover:show_text:'<yellow>Click here for skill statistics!'><click:run_command:'%statisticcmd%'><red>Total skill value: <white>%value%</click></hover>"}));
		languageKeys.put("Statistic.Base."+SortingType.SPECIAL.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<hover:show_text:'<yellow>Klicke hier für die Spezialstatistik!'><click:run_command:'%statisticcmd%'><red>Insgesamte Spezialwerte: <white>%value%</click></hover>",
						"<hover:show_text:'<yellow>Click here for special statistics!'><click:run_command:'%statisticcmd%'><red>Total special values: <white>%value%</click></hover>"}));
		languageKeys.put("Statistic.Base."+SortingType.TIME.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<hover:show_text:'<yellow>Klicke hier für die Zeitstatistik!'><click:run_command:'%statisticcmd%'><red>Insgesamte Zeitwerte: <white>%value%</click></hover>",
						"<hover:show_text:'<yellow>Click here for time statistics!'><click:run_command:'%statisticcmd%'><red>Total time: <white>%value%</click></hover>"}));
		languageKeys.put("Statistic.Base."+SortingType.WITH_SUBSTATISTIC.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<hover:show_text:'<yellow>Klicke hier für die Statistik mit Substatistiken!'><click:run_command:'%statisticcmd%'><red>Insgesamte Werte mit Substatistiken: <white>%value%</click></hover>",
						"<hover:show_text:'<yellow>Click here for the statistic with substatistics!'><click:run_command:'%statisticcmd%'><red>Total values ​​with substats: <white>%value%</click></hover>"}));
		languageKeys.put("Statistic.NoEntryExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>Du hast keine Einträge in der Statistik <white>%statistic%<red>!",
						"<red>You have no entries in the statistics <white>%statistic%<red>!"}));
		languageKeys.put("Statistic.Headline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<gray>========<yellow>Statistik %statistic%<gray>========",
						"<gray>========<yellow>Statistik %statistic%<gray>========"}));
		languageKeys.put("Statistic.Substatistic.Info", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>%statistic%<white>: %value%",
						"<red>%statistic%<white>: %value%"}));
		languageKeys.put("Statistic.Statistic.Info", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>%statistic%<white>: %value%",
						"<red>%statistic%<white>: %value%"}));
		languageKeys.put("Statistic.Translate.null", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"Gesamt",
						"Total"}));
		languageKeys.put("Statistic.Translate."+StatisticType.AFK_ONE_MINUTE.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"Afkzeit",
						"Afktime"}));
		languageKeys.put("Statistic.Translate."+StatisticType.ANIMALS_BRED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"Tiere gepaart",
						"Animals bred"}));
		languageKeys.put("Statistic.Translate."+StatisticType.ARMOR_CLEANED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"Rüstung gesäubert",
						"Armor cleaned"}));
		languageKeys.put("Statistic.Translate."+StatisticType.AVIATE_ONE_CM.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"Gleitdistanz",
						"Aviate distance"}));
		languageKeys.put("Statistic.Translate."+StatisticType.BANNER_CLEANED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"Banner gesäubert",
						"Banner cleaned"}));
		languageKeys.put("Statistic.Translate."+StatisticType.BEACON_INTERACTION.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"Interagiert mit Beacon",
						"Interaction with beacon"}));
		languageKeys.put("Statistic.Translate."+StatisticType.BELL_RING.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"Glocke gerungen",
						"Bell ring"}));
		languageKeys.put("Statistic.Translate."+StatisticType.BOAT_ONE_CM.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.BREAK_ITEM.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.BREWINGSTAND_INTERACTION.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.CAKE_SLICES_EATEN.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.CAULDRON_FILLED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.CAULDRON_USED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.CHAT_CHARACTER.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.CHAT_WORD.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.CHEST_OPENED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.CLEAN_SHULKER_BOX.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.CLIMB_ONE_CM.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.COMMAND_EXECUTED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.CRAFT_ITEM.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.CRAFTING_TABLE_INTERACTION.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.CRAWLING_ONE_CM.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.CROUCH_ONE_CM.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.DAMAGE_ABSORBED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.DAMAGE_BLOCKED_BY_SHIELD.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.DAMAGE_DEALT.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.DAMAGE_DEALT_ABSORBED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.DAMAGE_DEALT_RESISTED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.DAMAGE_RESISTED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.DAMAGE_TAKEN.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.DEATHS.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.DISPENSER_INSPECTED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.DROP.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.DROP_COUNT.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.DROPPER_INSPECTED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.ENDERCHEST_OPENED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.ENTITY_KILLED_BY.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.FISH_CAUGHT.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.FLOWER_POTTED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.FLY_ONE_CM.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.FURNACE_INTERACTION.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.HOPPER_INSPECTED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.HORSE_ONE_CM.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.INTERACT_WITH_ANVIL.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.INTERACT_WITH_BLAST_FURNACE.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.INTERACT_WITH_CAMPFIRE.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.INTERACT_WITH_CARTOGRAPHY_TABLE.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.INTERACT_WITH_GRINDSTONE.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.INTERACT_WITH_LECTERN.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.INTERACT_WITH_LOOM.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.INTERACT_WITH_SMITHING_TABLE.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.INTERACT_WITH_SMOKER.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.INTERACT_WITH_STONECUTTER.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.ITEM_ENCHANTED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.JUMP.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.KILL_ENTITY.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.LOGIN.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.MINE_BLOCK.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.MINECART_ONE_CM.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.MOB_KILLS.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.NOTEBLOCK_PLAYED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.NOTEBLOCK_TUNED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.OPEN_BARREL.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.PICKUP.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.PIG_ONE_CM.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.PLAY_ONE_MINUTE.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.PLAYER_KILLS.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.RAID_TRIGGER.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.RAID_WIN.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.RECORD_PLAYED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.SHULKER_BOX_OPENED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.SLEEP_IN_BED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.SNEAK_TIME_ONE_MINUTE.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.SPRINT_ONE_CM.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.STRIDER_ONE_CM.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.SWIM_ONE_CM.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.TALKED_TO_VILLAGER.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.TARGET_HIT.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.TOTAL_ONLINE_ONE_MINUTE.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.TRADED_WITH_VILLAGER.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.TRAPPED_CHEST_TRIGGERED.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.USE_ITEM.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.VOTE.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		languageKeys.put("Statistic.Translate."+StatisticType.WALK_ONE_CM.toString(), 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
	}
	
	public void initFileAchievementGoal() 
	{
		setFileAchievementGoal("block_break_10",
				"<red>Blöcke Abbauen: <white>10",
				"<red>Block break: <white>10",
				StatisticType.MINE_BLOCK, "null", 10,
				new Object[] {"money give %player% 100", "xp add %player% 100 points"},	true,
				new Object[] {
				"<bold><gold>Gratulation!",
				"<gold>Spieler <white>%player% <gold>hat 10 Blöcke abgebaut!",
				"<bold><gold>Congratulations!",
				"<gold>Player <white>%player% <gold>has mined 10 blocks!"}, 0, 
				Material.STONE, new Object[] {
						"<reset><green>Blöcke Abbauen: <white>10",
						"<reset><green>Block mined: <white>10"},
				new Object[] {
						"<reset>Du hast 10",
						"<reset>Blöcke aller Art abgebaut!",
						"<reset>You have mined 10",
						"<reset>blocks of all types!"
				}, true, 
				Material.BARRIER, new Object[] {
						"<reset><red>Blöcke Abbauen: <white>10",
						"<reset><red>Block break: <white>10"},
				null, false);
		setFileAchievementGoal("block_break_100",
				"<red>Blöcke Abbauen: <white>100",
				"<red>Block break: <white>100",
				StatisticType.MINE_BLOCK, "null", 100,
				new Object[] {"money give %player% 100", "xp add %player% 100 points"},	true,
				new Object[] {
				"<bold><gold>Gratulation!",
				"<gold>Spieler <white>%player% <gold>hat 100 Blöcke abgebaut!",
				"<bold><gold>Congratulations!",
				"<gold>Player <white>%player% <gold>has mined 100 blocks!"}, 1, 
				Material.STONE, new Object[] {
						"<reset><green>Blöcke Abbauen: <white>100",
						"<reset><green>Block mined: <white>100"},
				new Object[] {
						"<reset>Du hast 100",
						"<reset>Blöcke aller Art abgebaut!",
						"<reset>You have mined 100",
						"<reset>blocks of all types!"
				}, true, 
				Material.BARRIER, new Object[] {
						"<reset><red>Blöcke Abbauen: <white>100",
						"<reset><red>Block break: <white>100"},
				null, false);
		setFileAchievementGoal("block_break_1M",
				"<red>Blöcke Abbauen: <white>1 Millionen",
				"<red>Block break: <white>1 Million",
				StatisticType.MINE_BLOCK, "null", 1000000,
				new Object[] {"money give %player% 100", "xp add %player% 100 points"},	true,
				new Object[] {
				"<bold><gold>Gratulation!",
				"<gold>Spieler <white>%player% <gold>hat 1 Millionen Blöcke abgebaut!",
				"<bold><gold>Congratulations!",
				"<gold>Player <white>%player% <gold>has mined 1 million blocks!"}, 3, 
				Material.STONE, new Object[] {
						"<reset><green>Blöcke Abbauen: <white>1 Millionen",
						"<reset><green>Block mined: <white>1 million"},
				new Object[] {
						"<reset>Du hast 1 Millionen",
						"<reset>Blöcke aller Art abgebaut!",
						"<reset>You have mined 1 million",
						"<reset>blocks of all types!"
				}, true, 
				Material.BARRIER, new Object[] {
						"<reset><red>Blöcke Abbauen: <white>1 Millionen",
						"<reset><red>Block break: <white>1 Million"},
				null, false);
	}
	
	private void setFileAchievementGoal(String unique, String displayGER, String displayENG, StatisticType st, String matOrEnt,
			long statisticValue,
			Object[] executeCmd, boolean broadcast, Object[] broadcastMsg, int guiSlot,
			Material item, Object[] itemDisplay, Object[] itemLore, boolean itemEnchGlintOverride,
			Material itemIfNot, Object[] itemDisplayIfNot, Object[] itemLoreIfNot, Boolean itemEnchGlintOverrideIfNot)
	{
		LinkedHashMap<String, Language> goal = new LinkedHashMap<>();
		goal.put("Uniquename", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						unique}));
		goal.put("#Uniquename", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"Um ein AchievementGoal zu erstellen, kann man in dem Ordner, wo sich diese Datei befindet unzählige weitere erstellen.",
						"Dabei sollte der Uniquename und der Dateiname übereinstimmen.",
						"Der Uniquename ist der interne Name um für Prozesse indeziert zu sein.",
						"To create an AchievementGoal, you can create countless more in the folder where this file is located.",
						"The unique name and the file name should match.",
						"The unique name is the internal name to be indexed for processes."}));
		goal.put("Displayname", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						displayGER,
						displayENG}));
		goal.put("#Displayname", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Der Displayname ist als Display im Chat gedacht.",
						"Für den Displaynamen in der Gui siehe weiter unten.",
						"",
						"The display name is intended as a display in the chat.",
						"For the display name in the GUI see below."}));
		goal.put("StatisticType", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						st.toString()}));
		goal.put("#StatisticType", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Der StatisticType legt den gewünschten Typen der Statistik fest, die das Achievement anvisieren soll.",
						"Möglich Typen können hier eingesehen werden => https://github.com/Avankziar/StatisticsJunkie/blob/main/src/me/avankziar/sj/general/objects/StatisticType.java",
						"Diese sind bis auf ein paar Ausnahmen exakt die von https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Statistic.html übernommen.",
						"",
						"The StatisticType specifies the desired type of statistic that the achievement should target.",
						"Possible types can be viewed here => https://github.com/Avankziar/StatisticsJunkie/blob/main/src/me/avankziar/sj/general/objects/StatisticType.java",
						"With a few exceptions, these are exactly those from https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Statistic.html."}));
		goal.put("MaterialOrEntityType", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						matOrEnt}));
		goal.put("#MaterialOrEntityType", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Die MaterialOrEntityType legt die Substatistik fest. Denn manche Statistiken erlaube es pro Block(bspw. STONE) oder EntityType(bspw. COW) zu zählen.",
						"Sollte aber keine Substatistik angesteuert sein, bspw. 'Baue 100 Blöcke alle Art ab' so kommt immer 'null' hier rein.",
						"'null' gilt aber auch bei allen Statisitiken, die keine Substatistiken haben. Folgende sind Substatistiken:",
						"DROP_ITEM(Material), PICKUP_ITEM(Material), MINE_BLOCK(Material), USE_ITEM(Material), ",
						"BREAK_ITEM(Material), CRAFT_ITEM(Material), KILL_ENTITY(EntityType), ENTITY_KILLED_BY(EntityType)",
						"",
						"The MaterialOrEntityType defines the substatistics. Some statistics allow counting per block(f.e. STONE) or entity type(f.e. COW).",
						"However, if no sub-statistic is controlled, e.g. 'Mine 100 blocks of all types', then 'zero' will always be entered here.",
						"'null' also applies to all statistics that have no substatistics. The following are substatistics:",
						"DROP_ITEM(Material), PICKUP_ITEM(Material), MINE_BLOCK(Material), USE_ITEM(Material), ",
						"BREAK_ITEM(Material), CRAFT_ITEM(Material), KILL_ENTITY(EntityType), ENTITY_KILLED_BY(EntityType)"}));
		goal.put("GoalValue", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						statisticValue}));
		goal.put("#GoalValue", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Definiert als Ganzzahl, wieviel für das Achievement erreicht werden muss. Ist als Long-Wert (2^63)-1.",
						"",
						"Defines as an integer how much must be achieved for the achievement. As a long value it is (2^63)-1."}));
		goal.put("Reward.ExecuteCommandAsConsole", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, executeCmd));
		goal.put("#Reward.ExecuteCommandAsConsole", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Definiert alle Befehle, welche als Belohnung für den Spieler durch die Konsole ausgeführt werden.",
						"%player% kann als Replacer genutzt werden.",
						"",
						"Defines all commands that are executed by the console as a reward for the player.",
						"%player% can be used as a replacer."}));
		goal.put("Reward.Broadcast", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						broadcast}));
		goal.put("#Reward.Broadcast", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Aktiviert/Deaktiviert den Broadcast an andere Spieler!",
						"",
						"Enable/Disable broadcast to other players!"}));
		goal.put("Reward.BroadcastMessage", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, broadcastMsg));
		goal.put("#Reward.BroadcastMessage", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Die Nachricht, die dem Spieler bzw. allen Spielern gesendet werden, wenn das Achievement erreicht wurde.",
						"",
						"The message sent to the player or all players when the achievement is achieved."}));
		goal.put("Gui.SlotNumber", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						guiSlot}));
		goal.put("#Gui.SlotNumber", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Die Nummer im Gui welche dieses Achievement einnehmen soll. 0 ist der erste Platzt und bis 44 einschließlich ist die erste Seite.",
						"Somit ist ab 45 die Zweite Seite etc.",
						"",
						"The number in the GUI that this achievement should take. 0 is the first place and up to and including 44 is the first page.",
						"So from 45 onwards the second page etc."}));
		goal.put("Gui.Item.Material", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						item.toString()}));
		goal.put("#Gui.Item.Material", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Um ein Achievement in der Gui einzubauen, muss das Material und der Displayname vorhanden sein.",
						"Lore und EnchantmentGlintOverride(Item sieht aus als wäre es verzaubert) müssen nicht vorhanden sein.",
						"",
						"To include an achievement in the GUI, the material and the display name must be present.",
						"Lore and Enchantment Glint Override (item looks like it is enchanted) do not need to be present."}));
		goal.put("Gui.Item.Displayname", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, itemDisplay));
		if(itemLore != null)
		{
			goal.put("Gui.Item.Lore", 
					new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, itemLore));
		}
		goal.put("Gui.Item.EnchantmentGlintOverride", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						itemEnchGlintOverride}));
		if(itemIfNot != null)
		{
			goal.put("Gui.ItemIfNotAchieved.Material", 
					new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
							itemIfNot.toString()}));
			goal.put("#Gui.ItemIfNotAchieved.Material", 
					new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
							"",
							"Um ein Achievement in der Gui einzubauen, welches der Spieler noch nicht hat, kann man ebenso wieder das Material und den Displayname einbauen.",
							"Lore und EnchantmentGlintOverride(Item sieht aus als wäre es verzaubert) müssen nicht vorhanden sein.",
							"Sollte man aber nichts anzeigen wollen, wenn der Spieler nichts erreicht hat, kann man diesen Teil auch herauslöschen.",
							"",
							"To include an achievement in the GUI that the player does not yet have, you can also include the material and the display name.",
							"Lore and Enchantment Glint Override (item looks like it is enchanted) do not need to be present.",
							"However, if you don't want to display anything when the player hasn't achieved anything, you can also delete this part."}));
			if(itemDisplayIfNot != null)
			{
				goal.put("Gui.ItemIfNotAchieved.Displayname", 
						new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, itemDisplayIfNot));
				if(itemLoreIfNot != null)
				{
					goal.put("Gui.ItemIfNotAchieved.Lore", 
							new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, itemLoreIfNot));
				}
				if(itemEnchGlintOverrideIfNot != null)
				{
					goal.put("Gui.ItemIfNotAchieved.EnchantmentGlintOverride", 
							new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
									itemEnchGlintOverrideIfNot}));
				}
			}
		}
		achievementgoalKeys.put(unique, goal);
	}
	
	public void initGui()
	{
		LinkedHashMap<String, Language> w = new LinkedHashMap<>();
		String path = "";
		path = "47"; //Past
		w.put(path+".Material", new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				Material.ARROW.toString()}));
		w.put(path+".HeadTexture",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						"https://textures.minecraft.net/texture/1a1ef398a17f1af7477014517f7f141d886df41a32c738cc8a83fb50297bd921"}));
		w.put(path+".Displayname",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						"<yellow>Zurück"}));
		w.put(path+".ClickFunction."+ClickType.LEFT.toString(),
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						ClickFunctionType.PAGE_PAST.toString()}));
		w.put(path+".ClickFunction."+ClickType.RIGHT.toString(),
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						ClickFunctionType.PAGE_PAST.toString()}));
		path = "51"; //Next
		w.put(path+".Material", new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				Material.PLAYER_HEAD.toString()}));
		w.put(path+".HeadTexture",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						"https://textures.minecraft.net/texture/ac9c67a9f1685cd1da43e841fe7ebb17f6af6ea12a7e1f2722f5e7f0898db9f3"}));
		w.put(path+".Displayname",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						"<yellow>Weiter"}));
		w.put(path+".ClickFunction."+ClickType.LEFT.toString(),
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						ClickFunctionType.PAGE_NEXT.toString()}));
		w.put(path+".ClickFunction."+ClickType.RIGHT.toString(),
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						ClickFunctionType.PAGE_NEXT.toString()}));
		guisKeys.put(GuiType.ACHIEVEMENT, w);
	}
}