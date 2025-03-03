package me.avankziar.saj.bungee;

import java.util.logging.Logger;

import me.avankziar.ifh.bungee.IFH;
import me.avankziar.ifh.bungee.administration.Administration;
import me.avankziar.ifh.bungee.plugin.RegisteredServiceProvider;
import me.avankziar.saj.bungee.database.MysqlHandler;
import me.avankziar.saj.bungee.database.MysqlSetup;
import me.avankziar.saj.bungee.hook.VotifierListener;
import me.avankziar.saj.bungee.listener.PlayerChatAndCommandListener;
import me.avankziar.saj.bungee.listener.PlayerJoinLeaveListener;
import me.avankziar.saj.bungee.metric.Metrics;
import me.avankziar.saj.general.database.YamlHandler;
import me.avankziar.saj.general.database.YamlManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class SAJ extends Plugin
{
	public static SAJ plugin;
	public static Logger logger;
	public static String pluginname = "StatisticalAchievementJunkie";
	private static YamlHandler yamlHandler;
	private static YamlManager yamlManager;
	private static MysqlHandler mysqlHandler;
	private static MysqlSetup mysqlSetup;
	private static Administration administrationConsumer;
	
	public void onEnable() 
	{
		plugin = this;
		logger = Logger.getLogger("SJ");
		
		//https://patorjk.com/software/taag/#p=display&f=ANSI%20Shadow&t=SAJ
		logger.info(" ███████╗ █████╗      ██╗ | Version: "+plugin.getDescription().getVersion());
		logger.info(" ██╔════╝██╔══██╗     ██║ | Author: "+plugin.getDescription().getAuthor());
		logger.info(" ███████╗███████║     ██║ | Plugin Website: https://www.spigotmc.org/resources/rootadministration.104833/");
		logger.info(" ╚════██║██╔══██║██   ██║ | Depend Plugins: "+plugin.getDescription().getDepends().toString());
		logger.info(" ███████║██║  ██║╚█████╔╝ | SoftDepend Plugins: "+plugin.getDescription().getSoftDepends().toString());
		logger.info(" ╚══════╝╚═╝  ╚═╝ ╚════╝  | Have Fun^^");
		
		setupIFHAdministration();
		
		yamlHandler = new YamlHandler(YamlManager.Type.BUNGEE, pluginname, logger, plugin.getDataFolder().toPath(),
        		(plugin.getAdministration() == null ? null : plugin.getAdministration().getLanguage()));
        setYamlManager(yamlHandler.getYamlManager());
		
		String path = plugin.getYamlHandler().getConfig().getString("IFHAdministrationPath");
		boolean adm = plugin.getAdministration() != null 
				&& plugin.getAdministration().isMysqlPathActive(path);
		if(adm || yamlHandler.getConfig().getBoolean("Mysql.Status", false) == true)
		{
			mysqlSetup = new MysqlSetup(plugin, adm, path);
			mysqlHandler = new MysqlHandler(plugin);
		}
		
		ListenerSetup();
		setupBstats();
	}
	
	public void onDisable()
	{
		getProxy().getScheduler().cancel(plugin);	
		logger = null;
		yamlHandler = null;
		yamlManager = null;
		mysqlSetup = null;
		mysqlHandler = null;
		getProxy().getPluginManager().unregisterListeners(plugin);
		getProxy().getPluginManager().unregisterCommands(plugin);
		Plugin ifhp = getProxy().getPluginManager().getPlugin("InterfaceHub");
        if(ifhp != null) 
        {
        	 me.avankziar.ifh.bungee.IFH ifh = (me.avankziar.ifh.bungee.IFH) ifhp;
        	 ifh.getServicesManager().unregisterAll(plugin);
        }
		logger.info(pluginname + " is disabled!");
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
	
	public MysqlHandler getMysqlHandler()
	{
		return mysqlHandler;
	}
	
	public MysqlSetup getMysqlSetup()
	{
		return mysqlSetup;
	}
	
	public void setYamlManager(YamlManager yamlManager)
	{
		SAJ.yamlManager = yamlManager;
	}
	
	public void ListenerSetup()
	{
		PluginManager pm = getProxy().getPluginManager();
		pm.registerListener(plugin, new PlayerJoinLeaveListener(plugin));
		pm.registerListener(plugin, new PlayerChatAndCommandListener());
        if(getProxy().getPluginManager().getPlugin("NuVotifier") != null
        		|| getProxy().getPluginManager().getPlugin("VotifierPlus") != null) 
        {
        	pm.registerListener(plugin, new VotifierListener());
        }
	}
	
	private void setupIFHAdministration()
	{ 
		Plugin plugin = getProxy().getPluginManager().getPlugin("InterfaceHub");
        if (plugin == null) 
        {
            return;
        }
        IFH ifh = (IFH) plugin;
        RegisteredServiceProvider<Administration> rsp = ifh
        		.getServicesManager()
        		.getRegistration(Administration.class);
        if (rsp == null) 
        {
            return;
        }
        administrationConsumer = rsp.getProvider();
        if(administrationConsumer != null)
        {
    		logger.info(pluginname + " detected InterfaceHub >>> Administration.class is consumed!");
        }
        return;
	}
	
	public Administration getAdministration()
	{
		return administrationConsumer;
	}
	
	public void setupBstats()
	{
		int pluginId = 24849;
        new Metrics(this, pluginId);
	}
}