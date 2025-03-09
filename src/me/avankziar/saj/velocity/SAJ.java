package me.avankziar.saj.velocity;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import me.avankziar.ifh.velocity.IFH;
import me.avankziar.ifh.velocity.administration.Administration;
import me.avankziar.ifh.velocity.plugin.RegisteredServiceProvider;
import me.avankziar.ifh.velocity.plugin.ServicePriority;
import me.avankziar.saj.general.assistance.Utility;
import me.avankziar.saj.general.cmdtree.BaseConstructor;
import me.avankziar.saj.general.database.YamlHandler;
import me.avankziar.saj.general.database.YamlManager;
import me.avankziar.saj.general.ifh.StatisticProvider;
import me.avankziar.saj.velocity.assistance.BackgroundTask;
import me.avankziar.saj.velocity.database.MysqlHandler;
import me.avankziar.saj.velocity.database.MysqlSetup;
import me.avankziar.saj.velocity.hook.VotifierListener;
import me.avankziar.saj.velocity.listener.JoinLeaveListener;
import me.avankziar.saj.velocity.listener.PlayerChatAndCommandListener;
import me.avankziar.saj.velocity.listener.PlayerStatisticIncrementListener;
import me.avankziar.saj.velocity.metric.Metrics;

@Plugin(
	id = "statisticalachievementjunkie",
	name = "StatisticalAchievementJunkie",
	version = "1-0-0",
	url = "TBA",
	dependencies = {
			@Dependency(id = "interfacehub")
	},
	description = "A MC Plugin for statistical purpose.",
	authors = {"Avankziar"}
)
public class SAJ
{
	private static SAJ plugin;
    private final ProxyServer server;
    private Logger logger = null;
    private Path dataDirectory;
    public String pluginname = "StatisticalAchievementJunkie";
    private final Metrics.Factory metricsFactory;
    private YamlHandler yamlHandler;
    private YamlManager yamlManager;
    private MysqlSetup mysqlSetup;
    private MysqlHandler mysqlHandler;
    private Utility utility;
    private ArrayList<CommandMeta> registeredCmds = new ArrayList<>();
    
	private static Administration administrationConsumer;
    
    @Inject
    public SAJ(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, Metrics.Factory metricsFactory) 
    {
    	SAJ.plugin = this;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;
    }
    
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) 
    {
    	logger = Logger.getLogger("SAJ");
    	PluginDescription pd = server.getPluginManager().getPlugin(pluginname.toLowerCase()).get().getDescription();
        List<String> dependencies = new ArrayList<>();
        pd.getDependencies().stream().allMatch(x -> dependencies.add(x.getId()));
        //https://patorjk.com/software/taag/#p=display&f=ANSI%20Shadow&t=SAJ
		logger.info(" ███████╗ █████╗      ██╗ | Id: "+pd.getId());
		logger.info(" ██╔════╝██╔══██╗     ██║ | Version: "+pd.getVersion().get());
		logger.info(" ███████╗███████║     ██║ | Author: ["+String.join(", ", pd.getAuthors())+"]");
		logger.info(" ╚════██║██╔══██║██   ██║ | Description: "+(pd.getDescription().isPresent() ? pd.getDescription().get() : "/"));
		logger.info(" ███████║██║  ██║╚█████╔╝ | Plugin Website:"+pd.getUrl().toString());
		logger.info(" ╚══════╝╚═╝  ╚═╝ ╚════╝  | Dependencies Plugins: ["+String.join(", ", dependencies)+"]");
        
		setupIFHAdministration();
		
		yamlHandler = new YamlHandler(YamlManager.Type.VELO, pluginname, logger, dataDirectory,
        		(plugin.getAdministration() == null ? null : plugin.getAdministration().getLanguage()));
        setYamlManager(yamlHandler.getYamlManager());
        utility = new Utility(mysqlHandler);
        
        String path = plugin.getYamlHandler().getConfig().getString("IFHAdministrationPath");
		boolean adm = plugin.getAdministration() != null 
				&& plugin.getAdministration().isMysqlPathActive(path);
		if(adm || yamlHandler.getConfig().getBoolean("Mysql.Status", false) == true)
		{
			mysqlSetup = new MysqlSetup(plugin, adm, path);
			mysqlHandler = new MysqlHandler(plugin);
		}
        
		BaseConstructor.init(yamlHandler);
        setListeners();
        setupIFHProvider();
        setupBstats();
        new BackgroundTask(plugin);
    }
    
    public void onDisable(ProxyShutdownEvent event)
	{
    	getServer().getScheduler().tasksByPlugin(plugin).forEach(x -> x.cancel());
    	logger = null;
    	yamlHandler = null;
    	yamlManager = null;
    	mysqlSetup = null;
    	mysqlHandler = null;
    	registeredCmds.forEach(x -> getServer().getCommandManager().unregister(x));
    	getServer().getEventManager().unregisterListeners(plugin);
    	Optional<PluginContainer> ifhp = plugin.getServer().getPluginManager().getPlugin("interfacehub");
        if(!ifhp.isEmpty()) 
        {
        	Optional<PluginContainer> plugins = plugin.getServer().getPluginManager().getPlugin(pluginname.toLowerCase());
        	me.avankziar.ifh.velocity.IFH ifh = IFH.getPlugin();
        	plugins.ifPresent(x -> ifh.getServicesManager().unregister(x));
        }
	}
    
    public static SAJ getPlugin()
    {
    	return SAJ.plugin;
    }
    
    public static void shutdown()
    {
    	SAJ.getPlugin().onDisable(null);
    }
    
    public ProxyServer getServer()
    {
    	return server;
    }
    
    public Logger getLogger()
    {
    	return logger;
    }
    
    public Path getDataDirectory()
    {
    	return dataDirectory;
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
    
    private void setListeners()
    {
    	EventManager em = server.getEventManager();
    	em.register(this, new JoinLeaveListener(plugin));
    	em.register(this, new PlayerChatAndCommandListener());
    	em.register(this, new PlayerStatisticIncrementListener());
    	Optional<PluginContainer> nuv = plugin.getServer().getPluginManager().getPlugin("nuvotifier");
    	Optional<PluginContainer> vp = plugin.getServer().getPluginManager().getPlugin("votifierplus");
        if (nuv.isPresent() || vp.isPresent()) 
        {
        	em.register(this, new VotifierListener());
            return;
        }
    }
    
    private void setupIFHProvider()
	{
		Optional<PluginContainer> ifhp = getServer().getPluginManager().getPlugin("interfacehub");
		Optional<PluginContainer> plugin = getServer().getPluginManager().getPlugin(pluginname.toLowerCase());
        if (ifhp.isEmpty()) 
        {
        	logger.info(pluginname + " dont find InterfaceHub!");
            return;
        }
        me.avankziar.ifh.velocity.IFH ifh = IFH.getPlugin();
        try
        {
            ifh.getServicesManager().register(
             		me.avankziar.ifh.general.statistic.Statistic.class,
             		new StatisticProvider(mysqlHandler), plugin.get(), ServicePriority.Normal);
            logger.info(pluginname + " detected InterfaceHub >>> Statistic.class is provided!");
        } catch(NoClassDefFoundError e){}
	}
    
    private void setupIFHAdministration()
	{ 
		Optional<PluginContainer> ifhp = plugin.getServer().getPluginManager().getPlugin("interfacehub");
        if (ifhp.isEmpty()) 
        {
        	logger.info(pluginname + " dont find InterfaceHub!");
            return;
        }
        me.avankziar.ifh.velocity.IFH ifh = IFH.getPlugin();
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
    	int pluginId = 24850;
        metricsFactory.make(this, pluginId);
	}
}