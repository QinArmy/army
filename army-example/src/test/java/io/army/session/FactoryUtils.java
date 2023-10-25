package io.army.session;

import com.alibaba.druid.pool.DruidDataSource;
import io.army.dialect.Database;
import io.army.dialect.mysql.MySQLDialect;
import io.army.dialect.postgre.PostgreDialect;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.env.SqlLogMode;
import io.army.env.StandardEnvironment;
import io.army.example.common.SimpleFieldGeneratorFactory;
import io.army.sync.LocalFactoryBuilder;
import io.army.sync.SyncLocalSessionFactory;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.Map;

public abstract class FactoryUtils {

    private FactoryUtils() {
    }

    public static SyncLocalSessionFactory createArmyBankSyncFactory(final Database database) {
        final DruidDataSource dataSource;
        dataSource = new DruidDataSource();
        DataSourceUtils.druidDataSourceProps(dataSource, mapDatabaseToUrl(database));
        return LocalFactoryBuilder.builder()
                .name(mapDatabaseToFactoryName(database))
                .packagesToScan(Collections.singletonList("io.army.example.bank.domain"))
                .datasource(dataSource)
                .environment(createEnvironment(database))
                .fieldGeneratorFactory(new SimpleFieldGeneratorFactory())
                .build();
    }


    private static ArmyEnvironment createEnvironment(final Database database) {
        final Map<String, Object> map = _Collections.hashMap();
        map.put(ArmyKey.DATABASE.name, database);
        switch (database) {
            case MySQL:
                map.put(ArmyKey.DIALECT.name, MySQLDialect.MySQL80);
                break;
            case PostgreSQL:
                map.put(ArmyKey.DIALECT.name, PostgreDialect.POSTGRE15);
                break;
            default:
                throw _Exceptions.unexpectedEnum(database);
        }
        map.put(ArmyKey.DATASOURCE_CLOSE_METHOD.name, "close");
        map.put(ArmyKey.QUERY_INSERT_MODE.name, AllowMode.SUPPORT);
        map.put(ArmyKey.DDL_MODE.name, DdlMode.UPDATE);
        map.put(ArmyKey.SQL_LOG_MODE.name, SqlLogMode.BEAUTIFY_DEBUG);

        map.put(ArmyKey.QUALIFIED_TABLE_NAME_ENABLE.name, Boolean.FALSE);

//         map.put(ArmyKey.DATABASE_NAME_MODE.name, NameMode.UPPER_CASE);
//        map.put(ArmyKey.TABLE_NAME_MODE.name, NameMode.UPPER_CASE);
//        map.put(ArmyKey.COLUMN_NAME_MODE.name, NameMode.UPPER_CASE);
        return StandardEnvironment.from(map);
    }

    private static String mapDatabaseToFactoryName(final Database database) {
        final String name;
        switch (database) {
            case MySQL:
                name = "mysql-bank";
                break;
            case PostgreSQL:
                name = "postgre-bank";
                break;
            default:
                throw _Exceptions.unexpectedEnum(database);
        }
        return name;
    }


    private static String mapDatabaseToUrl(final Database database) {
        final String url;
        switch (database) {
            case MySQL:
                url = "jdbc:mysql://localhost:3306/army_bank";
                break;
            case PostgreSQL:
                url = "jdbc:postgresql://localhost:5432/army_bank";
                break;
            default:
                throw _Exceptions.unexpectedEnum(database);
        }
        return url;
    }


}
