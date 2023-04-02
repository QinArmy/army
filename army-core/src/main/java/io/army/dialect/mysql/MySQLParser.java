package io.army.dialect.mysql;

import io.army.criteria.SQLWords;
import io.army.criteria.UpdateStatement;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.dialect.*;
import io.army.lang.Nullable;
import io.army.mapping.BooleanType;
import io.army.meta.ChildTableMeta;
import io.army.meta.DatabaseObject;
import io.army.meta.ParentTableMeta;
import io.army.meta.TypeMeta;
import io.army.modelgen._MetaBridge;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.SqlType;
import io.army.tx.Isolation;
import io.army.util._Exceptions;
import io.army.util._TimeUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;

abstract class MySQLParser extends _ArmyDialectParser {

    static MySQLParser standard(DialectEnv environment, MySQLDialect dialect) {
        return new Standard(environment, dialect);
    }


    static final char IDENTIFIER_QUOTE = '`';

    final boolean asOf80;

    MySQLParser(DialectEnv environment, MySQLDialect dialect) {
        super(environment, dialect);
        this.asOf80 = this.dialect().version() >= MySQLDialect.MySQL80.version();
    }


    @Override
    public final List<String> startTransaction(final Isolation isolation, final boolean readonly) {
        final String startStmt;
        if (readonly) {
            startStmt = "START TRANSACTION READ ONLY";
        } else {
            startStmt = "START TRANSACTION READ WRITE";
        }
        List<String> stmtList;
        if (isolation == Isolation.DEFAULT) {
            stmtList = Collections.singletonList(startStmt);
        } else {
            stmtList = new ArrayList<>(2);
            // no key word 'SESSION' or 'GLOBAL',so Next transaction only
            stmtList.add("SET TRANSACTION ISOLATION LEVEL " + isolation.command);
            stmtList.add(startStmt);
            stmtList = Collections.unmodifiableList(stmtList);
        }
        return stmtList;
    }


    @Override
    public final String identifier(final String identifier) {
        final String safeIdentifier;
        if (!this.keyWordSet.contains(identifier.toUpperCase(Locale.ROOT))
                && _DialectUtils.isSafeIdentifier(identifier)) {
            safeIdentifier = identifier;
        } else if (identifier.indexOf(IDENTIFIER_QUOTE) > -1) {
            throw _Exceptions.identifierContainsDelimited(Database.MySQL, identifier, IDENTIFIER_QUOTE);
        } else {
            final StringBuilder builder = new StringBuilder(identifier.length() + 2);
            safeIdentifier = builder.append(IDENTIFIER_QUOTE)
                    .append(identifier)
                    .append(IDENTIFIER_QUOTE)
                    .toString();
        }
        return safeIdentifier;
    }

    @Override
    public final StringBuilder identifier(final String identifier, final StringBuilder builder) {
        if (!this.keyWordSet.contains(identifier.toUpperCase(Locale.ROOT))
                && _DialectUtils.isSafeIdentifier(identifier)) {
            builder.append(identifier);
        } else if (identifier.indexOf(IDENTIFIER_QUOTE) > -1) {
            throw _Exceptions.identifierContainsDelimited(Database.MySQL, identifier, IDENTIFIER_QUOTE);
        } else {
            builder.append(IDENTIFIER_QUOTE)
                    .append(identifier)
                    .append(IDENTIFIER_QUOTE);
        }
        return builder;
    }


    @Override
    protected final String doSafeObjectName(final DatabaseObject object) {
        final String objectName, safeObjectName;
        objectName = object.objectName();
        if (!this.keyWordSet.contains(objectName.toUpperCase(Locale.ROOT))
                && _DialectUtils.isSafeIdentifier(objectName)) {
            safeObjectName = objectName;
        } else if (objectName.indexOf(IDENTIFIER_QUOTE) > -1) {
            throw _Exceptions.objectNameContainsDelimited(Database.MySQL, object, IDENTIFIER_QUOTE);
        } else {
            final StringBuilder builder = new StringBuilder(objectName.length() + 2);
            safeObjectName = builder.append(IDENTIFIER_QUOTE)
                    .append(objectName)
                    .append(IDENTIFIER_QUOTE)
                    .toString();
        }
        return safeObjectName;
    }

    @Override
    protected final StringBuilder doSafeObjectName(final DatabaseObject object, final StringBuilder builder) {
        final String objectName;
        objectName = object.objectName();
        if (!this.keyWordSet.contains(objectName.toUpperCase(Locale.ROOT))
                && _DialectUtils.isSafeIdentifier(objectName)) {
            builder.append(objectName);
        } else if (objectName.indexOf(IDENTIFIER_QUOTE) > -1) {
            throw _Exceptions.objectNameContainsDelimited(Database.MySQL, object, IDENTIFIER_QUOTE);
        } else {
            builder.append(IDENTIFIER_QUOTE)
                    .append(objectName)
                    .append(IDENTIFIER_QUOTE);
        }
        return builder;
    }


    @Override
    protected final MySQLDdl createDdlDialect() {
        return MySQLDdl.create(this);
    }


    @Override
    protected final boolean isNeedConvert(final SqlType type, final Object nonNull) {
        return !(type == MySQLTypes.DATETIME
                && this.asOf80
                && (nonNull instanceof OffsetDateTime || nonNull instanceof ZonedDateTime));
    }


    @Override
    protected final void bindLiteral(final TypeMeta typeMeta, final SqlType type, final Object value,
                                     final StringBuilder sqlBuilder) {

        switch ((MySQLTypes) type) {
            case INT:
            case SMALLINT_UNSIGNED:
            case MEDIUMINT:
            case MEDIUMINT_UNSIGNED:
            case YEAR: {
                if (!(value instanceof Integer)) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(value);
            }
            break;
            case BIGINT:
            case INT_UNSIGNED: {
                if (!(value instanceof Long)) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(value);
            }
            break;
            case DECIMAL:
            case DECIMAL_UNSIGNED: {
                if (!(value instanceof BigDecimal)) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(((BigDecimal) value).toPlainString());
            }
            break;
            case BOOLEAN: {
                if (!(value instanceof Boolean)) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(((Boolean) value) ? BooleanType.TRUE : BooleanType.FALSE);
            }
            break;
            case DATETIME: {
                final String timeText;
                if (value instanceof LocalDateTime) {
                    timeText = _TimeUtils.format((LocalDateTime) value, typeMeta);
                } else if (!this.asOf80) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                } else if (value instanceof OffsetDateTime) {
                    timeText = _TimeUtils.format((OffsetDateTime) value, typeMeta);
                } else if (value instanceof ZonedDateTime) {
                    timeText = _TimeUtils.format(((ZonedDateTime) value).toOffsetDateTime(), typeMeta);
                } else {
                    throw _Exceptions.outRangeOfSqlType(MySQLTypes.DATETIME, value);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(timeText)
                        .append(_Constant.QUOTE);
            }
            break;
            case DATE:
                _Literals.bindLocalDate(typeMeta, type, value, sqlBuilder);
                break;
            case TIME:
                _Literals.bindLocalTime(typeMeta, type, value, sqlBuilder);
                break;
            case CHAR:
            case VARCHAR:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
            case LONGTEXT:
            case JSON:
            case ENUM:
            case SET: {
                if (!(value instanceof String)) { //TODO LongString
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                MySQLLiterals.mysqlEscapes((String) value, sqlBuilder);
            }
            break;
            case BINARY:
            case VARBINARY:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
            case LONGBLOB: {
                if (!(value instanceof byte[])) { //TODO LongBinary
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append("0x")
                        .append(_Literals.hexEscapes((byte[]) value));
            }
            break;
            case BIT: {
                if (!(value instanceof Long)) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(Long.toBinaryString((Long) value));
            }
            break;
            case DOUBLE: {
                if (!(value instanceof Double)) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(value);
            }
            break;
            case FLOAT: {
                if (!(value instanceof Float)) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(value);
            }
            break;
            case TINYINT: {
                if (!(value instanceof Byte)) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(value);
            }
            break;
            case SMALLINT:
            case TINYINT_UNSIGNED: {
                if (!(value instanceof Short)) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(value);
            }
            break;
            case BIGINT_UNSIGNED: {
                if (!(value instanceof BigInteger)) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(value);
            }
            break;
            case GEOMETRY://TODO
                throw _Exceptions.outRangeOfSqlType(type, typeMeta.mappingType());
            default:
                throw _Exceptions.unexpectedEnum((MySQLTypes) type);
        }

    }

    @Override
    protected final void standardLimitClause(final @Nullable _Expression offset, final @Nullable _Expression rowCount
            , _SqlContext context) {

        if (offset != null && rowCount != null) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder().append(_Constant.SPACE_LIMIT);
            offset.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            rowCount.appendSql(context);
        } else if (rowCount != null) {
            context.sqlBuilder().append(_Constant.SPACE_LIMIT);
            rowCount.appendSql(context);
        }

    }

    @Override
    protected final void standardLockClause(final SQLWords lockMode, final _SqlContext context) {
        if (!_Constant.SPACE_FOR_UPDATE.equals(lockMode.render())) {
            throw _Exceptions.castCriteriaApi();
        }
        context.sqlBuilder().append(_Constant.SPACE_FOR_UPDATE);
    }

    /**
     * @see #update(UpdateStatement, Visible)
     */
    @Nullable
    @Override
    protected final void parseDomainChildUpdate(final _SingleUpdate update, final _UpdateContext ctx) {
        assert ctx instanceof _MultiUpdateContext;
        final _MultiUpdateContext context = (_MultiUpdateContext) ctx;

        // 1. UPDATE clause
        final StringBuilder sqlBuilder = context.sqlBuilder();
        if (sqlBuilder.length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
        sqlBuilder.append(_Constant.UPDATE);

        //2. child join parent
        this.appendChildJoinParent(context, (ChildTableMeta<?>) update.table());

        //3. set clause
        this.multiTableChildSetClause(update, context);

        //4. where clause
        this.dmlWhereClause(update.wherePredicateList(), context);

        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) update.table();
        final ParentTableMeta<?> parentTable = childTable.parentMeta();
        final String safeParentTableAlias = context.saTableAliasOf(parentTable);

        //4.1 append discriminator for child
        this.discriminator(childTable, safeParentTableAlias, context);

        //4.2 append condition fields
        context.appendConditionFields();

        //4.3 append visible
        if (parentTable.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(parentTable, safeParentTableAlias, context, false);
        }
    }


    @Override
    protected final void parseDomainChildDelete(final _SingleDelete delete, final _DeleteContext ctx) {
        final _MultiDeleteContext context = (_MultiDeleteContext) ctx;

        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) delete.table();
        final ParentTableMeta<?> parentTable = childTable.parentMeta();

        // 1. delete clause
        final StringBuilder sqlBuilder;
        if ((sqlBuilder = context.sqlBuilder()).length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
        sqlBuilder.append(_Constant.DELETE_SPACE);

        final String safeParentTableAlias, safeChildTableAlias;
        safeChildTableAlias = context.saTableAliasOf(childTable);
        safeParentTableAlias = context.saTableAliasOf(parentTable);

        sqlBuilder.append(safeChildTableAlias)// child table alias
                .append(_Constant.SPACE_COMMA_SPACE)
                .append(safeParentTableAlias)// parent table name
                .append(_Constant.SPACE_FROM);

        //2. child join parent
        this.appendChildJoinParent(context, childTable);

        //3. where clause
        this.dmlWhereClause(delete.wherePredicateList(), context);

        //3.1 append discriminator for child
        this.discriminator(childTable, safeParentTableAlias, context);

        //3.2 append visible
        if (parentTable.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(parentTable, safeParentTableAlias, context, false);
        }
    }



    /*################################## blow properties template method ##################################*/


    @Override
    protected final boolean isSupportTableOnly() {
        // MySQL don't support only before table name.
        return false;
    }

    @Override
    protected final boolean isSetClauseTableAlias() {
        // MySQL support table alias in set clause.
        return true;
    }

    @Override
    protected final String defaultFuncName() {
        return "DEFAULT";
    }


    @Override
    protected final Set<String> createKeyWordSet() {
        final Set<String> keyWordSet;
        switch ((MySQLDialect) dialect) {
            case MySQL55:
            case MySQL56:
            case MySQL57:
                keyWordSet = MySQLDialectUtils.create57KeywordsSet();
                break;
            case MySQL80:
                keyWordSet = MySQLDialectUtils.create80KeywordsSet();
                break;
            default:
                throw _Exceptions.unexpectedEnum((Enum<?>) dialect);
        }
        return keyWordSet;
    }

    @Override
    protected final char identifierDelimitedQuote() {
        return IDENTIFIER_QUOTE;
    }

    @Override
    protected final boolean isSupportZone() {
        // MySQL don't support zone.
        return false;
    }

    @Override
    protected final boolean isTableAliasAfterAs() {
        // MySQL don't support AS key word before table alias.
        return true;
    }

    @Override
    protected final boolean isSupportOnlyDefault() {
        // MySQL support DEFAULT() function.
        return true;
    }

    @Override
    protected final boolean isSupportRowAlias() {
        return this.dialect.version() >= MySQLDialect.MySQL80.version();
    }

    @Override
    protected final _ChildUpdateMode childUpdateMode() {
        return _ChildUpdateMode.MULTI_TABLE;
    }

    @Override
    protected final boolean isSupportSingleUpdateAlias() {
        //MySQL always support single update alias;
        return true;
    }

    @Override
    protected final boolean isSupportSingleDeleteAlias() {
        //as of 8.0 MySQL support single delete alias
        return this.asOf80;
    }

    @Override
    protected final boolean isSupportUpdateRow() {
        //MySQL don't support update row
        return false;
    }

    @Override
    protected final boolean isSupportUpdateDerivedField() {
        //MySQL don't support update derive field
        return false;
    }

    /*################################## blow private method ##################################*/

//    private static void re(){
//        switch ((MySqlType) sqlType) {
//            case INT:
//            case BIGINT:
//            case DECIMAL:
//            case BOOLEAN:
//            case DATETIME:
//            case DATE:
//            case TIME:
//            case YEAR:
//
//            case CHAR:
//            case VARCHAR:
//            case ENUM:
//            case JSON:
//            case SET:
//            case TINYTEXT:
//            case TEXT:
//            case MEDIUMTEXT:
//            case LONGTEXT:
//
//            case BINARY:
//            case VARBINARY:
//            case TINYBLOB:
//            case BLOB:
//            case MEDIUMBLOB:
//            case LONGBLOB:
//
//            case BIT:
//            case FLOAT:
//            case DOUBLE:
//
//            case TINYINT:
//            case TINYINT_UNSIGNED:
//            case SMALLINT:
//            case SMALLINT_UNSIGNED:
//            case MEDIUMINT:
//            case MEDIUMINT_UNSIGNED:
//            case INT_UNSIGNED:
//            case BIGINT_UNSIGNED:
//            case DECIMAL_UNSIGNED:
//
//            case POINT:
//            case LINESTRING:
//            case POLYGON:
//            case MULTIPOINT:
//            case MULTIPOLYGON:
//            case MULTILINESTRING:
//            case GEOMETRYCOLLECTION:
//            break;
//            default:
//                throw _Exceptions.unexpectedEnum((MySqlType) sqlType);
//        }
//    }


    private static final class Standard extends MySQLParser {

        private Standard(DialectEnv environment, MySQLDialect dialect) {
            super(environment, dialect);
        }

    }//Standard


}
