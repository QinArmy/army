/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.dialect.postgre;

import io.army.criteria.*;
import io.army.criteria.impl._LiteralExpression;
import io.army.criteria.impl._UnionType;
import io.army.criteria.impl.inner.*;
import io.army.criteria.standard.StandardStatement;
import io.army.criteria.standard._SQLConsultant;
import io.army.dialect.DialectEnv;
import io.army.dialect.PostgreDialect;
import io.army.dialect.impl._ArmyDialectParser;
import io.army.dialect.impl._SqlContext;
import io.army.executor.ExecutorSupport;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SQLType;
import io.army.util.*;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

abstract class PostgreParser extends _ArmyDialectParser {

    static PostgreParser standard(DialectEnv environment, PostgreDialect dialect) {
        return new Standard(environment, dialect);
    }

    PostgreParser(DialectEnv environment, PostgreDialect dialect) {
        super(environment, dialect);
        switch (this.literalEscapeMode) {
            case DEFAULT:
            case BACK_SLASH:
            case UNICODE:
                break;
            default:
                throw _Exceptions.literalEscapeModeError(this.literalEscapeMode, this.dialect);
        }

        switch (this.identifierEscapeMode) {
            case DEFAULT:
            case UNICODE:
                break;
            default:
                throw _Exceptions.literalEscapeModeError(this.identifierEscapeMode, this.dialect);
        }

    }


    @Override
    public final void typeName(final MappingType type, final StringBuilder sqlBuilder) {
        final DataType dataType;
        dataType = type.map(this.serverMeta);

        if (!(dataType instanceof PostgreType)) { // user defined type or unrecognized type
            unrecognizedTypeName(type, dataType, true, sqlBuilder);
        } else if (dataType.isArray()) {
            final SQLType elementType;
            elementType = ((PostgreType) dataType).elementType();
            assert elementType != null;
            arrayTypeName(elementType.typeName(), ArrayUtils.dimensionOfType(type), sqlBuilder);
        } else switch ((PostgreType) dataType) {
            case REF_CURSOR:
            case UNKNOWN:
                throw ExecutorSupport.mapMethodError(type, dataType);
            default:
                sqlBuilder.append(dataType.typeName());

        } // switch

    }

    @Override
    protected final void parseWithClause(_Statement._WithClauseSpec spec, _SqlContext context) {
        final List<_Cte> cteList;
        cteList = spec.cteList();
        if (cteList.size() == 0) {
            return;
        }
        if (spec instanceof StandardStatement) {
            withSubQuery(spec.isRecursive(), cteList, context, _SQLConsultant::assertStandardCte);
        } else {
            postgreWithClause(cteList, spec.isRecursive(), context);
        }
    }

    protected void postgreWithClause(List<_Cte> cteList, boolean recursive, _SqlContext mainContext) {
        throw _Exceptions.dontSupportWithClause(this.dialect);
    }

    @Override
    protected final boolean isSupportOnlyDefault() {
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
    protected final void arrayTypeName(final String safeTypeNme, final int dimension,
                                       final StringBuilder sqlBuilder) {
        assert dimension > 0;
        sqlBuilder.append(safeTypeNme);
        for (int i = 0; i < dimension; i++) {
            sqlBuilder.append("[]");
        }

    }

    @Override
    protected final void bindLiteralNull(final MappingType type, final DataType dataType, final boolean typeName,
                                         final StringBuilder sqlBuilder) {
        if (!(dataType instanceof SQLType)) {
            sqlBuilder.append(_Constant.NULL);
            if (typeName) {
                sqlBuilder.append("::");
                identifier(dataType.typeName(), sqlBuilder);
            }
        } else switch ((PostgreType) dataType) {
            case UNKNOWN:
            case REF_CURSOR:
                throw ExecutorSupport.mapMethodError(type, dataType);
            default: {
                sqlBuilder.append(_Constant.NULL);
                if (typeName) {
                    sqlBuilder.append("::")
                            .append(dataType.typeName());
                }
            }

        }//switch

    }

    @Override
    protected final void bindLiteral(final TypeMeta typeMeta, final DataType dataType, final Object value,
                                     final boolean typeName, final StringBuilder sqlBuilder) {


        if (!(dataType instanceof PostgreType)) {
            if (!(value instanceof String)) {
                throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
            }
            bindUserDefinedLiteral(typeMeta, dataType, (String) value, typeName, sqlBuilder);
        } else if (dataType.isArray()) {
            if (!(value instanceof String) || dataType == PostgreType.RECORD_ARRAY) {
                throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
            }
            final SQLType elementType = ((SQLType) dataType).elementType();
            assert elementType != null;

            stringEscape((String) value, sqlBuilder);

            if (typeName) {
                sqlBuilder.append("::");
                arrayTypeName(elementType.typeName(), ArrayUtils.dimensionOfType(typeMeta.mappingType()), sqlBuilder);
            }
        } else switch ((PostgreType) dataType) {
            case BOOLEAN:
                _Literals.bindBoolean(typeMeta, dataType, value, sqlBuilder);
                break;
            case SMALLINT: {
                if (!(value instanceof Short)) {
                    throw _Exceptions.beforeBindMethod(dataType, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(value);
                if (typeName) {
                    sqlBuilder.append("::SMALLINT");
                }
            }
            break;
            case INTEGER: {
                if (!(value instanceof Integer)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
                if (typeName) {
                    sqlBuilder.append("::INTEGER");
                }
            }
            break;
            case BIGINT: {
                if (!(value instanceof Long)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
                if (typeName) {
                    sqlBuilder.append("::BIGINT");
                }
            }
            break;
            case DECIMAL: {
                if (!(value instanceof BigDecimal)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(((BigDecimal) value).toPlainString());
                if (typeName) {
                    sqlBuilder.append("::DECIMAL");
                }
            }
            break;
            case FLOAT8: {
                if (!(value instanceof Double)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
                if (typeName) {
                    sqlBuilder.append("::FLOAT8");
                }
            }
            break;
            case REAL: {
                if (!(value instanceof Float)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
                if (typeName) {
                    sqlBuilder.append("::REAL");
                }
            }
            break;
            case TIME: {
                if (!(value instanceof LocalTime)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                if (typeName) {
                    sqlBuilder.append("TIME ");
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(_TimeUtils.TIME_FORMATTER_6.format((LocalTime) value))
                        .append(_Constant.QUOTE);
            }
            break;
            case DATE: {
                if (!(value instanceof LocalDate)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                if (typeName) {
                    sqlBuilder.append("DATE ");
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(DateTimeFormatter.ISO_LOCAL_DATE.format((LocalDate) value))
                        .append(_Constant.QUOTE);
            }
            break;
            case TIMETZ: {
                if (!(value instanceof OffsetTime)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                if (typeName) {
                    sqlBuilder.append("TIMETZ ");
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(_TimeUtils.OFFSET_TIME_FORMATTER_6.format((OffsetTime) value))
                        .append(_Constant.QUOTE);
            }
            break;
            case TIMESTAMP: {
                if (!(value instanceof LocalDateTime)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                if (typeName) {
                    sqlBuilder.append("TIMESTAMP ");
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(_TimeUtils.DATETIME_FORMATTER_6.format((LocalDateTime) value))
                        .append(_Constant.QUOTE);
            }
            break;
            case TIMESTAMPTZ: {
                if (!(value instanceof OffsetDateTime)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                if (typeName) {
                    sqlBuilder.append("TIMESTAMPTZ ");
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(_TimeUtils.OFFSET_DATETIME_FORMATTER_6.format((OffsetDateTime) value))
                        .append(_Constant.QUOTE);
            }
            break;
            case CHAR:
            case BPCHAR:
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
                // multi range Types
            case INT4MULTIRANGE:
            case INT8MULTIRANGE:
            case NUMMULTIRANGE:
            case DATEMULTIRANGE:
            case TSMULTIRANGE:
            case TSTZMULTIRANGE:

            case INTERVAL:

            case ACLITEM:
            case PG_LSN:
            case PG_SNAPSHOT: {
                if (!(value instanceof String)) {
                    throw _Exceptions.beforeBindMethod(dataType, typeMeta.mappingType(), value);
                }
                if (typeName) {
                    sqlBuilder.append(dataType.typeName())
                            .append(_Constant.SPACE); //use dataType 'string' syntax not 'string'::dataType syntax,because XMLEXISTS function not work, see PostgreSQL 15.1 on x86_64-apple-darwin20.6.0, compiled by Apple clang version 12.0.0 (clang-1200.0.32.29), 64-bit
                }
                stringEscape((String) value, sqlBuilder);
            }
            break;
            case BYTEA: {
                if (!(value instanceof byte[])) {
                    throw _Exceptions.beforeBindMethod(dataType, typeMeta.mappingType(), value);
                }

                if (typeName) {
                    sqlBuilder.append(dataType.typeName())
                            .append(_Constant.SPACE); //use dataType 'string' syntax not 'string'::dataType syntax,because XMLEXISTS function not work, see PostgreSQL 15.1 on x86_64-apple-darwin20.6.0, compiled by Apple clang version 12.0.0 (clang-1200.0.32.29), 64-bit
                }

                sqlBuilder.append(_Constant.QUOTE)
                        .append(_Constant.BACK_SLASH)
                        .append('x')
                        .append(HexUtils.hexEscapesText(true, (byte[]) value))
                        .append(_Constant.QUOTE);
            }
            break;
            case VARBIT:
            case BIT: {

                if (!(value instanceof String)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                } else if (!_StringUtils.isBinary((String) value)) {
                    throw _Exceptions.valueOutRange(dataType, value);
                }
                if (typeName) {
                    sqlBuilder.append(dataType.typeName())
                            .append(_Constant.SPACE); //use dataType 'string' syntax not 'string'::dataType syntax,because XMLEXISTS function not work, see PostgreSQL 15.1 on x86_64-apple-darwin20.6.0, compiled by Apple clang version 12.0.0 (clang-1200.0.32.29), 64-bit
                }
                sqlBuilder.append('B')
                        .append(_Constant.QUOTE)
                        .append(value)
                        .append(_Constant.QUOTE);
            }
            break;
            case UNKNOWN:
            case REF_CURSOR:
            case RECORD:
                throw ExecutorSupport.mapMethodError(typeMeta.mappingType(), dataType);
            default:
                throw _Exceptions.unexpectedEnum((PostgreType) dataType);


        } // switch
    }


    @Override
    protected final PostgreDdlParser createDdlDialect() {
        return PostgreDdlParser.create(this);
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
    protected final boolean isSupportWithClause() {
        // Postgre support WITH clause
        return true;
    }

    @Override
    protected final boolean isSupportWithClauseInInsert() {
        // Postgre support WITH clause in INSERT statement
        return true;
    }

    @Override
    protected final boolean isSupportWindowClause() {
        // Postgre support WINDOW clause
        return true;
    }

    @Override
    protected final boolean isSupportUpdateRow() {
        // Postgre support update row
        return true;
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-update.html">UPDATE statement</a>
     */
    @Override
    protected final boolean isSupportJoinableSingleUpdate() {
        // true ,Postgre support single-table joinable update
        return true;
    }

    @Override
    protected final boolean isSupportUpdateDerivedField() {
        // Postgre don't support update derived field
        return false;
    }

    @Override
    protected final boolean isSupportReturningClause() {
        // Postgre support RETURNING clause
        return true;
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
                .append(_Constant.PERIOD)
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
                if (!upperCase) {
                    upperCase = true;
                }
                continue;
            } else if ((ch >= '0' && ch <= '9') || ch == '$') {
                if (i == 0) {
                    mode = IdentifierMode.QUOTING;
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
                case _Constant.BACK_SLASH: {
                    if (mode != IdentifierMode.ESCAPES) {
                        mode = IdentifierMode.ESCAPES;
                    }
                }
                break;
                default: {
                    if (mode == null) {
                        mode = IdentifierMode.QUOTING;
                    }
                    break;
                } // default
            } // switch


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

    @Override
    protected final void escapesIdentifier(final String identifier, final StringBuilder sqlBuilder) {
        switch (this.identifierEscapeMode) {
            case DEFAULT:
                simplyEscapeIdentifier(identifier, _Constant.DOUBLE_QUOTE, sqlBuilder);
                break;
            case UNICODE:
                _PostgreLiterals.unicodeEscape(identifier, 0, identifier.length(), _Constant.BACK_SLASH, sqlBuilder);
                break;
            default:
                throw _Exceptions.identifierEscapeModeError(this.identifierEscapeMode, this.dialect);
        }

    }


    @Override
    protected final boolean isUseObjectNameModeMethod() {
        // true ,Postgre use objectNameMode() method
        return true;
    }

    @Override
    protected final IdentifierMode objectNameMode(final DatabaseObject object, final String effectiveName) {
        final int length = effectiveName.length();
        if (length == 0) {
            return IdentifierMode.ERROR;
        }
        IdentifierMode mode = null;
        char ch;
        outerFor:
        for (int i = 0; i < length; i++) {
            ch = effectiveName.charAt(i);
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_') {
                continue;
            } else if ((ch >= '0' && ch <= '9') || ch == '$') {
                if (i == 0) {
                    mode = IdentifierMode.QUOTING;
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
            mode = IdentifierMode.SIMPLE;
        }
        return mode;
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
                .append(_Constant.SPACE_AS)
                .append(_Constant.SPACE_LEFT_PAREN)
                .append(_Constant.SPACE)
                .append(_Constant.UPDATE)
                .append(_Constant.SPACE_ONLY)
                .append(_Constant.SPACE)
                .append(safeChildTableName) // child table name.
                .append(_Constant.SPACE_AS_SPACE)
                .append(safeChildTableAlias);

        this.singleTableSetClause(((_DomainUpdate) stmt).childItemPairList(), childContext); // child SET clause

        sqlBuilder.append(_Constant.SPACE_FROM_SPACE)
                .append(safeParentTableName)
                .append(_Constant.SPACE_AS_SPACE)
                .append(safeParentAlias);

        // child cte WHERE clause
        this.childDomainCteWhereClause(stmt.wherePredicateList(), childContext);
        this.discriminator(domainTable, safeParentAlias, childContext);
        childContext.appendConditionFields();
        if (parentTable.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(parentTable, safeParentAlias, childContext, false);
        }

        final String safeIdColumnName;
        safeIdColumnName = safeObjectName(domainTable.id());

        // RETURNING clause
        sqlBuilder.append(_Constant.SPACE_RETURNING)
                .append(_Constant.SPACE)
                .append(safeChildTableAlias)
                .append(_Constant.PERIOD)
                .append(safeIdColumnName);

        sqlBuilder.append(_Constant.SPACE_AS_SPACE)
                .append(_MetaBridge.ID)
                .append(_Constant.SPACE_RIGHT_PAREN);

        // child cte end

        // below primary UPDATE statement part, parent table.
        sqlBuilder.append(_Constant.SPACE)
                .append(_Constant.UPDATE)
                .append(_Constant.SPACE_ONLY)
                .append(_Constant.SPACE)
                .append(safeParentTableName) // parent table name.
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
                    .append(_Constant.PERIOD)
                    .append(safeIdColumnName)
                    .append(_Constant.SPACE_EQUAL_SPACE)
                    .append(childCte)
                    .append(_Constant.PERIOD)
                    .append(_MetaBridge.ID);

        }

        sqlBuilder.append(_Constant.SPACE_WHERE)
                .append(_Constant.SPACE)
                .append(safeParentAlias)
                .append(_Constant.PERIOD)
                .append(safeIdColumnName)
                .append(_Constant.SPACE_EQUAL_SPACE)
                .append(childCte)
                .append(_Constant.PERIOD)
                .append(_MetaBridge.ID);


    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-delete.html">Postgre DELETE syntax</a>
     */
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

        final String safeIdColumnName;
        safeIdColumnName = safeObjectName(domainTable.id());

        // RETURNING clause
        sqlBuilder.append(_Constant.SPACE_RETURNING)
                .append(_Constant.SPACE)
                .append(safeChildTableAlias)
                .append(_Constant.PERIOD)
                .append(safeIdColumnName);

        sqlBuilder.append(_Constant.SPACE_AS_SPACE)
                .append(_MetaBridge.ID)
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
                .append(_Constant.SPACE)
                .append(deleteCte)
                .append(_Constant.SPACE_WHERE)
                .append(_Constant.SPACE)
                .append(safeParentAlias)
                .append(_Constant.PERIOD)
                .append(safeIdColumnName)
                .append(_Constant.SPACE_EQUAL_SPACE)
                .append(deleteCte)
                .append(_Constant.PERIOD)
                .append(_MetaBridge.ID);
    }

    @Override
    protected final void standardLimitClause(final @Nullable _Expression offset, final @Nullable _Expression rowCount,
                                             final _SqlContext context) {

        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();
        if (rowCount != null) {
            sqlBuilder.append(_Constant.SPACE_LIMIT);
            if (rowCount instanceof LiteralExpression) {
                ((_LiteralExpression) rowCount).appendSqlWithoutType(sqlBuilder, context);
            } else {
                rowCount.appendSql(sqlBuilder, context);
            }

        }
        if (offset != null) {
            sqlBuilder.append(_Constant.SPACE_OFFSET);
            if (rowCount instanceof LiteralExpression) {
                ((_LiteralExpression) offset).appendSqlWithoutType(sqlBuilder, context);
            } else {
                offset.appendSql(sqlBuilder, context);
            }
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
        throw _Exceptions.unexpectedStatement(delete);
    }

    /*-------------------below private methods -------------------*/


    /**
     * @see #bindLiteral(TypeMeta, DataType, Object, boolean, StringBuilder)
     */
    private void bindUserDefinedLiteral(final TypeMeta typeMeta, final DataType dataType, final String value,
                                        final boolean typeName, final StringBuilder sqlBuilder) {

        final int length = value.length();
        if (dataType.isArray()) {
            if (length < 2
                    || value.charAt(0) != _Constant.LEFT_BRACE
                    || value.charAt(length - 1) != _Constant.RIGHT_BRACE) {
                throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
            }
        }

        stringEscape(value, sqlBuilder);

        if (typeName) {
            sqlBuilder.append("::");
            unrecognizedTypeName(typeMeta.mappingType(), dataType, true, sqlBuilder);
        }

    }


    /**
     * @see #bindLiteral(TypeMeta, DataType, Object, boolean, StringBuilder)
     */
    private void stringEscape(final CharSequence value, final StringBuilder builder) {
        switch (this.literalEscapeMode) {
            case DEFAULT:
            case BACK_SLASH:
                _PostgreLiterals.backslashEscape(value, 0, value.length(), builder);
                break;
            case UNICODE:
                _PostgreLiterals.unicodeEscape(value, 0, value.length(), _Constant.BACK_SLASH, builder);
                break;
            default:
                throw _Exceptions.dontSupportEscapeMode(this.literalEscapeMode, this.dialect);
        }

    }


    private static final class Standard extends PostgreParser {

        private Standard(DialectEnv environment, PostgreDialect dialect) {
            super(environment, dialect);
        }

    } // Standard


}
