package io.army.dialect.mysql;

import io.army.criteria.SQLWords;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.dialect.*;
import io.army.lang.Nullable;
import io.army.mapping.BooleanType;
import io.army.mapping.MappingType;
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
import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
    public final String safeObjectName(final DatabaseObject object) {
        final StringBuilder builder = new StringBuilder();
        return this.safeObjectName(object, builder)
                .toString();
    }

    @Override
    public final StringBuilder safeObjectName(final DatabaseObject object, final StringBuilder builder) {
        final String objectName;
        objectName = object.objectName();
        if (this.keyWordSet.contains(objectName)) {
            builder.append(IDENTIFIER_QUOTE)
                    .append(objectName)
                    .append(IDENTIFIER_QUOTE);
        } else {
            builder.append(objectName);
        }
        return builder;
    }


    @Override
    protected final MySQLDdl createDdlDialect() {
        return MySQLDdl.create(this);
    }


    @Override
    public final StringBuilder literal(final TypeMeta paramMeta, final Object nonNull, final StringBuilder sqlBuilder) {

        final SqlType sqlType;
        final MappingType mappingType;
        if (paramMeta instanceof MappingType) { //TODO validate non-field codec
            mappingType = (MappingType) paramMeta;
        } else {
            mappingType = paramMeta.mappingType();
        }
        sqlType = mappingType.map(this.serverMeta);

        final Object valueAfterConvert;
        if (sqlType == MySQLTypes.DATETIME
                && this.asOf80
                && (nonNull instanceof OffsetDateTime || nonNull instanceof ZonedDateTime)) {
            valueAfterConvert = nonNull;
        } else {
            valueAfterConvert = mappingType.beforeBind(sqlType, this.mappingEnv, nonNull);
        }
        switch ((MySQLTypes) sqlType) {
            case INT:
            case SMALLINT_UNSIGNED:
            case MEDIUMINT:
            case MEDIUMINT_UNSIGNED:
            case YEAR: {
                if (!(valueAfterConvert instanceof Integer)) {
                    throw _Exceptions.beforeBindMethod(sqlType, mappingType, valueAfterConvert);
                }
                sqlBuilder.append(valueAfterConvert);
            }
            break;
            case BIGINT:
            case INT_UNSIGNED: {
                if (!(valueAfterConvert instanceof Long)) {
                    throw _Exceptions.beforeBindMethod(sqlType, mappingType, valueAfterConvert);
                }
                sqlBuilder.append(valueAfterConvert);
            }
            break;
            case DECIMAL:
            case DECIMAL_UNSIGNED: {
                if (!(valueAfterConvert instanceof BigDecimal)) {
                    throw _Exceptions.beforeBindMethod(sqlType, mappingType, valueAfterConvert);
                }
                sqlBuilder.append(valueAfterConvert);
            }
            break;
            case BOOLEAN: {
                if (!(valueAfterConvert instanceof Boolean)) {
                    throw _Exceptions.beforeBindMethod(sqlType, mappingType, valueAfterConvert);
                }
                sqlBuilder.append(((Boolean) valueAfterConvert) ? BooleanType.TRUE : BooleanType.FALSE);
            }
            break;
            case DATETIME: {
                final String timeText;
                if (valueAfterConvert instanceof LocalDateTime) {
                    timeText = _TimeUtils.format((LocalDateTime) valueAfterConvert, paramMeta);
                } else if (!this.asOf80) {
                    throw _Exceptions.beforeBindMethod(sqlType, mappingType, valueAfterConvert);
                } else if (nonNull instanceof OffsetDateTime) {
                    timeText = _TimeUtils.format((OffsetDateTime) nonNull, paramMeta);
                } else if (nonNull instanceof ZonedDateTime) {
                    timeText = _TimeUtils.format(((ZonedDateTime) nonNull).toOffsetDateTime(), paramMeta);
                } else {
                    throw _Exceptions.outRangeOfSqlType(MySQLTypes.DATETIME, nonNull);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(timeText)
                        .append(_Constant.QUOTE);
            }
            break;
            case DATE: {
                if (!(valueAfterConvert instanceof LocalDate)) {
                    throw _Exceptions.beforeBindMethod(sqlType, mappingType, valueAfterConvert);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(valueAfterConvert)
                        .append(_Constant.QUOTE);
            }
            break;
            case TIME: {
                if (!(valueAfterConvert instanceof LocalTime)) {
                    throw _Exceptions.beforeBindMethod(sqlType, mappingType, valueAfterConvert);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(_TimeUtils.format((LocalTime) valueAfterConvert, paramMeta))
                        .append(_Constant.QUOTE);
            }
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
                if (!(valueAfterConvert instanceof String)) { //TODO LongString
                    throw _Exceptions.beforeBindMethod(sqlType, mappingType, valueAfterConvert);
                }
                MySQLLiterals.mysqlEscapes((String) valueAfterConvert, sqlBuilder);
            }
            break;
            case BINARY:
            case VARBINARY:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
            case LONGBLOB: {
                if (!(valueAfterConvert instanceof byte[])) { //TODO LongBinary
                    throw _Exceptions.beforeBindMethod(sqlType, mappingType, valueAfterConvert);
                }
                sqlBuilder.append("0x")
                        .append(_Literals.hexEscapes((byte[]) valueAfterConvert));
            }
            break;
            case BIT: {
                if (!(valueAfterConvert instanceof Long)) {
                    throw _Exceptions.beforeBindMethod(sqlType, mappingType, valueAfterConvert);
                }
                sqlBuilder.append(Long.toBinaryString((Long) valueAfterConvert));
            }
            break;
            case DOUBLE: {
                if (!(valueAfterConvert instanceof Double)) {
                    throw _Exceptions.beforeBindMethod(sqlType, mappingType, valueAfterConvert);
                }
                sqlBuilder.append(valueAfterConvert);
            }
            break;
            case FLOAT: {
                if (!(valueAfterConvert instanceof Float)) {
                    throw _Exceptions.beforeBindMethod(sqlType, mappingType, valueAfterConvert);
                }
                sqlBuilder.append(valueAfterConvert);
            }
            break;
            case TINYINT: {
                if (!(valueAfterConvert instanceof Byte)) {
                    throw _Exceptions.beforeBindMethod(sqlType, mappingType, valueAfterConvert);
                }
                sqlBuilder.append(valueAfterConvert);
            }
            break;
            case SMALLINT:
            case TINYINT_UNSIGNED: {
                if (!(valueAfterConvert instanceof Short)) {
                    throw _Exceptions.beforeBindMethod(sqlType, mappingType, valueAfterConvert);
                }
                sqlBuilder.append(valueAfterConvert);
            }
            break;
            case BIGINT_UNSIGNED: {
                if (!(valueAfterConvert instanceof BigInteger)) {
                    throw _Exceptions.beforeBindMethod(sqlType, mappingType, valueAfterConvert);
                }
                sqlBuilder.append(valueAfterConvert);
            }
            break;
            case GEOMETRY://TODO
                throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
            default:
                throw _Exceptions.unexpectedEnum((MySQLTypes) sqlType);
        }

        return sqlBuilder;
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
     * @see #update(Update, Visible)
     */
    @Nullable
    @Override
    protected final void parseDomainChildUpdate(final _SingleUpdate update, final _UpdateContext ctx) {
        assert ctx instanceof _MultiUpdateContext;
        final _MultiUpdateContext context = (_MultiUpdateContext) ctx;

        // 1. UPDATE clause
        context.sqlBuilder().append(_Constant.UPDATE);

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
    protected final char identifierQuote() {
        return IDENTIFIER_QUOTE;
    }

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
    protected final boolean isIdentifierCaseSensitivity() {
        //MySQL Identifier Case Sensitivity
        return true;
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
        return dialect.version() >= MySQLDialect.MySQL80.version();
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
