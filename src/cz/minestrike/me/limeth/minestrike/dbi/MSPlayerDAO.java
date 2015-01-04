package cz.minestrike.me.limeth.minestrike.dbi;

import org.skife.jdbi.v2.Batch;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;

@RegisterMapper(MSPlayerDataMapper.class)
public interface MSPlayerDAO
{
	String FIELD_DATA_USERNAME = "username";
	String FIELD_DATA_XP = "xp";
	String FIELD_DATA_KILLS = "kills";
	String FIELD_DATA_ASSISTS = "assists";
	String FIELD_DATA_DEATHS = "deaths";
	String FIELD_DATA_PLAYTIME = "playtime";
	String FIELD_EQUIPMENT_USERNAME = FIELD_DATA_USERNAME;
	String FIELD_EQUIPMENT_SERVER = "server";
	String FIELD_EQUIPMENT_ID = "id";
	String FIELD_EQUIPMENT_DATA = "data";
	String VALUE_EQUIPMENT_SERVER = "minestrike";

	static void prepareTableData()
	{
		DBI dbi = MineStrike.getDBI();
		Handle handle = dbi.open();
		Batch batch = handle.createBatch();
		
		batch.define("table", MSConfig.getMySQLTablePlayers());
		batch.add("CREATE TABLE IF NOT EXISTS <table> ("
		    	+ "`" + FIELD_DATA_USERNAME + "` varchar(16) COLLATE utf8_czech_ci NOT NULL,"
				+ "`" + FIELD_DATA_XP + "` int(11) NOT NULL,"
				+ "`" + FIELD_DATA_KILLS + "` int(11) NOT NULL,"
				+ "`" + FIELD_DATA_ASSISTS + "` int(11) NOT NULL,"
				+ "`" + FIELD_DATA_DEATHS + "` int(11) NOT NULL,"
				+ "`" + FIELD_DATA_PLAYTIME + "` bigint(20) NOT NULL,"
				+ "PRIMARY KEY (`" + FIELD_DATA_USERNAME + "`)"
			+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;");
		batch.add("ALTER TABLE <table> ADD COLUMN `" + FIELD_DATA_USERNAME + "` varchar(16) COLLATE utf8_czech_ci NOT NULL");
		batch.add("ALTER TABLE <table> ADD COLUMN `" + FIELD_DATA_XP + "` int(11) NOT NULL");
		batch.add("ALTER TABLE <table> ADD COLUMN `" + FIELD_DATA_KILLS + "` int(11) NOT NULL");
		batch.add("ALTER TABLE <table> ADD COLUMN `" + FIELD_DATA_ASSISTS + "` int(11) NOT NULL");
		batch.add("ALTER TABLE <table> ADD COLUMN `" + FIELD_DATA_DEATHS + "` int(11) NOT NULL");
		batch.add("ALTER TABLE <table> ADD COLUMN `" + FIELD_DATA_PLAYTIME + "` bigint(20) NOT NULL");
		batch.execute();
		handle.close();
	}
	
	static void prepareTableEquipment()
	{
		DBI dbi = MineStrike.getDBI();
		Handle handle = dbi.open();
		Batch batch = handle.createBatch();
		
		batch.define("table", MSConfig.getMySQLTableEquipment());
		batch.add("CREATE TABLE IF NOT EXISTS <table> ("
		    	+ "`" + FIELD_EQUIPMENT_USERNAME + "` varchar(16) COLLATE utf8_czech_ci NOT NULL,"
				+ "`" + FIELD_EQUIPMENT_SERVER + "` varchar(16) NOT NULL,"
				+ "`" + FIELD_EQUIPMENT_ID + "` varchar(64) NOT NULL,"
				+ "`" + FIELD_EQUIPMENT_DATA + "` varchar(256) NOT NULL,"
			+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;");
		batch.add("ALTER TABLE <table> ADD COLUMN `" + FIELD_EQUIPMENT_USERNAME + "` varchar(16) COLLATE utf8_czech_ci NOT NULL");
		batch.add("ALTER TABLE <table> ADD COLUMN `" + FIELD_EQUIPMENT_SERVER + "` varchar(16) NOT NULL");
		batch.add("ALTER TABLE <table> ADD COLUMN `" + FIELD_EQUIPMENT_ID + "` varchar(64) NOT NULL");
		batch.add("ALTER TABLE <table> ADD COLUMN `" + FIELD_EQUIPMENT_DATA + "` varchar(256) NOT NULL");
		batch.execute();
		handle.close();
	}
	
	static void prepareTables()
	{
		prepareTableData();
		prepareTableEquipment();
	}
	
	@SqlQuery("SELECT * FROM <table> WHERE `" + FIELD_DATA_USERNAME + "` = :" + FIELD_DATA_USERNAME + "")
	MSPlayerData selectData(@Define("table") String tableName, @Bind("username") String playerName);
	
	@SqlUpdate("INSERT INTO <table> (`" + FIELD_DATA_USERNAME + "`, `" + FIELD_DATA_XP + "`, `" + FIELD_DATA_KILLS + "`, `" + FIELD_DATA_ASSISTS + "`, `" + FIELD_DATA_DEATHS + "`, `" + FIELD_DATA_PLAYTIME + "`) VALUES"
			+ "(:player." + FIELD_DATA_USERNAME + ", :player." + FIELD_DATA_XP + ", :player." + FIELD_DATA_KILLS + ", :player." + FIELD_DATA_ASSISTS + ", :player." + FIELD_DATA_DEATHS + ", :player." + FIELD_DATA_PLAYTIME + ")")
	void insertData(@Define("table") String tableName, @BindBean("player") MSPlayerData msPlayer);
	
	@SqlQuery("SELECT * FROM <table> WHERE `" + FIELD_EQUIPMENT_SERVER + "` = '" + VALUE_EQUIPMENT_SERVER + "' AND `" + FIELD_DATA_USERNAME + "` = :username")
	public InventoryContainer selectEquipment(@Define("table") String tableName, @Bind("username") String playerName);
	
	@SqlUpdate("INSERT INTO <table> (`" + FIELD_EQUIPMENT_USERNAME + "`, `" + FIELD_EQUIPMENT_SERVER + "`, `" + FIELD_EQUIPMENT_ID + "`, `" + FIELD_EQUIPMENT_DATA + "`) VALUES"
			+ "()") //TODO
	
	void close();
}
