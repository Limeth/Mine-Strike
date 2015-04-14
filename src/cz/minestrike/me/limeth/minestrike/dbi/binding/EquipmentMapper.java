package cz.minestrike.me.limeth.minestrike.dbi.binding;

import cz.minestrike.me.limeth.minestrike.dbi.MSPlayerDAO;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCategory;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;
import cz.minestrike.me.limeth.minestrike.equipment.simple.Placeholder;
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
		Equipment equipment = EquipmentManager.fromJson(data);

		if(equipment != null)
			return equipment;

		boolean tradable = r.getBoolean(MSPlayerDAO.FIELD_EQUIPMENT_TRADABLE);
		EquipmentCategory category = EquipmentCategory.valueOf(r.getString(MSPlayerDAO.FIELD_EQUIPMENT_CATEGORY));

		return Placeholder.parse(data, tradable, category);
	}
}
