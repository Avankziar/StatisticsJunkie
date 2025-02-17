package me.avankziar.saj.general.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import me.avankziar.ifh.general.enums.StatisticType;
import me.avankziar.saj.general.database.MysqlBaseHandler;
import me.avankziar.saj.general.database.MysqlBaseSetup;
import me.avankziar.saj.general.database.MysqlTable;
import me.avankziar.saj.general.database.QueryType;
import me.avankziar.saj.general.database.ServerType;

public class StatisticEntry implements MysqlTable<StatisticEntry>
{
	private long id;
	private UUID uuid;
	private StatisticType statisticType;
	private String materialEntityType;
	private long statisticValue;
	
	public StatisticEntry() {}
	
	public StatisticEntry(long id, UUID uuid, StatisticType statisticType,
			String materialEntityType, long statisticValue)
	{
		setId(id);
		setUUID(uuid);
		setStatisticType(statisticType);
		setMaterialEntityType(materialEntityType);
		setStatisticValue(statisticValue);
	}
	
	public ServerType getServerType()
	{
		return ServerType.ALL;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	public StatisticType getStatisticType() {
		return statisticType;
	}

	public void setStatisticType(StatisticType statisticType) {
		this.statisticType = statisticType;
	}

	public String getMaterialEntityType() {
		return materialEntityType;
	}

	public void setMaterialEntityType(String materialEntityType) {
		this.materialEntityType = materialEntityType;
	}

	public long getStatisticValue() {
		return statisticValue;
	}

	public void setStatisticValue(long statisticValue) {
		this.statisticValue = statisticValue;
	}

	public String getMysqlTableName()
	{
		return "sjStatisticEntry";
	}
	
	public boolean setupMysql(MysqlBaseSetup mysqlSetup, ServerType serverType)
	{
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE IF NOT EXISTS `"+getMysqlTableName()
				+ "` (id bigint AUTO_INCREMENT PRIMARY KEY,"
				+ " player_uuid char(36) NOT NULL,"
				+ " statistic_type text,"
				+ " material_or_entitytype text,"
				+ " statistic_value bigint);");
		return mysqlSetup.baseSetup(sql.toString());
	}

	@Override
	public boolean create(Connection conn)
	{
		try
		{
			String sql = "INSERT INTO `" + getMysqlTableName()
					+ "`(`player_uuid`, `statistic_type`, `material_or_entitytype`, `statistic_value`) " 
					+ "VALUES(?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, getUUID().toString());
	        ps.setString(2, getStatisticType().toString());
	        ps.setString(3, getMaterialEntityType());
	        ps.setLong(4, getStatisticValue());
	        int i = ps.executeUpdate();
	        MysqlBaseHandler.addRows(QueryType.INSERT, i);
	        return true;
		} catch (SQLException e)
		{
			this.log(MysqlBaseHandler.getLogger(), Level.WARNING, "SQLException! Could not create a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public boolean update(Connection conn, String whereColumn, Object... whereObject)
	{
		try
		{
			String sql = "UPDATE `" + getMysqlTableName()
				+ "` SET `player_uuid` = ?, `statistic_type` = ?, `material_or_entitytype` = ?, `statistic_value` = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getUUID().toString());
	        ps.setString(2, getStatisticType().toString());
	        ps.setString(3, getMaterialEntityType());
	        ps.setLong(4, getStatisticValue());
			int i = 5;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}			
			int u = ps.executeUpdate();
			MysqlBaseHandler.addRows(QueryType.UPDATE, u);
			return true;
		} catch (SQLException e)
		{
			this.log(MysqlBaseHandler.getLogger(), Level.WARNING, "SQLException! Could not update a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public ArrayList<StatisticEntry> get(Connection conn, String orderby, String limit, String whereColumn, Object... whereObject)
	{
		try
		{
			String sql = "SELECT * FROM `" + getMysqlTableName()
				+ "` WHERE "+whereColumn+" ORDER BY "+orderby+limit;
			PreparedStatement ps = conn.prepareStatement(sql);
			int i = 1;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}
			
			ResultSet rs = ps.executeQuery();
			MysqlBaseHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
			ArrayList<StatisticEntry> al = new ArrayList<>();
			while (rs.next()) 
			{
				al.add(new StatisticEntry(rs.getInt("id"),
						UUID.fromString(rs.getString("player_uuid")),
						StatisticType.valueOf(rs.getString("statistic_type")),
						rs.getString("material_or_entitytype"),
						rs.getLong("statistic_value")));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(MysqlBaseHandler.getLogger(), Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
}