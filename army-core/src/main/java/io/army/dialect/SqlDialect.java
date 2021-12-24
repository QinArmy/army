package io.army.dialect;

import io.army.meta.ParamMeta;
import io.army.session.GenericRmSessionFactory;

import java.time.ZoneId;

public interface SqlDialect {


    default String safeTableName(String tableName) {
        throw new UnsupportedOperationException();
    }

    /**
     * design for standard statement.
     */
    default String safeColumnName(String columnName) {
        throw new UnsupportedOperationException();
    }

    boolean isKeyWord(String identifier);

    @Deprecated
    ZoneId zoneId();

    boolean supportZone();

    boolean supportOnlyDefault();

    GenericRmSessionFactory sessionFactory();

    Database database();

    boolean tableAliasAfterAs();

    boolean singleDeleteHasTableAlias();

    boolean hasRowKeywords();


    String literal(ParamMeta paramMeta, Object value);


}
