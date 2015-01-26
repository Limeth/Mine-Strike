package cz.minestrike.me.limeth.minestrike.dbi;

import cz.minestrike.me.limeth.minestrike.MineStrike;
import org.skife.jdbi.v2.Batch;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class SQLHelper
{
    private SQLHelper() {}

    static void prepareTable(String table, String primaryKeyName, Column... columns)
    {
        DBI dbi = MineStrike.getDBI();
        Handle handle = dbi.open();
        Batch batch = handle.createBatch();
        StringBuilder csvColumns = new StringBuilder();

        for(int i = 0; i < columns.length; i++)
        {
            if(i > 0)
                csvColumns.append(", ");

            csvColumns.append(columns[i].toString());
        }

        batch.add("CREATE TABLE IF NOT EXISTS " + table + " (" +
                csvColumns +
                (primaryKeyName != null ? (", PRIMARY KEY (`" + primaryKeyName + "`)") : "") +
                ") ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci");

        for(Column column : columns)
            prepareColumn(batch, table, column.getName(), column.getProperties());

        batch.execute();
        handle.close();
    }

    public static void prepareColumn(Batch batch, String tableName, String columnName, String type)
    {
        batch.add("SET @s = (SELECT IF(\n" +
                "    (SELECT COUNT(*)\n" +
                "        FROM INFORMATION_SCHEMA.COLUMNS\n" +
                "        WHERE table_name = '" + tableName + "'\n" +
                "        AND table_schema = DATABASE()\n" +
                "        AND column_name = '" + columnName + "'\n" +
                "    ) > 0,\n" +
                "    \"SELECT 1\",\n" +
                "    \"ALTER TABLE `" + tableName + "` ADD `" + columnName + "` " + type + "\"\n" +
                "))");
        batch.add("PREPARE stmt FROM @s");
        batch.add("EXECUTE stmt");
        batch.add("DEALLOCATE PREPARE stmt");
    }

    public static Column column(String name, String properties)
    {
        return new Column(name, properties);
    }

    public static class Column
    {
        private final String name;
        private final String properties;

        public Column(String name, String properties)
        {
            this.name = name;
            this.properties = properties;
        }

        public String getName() {
            return name;
        }

        public String getProperties() {
            return properties;
        }

        @Override
        public String toString() {
            return "`" + name + "` " + properties;
        }
    }
}
