package me.avankziar.saj.spigot;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ifh.general.statistic.StatisticType;
import me.avankziar.ifh.general.statistic.StatisticType.SortingType;
import me.avankziar.ifh.spigot.administration.Administration;
import me.avankziar.ifh.spigot.tobungee.chatlike.MessageToBungee;
import me.avankziar.ifh.spigot.tovelocity.chatlike.MessageToVelocity;
import me.avankziar.saj.general.assistance.Utility;
import me.avankziar.saj.general.cmdtree.ArgumentConstructor;
import me.avankziar.saj.general.cmdtree.BaseConstructor;
import me.avankziar.saj.general.cmdtree.CommandConstructor;
import me.avankziar.saj.general.cmdtree.CommandSuggest;
import me.avankziar.saj.general.cmdtree.CommandSuggest.Type;
import me.avankziar.saj.general.database.YamlHandler;
import me.avankziar.saj.general.database.YamlManager;
import me.avankziar.saj.general.objects.PlayerData;
import me.avankziar.saj.spigot.ModifierValueEntry.Bypass;
import me.avankziar.saj.spigot.assistance.BackgroundTask;
import me.avankziar.saj.spigot.cmd.AchievementCommandExecutor;
import me.avankziar.saj.spigot.cmd.SAJCommandExecutor;
import me.avankziar.saj.spigot.cmd.StatisticCommandExecutor;
import me.avankziar.saj.spigot.cmd.TabCompletion;
import me.avankziar.saj.spigot.cmd.achievement.ARG_Info;
import me.avankziar.saj.spigot.cmd.statistic.ARG_DamageAndDeath;
import me.avankziar.saj.spigot.cmd.statistic.ARG_Economy;
import me.avankziar.saj.spigot.cmd.statistic.ARG_InteractionWithBlocks;
import me.avankziar.saj.spigot.cmd.statistic.ARG_Miscellaneous;
import me.avankziar.saj.spigot.cmd.statistic.ARG_Movement;
import me.avankziar.saj.spigot.cmd.statistic.ARG_Plugins;
import me.avankziar.saj.spigot.cmd.statistic.ARG_Skill;
import me.avankziar.saj.spigot.cmd.statistic.ARG_Special;
import me.avankziar.saj.spigot.cmd.statistic.ARG_Time;
import me.avankziar.saj.spigot.cmd.statistic.ARG_WithSubstatistic;
import me.avankziar.saj.spigot.cmdtree.ArgumentModule;
import me.avankziar.saj.spigot.database.MysqlHandler;
import me.avankziar.saj.spigot.database.MysqlSetup;
import me.avankziar.saj.spigot.gui.listener.GuiPreListener;
import me.avankziar.saj.spigot.gui.listener.UpperListener;
import me.avankziar.saj.spigot.handler.FileAchievementGoalHandler;
import me.avankziar.saj.spigot.handler.StatisticHandler;
import me.avankziar.saj.spigot.hook.VotifierListener;
import me.avankziar.saj.spigot.hook.WorldGuardHook;
import me.avankziar.saj.spigot.listener.JoinLeaveListener;
import me.avankziar.saj.spigot.listener.PlayerMoveListener;
import me.avankziar.saj.spigot.listener.StatisticIncrementListener;
import me.avankziar.saj.spigot.metric.Metrics;

public class SAJ extends JavaPlugin
{
	public static Logger logger;
	private static SAJ plugin;
	public static String pluginname = "StatisticalAchievementJunkie";
	private YamlHandler yamlHandler;
	private YamlManager yamlManager;
	private MysqlSetup mysqlSetup;
	private MysqlHandler mysqlHandler;
	private Utility utility;
	private BackgroundTask backgroundTask;
	private static boolean worldGuard = false;
	
	private Administration administrationConsumer;
	
	private MessageToVelocity mtvConsumer;
	private MessageToBungee mtbConsumer;
	
	public void onLoad() 
	{
		setupWordEditGuard();
	}
	
	public void onEnable()
	{
		plugin = this;
		logger = getLogger();
		
		//https://patorjk.com/software/taag/#p=display&f=ANSI%20Shadow&t=SAJ
		logger.info(" ███████╗ █████╗      ██╗ | API-Version: "+plugin.getDescription().getAPIVersion());
		logger.info(" ██╔════╝██╔══██╗     ██║ | Author: "+plugin.getDescription().getAuthors().toString());
		logger.info(" ███████╗███████║     ██║ | Plugin Website: "+plugin.getDescription().getWebsite());
		logger.info(" ╚════██║██╔══██║██   ██║ | Depend Plugins: "+plugin.getDescription().getDepend().toString());
		logger.info(" ███████║██║  ██║╚█████╔╝ | SoftDepend Plugins: "+plugin.getDescription().getSoftDepend().toString());
		logger.info(" ╚══════╝╚═╝  ╚═╝ ╚════╝  | LoadBefore: "+plugin.getDescription().getLoadBefore().toString());
		
		setupIFHAdministration();
		
		yamlHandler = new YamlHandler(YamlManager.Type.SPIGOT, pluginname, logger, plugin.getDataFolder().toPath(),
        		(plugin.getAdministration() == null ? null : plugin.getAdministration().getLanguage()));
        setYamlManager(yamlHandler.getYamlManager());
		
		String path = plugin.getYamlHandler().getConfig().getString("IFHAdministrationPath");
		boolean adm = plugin.getAdministration() != null 
				&& plugin.getYamlHandler().getConfig().getBoolean("useIFHAdministration")
				&& plugin.getAdministration().isMysqlPathActive(path);
		if(adm || yamlHandler.getConfig().getBoolean("Mysql.Status", false) == true)
		{
			mysqlSetup = new MysqlSetup(plugin, adm, path);
			mysqlHandler = new MysqlHandler(plugin);
		} else
		{
			logger.severe("MySQL is not set in the Plugin " + pluginname + "!");
			Bukkit.getPluginManager().getPlugin(pluginname).getPluginLoader().disablePlugin(this);
			return;
		}
		
		BaseConstructor.init(yamlHandler);
		utility = new Utility(mysqlHandler);
		backgroundTask = new BackgroundTask(this);
		
		setupBypassPerm();
		setupCommandTree();
		setupListeners();
		setupIFHConsumer();
		setupBstats();
		FileAchievementGoalHandler.init(false);
		StatisticHandler.processStatistic();
	}
	
	public void onDisable()
	{
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll(this);
		yamlHandler = null;
		yamlManager = null;
		mysqlSetup = null;
		mysqlHandler = null;
		if(getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
	    	getServer().getServicesManager().unregisterAll(plugin);
	    }
		logger.info(pluginname + " is disabled!");
		logger = null;
	}

	public static SAJ getPlugin()
	{
		return plugin;
	}
	
	public static void shutdown()
	{
		SAJ.getPlugin().onDisable();
	}
	
	public YamlHandler getYamlHandler() 
	{
		return yamlHandler;
	}
	
	public YamlManager getYamlManager()
	{
		return yamlManager;
	}

	public void setYamlManager(YamlManager yamlManager)
	{
		this.yamlManager = yamlManager;
	}
	
	public MysqlSetup getMysqlSetup() 
	{
		return mysqlSetup;
	}
	
	public MysqlHandler getMysqlHandler()
	{
		return mysqlHandler;
	}
	
	public Utility getUtility()
	{
		return utility;
	}
	
	public BackgroundTask getBackgroundTask()
	{
		return backgroundTask;
	}
	
	public String getServername()
	{
		return getPlugin().getAdministration() != null ? getPlugin().getAdministration().getSpigotServerName() 
				: getPlugin().getYamlHandler().getConfig().getString("ServerName");
	}
	
	private void setupCommandTree()
	{
		LinkedHashMap<Integer, ArrayList<String>> playerMapI = new LinkedHashMap<>();
		ArrayList<String> playerarray = new ArrayList<>();
		plugin.getMysqlHandler().getFullList(new PlayerData(), "`player_name` ASC", "`id` > ?", 0)
		.forEach(x ->playerarray.add(x.getName()));
		
		playerMapI.put(1, playerarray);
		
		ArrayList<String> sortingType = new ArrayList<>();
		new ArrayList<StatisticType.SortingType>(EnumSet.allOf(StatisticType.SortingType.class)).forEach(x -> sortingType.add(x.toString()));
		
		LinkedHashMap<Integer, ArrayList<String>> statistic_page_playername = new LinkedHashMap<>();
		statistic_page_playername.put(1, sortingType);
		statistic_page_playername.put(3, playerarray);
		
		ArrayList<String> withSubStatistic = new ArrayList<>();
		HashSet<StatisticType> hm = StatisticType.getStatisticTypes(SortingType.WITH_SUBSTATISTIC);
		if(hm != null)
		{
			for(StatisticType sct : hm)
			{
				withSubStatistic.add(sct.toString());
			}
		}		
		
		LinkedHashMap<Integer, ArrayList<String>> statistic_sub_page_playername = new LinkedHashMap<>();
		statistic_sub_page_playername.put(1, sortingType);
		statistic_sub_page_playername.put(2, withSubStatistic);
		statistic_sub_page_playername.put(4, playerarray);
		
		TabCompletion tab = new TabCompletion();
		
		CommandConstructor saj = new CommandConstructor(CommandSuggest.Type.SAJ, "saj", false, false);
		registerCommand(saj, new SAJCommandExecutor(plugin, saj), tab);
		
		ArgumentConstructor ach_info = new ArgumentConstructor(Type.ACHIEVEMENT_INFO, "achievement_info",
				0, 0, 1, false, false, playerMapI);
		
		CommandConstructor achievement = new CommandConstructor(CommandSuggest.Type.ACHIEVEMENT, "achievement", false, false,
				ach_info);
		registerCommand(achievement, new AchievementCommandExecutor(plugin, achievement), tab);
		
		new ARG_Info(ach_info);
		
		ArgumentConstructor st_dad = new ArgumentConstructor(Type.STATISTIC_DAMAGEANDDEATH, "statistic_damageanddeath",
				0, 0, 2, false, false, statistic_page_playername);
		ArgumentConstructor st_eco = new ArgumentConstructor(Type.STATISTIC_ECONOMY, "statistic_economy",
				0, 0, 2, false, false, statistic_page_playername);
		ArgumentConstructor st_iwb = new ArgumentConstructor(Type.STATISTIC_INTERACTIONWITHBLOCKS, "statistic_interactionwithblocks",
				0, 0, 2, false, false, statistic_page_playername);
		ArgumentConstructor st_misc = new ArgumentConstructor(Type.STATISTIC_MISCELLANEOUS, "statistic_miscellaneous",
				0, 0, 2, false, false, statistic_page_playername);
		ArgumentConstructor st_move = new ArgumentConstructor(Type.STATISTIC_MOVEMENT, "statistic_movement",
				0, 0, 2, false, false, statistic_page_playername);
		ArgumentConstructor st_plugin = new ArgumentConstructor(Type.STATISTIC_PLUGINS, "statistic_plugins",
				0, 0, 2, false, false, statistic_page_playername);
		ArgumentConstructor st_skill = new ArgumentConstructor(Type.STATISTIC_SKILL, "statistic_skill",
				0, 0, 2, false, false, statistic_page_playername);
		ArgumentConstructor st_special = new ArgumentConstructor(Type.STATISTIC_SPECIAL, "statistic_special",
				0, 0, 2, false, false, statistic_page_playername);
		ArgumentConstructor st_time = new ArgumentConstructor(Type.STATISTIC_TIME, "statistic_time",
				0, 0, 2, false, false, statistic_page_playername);
		ArgumentConstructor st_wsub = new ArgumentConstructor(Type.STATISTIC_WITHSUBSTATISTIC, "statistic_withsubstatistic",
				0, 0, 3, false, false, statistic_sub_page_playername);
		
		CommandConstructor statistic = new CommandConstructor(CommandSuggest.Type.STATISTIC, "statistic", false, false,
				st_dad, st_eco, st_iwb, st_misc, st_move, st_plugin, st_skill, st_special, st_time, st_wsub);
		registerCommand(statistic, new StatisticCommandExecutor(plugin, statistic), tab);
		new ARG_DamageAndDeath(st_dad);
		new ARG_Economy(st_eco);
		new ARG_InteractionWithBlocks(st_iwb);
		new ARG_Miscellaneous(st_misc);
		new ARG_Movement(st_move);
		new ARG_Plugins(st_plugin);
		new ARG_Skill(st_skill);
		new ARG_Special(st_special);
		new ARG_Time(st_time);
		new ARG_WithSubstatistic(st_wsub);
	}
	
	private void setupBypassPerm()
	{
		String path = "Bypass.";
		for(Bypass.Permission bypass : new ArrayList<Bypass.Permission>(EnumSet.allOf(Bypass.Permission.class)))
		{
			Bypass.set(bypass, yamlHandler.getCommands().getString(path+bypass.toString().replace("_", ".")));
		}
	}
	
	public ArrayList<BaseConstructor> getHelpList()
	{
		return BaseConstructor.getHelpList();
	}
	
	public ArrayList<CommandConstructor> getCommandTree()
	{
		return BaseConstructor.getCommandTree();
	}
	
	public void registerCommand(CommandConstructor cc, CommandExecutor ce, TabCompletion tab)
	{
		registerCommand(cc.getPath(), cc.getName());
		getCommand(cc.getName()).setExecutor(ce);
		getCommand(cc.getName()).setTabCompleter(tab);
	}
	
	public void registerCommand(String... aliases) 
	{
		PluginCommand command = getCommand(aliases[0], plugin);
	 
		command.setAliases(Arrays.asList(aliases));
		getCommandMap().register(plugin.getDescription().getName(), command);
	}
	 
	private static PluginCommand getCommand(String name, SAJ plugin) 
	{
		PluginCommand command = null;
		try 
		{
			Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			c.setAccessible(true);
	 
			command = c.newInstance(name, plugin);
		} catch (SecurityException e) 
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e) 
		{
			e.printStackTrace();
		} catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		} catch (InstantiationException e) 
		{
			e.printStackTrace();
		} catch (InvocationTargetException e) 
		{
			e.printStackTrace();
		} catch (NoSuchMethodException e) 
		{
			e.printStackTrace();
		}
	 
		return command;
	}
	 
	private static CommandMap getCommandMap() 
	{
		CommandMap commandMap = null;
	 
		try {
			if (Bukkit.getPluginManager() instanceof SimplePluginManager) 
			{
				Field f = SimplePluginManager.class.getDeclaredField("commandMap");
				f.setAccessible(true);
	 
				commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
			}
		} catch (NoSuchFieldException e) 
		{
			e.printStackTrace();
		} catch (SecurityException e) 
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e) 
		{
			e.printStackTrace();
		} catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		}
	 
		return commandMap;
	}
	
	public LinkedHashMap<String, ArgumentModule> getArgumentMap()
	{
		return BaseConstructor.getArgumentMapSpigot();
	}
	
	private void setupListeners()
	{
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new JoinLeaveListener(), plugin);
		pm.registerEvents(new StatisticIncrementListener(), plugin);
		pm.registerEvents(new PlayerMoveListener(), plugin);
		
		pm.registerEvents(new GuiPreListener(plugin), plugin);
		pm.registerEvents(new UpperListener(plugin), plugin);
		if(!SAJ.getPlugin().getYamlHandler().getConfig().getBoolean("IsInstalledOnProxy"))
		{
			//VoteListener
			if(plugin.getServer().getPluginManager().isPluginEnabled("NuVotifier")
					|| plugin.getServer().getPluginManager().isPluginEnabled("VotifierPlus")) 
		    {
		    	pm.registerEvents(new VotifierListener(), plugin);
		    }
		}
	}
	
	public boolean existHook(String externPluginName)
	{
		if(plugin.getServer().getPluginManager().getPlugin(externPluginName) == null)
		{
			return false;
		}
		logger.info(pluginname+" hook with "+externPluginName);
		return true;
	}
	
	private void setupIFHAdministration()
	{ 
		if(!plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
	    	return;
	    }
		RegisteredServiceProvider<me.avankziar.ifh.spigot.administration.Administration> rsp = 
                getServer().getServicesManager().getRegistration(Administration.class);
		if (rsp == null) 
		{
		   return;
		}
		administrationConsumer = rsp.getProvider();
		logger.info(pluginname + " detected InterfaceHub >>> Administration.class is consumed!");
	}
	
	public Administration getAdministration()
	{
		return administrationConsumer;
	}
	
	public void setupIFHConsumer()
	{
		setupIFHMessageToVelocity();
	}
	
	private void setupIFHMessageToVelocity() 
	{
        if(Bukkit.getPluginManager().getPlugin("InterfaceHub") == null) 
        {
            return;
        }
        new BukkitRunnable()
        {
        	int i = 0;
			@Override
			public void run()
			{
				try
				{
					if(i == 20)
				    {
						cancel();
						setupIFHMessageTBungee();
						return;
				    }
				    RegisteredServiceProvider<me.avankziar.ifh.spigot.tovelocity.chatlike.MessageToVelocity> rsp = 
		                             getServer().getServicesManager().getRegistration(
		                            		 me.avankziar.ifh.spigot.tovelocity.chatlike.MessageToVelocity.class);
				    if(rsp == null) 
				    {
				    	i++;
				        return;
				    }
				    mtvConsumer = rsp.getProvider();
				    logger.info(pluginname + " detected InterfaceHub >>> MessageToVelocity.class is consumed!");
				    cancel();
				} catch(NoClassDefFoundError e)
				{
					cancel();
				}			    
			}
        }.runTaskTimer(plugin, 20L, 20*2);
	}
	
	public MessageToVelocity getMtV()
	{
		return mtvConsumer;
	}
	
	private void setupIFHMessageTBungee() 
	{
        if(Bukkit.getPluginManager().getPlugin("InterfaceHub") == null) 
        {
            return;
        }
        new BukkitRunnable()
        {
        	int i = 0;
			@Override
			public void run()
			{
				try
				{
					if(i == 20)
				    {
						cancel();
						return;
				    }
				    RegisteredServiceProvider<me.avankziar.ifh.spigot.tobungee.chatlike.MessageToBungee> rsp = 
		                             getServer().getServicesManager().getRegistration(
		                            		 me.avankziar.ifh.spigot.tobungee.chatlike.MessageToBungee.class);
				    if(rsp == null) 
				    {
				    	i++;
				        return;
				    }
				    mtbConsumer = rsp.getProvider();
				    logger.info(pluginname + " detected InterfaceHub >>> MessageToBungee.class is consumed!");
				    cancel();
				} catch(NoClassDefFoundError e)
				{
					cancel();
				}			    
			}
        }.runTaskTimer(plugin, 20L, 20*2);
	}
	
	public MessageToBungee getMtB()
	{
		return mtbConsumer;
	}
	
	private void setupWordEditGuard()
	{
		if(Bukkit.getPluginManager().getPlugin("WorldGuard") != null)
		{
			worldGuard = WorldGuardHook.init();
		}
	}
	
	public static boolean getWorldGuard()
	{
		return worldGuard;
	}
	
	public void setupBstats()
	{
		int pluginId = 24848;
        new Metrics(this, pluginId);
	}
	
	public void reload()
	{
		yamlHandler = new YamlHandler(YamlManager.Type.SPIGOT, pluginname, logger, plugin.getDataFolder().toPath(),
        		(plugin.getAdministration() == null ? null : plugin.getAdministration().getLanguage()));
        setYamlManager(yamlHandler.getYamlManager());
        FileAchievementGoalHandler.init(true);
	}
}