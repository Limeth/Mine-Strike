package cz.minestrike.me.limeth.minestrike.dbi;
import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Define;

import static cz.minestrike.me.limeth.minestrike.dbi.SQLHelper.*;

public interface RewardRecordDAO
{
    String FIELD_ID = "id", PROPERTIES_ID = "int(11) NOT NULL AUTO_INCREMENT";
    String FIELD_USERNAME = "username", PROPERTIES_USERNAME = "varchar(16) COLLATE utf8_czech_ci NOT NULL";
    String FIELD_TIMESTAMP = "timestamp", PROPERTIES_TIMESTAMP = "long NOT NULL";
    String FIELD_RESULT = "result";
    long REWARD_CHECK_PERIOD = 1000 * 60 * 60;
    long REWARD_PERIOD_DEFAULT = 1000 * 60 * 60 * 24 * 2; //2 days
    int REWARD_AMOUNT_DEFAULT = 1;

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

    public static boolean canBeRewarded(String username)
    {
        DBI dbi = MineStrike.getDBI();
        RewardRecordDAO dao = dbi.open(RewardRecordDAO.class);

        return dao.canBeRewarded(
                MSConfig.getMySQLTableRewardRecords(),
                username,
                System.currentTimeMillis(),
                MSConfig.getRewardPeriod(),
                MSConfig.getRewardAmount()
        );
    }

    /**
     * Use {@link RewardRecordDAO#canBeRewarded(String)} instead.
     */
    @Deprecated
    @SqlQuery("SELECT IF(COUNT(*) < :amount) AS `" + FIELD_RESULT + "` WHERE `" + FIELD_USERNAME +
            "` = :username AND `" + FIELD_TIMESTAMP + "` > :timestamp - :period")
    boolean canBeRewarded(@Define("table") String table, @Bind("username") String username,
                          @Bind("timestamp") long timestamp, @Bind("period") long period, @Bind("amount") int amount);
}
