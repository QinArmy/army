package io.army.dialect.postgre;

import io.army.criteria.CriteriaException;
import io.army.criteria.SQLWords;
import io.army.criteria.Statement;
import io.army.criteria.Visible;
import io.army.criteria.impl._UnionType;
import io.army.criteria.impl.inner.*;
import io.army.dialect.*;
import io.army.lang.Nullable;
import io.army.mapping.BooleanType;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TypeMeta;
import io.army.modelgen._MetaBridge;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SqlType;
import io.army.tx.Isolation;
import io.army.util.ArrayUtils;
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
    protected final void buildInTypeName(final SqlType sqlType, final MappingType type, final StringBuilder sqlBuilder) {
        switch ((PostgreSqlType) sqlType) {
            case BOOLEAN:
            case SMALLINT:
            case INTEGER:
            case BIGINT:
            case DECIMAL:
            case FLOAT8:
            case REAL:
            case TIME:
            case DATE:
            case TIMESTAMP:
            case TIMETZ:
            case TIMESTAMPTZ:
            case CHAR:
            case VARCHAR:
            case TEXT:
            case JSON:
            case JSONB:
            case JSONPATH:
            case BYTEA:
            case BIT:
            case VARBIT:
            case XML:
            case CIDR:
            case INET:
            case LINE:
            case PATH:
            case UUID:
            case MONEY:
            case MACADDR8:
            case POINT:
            case BOX:
            case POLYGON:
            case CIRCLE:
            case TSQUERY:
            case TSVECTOR:
            case LSEG:
            case TSRANGE:
            case INTERVAL:
            case NUMRANGE:
            case DATERANGE:
            case INT4RANGE:
            case INT8RANGE:
            case TSTZRANGE:
            case MACADDR:
                sqlBuilder.append(sqlType.name());
                break;
            case NO_CAST_INTEGER:
                sqlBuilder.append(PostgreSqlType.INTEGER.name());
                break;
            case NO_CAST_TEXT:
                sqlBuilder.append(PostgreSqlType.TEXT.name());
                break;
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
            case REF_CURSOR:
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
            case LSEG_ARRAY: {
                final String name, typeName;
                name = sqlType.name();
                typeName = name.substring(0, name.indexOf("_ARRAY"));
                this.arrayTypeName(typeName, ArrayUtils.dimensionOfType(type), sqlBuilder);
            }
            break;
            case UNKNOWN:
                throw _Exceptions.mapMethodError(type, PostgreSqlType.class);
            default:
                // no bug,never here
                throw _Exceptions.unexpectedEnum((PostgreSqlType) sqlType);
        }

    }

    @Override
    protected final void arrayTypeName(final String safeTypeNme, final int dimension,
                                       final StringBuilder sqlBuilder) {
        assert dimension > 0;
        sqlBuilder.append(safeTypeNme);
        for (int i = 0; i < dimension; i++) {
            sqlBuilder.append("[]");
        }

    }

    @Override
    protected final void bindLiteralNull(final SqlType sqlType, final MappingType type, final StringBuilder sqlBuilder) {
        switch ((PostgreSqlType) sqlType) {
            case UNKNOWN:
                throw _Exceptions.mapMethodError(type, PostgreSqlType.class);
            case NO_CAST_TEXT:
                sqlBuilder.append(_Constant.NULL);
                break;
            default: {
                sqlBuilder.append(_Constant.NULL)
                        .append("::");
                this.typeName(type, sqlBuilder);
            }

        }//switch

    }

    @Override
    protected final void bindLiteral(final TypeMeta typeMeta, final SqlType type, final Object value,
                                     final StringBuilder sqlBuilder) {
        switch ((PostgreSqlType) type) {
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
            case NO_CAST_INTEGER: {
                if (!(value instanceof Integer)) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(value);
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
            case FLOAT8: {
                if (!(value instanceof Double)) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(value)
                        .append("::FLOAT8");
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
            case TIME: {
                sqlBuilder.append("TIME ");
                _Literals.bindLocalTime(typeMeta, type, value, sqlBuilder);
            }
            break;
            case DATE: {
                sqlBuilder.append("DATE ");
                _Literals.bindLocalDate(typeMeta, type, value, sqlBuilder);
            }
            break;
            case TIMETZ: {
                sqlBuilder.append("TIMETZ ");
                _Literals.bindOffsetTime(typeMeta, type, value, sqlBuilder);
            }
            break;
            case TIMESTAMP: {
                sqlBuilder.append("TIMESTAMP ");
                _Literals.bindLocalDateTime(typeMeta, type, value, sqlBuilder);
            }
            break;
            case TIMESTAMPTZ: {
                sqlBuilder.append("TIMESTAMPTZ ");
                _Literals.bindOffsetDateTime(typeMeta, type, value, sqlBuilder);
            }
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
            case VARCHAR:
            case TEXT:
            case JSON:
            case JSONB:
            case JSONPATH:
            case XML:
                // Geometric Types
            case POINT:
            case LINE:
            case LSEG:
            case BOX:
            case PATH:
            case POLYGON:
            case CIRCLE:
                // Network Address Types
            case CIDR:
            case INET:
            case MACADDR:
            case MACADDR8:
                //
            case UUID:
            case MONEY:
            case TSQUERY:
            case TSVECTOR:
                // Range Types
            case INT4RANGE:
            case INT8RANGE:
            case NUMRANGE:
            case TSRANGE:
            case TSTZRANGE:
            case DATERANGE:
            case INTERVAL:
            case REF_CURSOR: {
                sqlBuilder.append(type.name())
                        .append(_Constant.SPACE); //use type 'string' syntax not 'string'::type syntax,because XMLEXISTS function not work, see PostgreSQL 15.1 on x86_64-apple-darwin20.6.0, compiled by Apple clang version 12.0.0 (clang-1200.0.32.29), 64-bit
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder);
            }
            break;
            case NO_CAST_TEXT:
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder);
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
            case VARBIT:
            case BIT: {
                sqlBuilder.append(type.name())
                        .append(_Constant.SPACE); //use type 'string' syntax not 'string'::type syntax,because XMLEXISTS function not work, see PostgreSQL 15.1 on x86_64-apple-darwin20.6.0, compiled by Apple clang version 12.0.0 (clang-1200.0.32.29), 64-bit
                PostgreLiterals.postgreBitString(typeMeta, type, value, sqlBuilder);
            }
            break;
            case USER_DEFINED: {
                if (!(value instanceof String)) {
                    throw _Exceptions.beforeBindMethod(type, typeMeta.mappingType(), value);
                }
                final MappingType mappingType;
                mappingType = typeMeta.mappingType();
                if (!(mappingType instanceof MappingType.SqlUserDefinedType)) {
                    throw _Exceptions.notUserDefinedType(mappingType, type);
                }
                final String typeName;
                typeName = ((MappingType.SqlUserDefinedType) mappingType).sqlTypeName(this.serverMeta);
                this.identifier(typeName, sqlBuilder)
                        .append(_Constant.SPACE);
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder);
            }
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
            case JSONB_ARRAY:
            case LINE_ARRAY:
            case PATH_ARRAY:
            case TEXT_ARRAY:
            case USER_DEFINED_ARRAY:
                this.unsafeArray(typeMeta, type, value, sqlBuilder, _Literals::stringArrayElement);
                break;
            case BOOLEAN_ARRAY:
                this.safeArray(typeMeta, type, value, sqlBuilder, _Literals::booleanArrayElement);
                break;
            case SMALLINT_ARRAY:
                this.safeArray(typeMeta, type, value, sqlBuilder, _Literals::shortArrayElement);
                break;
            case INTEGER_ARRAY:
                this.safeArray(typeMeta, type, value, sqlBuilder, _Literals::integerArrayElement);
                break;
            case BIGINT_ARRAY:
                this.safeArray(typeMeta, type, value, sqlBuilder, _Literals::longArrayElement);
                break;
            case DECIMAL_ARRAY:
                this.safeArray(typeMeta, type, value, sqlBuilder, _Literals::bigDecimalArrayElement);
                break;
            case DOUBLE_ARRAY:
                this.safeArray(typeMeta, type, value, sqlBuilder, _Literals::doubleArrayElement);
                break;
            case REAL_ARRAY:
                this.safeArray(typeMeta, type, value, sqlBuilder, _Literals::floatArrayElement);
                break;
            case TIME_ARRAY:
            case TIMETZ_ARRAY:
            case TIMESTAMP_ARRAY:
            case TIMESTAMPTZ_ARRAY:


            case UUID_ARRAY:
            case BYTEA_ARRAY:
            case MONEY_ARRAY:
            case POINT_ARRAY:

            case VARBIT_ARRAY:
            case CIRCLES_ARRAY:
            case MACADDR_ARRAY:
            case POLYGON_ARRAY:
            case TSQUERY_ARRAY:
            case TSRANGE_ARRAY:
            case VARCHAR_ARRAY:
            case INTERVAL_ARRAY:
            case MACADDR8_ARRAY:

            case TSVECTOR_ARRAY:
            case DATERANGE_ARRAY:
            case NUMRANGE_ARRAY:
            case INT4RANGE_ARRAY:
            case INT8RANGE_ARRAY:
            case TSTZRANGE_ARRAY:

            case LSEG_ARRAY:
                //TODO check array syntax
                PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder);
                break;
            case UNKNOWN:
                throw _Exceptions.literalDontSupport(type);
            default:
                throw _Exceptions.unexpectedEnum((PostgreSqlType) type);

        }// switch


    }


    @Override
    protected final PostgreDdlParser createDdlDialect() {
        return PostgreDdlParser.create(this);
    }

    @Override
    protected final boolean existsIgnoreOnConflict() {
        //true,Postgre support DO NOTHING and WHERE in ON CONFLICT clause.
        return true;
    }

    @Override
    protected final CriteriaException supportChildInsert(_Insert._ChildInsert childStmt, Visible visible) {
        return null;
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
    protected final ChildUpdateMode childUpdateMode() {
        // Postgre support DML in cte.
        return ChildUpdateMode.CTE;
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
    protected final boolean isValidateUnionType() {
        // false
        return false;
    }

    @Override
    protected final void validateUnionType(_UnionType unionType) {
        //no-op, no bug never here
    }

    @Override
    protected final String qualifiedSchemaName(final ServerMeta meta) {
        final String catalog, schema;
        catalog = meta.catalog();
        schema = meta.schema();
        if (!_StringUtils.hasText(catalog) || !_StringUtils.hasText(schema)) {
            throw _Exceptions.serverMetaError(meta);
        }
        return _StringUtils.builder()
                .append(catalog)
                .append(_Constant.POINT)
                .append(schema)
                .toString();
    }

    @Override
    protected final IdentifierMode identifierMode(String identifier) {
        final int length = identifier.length();
        if (length == 0) {
            return IdentifierMode.ERROR;
        }
        IdentifierMode mode = null;
        char ch;
        boolean upperCase = false;

        outerFor:
        for (int i = 0; i < length; i++) {
            ch = identifier.charAt(i);
            if ((ch >= 'a' && ch <= 'z') || ch == '_') {
                continue;
            } else if (ch >= 'A' && ch <= 'Z') {
                upperCase = true;
                continue;
            } else if ((ch >= '0' && ch <= '9') || ch == '$') {
                if (i == 0) {
                    mode = IdentifierMode.ERROR;
                    break;
                }
                continue;
            }

            switch (ch) {
                case _Constant.NUL_CHAR:
                    mode = IdentifierMode.ERROR;
                    break outerFor;
                case '\b':
                case '\f':
                case '\n':
                case '\r':
                case '\t':
                case _Constant.DOUBLE_QUOTE:
                case _Constant.BACK_SLASH:
                    mode = IdentifierMode.ESCAPES;
                    break outerFor;
                default: {
                    if (mode == null) {
                        mode = IdentifierMode.QUOTING;
                    }
                }
                break;
            }// switch


        } // for

        if (mode == null) {
            if (upperCase) {
                mode = IdentifierMode.QUOTING;
            } else {
                mode = IdentifierMode.SIMPLE;
            }
        }
        return mode;
    }


    protected final void escapesIdentifier(final String identifier, final StringBuilder sqlBuilder) {
        final int length;
        length = identifier.length();
        if (length == 0) {
            throw _Exceptions.identifierError(identifier, this.dialect);
        }
        final int startIndex;
        startIndex = sqlBuilder.length();

        sqlBuilder.append(_Constant.DOUBLE_QUOTE);
        int lastWritten = 0;
        String unicode = null;
        char ch;
        for (int i = 0; i < length; i++) {
            ch = identifier.charAt(i);
            switch (ch) {
                case _Constant.NUL_CHAR:
                    throw _Exceptions.identifierError(identifier, this.dialect);
                case _Constant.DOUBLE_QUOTE: {
                    if (i > lastWritten) {
                        sqlBuilder.append(identifier, lastWritten, i); // identifier is String not char[],so is i not i- lastWritten
                    }
                    sqlBuilder.append(_Constant.DOUBLE_QUOTE); //
                    lastWritten = i;//not i + 1 as current char wasn't written
                }
                continue;
                case '\b':
                    unicode = "\\u0008";
                    break;
                case '\f':
                    unicode = "\\u000c";
                    break;
                case '\n':
                    unicode = "\\u000a";
                    break;
                case '\r':
                    unicode = "\\u000d";
                    break;
                case '\t':
                    unicode = "\\u0009";
                    break;
                case _Constant.BACK_SLASH:
                    unicode = "\\\\";
                    break;
                default:
                    continue;
            }

            if (i > lastWritten) {
                sqlBuilder.append(identifier, lastWritten, i); // identifier is String not char[],so is i not i- lastWritten
            }
            sqlBuilder.append(unicode);
            lastWritten = i + 1;

        }// for

        if (lastWritten < length) {
            sqlBuilder.append(identifier, lastWritten, length); // identifier is String not char[],so is length not length- lastWritten
        }
        sqlBuilder.append(_Constant.DOUBLE_QUOTE);
        if (unicode != null) {
            sqlBuilder.insert(startIndex, "U&");
            sqlBuilder.append(" UESCAPE '\\'");
        }


    }

    @Override
    protected final String beautifySql(String sql) {
        return sql;
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

        final String safeParentTableName, safeChildTableName, safeParentAlias, safeChildTableAlias;

        // child table part
        final ChildTableMeta<?> domainTable = (ChildTableMeta<?>) childContext.domainTable();
        assert domainTable == stmt.table() && domainTable == childContext.targetTable();
        safeChildTableName = this.safeObjectName(domainTable);
        safeChildTableAlias = childContext.safeTargetTableAlias();

        // parent table part
        final ParentTableMeta<?> parentTable = (ParentTableMeta<?>) parentContext.targetTable();
        assert domainTable.parentMeta() == parentTable;
        safeParentTableName = this.safeObjectName(parentTable);
        safeParentAlias = parentContext.safeTargetTableAlias();


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
                .append(_Constant.DOUBLE_QUOTE)
                .append(_MetaBridge.ID)
                .append(_Constant.DOUBLE_QUOTE)
                .append(_Constant.SPACE_RIGHT_PAREN)
                .append(_Constant.SPACE_AS)
                .append(_Constant.SPACE_LEFT_PAREN)
                .append(_Constant.SPACE)
                .append(_Constant.UPDATE)
                .append(_Constant.SPACE_ONLY)
                .append(_Constant.SPACE)
                .append(safeChildTableName)// child table name.
                .append(_Constant.SPACE_AS_SPACE)
                .append(safeChildTableAlias);

        this.singleTableSetClause(((_DomainUpdate) stmt).childItemPairList(), childContext); // child SET clause

        sqlBuilder.append(_Constant.SPACE_FROM_SPACE)
                .append(safeParentTableName)
                .append(_Constant.SPACE_AS_SPACE)
                .append(safeParentAlias);

        // child cte WHERE clause
        this.childDomainCteWhereClause(stmt.wherePredicateList(), childContext);
        this.discriminator(domainTable, safeParentAlias, context);
        childContext.appendConditionFields();
        if (parentTable.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(parentTable, safeParentAlias, childContext, false);
        }


        // RETURNING clause
        sqlBuilder.append(_Constant.SPACE_RETURNING)
                .append(_Constant.SPACE)
                .append(safeChildTableAlias)
                .append(_Constant.POINT)
                .append(_Constant.DOUBLE_QUOTE)
                .append(_MetaBridge.ID)
                .append(_Constant.DOUBLE_QUOTE)
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
            // append join child table
            sqlBuilder.append(_Constant.SPACE_JOIN_SPACE)
                    .append(safeChildTableName)
                    .append(_Constant.SPACE_AS_SPACE)
                    .append(safeChildTableAlias)
                    .append(_Constant.SPACE_ON_SPACE)
                    .append(safeChildTableAlias)
                    .append(_Constant.POINT)
                    .append(_Constant.DOUBLE_QUOTE)
                    .append(_MetaBridge.ID)
                    .append(_Constant.DOUBLE_QUOTE)
                    .append(_Constant.SPACE_EQUAL_SPACE)
                    .append(childCte)
                    .append(_Constant.POINT)
                    .append(_Constant.DOUBLE_QUOTE)
                    .append(_MetaBridge.ID)
                    .append(_Constant.DOUBLE_QUOTE);

        }

        sqlBuilder.append(_Constant.SPACE_WHERE)
                .append(safeParentAlias)
                .append(_Constant.POINT)
                .append(_Constant.DOUBLE_QUOTE)
                .append(_MetaBridge.ID)
                .append(_Constant.DOUBLE_QUOTE)
                .append(_Constant.SPACE_EQUAL_SPACE)
                .append(childCte)
                .append(_Constant.POINT)
                .append(_Constant.DOUBLE_QUOTE)
                .append(_MetaBridge.ID)
                .append(_Constant.DOUBLE_QUOTE);


    }

    @Override
    protected final void parseDomainChildDelete(final _SingleDelete stmt, final _DeleteContext context) {

        final _SingleDeleteContext childContext = (_SingleDeleteContext) context;
        final _SingleDeleteContext parentContext = (_SingleDeleteContext) childContext.parentContext();
        assert parentContext != null;

        final String safeParentTableName, safeChildTableName, safeParentAlias, safeChildTableAlias;

        // child table part
        final ChildTableMeta<?> domainTable = (ChildTableMeta<?>) childContext.domainTable();
        assert domainTable == stmt.table() && domainTable == childContext.targetTable();
        safeChildTableName = this.safeObjectName(domainTable);
        safeChildTableAlias = childContext.safeTargetTableAlias();

        // parent table part
        final ParentTableMeta<?> parentTable = (ParentTableMeta<?>) parentContext.targetTable();
        assert domainTable.parentMeta() == parentTable;
        safeParentTableName = this.safeObjectName(parentTable);
        safeParentAlias = parentContext.safeTargetTableAlias();


        final StringBuilder sqlBuilder;
        sqlBuilder = childContext.sqlBuilder();
        assert parentContext.sqlBuilder() == sqlBuilder; // must assert

        if (sqlBuilder.length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }

        // append child table DELETE cte statement
        final String deleteCte;
        deleteCte = this.identifier(childContext.targetTableAlias() + "_delete_cte");
        sqlBuilder.append(_Constant.WITH)
                .append(_Constant.SPACE)
                .append(deleteCte)
                .append(_Constant.SPACE_LEFT_PAREN)
                .append(_Constant.SPACE)
                .append(_Constant.DOUBLE_QUOTE)
                .append(_MetaBridge.ID)
                .append(_Constant.DOUBLE_QUOTE)
                .append(_Constant.SPACE_RIGHT_PAREN)
                .append(_Constant.SPACE_AS)
                .append(_Constant.SPACE_LEFT_PAREN)
                .append(_Constant.SPACE)
                .append(_Constant.DELETE_FROM)
                .append(_Constant.SPACE_ONLY)
                .append(_Constant.SPACE)
                .append(safeChildTableName)// child table name.
                .append(_Constant.SPACE_AS_SPACE)
                .append(safeChildTableAlias)
                .append(_Constant.SPACE_USING)
                .append(_Constant.SPACE)
                .append(safeParentTableName)
                .append(_Constant.SPACE_AS_SPACE)
                .append(safeParentAlias);

        // child cte WHERE clause
        this.childDomainCteWhereClause(stmt.wherePredicateList(), childContext);
        this.discriminator(domainTable, safeParentAlias, context);
        if (parentTable.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(parentTable, safeParentAlias, childContext, false);
        }

        // RETURNING clause
        sqlBuilder.append(_Constant.SPACE_RETURNING)
                .append(_Constant.SPACE)
                .append(safeChildTableAlias)
                .append(_Constant.POINT)
                .append(_Constant.DOUBLE_QUOTE)
                .append(_MetaBridge.ID)
                .append(_Constant.DOUBLE_QUOTE)
                .append(_Constant.SPACE_AS_SPACE)
                .append(_Constant.DOUBLE_QUOTE)
                .append(_MetaBridge.ID)
                .append(_Constant.DOUBLE_QUOTE)
                .append(_Constant.SPACE_RIGHT_PAREN);

        // child cte end


        // below primary DELETE statement part, parent table.
        sqlBuilder.append(_Constant.SPACE)
                .append(_Constant.DELETE_FROM)
                .append(_Constant.SPACE_ONLY)
                .append(_Constant.SPACE)
                .append(safeParentTableName)// parent table name.
                .append(_Constant.SPACE_AS_SPACE)
                .append(safeParentAlias)
                .append(_Constant.SPACE_USING)   // parent part USING clause
                .append(deleteCte)
                .append(_Constant.SPACE_WHERE)
                .append(_Constant.SPACE)
                .append(safeParentAlias)
                .append(_Constant.POINT)
                .append(_Constant.DOUBLE_QUOTE)
                .append(_MetaBridge.ID)
                .append(_Constant.DOUBLE_QUOTE)
                .append(_Constant.SPACE_EQUAL_SPACE)
                .append(deleteCte)
                .append(_Constant.POINT)
                .append(_Constant.DOUBLE_QUOTE)
                .append(_MetaBridge.ID)
                .append(_Constant.DOUBLE_QUOTE);
    }

    @Override
    protected final void standardLimitClause(final @Nullable _Expression offset, final @Nullable _Expression rowCount,
                                             final _SqlContext context) {

        if (rowCount != null) {
            context.sqlBuilder().append(_Constant.SPACE_LIMIT);
            rowCount.appendSql(context);
        }
        if (offset != null) {
            context.sqlBuilder().append(_Constant.SPACE_OFFSET);
            offset.appendSql(context);
        }

    }

    @Override
    protected final void standardLockClause(SQLWords lockMode, _SqlContext context) {
        if (!_Constant.SPACE_FOR_UPDATE.equals(lockMode.spaceRender())) {
            throw _Exceptions.castCriteriaApi();
        }
        context.sqlBuilder().append(_Constant.SPACE_FOR_UPDATE);
    }

    @Override
    protected final void parseMultiUpdate(_MultiUpdate update, _MultiUpdateContext context) {
        // Postgre don't support multi-table UPDATE syntax
        throw _Exceptions.unexpectedStatement((Statement) update);
    }

    @Override
    protected final void parseMultiDelete(_MultiDelete delete, _MultiDeleteContext context) {
        // Postgre don't support multi-table DELETE syntax
        throw _Exceptions.unexpectedStatement((Statement) delete);
    }

    /**
     * @see #bindLiteral(TypeMeta, SqlType, Object, StringBuilder)
     */
    private void safeArray(final TypeMeta typeMeta, final SqlType type, final Object value,
                           final StringBuilder sqlBuilder,
                           final _Literals.ArrayElementHandler handler) {
        final MappingType mappingType;
        if (typeMeta instanceof MappingType) {
            mappingType = (MappingType) typeMeta;
        } else {
            mappingType = typeMeta.mappingType();
        }
        assert !(mappingType instanceof _ArmyBuildInMapping) || mappingType instanceof _ArmyNoInjectionMapping;

        if (value instanceof String) {
            arrayForStringValue(typeMeta, type, (String) value, sqlBuilder);
        } else if (value.getClass().isArray()) {
            sqlBuilder.append(_Constant.QUOTE);
            PostgreLiterals.appendSimpleTypeArray(mappingType, type, value, sqlBuilder, handler);
            sqlBuilder.append(_Constant.QUOTE);
        } else {
            throw _Exceptions.valueOutRange(type, value);
        }
        sqlBuilder.append("::");
        this.typeName(mappingType, sqlBuilder);
    }

    /**
     * @see #bindLiteral(TypeMeta, SqlType, Object, StringBuilder)
     */
    private void unsafeArray(final TypeMeta typeMeta, final SqlType type, final Object value,
                             final StringBuilder sqlBuilder,
                             final _Literals.ArrayElementHandler handler) {
        final MappingType mappingType;
        if (typeMeta instanceof MappingType) {
            mappingType = (MappingType) typeMeta;
        } else {
            mappingType = typeMeta.mappingType();
        }
        assert !(mappingType instanceof _ArmyNoInjectionMapping);
        if (value instanceof String) {
            arrayForStringValue(typeMeta, type, (String) value, sqlBuilder);
        } else if (value.getClass().isArray()) {

            final StringBuilder tempBuilder = new StringBuilder();
            PostgreLiterals.appendSimpleTypeArray(mappingType, type, value, tempBuilder, handler);

            PostgreLiterals.postgreBackslashEscapes(typeMeta, type, tempBuilder.toString(), sqlBuilder);
        } else {
            throw _Exceptions.valueOutRange(type, value);
        }
        sqlBuilder.append("::");
        this.typeName(mappingType, sqlBuilder);
    }

    /**
     * @see #bindLiteral(TypeMeta, SqlType, Object, StringBuilder)
     * @see #safeArray(TypeMeta, SqlType, Object, StringBuilder, _Literals.ArrayElementHandler)
     */
    private void arrayForStringValue(final TypeMeta typeMeta, final SqlType type, final String value,
                                     final StringBuilder sqlBuilder) {
        final int length;
        length = value.length();
        if (length < 2
                || value.charAt(0) != _Constant.LEFT_BRACE
                || value.charAt(length - 1) != _Constant.RIGHT_BRACE) {
            throw _Exceptions.valueOutRange(type, value);
        }
        PostgreLiterals.postgreBackslashEscapes(typeMeta, type, value, sqlBuilder);
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
