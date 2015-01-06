package cz.minestrike.me.limeth.minestrike.dbi;

import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EquipmentMapper implements ResultSetMapper<Equipment>
{
	@Override
	public Equipment map(int index, ResultSet r, StatementContext ctx) throws SQLException
	{
		String data = r.getString(MSPlayerDAO.FIELD_EQUIPMENT_DATA);

		return EquipmentManager.fromJson(data);
	}
}
