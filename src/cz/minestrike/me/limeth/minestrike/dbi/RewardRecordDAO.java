package cz.minestrike.me.limeth.minestrike.dbi;

import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.dbi.binding.RewardRecordMapper;
import cz.minestrike.me.limeth.minestrike.equipment.rewards.PartialRewardRecord;
import cz.minestrike.me.limeth.minestrike.equipment.rewards.RewardRecord;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.List;

import static cz.minestrike.me.limeth.minestrike.dbi.SQLHelper.column;

@RegisterMapper(RewardRecordMapper.class)
@UseStringTemplate3StatementLocator
public interface RewardRecordDAO
{
    String FIELD_ID = "id", PROPERTIES_ID = "int(11) NOT NULL AUTO_INCREMENT";
    String FIELD_USERNAME = "username", PROPERTIES_USERNAME = "varchar(16) COLLATE utf8_czech_ci NOT NULL";
    String FIELD_TIMESTAMP = "timestamp", PROPERTIES_TIMESTAMP = "bigint(20) NOT NULL";

    public static void prepareTable()
    {
        SQLHelper.prepareTable(
                MSConfig.getMySQLTableRewardRecords(),
                FIELD_ID,
                column(FIELD_ID, PROPERTIES_ID),
                column(FIELD_USERNAME, PROPERTIES_USERNAME),
                column(FIELD_TIMESTAMP, PROPERTIES_TIMESTAMP)
        );
    }

    public static List<RewardRecord> selectRewardRecords(String username, Long minTimestamp)
    {
        if(minTimestamp == null)
            return selectRewardRecords(username);

        DBI dbi = MineStrike.getDBI();
        RewardRecordDAO dao = dbi.open(RewardRecordDAO.class);

        List<RewardRecord> result = dao.selectRewardRecords(MSConfig.getMySQLTableRewardRecords(), username, minTimestamp);
        dao.close();

        return result;
    }

    public static List<RewardRecord> selectRewardRecords(String username)
    {
        DBI dbi = MineStrike.getDBI();
        RewardRecordDAO dao = dbi.open(RewardRecordDAO.class);

        List<RewardRecord> result = dao.selectRewardRecords(MSConfig.getMySQLTableRewardRecords(), username);
        dao.close();

        return result;
    }

    public static int insertRewardRecord(PartialRewardRecord record)
    {
        DBI dbi = MineStrike.getDBI();
        RewardRecordDAO dao = dbi.open(RewardRecordDAO.class);

        int result = dao.insertRewardRecord(MSConfig.getMySQLTableRewardRecords(), record);
        dao.close();

        return result;
    }

    /**
     * Use {@link cz.minestrike.me.limeth.minestrike.dbi.RewardRecordDAO#selectRewardRecords(String, Long)} instead.
     */
    @Deprecated
    @SqlQuery("SELECT * FROM <table> WHERE `" + FIELD_USERNAME + "` = :username AND `" + FIELD_TIMESTAMP + "` > :minTimestamp ORDER BY `" + FIELD_TIMESTAMP + "`")
    List<RewardRecord> selectRewardRecords(@Define("table") String tableName, @Bind("username") String username, @Bind("minTimestamp") long minTimestamp);

    /**
     * Use {@link cz.minestrike.me.limeth.minestrike.dbi.RewardRecordDAO#selectRewardRecords(String)} instead.
     */
    @Deprecated
    @SqlQuery("SELECT * FROM <table> WHERE `" + FIELD_USERNAME + "` = :username ORDER BY `" + FIELD_TIMESTAMP + "`")
    List<RewardRecord> selectRewardRecords(@Define("table") String tableName, @Bind("username") String username);

    /**
     * Use {@link cz.minestrike.me.limeth.minestrike.dbi.RewardRecordDAO#insertRewardRecord(PartialRewardRecord)} instead.
     */
    @Deprecated
    @SqlUpdate("INSERT INTO <table> (`" + FIELD_USERNAME + "`, `" + FIELD_TIMESTAMP + "`) VALUES(:record." + FIELD_USERNAME + ", :record." + FIELD_TIMESTAMP + ")")
    int insertRewardRecord(@Define("table") String tableName, @BindBean("record") PartialRewardRecord record);

    void close();
}
