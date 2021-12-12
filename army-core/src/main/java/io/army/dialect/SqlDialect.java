package io.army.dialect;

import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.session.GenericRmSessionFactory;

import java.time.ZoneId;

public interface SqlDialect {

    String quoteIfNeed(String identifier);

    String safeTableName(TableMeta<?> tableMeta, @Nullable String suffix);

    String safeFieldName(FieldMeta<?, ?> fieldMeta);

    boolean isKeyWord(String identifier);

    @Deprecated
    ZoneId zoneId();

    boolean supportZone();

    GenericRmSessionFactory sessionFactory();

    Database database();

    boolean tableAliasAfterAs();

    boolean singleDeleteHasTableAlias();

    boolean hasRowKeywords();

}
