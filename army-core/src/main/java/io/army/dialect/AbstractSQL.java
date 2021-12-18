package io.army.dialect;


import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.session.FactoryMode;
import io.army.session.GenericRmSessionFactory;

import java.time.ZoneId;

public abstract class AbstractSQL implements SqlDialect {

    protected static final char[] COMMA = new char[]{' ', ','};

    protected static final char[] EQUAL = new char[]{' ', '='};

    protected static final char[] AND = new char[]{' ', 'A', 'N', 'D'};

    protected static final char[] IS_NULL = new char[]{' ', 'I', 'S', ' ', 'N', 'U', 'L', 'L'};

    protected static final char[] LEFT_BRACKET = new char[]{' ', '('};

    protected static final char[] RIGHT_BRACKET = new char[]{' ', ')'};

    protected static final char[] SET_WORD = new char[]{' ', 'S', 'E', 'T'};

    protected static final char[] WHERE_WORD = new char[]{' ', 'W', 'H', 'E', 'R', 'E'};

    protected static final char[] UPDATE = new char[]{'U', 'P', 'D', 'A', 'T', 'E'};

    protected static final char[] AS_WORD = new char[]{' ', 'A', 'S'};

    protected static final char[] JOIN_WORD = new char[]{' ', 'J', 'O', 'I', 'N'};


    protected final Dialect dialect;

    protected final boolean sharding;

    protected AbstractSQL(Dialect dialect) {
        this.dialect = dialect;
        this.sharding = this.dialect.sessionFactory().factoryMode() != FactoryMode.NO_SHARDING;
    }

    @Override
    public final String quoteIfNeed(String identifier) {
        return this.dialect.quoteIfNeed(identifier);
    }

    @Override
    public final boolean isKeyWord(String identifier) {
        return this.dialect.isKeyWord(identifier);
    }

    @Override
    public final ZoneId zoneId() {
        return this.dialect.zoneId();
    }

    @Override
    public final boolean supportZone() {
        return this.dialect.supportZone();
    }

    @Override
    public final GenericRmSessionFactory sessionFactory() {
        return this.dialect.sessionFactory();
    }

    @Override
    public final Database database() {
        return this.dialect.database();
    }

    @Override
    public final boolean tableAliasAfterAs() {
        return this.dialect.tableAliasAfterAs();
    }

    @Override
    public final boolean singleDeleteHasTableAlias() {
        return this.dialect.singleDeleteHasTableAlias();
    }

    @Override
    public final boolean hasRowKeywords() {
        return this.dialect.hasRowKeywords();
    }


    @Override
    public String safeTableName(TableMeta<?> tableMeta, @Nullable String suffix) {
        final String name;
        if (suffix == null) {
            name = this.quoteIfNeed(tableMeta.tableName());
        } else {
            name = tableMeta.tableName() + suffix;
        }
        return name;
    }

    @Override
    public String safeColumnName(FieldMeta<?, ?> fieldMeta) {
        return null;
    }

    @Override
    public String constant(MappingType type, Object value) {
        return null;
    }
}
