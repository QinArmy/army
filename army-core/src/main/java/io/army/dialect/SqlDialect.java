package io.army.dialect;

import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.session.GenericRmSessionFactory;

import java.time.ZoneId;

public interface SqlDialect {

    String quoteIfNeed(String identifier);

    String safeTableName(TableMeta<?> tableMeta, @Nullable String suffix);

    String safeColumnName(FieldMeta<?, ?> fieldMeta);

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


    String constant(MappingType type, Object value);


}
