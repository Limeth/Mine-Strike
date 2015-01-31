package cz.minestrike.me.limeth.minestrike.dbi.binding;

import cz.minestrike.me.limeth.minestrike.dbi.MSPlayerDAO;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MSPlayerDataMapper implements ResultSetMapper<MSPlayerData>
{
	@Override
	public MSPlayerData map(int index, ResultSet r, StatementContext ctx) throws SQLException
	{
		return new MSPlayerData(
				r.getString(MSPlayerDAO.FIELD_DATA_USERNAME),
		        r.getInt(MSPlayerDAO.FIELD_DATA_XP),
		        r.getInt(MSPlayerDAO.FIELD_DATA_KILLS),
		        r.getInt(MSPlayerDAO.FIELD_DATA_ASSISTS),
		        r.getInt(MSPlayerDAO.FIELD_DATA_DEATHS),
		        r.getLong(MSPlayerDAO.FIELD_DATA_PLAYTIME)
		);
	}
}
