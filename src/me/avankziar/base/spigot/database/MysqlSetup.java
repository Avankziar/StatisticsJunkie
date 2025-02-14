package me.avankziar.base.spigot.database;

import java.util.logging.Level;

import me.avankziar.base.general.database.MysqlBaseSetup;
import me.avankziar.base.general.database.ServerType;
import me.avankziar.base.spigot.BM;

public class MysqlSetup extends MysqlBaseSetup
{
	public MysqlSetup(BM plugin, boolean adm, String path)
	{
		super(plugin.getLogger());
		if(adm)
		{
			plugin.getLogger().log(Level.INFO, "Using IFH Administration");
		}
		String host = adm ? plugin.getAdministration().getHost(path)
				: plugin.getYamlHandler().getConfig().getString("Mysql.Host");
		int port = adm ? plugin.getAdministration().getPort(path)
				: plugin.getYamlHandler().getConfig().getInt("Mysql.Port", 3306);
		String database = adm ? plugin.getAdministration().getDatabase(path)
				: plugin.getYamlHandler().getConfig().getString("Mysql.DatabaseName");
		String user = adm ? plugin.getAdministration().getUsername(path)
				: plugin.getYamlHandler().getConfig().getString("Mysql.User");
		String password = adm ? plugin.getAdministration().getPassword(path)
				: plugin.getYamlHandler().getConfig().getString("Mysql.Password");
		boolean isAutoConnect = adm ? plugin.getAdministration().isAutoReconnect(path)
				: plugin.getYamlHandler().getConfig().getBoolean("Mysql.AutoReconnect", true);
		boolean isVerifyServerCertificate = adm ? plugin.getAdministration().isVerifyServerCertificate(path)
				: plugin.getYamlHandler().getConfig().getBoolean("Mysql.VerifyServerCertificate", false);
		boolean isSSLEnabled = adm ? plugin.getAdministration().useSSL(path)
				: plugin.getYamlHandler().getConfig().getBoolean("Mysql.SSLEnabled", false);
		init(host, port, database, user, password, isAutoConnect, isVerifyServerCertificate, isSSLEnabled);
		loadMysqlSetup(ServerType.SPIGOT);
	}
}