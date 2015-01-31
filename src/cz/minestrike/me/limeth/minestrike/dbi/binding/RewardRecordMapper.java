package cz.minestrike.me.limeth.minestrike.dbi.binding;

import cz.minestrike.me.limeth.minestrike.equipment.rewards.RewardRecord;
import cz.minestrike.me.limeth.minestrike.dbi.RewardRecordDAO;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Limeth
 */
public class RewardRecordMapper implements ResultSetMapper<RewardRecord>
{
	@Override
	public RewardRecord map(int index, ResultSet r, StatementContext ctx) throws SQLException
	{
		return new RewardRecord(r.getInt(RewardRecordDAO.FIELD_ID),
		                        r.getString(RewardRecordDAO.FIELD_USERNAME),
		                        r.getLong(RewardRecordDAO.FIELD_TIMESTAMP));
	}
}
