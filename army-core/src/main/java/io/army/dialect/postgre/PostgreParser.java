package io.army.dialect.postgre;

import io.army.criteria.CriteriaException;
import io.army.criteria.impl.inner.*;
import io.army.dialect.*;
import io.army.lang.Nullable;
import io.army.mapping.BooleanType;
import io.army.meta.ChildTableMeta;
import io.army.meta.DatabaseObject;
import io.army.meta.ParentTableMeta;
import io.army.meta.TypeMeta;
import io.army.modelgen._MetaBridge;
import io.army.sqltype.PostgreTypes;
import io.army.sqltype.SqlType;
import io.army.tx.Isolation;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

abstract class PostgreParser extends _ArmyDialectParser {

    static PostgreParser standard(DialectEnv environment, PostgreDialect dialect) {
        return new Standard(environment, dialect);
    }

    PostgreParser(DialectEnv environment, PostgreDialect dialect) {
        super(environment, dialect);
    }


    @Override
    public final List<String> startTransaction(final Isolation isolation, final boolean readonly) {
        final StringBuilder builder = new StringBuilder();
        builder.append("START TRANSACTION");

        if (readonly) {
            builder.append(" READ ONLY");
        } else {
            builder.append(" READ WRITE");
        }

        if (isolation != Isolation.DEFAULT) {
            builder.append(" , ISOLATION LEVEL ")
                    .append(isolation.command);
        }
        return Collections.singletonList(builder.toString());
    }

    @Override
    public final boolean isSupportOnlyDefault() {
        //Postgre don't support
        return false;
    }

    @Override
    protected final boolean isSupportRowAlias() {
        //true,Postgre support
        return true;
    }

    @Override
    protected final boolean isSupportTableOnly() {
        //Postgre support 'ONLY' key word before table name.
        return true;
    }

    @Override
    protected final boolean isNeedConvert(SqlType type, Object nonNull) {
        //always need
        return true;
    }

    @Override
    protected final void bindLiteral(final TypeMeta typeMeta, final SqlType type, final Object value,
                                     final StringBuilder sqlBuilder) {
        switch ((PostgreTypes) type) {
            case BOOLEAN: {
                if (!(value instanceof Boolean)) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(((Boolean) value) ? BooleanType.TRUE : BooleanType.FALSE);
            }
            break;
            case INTEGER: {
                if (!(value instanceof Integer)) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(value)
                        .append("::INTEGER");
            }
            break;
            case BIGINT: {
                if (!(value instanceof Long)) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(value)
                        .append("::BIGINT");
            }
            break;
            case DECIMAL: {
                if (!(value instanceof BigDecimal)) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(((BigDecimal) value).toPlainString())
                        .append("::DECIMAL");
            }
            break;
            case DOUBLE: {
                if (!(value instanceof Double)) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(value)
                        .append("::DOUBLE");
            }
            break;
            case REAL: {
                if (!(value instanceof Float)) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(value)
                        .append("::REAL");
            }
            break;
            case TIME:
                _Literals.bindLocalTime(typeMeta, type, value, sqlBuilder)
                        .append("::TIME WITHOUT TIME ZONE");
                break;
            case DATE:
                _Literals.bindLocalDate(typeMeta, type, value, sqlBuilder)
                        .append("::DATE");
                break;
            case TIMETZ:
                _Literals.bindOffsetTime(typeMeta, type, value, sqlBuilder)
                        .append("::TIME WITH TIME ZONE");
                break;
            case TIMESTAMP:
                _Literals.bindLocalDateTime(typeMeta, type, value, sqlBuilder)
                        .append("::TIMESTAMP WITHOUT TIME ZONE");
                break;
            case TIMESTAMPTZ:
                _Literals.bindOffsetDateTime(typeMeta, type, value, sqlBuilder)
                        .append("::TIMESTAMP WITH TIME ZONE");
                break;
            case SMALLINT: {
                if (!(value instanceof Short)) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(value)
                        .append("::SMALLINT");
            }
            break;
            case CHAR:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::CHAR");
                break;
            case VARCHAR:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::VARCHAR");
                break;
            case TEXT:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::TEXT");
                break;
            case JSON:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::JSON");
                break;
            case BYTEA: {
                if (!(value instanceof byte[])) {//TODO think long binary
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(_Constant.BACK_SLASH)
                        .append('x')
                        .append(_Literals.hexEscapes((byte[]) value))
                        .append(_Constant.QUOTE);
            }
            break;
            case BIT:
                PostgreLiterals.postgreBitString(typeMeta, type, value, sqlBuilder)
                        .append("::BIT");
                break;
            case VARBIT:
                PostgreLiterals.postgreBitString(typeMeta, type, value, sqlBuilder)
                        .append("::BIT VARYING");
                break;
            case JSONB:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::JSONB");
                break;
            case XML:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::XML");
                break;
            // Geometric Types
            case POINT:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::POINT");
                break;
            case LINE:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::LINE");
                break;
            case LSEG:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::LSEG");
                break;
            case BOX:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::BOX");
                break;
            case PATH:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::PATH");
                break;
            case POLYGON:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::POLYGON");
                break;
            case CIRCLES:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::CIRCLES");
                break;
            // Network Address Types
            case CIDR:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::CIDR");
                break;
            case INET:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::INET");
                break;
            case MACADDR:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::MACADDR");
                break;
            case MACADDR8:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::MACADDR8");
                break;
            //
            case UUID:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::UUID");
                break;
            case MONEY:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::MONEY");
                break;
            case TSQUERY:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::TSQUERY");
                break;
            case TSVECTOR:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::TSVECTOR");
                break;
            // Range Types
            case INT4RANGE:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::INT4RANGE");
                break;
            case INT8RANGE:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::INT8RANGE");
                break;
            case NUMRANGE:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::NUMRANGE");
                break;
            case TSRANGE:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::TSRANGE");
                break;
            case TSTZRANGE:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::TSTZRANGE");
                break;
            case DATERANGE:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::DATERANGE");
                break;
            case INTERVAL:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::INTERVAL");
                break;
            case REF_CURSOR:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder)
                        .append("::REF_CURSOR");
                break;
            // below array
            case BOX_ARRAY:
            case OID_ARRAY:
            case BIT_ARRAY:
            case XML_ARRAY:
            case CHAR_ARRAY:
            case CIDR_ARRAY:
            case DATE_ARRAY:
            case INET_ARRAY:
            case JSON_ARRAY:
            case LINE_ARRAY:
            case PATH_ARRAY:
            case REAL_ARRAY:
            case TEXT_ARRAY:
            case TIME_ARRAY:
            case UUID_ARRAY:
            case BYTEA_ARRAY:
            case JSONB_ARRAY:
            case MONEY_ARRAY:
            case POINT_ARRAY:
            case BIGINT_ARRAY:
            case DOUBLE_ARRAY:
            case TIMETZ_ARRAY:
            case VARBIT_ARRAY:
            case BOOLEAN_ARRAY:
            case CIRCLES_ARRAY:
            case DECIMAL_ARRAY:
            case INTEGER_ARRAY:
            case MACADDR_ARRAY:
            case POLYGON_ARRAY:
            case TSQUERY_ARRAY:
            case TSRANGE_ARRAY:
            case VARCHAR_ARRAY:
            case INTERVAL_ARRAY:
            case MACADDR8_ARRAY:
            case NUMRANGE_ARRAY:
            case SMALLINT_ARRAY:
            case TSVECTOR_ARRAY:
            case DATERANGE_ARRAY:
            case INT4RANGE_ARRAY:
            case INT8RANGE_ARRAY:
            case TIMESTAMP_ARRAY:
            case TSTZRANGE_ARRAY:
            case TIMESTAMPTZ_ARRAY:
            case LINE_SEGMENT_ARRAY:
                //TODO check array syntax
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder);
                break;
            default:
                throw _Exceptions.unexpectedEnum((PostgreTypes) type);

        }// switch


    }

    @Override
    protected final PostgreDdl createDdlDialect() {
        return PostgreDdl.create(this);
    }


    @Override
    public final String identifier(final String identifier) {
        return this.identifier(identifier, new StringBuilder(2 + identifier.length()))
                .toString();
    }


    @Override
    public final StringBuilder identifier(final String identifier, final StringBuilder builder) {
        // due to postgre identifier is case insensitivity,so have to use quoted identifier.
        if (!_StringUtils.hasText(identifier)) {
            throw _Exceptions.identifierNoText();
        } else if (identifier.indexOf(_Constant.DOUBLE_QUOTE) > -1) {
            String m = String.format("%s identifier[%s] couldn't contains double-quotes.",
                    Database.PostgreSQL, identifier);
            throw new CriteriaException(m);
        }
        return builder.append(_Constant.DOUBLE_QUOTE)
                .append(identifier)
                .append(_Constant.DOUBLE_QUOTE);
    }

    @Override
    protected final String doSafeObjectName(final DatabaseObject object) {
        final String objectName, safeObjectName;
        objectName = object.objectName();
        if (!this.keyWordSet.contains(objectName) && _DialectUtils.isSafeIdentifier(objectName)) {
            safeObjectName = objectName;
        } else if (objectName.indexOf(_Constant.DOUBLE_QUOTE) > -1) {
            throw _Exceptions.objectNameContainsDelimited(Database.PostgreSQL, object, _Constant.DOUBLE_QUOTE);
        } else {
            final StringBuilder builder = new StringBuilder(2 + objectName.length());
            safeObjectName = builder.append(_Constant.DOUBLE_QUOTE)
                    .append(objectName)
                    .append(_Constant.DOUBLE_QUOTE)
                    .toString();
        }
        return safeObjectName;
    }


    @Override
    protected final StringBuilder doSafeObjectName(final DatabaseObject object, final StringBuilder builder) {
        final String objectName;
        objectName = object.objectName();
        if (!this.keyWordSet.contains(objectName) && _DialectUtils.isSafeIdentifier(objectName)) {
            builder.append(objectName);
        } else if (objectName.indexOf(_Constant.DOUBLE_QUOTE) > -1) {
            throw _Exceptions.objectNameContainsDelimited(Database.PostgreSQL, object, _Constant.DOUBLE_QUOTE);
        } else {
            builder.append(_Constant.DOUBLE_QUOTE)
                    .append(objectName)
                    .append(_Constant.DOUBLE_QUOTE);
        }
        return builder;
    }


    @Override
    protected final void standardLimitClause(final @Nullable _Expression offset, final @Nullable _Expression rowCount,
                                             final _SqlContext context) {
        if (offset != null && rowCount != null) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder().append(_Constant.SPACE_LIMIT_SPACE);
            rowCount.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_OFFSET_SPACE);
            offset.appendSql(context);
        } else if (rowCount != null) {
            context.sqlBuilder().append(_Constant.SPACE_LIMIT_SPACE);
            rowCount.appendSql(context);
        }
    }

    @Override
    protected final Set<String> createKeyWordSet() {
        return PostgreDialectUtils.createKeywordsSet();
    }

    @Override
    protected final char identifierDelimitedQuote() {
        return _Constant.DOUBLE_QUOTE;
    }

    @Override
    protected final String defaultFuncName() {
        //Postgre don't support DEFAULT() function
        throw new UnsupportedOperationException();
    }

    @Override
    protected final boolean isSupportZone() {
        //Postgre support zone
        return true;
    }

    @Override
    protected final boolean isSetClauseTableAlias() {
        //Postgre don't support table alias in SET clause
        return false;
    }

    @Override
    protected final boolean isTableAliasAfterAs() {
        //Postgre support AS key word
        return true;
    }

    @Override
    protected final _ChildUpdateMode childUpdateMode() {
        // Postgre support DML in cte.
        return _ChildUpdateMode.CTE;
    }

    @Override
    protected final boolean isSupportSingleUpdateAlias() {
        // Postgre support single table update alias
        return true;
    }

    @Override
    protected final boolean isSupportSingleDeleteAlias() {
        // Postgre support single table DELETE alias
        return true;
    }

    @Override
    protected final boolean isSupportUpdateRow() {
        // Postgre support update row
        return true;
    }

    @Override
    protected final boolean isSupportUpdateDerivedField() {
        // Postgre don't support update derived field
        return false;
    }


    @Override
    protected final void parseAssignmentInsert(_AssignmentInsertContext context, _Insert._AssignmentInsert insert) {
        throw _Exceptions.dontSupportAssignmentInsert(this.dialect);
    }

    @Override
    protected final void parseDomainChildUpdate(final _SingleUpdate stmt, final _UpdateContext context) {
        final _SingleUpdateContext childContext = (_SingleUpdateContext) context;
        final _SingleUpdateContext parentContext = (_SingleUpdateContext) childContext.parentContext();
        assert parentContext != null;

        final ChildTableMeta<?> domainTable = (ChildTableMeta<?>) childContext.domainTable();
        assert domainTable == stmt.table() && domainTable == childContext.targetTable();
        final String safeChildTableAlias = childContext.safeTargetTableAlias();

        final ParentTableMeta<?> parentTable = (ParentTableMeta<?>) parentContext.targetTable();
        assert domainTable.parentMeta() == parentTable;
        final String safeParentAlias = parentContext.safeTargetTableAlias();
        final String safeParentTableName, safeChildTableName;
        safeParentTableName = this.safeObjectName(parentTable);
        safeChildTableName = this.safeObjectName(domainTable);

        final StringBuilder sqlBuilder;
        sqlBuilder = childContext.sqlBuilder();
        assert parentContext.sqlBuilder() == sqlBuilder; // must assert

        if (sqlBuilder.length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }

        // append child table update cte statement
        final String childCte;
        childCte = this.identifier(childContext.targetTableAlias() + "_update_cte");
        sqlBuilder.append(_Constant.WITH)
                .append(_Constant.SPACE)
                .append(childCte)
                .append(_Constant.SPACE_LEFT_PAREN)
                .append(_Constant.SPACE)
                .append(_MetaBridge.ID)
                .append(_Constant.SPACE_RIGHT_PAREN)
                .append(_Constant.SPACE_AS)
                .append(_Constant.SPACE_LEFT_PAREN)
                .append(_Constant.SPACE)
                .append(_Constant.UPDATE)
                .append(_Constant.SPACE_ONLY)
                .append(_Constant.SPACE);

        sqlBuilder.append(safeChildTableName)// child table name.
                .append(_Constant.SPACE_AS_SPACE)
                .append(safeChildTableAlias);

        this.singleTableSetClause(((_DomainUpdate) stmt).childItemPairList(), childContext); // child SET clause

        sqlBuilder.append(_Constant.SPACE_FROM_SPACE)
                .append(safeParentTableName)
                .append(_Constant.SPACE_AS_SPACE)
                .append(safeParentAlias);

        // child cte WHERE clause
        this.childDomainCteWhereClause(stmt.wherePredicateList(), childContext);
        childContext.appendConditionFields();
        this.visiblePredicate(parentTable, safeParentAlias, childContext, false);

        // RETURNING clause
        sqlBuilder.append(_Constant.SPACE_RETURNING)
                .append(_Constant.SPACE)
                .append(safeChildTableAlias)
                .append(_Constant.POINT)
                .append(_MetaBridge.ID)
                .append(_Constant.SPACE_AS_SPACE)
                .append(_Constant.DOUBLE_QUOTE)
                .append(_MetaBridge.ID)
                .append(_Constant.DOUBLE_QUOTE)
                .append(_Constant.SPACE_RIGHT_PAREN);

        // child cte end

        // below primary UPDATE statement part, parent table.
        sqlBuilder.append(_Constant.SPACE)
                .append(_Constant.UPDATE)
                .append(_Constant.SPACE_ONLY)
                .append(_Constant.SPACE);


        sqlBuilder.append(safeParentTableName)// parent table name.
                .append(_Constant.SPACE_AS_SPACE)
                .append(safeParentAlias);

        this.singleTableSetClause(stmt.itemPairList(), parentContext); // parent SET clause

        // parent part FROM clause
        sqlBuilder.append(_Constant.SPACE_FROM_SPACE)
                .append(childCte);

        if (((_DmlContext._DomainUpdateSpec) parentContext).isExistsChildFiledInSetClause()) { // after SET clause
            sqlBuilder.append(_Constant.SPACE_JOIN_SPACE)
                    .append(safeChildTableName)
                    .append(_Constant.SPACE_AS_SPACE)
                    .append(safeChildTableAlias)
                    .append(_Constant.SPACE_ON_SPACE)
                    .append(safeChildTableAlias)
                    .append(_Constant.POINT)
                    .append(_MetaBridge.ID)
                    .append(_Constant.SPACE_EQUAL_SPACE)
                    .append(childCte)
                    .append(_Constant.POINT)
                    .append(_MetaBridge.ID);

        }

        sqlBuilder.append(_Constant.SPACE_WHERE)
                .append(safeParentAlias)
                .append(_Constant.POINT)
                .append(_MetaBridge.ID)
                .append(_Constant.SPACE_EQUAL_SPACE)
                .append(childCte)
                .append(_Constant.POINT)
                .append(_MetaBridge.ID);


    }

    @Override
    protected final void parseDomainChildDelete(final _SingleDelete delete, final _DeleteContext context) {
        super.parseDomainChildDelete(delete, context);
    }


    private static final class Standard extends PostgreParser {

        private Standard(DialectEnv environment, PostgreDialect dialect) {
            super(environment, dialect);
        }

    }//Standard


//    private void types(){
//        PostgreType type = PostgreType.TIMESTAMPTZ;
//        switch ((PostgreType) type) {
//            case BOOLEAN:
//            case INTEGER:
//            case BIGINT:
//            case DECIMAL:
//            case DOUBLE:
//            case REAL:
//            case TIME:
//            case DATE:
//            case TIMETZ:
//            case TIMESTAMP:
//            case TIMESTAMPTZ:
//            case SMALLINT:
//            case CHAR:
//            case TEXT:
//            case JSON:
//
//            case BYTEA:
//            case BIT:
//            case VARBIT:
//
//            case BOX:
//            case XML:
//            case CIDR:
//            case INET:
//            case LINE:
//            case PATH:
//            case UUID:
//            case JSONB:
//            case MONEY:
//            case POINT:
//            case CIRCLES:
//
//            case MACADDR:
//            case POLYGON:
//            case TSQUERY:
//            case TSRANGE:
//            case VARCHAR:
//            case INTERVAL:
//            case MACADDR8:
//            case NUMRANGE:
//
//            case TSVECTOR:
//
//            case DATERANGE:
//            case BOX_ARRAY:
//            case INT4RANGE:
//            case INT8RANGE:
//            case OID_ARRAY:
//            case LINE_SEGMENT:
//
//            case TSTZRANGE:
//
//            case BIT_ARRAY:
//            case XML_ARRAY:
//            case CHAR_ARRAY:
//            case CIDR_ARRAY:
//            case DATE_ARRAY:
//            case INET_ARRAY:
//            case JSON_ARRAY:
//            case LINE_ARRAY:
//            case PATH_ARRAY:
//            case REAL_ARRAY:
//            case REF_CURSOR:
//            case TEXT_ARRAY:
//            case TIME_ARRAY:
//            case UUID_ARRAY:
//            case BYTEA_ARRAY:
//            case JSONB_ARRAY:
//            case MONEY_ARRAY:
//            case POINT_ARRAY:
//            case BIGINT_ARRAY:
//            case DOUBLE_ARRAY:
//            case TIMETZ_ARRAY:
//            case VARBIT_ARRAY:
//            case BOOLEAN_ARRAY:
//            case CIRCLES_ARRAY:
//            case DECIMAL_ARRAY:
//            case INTEGER_ARRAY:
//            case MACADDR_ARRAY:
//            case POLYGON_ARRAY:
//            case TSQUERY_ARRAY:
//            case TSRANGE_ARRAY:
//            case VARCHAR_ARRAY:
//            case INTERVAL_ARRAY:
//            case MACADDR8_ARRAY:
//            case NUMRANGE_ARRAY:
//            case SMALLINT_ARRAY:
//            case TSVECTOR_ARRAY:
//            case DATERANGE_ARRAY:
//            case INT4RANGE_ARRAY:
//            case INT8RANGE_ARRAY:
//            case TIMESTAMP_ARRAY:
//            case TSTZRANGE_ARRAY:
//            case TIMESTAMPTZ_ARRAY:
//            case LINE_SEGMENT_ARRAY:
//                break;
//            default:
//                throw _Exceptions.unexpectedEnum((PostgreType) type);
//
//        }// switch
//    }


}
