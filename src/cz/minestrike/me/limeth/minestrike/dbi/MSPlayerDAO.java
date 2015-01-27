package cz.minestrike.me.limeth.minestrike.dbi;

import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import org.skife.jdbi.v2.Batch;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import static cz.minestrike.me.limeth.minestrike.dbi.SQLHelper.*;

import java.util.Collection;

@RegisterMapper({MSPlayerDataMapper.class, EquipmentMapper.class})
@UseStringTemplate3StatementLocator
public interface MSPlayerDAO
{
	String FIELD_DATA_USERNAME      = "username";
	String FIELD_DATA_XP            = "xp";
	String FIELD_DATA_KILLS         = "kills";
	String FIELD_DATA_ASSISTS       = "assists";
	String FIELD_DATA_DEATHS        = "deaths";
	String FIELD_DATA_PLAYTIME      = "playtime";
	String FIELD_EQUIPMENT_USERNAME = FIELD_DATA_USERNAME;
	String FIELD_EQUIPMENT_SERVER   = "server";
	String FIELD_EQUIPMENT_CATEGORY   = "category";
	String FIELD_EQUIPMENT_TRADABLE   = "tradable";
	String FIELD_EQUIPMENT_TYPE     = "type";
	String FIELD_EQUIPMENT_DATA     = "data";
	String VALUE_EQUIPMENT_SERVER   = "minestrike";

	static void prepareTableData()
	{
		prepareTable(
				MSConfig.getMySQLTablePlayers(),
				FIELD_DATA_USERNAME,
				column(FIELD_DATA_USERNAME, "varchar(16) COLLATE utf8_czech_ci NOT NULL"),
				column(FIELD_DATA_XP, "int(11) NOT NULL"),
				column(FIELD_DATA_KILLS, "int(11) NOT NULL"),
				column(FIELD_DATA_ASSISTS, "int(11) NOT NULL"),
				column(FIELD_DATA_DEATHS, "int(11) NOT NULL"),
				column(FIELD_DATA_PLAYTIME, "bigint(20) NOT NULL")
		);
	}
	
	static void prepareTableEquipment()
	{
		prepareTable(
				MSConfig.getMySQLTableEquipment(),
				null,
		        column(FIELD_EQUIPMENT_USERNAME, "varchar(16) COLLATE utf8_czech_ci NOT NULL"),
		        column(FIELD_EQUIPMENT_SERVER, "varchar(16) NOT NULL"),
		        column(FIELD_EQUIPMENT_CATEGORY, "varchar(32) NOT NULL"),
		        column(FIELD_EQUIPMENT_TRADABLE, "BOOLEAN NOT NULL"),
		        column(FIELD_EQUIPMENT_TYPE, "varchar(64) NOT NULL"),
		        column(FIELD_EQUIPMENT_DATA, "varchar(256) NOT NULL")
		);
	}
	
	static void prepareTables()
	{
		prepareTableData();
		prepareTableEquipment();
	}
	
	@SqlQuery("SELECT * FROM <table> WHERE `" + FIELD_DATA_USERNAME + "` = :" + FIELD_DATA_USERNAME)
	MSPlayerData selectData(@Define("table") String tableName, @Bind("username") String playerName);
	
	@SqlUpdate("REPLACE INTO <table> (`" + FIELD_DATA_USERNAME + "`, `" + FIELD_DATA_XP + "`, `" + FIELD_DATA_KILLS + "`, `" + FIELD_DATA_ASSISTS + "`, `" + FIELD_DATA_DEATHS + "`, `" + FIELD_DATA_PLAYTIME + "`) VALUES"
			+ "(:player." + FIELD_DATA_USERNAME + ", :player." + FIELD_DATA_XP + ", :player." + FIELD_DATA_KILLS + ", :player." + FIELD_DATA_ASSISTS + ", :player." + FIELD_DATA_DEATHS + ", :player." + FIELD_DATA_PLAYTIME + ")")
	void insertData(@Define("table") String tableName, @BindBean("player") MSPlayerData msPlayer);
	
	@SqlQuery("SELECT * FROM <table> WHERE `" + FIELD_EQUIPMENT_SERVER + "` = '" + VALUE_EQUIPMENT_SERVER + "' AND `" + FIELD_DATA_USERNAME + "` = :username")
	public Collection<Equipment> selectEquipment(@Define("table") String tableName, @Bind("username") String playerName);

	@SqlBatch("INSERT INTO <table> (`" + FIELD_EQUIPMENT_USERNAME + "`, `" + FIELD_EQUIPMENT_SERVER + "`, `" + FIELD_EQUIPMENT_CATEGORY + "`, `" + FIELD_EQUIPMENT_TRADABLE + "`, `" + FIELD_EQUIPMENT_TYPE + "`, `" + FIELD_EQUIPMENT_DATA + "`) VALUES"
			+ "(:username, '" + VALUE_EQUIPMENT_SERVER + "', :equipment." + FIELD_EQUIPMENT_CATEGORY + ", :equipment." + FIELD_EQUIPMENT_TRADABLE + ", :equipment." + FIELD_EQUIPMENT_TYPE + ", :equipment." + FIELD_EQUIPMENT_DATA + ")")
	void insertEquipment(@Define("table") String tableName, @Bind("username") String playerName, @BindEquipment("equipment") Iterable<Equipment> container);
	
	@SqlUpdate("DELETE FROM <table> WHERE `" + FIELD_EQUIPMENT_USERNAME + "` = :username")
	void clearEquipment(@Define("table") String tableName, @Bind("username") String playerName);
	
	void close();
}
