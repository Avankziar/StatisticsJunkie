package me.avankziar.sj.general.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import me.avankziar.sj.general.database.MysqlBaseHandler;
import me.avankziar.sj.general.database.MysqlBaseSetup;
import me.avankziar.sj.general.database.MysqlTable;
import me.avankziar.sj.general.database.QueryType;
import me.avankziar.sj.general.database.ServerType;

public class AchievementGoal implements MysqlTable<AchievementGoal>
{
	private long id;
	private UUID uuid;
	private String achievementGoalUniqueName;
	private long receivedTime;
	
	public AchievementGoal() {}
	
	public AchievementGoal(long id, UUID uuid, String achievementGoalUniqueName, long receivedTime)
	{
		setId(id);
		setUUID(uuid);
		setAchievementGoalUniqueName(achievementGoalUniqueName);
		setReceivedTime(receivedTime);
	}
	
	public ServerType getServerType()
	{
		return ServerType.SPIGOT;
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

	public String getAchievementGoalUniqueName() {
		return achievementGoalUniqueName;
	}

	public void setAchievementGoalUniqueName(String achievementGoalUniqueName) {
		this.achievementGoalUniqueName = achievementGoalUniqueName;
	}

	public long getReceivedTime() {
		return receivedTime;
	}

	public void setReceivedTime(long receivedTime) {
		this.receivedTime = receivedTime;
	}

	public String getMysqlTableName()
	{
		return "sjAchievementGoal";
	}
	
	public boolean setupMysql(MysqlBaseSetup mysqlSetup, ServerType serverType)
	{
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE IF NOT EXISTS `"+getMysqlTableName()
				+ "` (id bigint AUTO_INCREMENT PRIMARY KEY,"
				+ " player_uuid char(36) NOT NULL,"
				+ " achievement_goal_uniquename text,"
				+ " received_time bigint);");
		return mysqlSetup.baseSetup(sql.toString());
	}

	@Override
	public boolean create(Connection conn)
	{
		try
		{
			String sql = "INSERT INTO `" + getMysqlTableName()
					+ "`(`player_uuid`, `achievement_goal_uniquename`, `received_time`) " 
					+ "VALUES(?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, getUUID().toString());
	        ps.setString(2, getAchievementGoalUniqueName());
	        ps.setLong(3, getReceivedTime());
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
				+ "` SET `player_uuid` = ?, `achievement_goal_uniquename` = ?, `received_time` = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getUUID().toString());
		    ps.setString(2, getAchievementGoalUniqueName());
		    ps.setLong(3, getReceivedTime());
			int i = 4;
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
	public ArrayList<AchievementGoal> get(Connection conn, String orderby, String limit, String whereColumn, Object... whereObject)
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
			ArrayList<AchievementGoal> al = new ArrayList<>();
			while (rs.next()) 
			{
				al.add(new AchievementGoal(rs.getInt("id"),
						UUID.fromString(rs.getString("player_uuid")),
						rs.getString("achievement_goal_uniquename"),
						rs.getLong("received_time")));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(MysqlBaseHandler.getLogger(), Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
}