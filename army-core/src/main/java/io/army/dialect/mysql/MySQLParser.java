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

package io.army.dialect.mysql;

import io.army.criteria.*;
import io.army.criteria.impl._SQLConsultant;
import io.army.criteria.impl._UnionType;
import io.army.criteria.impl.inner.*;
import io.army.dialect.*;
import io.army.env.EscapeMode;
import io.army.executor.ExecutorSupport;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.util.HexUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;
import io.army.util._TimeUtils;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.time.*;
import java.util.List;
import java.util.Set;

abstract class MySQLParser extends _ArmyDialectParser {

    static MySQLParser standard(DialectEnv environment, MySQLDialect dialect) {
        return new Standard(environment, dialect);
    }


    static final char BACKTICK = '`';

    final boolean asOf80;


    MySQLParser(DialectEnv environment, MySQLDialect dialect) {
        super(environment, dialect);

        if (this.identifierEscapeMode != EscapeMode.DEFAULT) {
            throw _Exceptions.identifierEscapeModeError(this.identifierEscapeMode, this.dialect);
        } else switch (this.literalEscapeMode) {
            case DEFAULT:
            case BACK_SLASH:
                break;
            default:
                throw _Exceptions.literalEscapeModeError(this.literalEscapeMode, this.dialect);

        }

        // Prior to / as of
        this.asOf80 = ((MySQLDialect) this.dialect).compareWith(MySQLDialect.MySQL80) >= 0;
    }


    @Override
    public final String sqlElement(SQLElement element) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    public final void typeName(final MappingType type, final StringBuilder sqlBuilder) {
        final DataType dataType;
        dataType = type.map(this.serverMeta);
        if (!(dataType instanceof MySQLType)) {
            unrecognizedTypeName(type, dataType, false, sqlBuilder);
        } else switch ((MySQLType) dataType) {
            case UNKNOWN:
            case NULL:
                throw ExecutorSupport.mapMethodError(type, dataType);
            default:
                sqlBuilder.append(dataType.typeName());
        }

    }



    /*-------------------below protected methods -------------------*/

    @Override
    protected final void parseWithClause(final _Statement._WithClauseSpec spec, final _SqlContext context) {
        final List<_Cte> cteList;
        cteList = spec.cteList();
        if (cteList.size() == 0) {
            return;
        }
        if (!this.asOf80) {
            throw _Exceptions.dontSupportWithClause(this.dialect);
        }
        withSubQuery(spec.isRecursive(), cteList, context, _SQLConsultant::assertStandardCte);
    }

    @Override
    protected final MySQLDdlParser createDdlDialect() {
        return MySQLDdlParser.create(this);
    }

    @Override
    protected final CriteriaException supportChildInsert(final _Insert._ChildInsert childStmt, final Visible visible) {

        return null;
    }


    @Override
    protected final void bindLiteralNull(final MappingType type, final DataType dataType, final boolean typeName,
                                         final StringBuilder sqlBuilder) {
        if (!(dataType instanceof MySQLType)) {
            if (!this.unrecognizedTypeAllowed) {
                throw _Exceptions.unrecognizedType(this.dialectDatabase, dataType);
            }
        } else switch ((MySQLType) dataType) {
            case UNKNOWN:
            case NULL:
                throw ExecutorSupport.mapMethodError(type, dataType);
            default:
                // no-op
        }

        sqlBuilder.append(_Constant.SPACE_NULL);
    }

    @Override
    protected final void bindLiteral(final TypeMeta typeMeta, final DataType dataType, final Object value,
                                     final boolean typeName, final StringBuilder sqlBuilder) {

        if (!(dataType instanceof MySQLType)) {
            throw _Exceptions.unrecognizedTypeLiteral(this.dialectDatabase, dataType);
        }

        switch ((MySQLType) dataType) {
            case BOOLEAN:
                _Literals.bindBoolean(typeMeta, dataType, value, sqlBuilder);
                break;
            case SMALLINT_UNSIGNED:
            case INT:
            case MEDIUMINT:
            case MEDIUMINT_UNSIGNED: {
                if (!(value instanceof Integer)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
            }
            break;
            case INT_UNSIGNED:
            case BIGINT: {
                if (!(value instanceof Long)) {
                    throw _Exceptions.beforeBindMethod(dataType, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(value);
            }
            break;
            case DECIMAL:
            case DECIMAL_UNSIGNED:
                _Literals.bindBigDecimal(typeMeta, dataType, value, sqlBuilder);
                break;
            case DATETIME: {
                final String timeText;
                if (value instanceof LocalDateTime) {
                    timeText = _TimeUtils.format((LocalDateTime) value, typeMeta);
                } else if (!this.asOf80) {
                    throw new CriteriaException("prior to MySQL 8.x,don't support time zone offset to DATETIME type.");
                } else if (value instanceof OffsetDateTime) {
                    timeText = _TimeUtils.format((OffsetDateTime) value, typeMeta);
                } else if (value instanceof ZonedDateTime) {
                    timeText = _TimeUtils.format(((ZonedDateTime) value).toOffsetDateTime(), typeMeta);
                } else {
                    throw _Exceptions.outRangeOfSqlType(MySQLType.DATETIME, value);
                }

                if (typeName) {
                    sqlBuilder.append("TIMESTAMP ");
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(timeText)
                        .append(_Constant.QUOTE);
            }
            break;
            case DATE: {
                if (typeName) {
                    sqlBuilder.append("DATE ");
                }
                _Literals.bindLocalDate(typeMeta, dataType, value, sqlBuilder);
            }
            break;
            case TIME: {
                if (typeName) {
                    sqlBuilder.append("TIME ");
                }
                sqlBuilder.append(_Constant.QUOTE);
                if (value instanceof LocalTime) {
                    sqlBuilder.append(_TimeUtils.format((LocalTime) value, typeMeta));
                } else if (value instanceof Duration) {
                    sqlBuilder.append(_TimeUtils.durationToTimeText((Duration) value));
                } else {
                    throw _Exceptions.beforeBindMethod(dataType, typeMeta.mappingType(), value);
                }
                sqlBuilder.append(_Constant.QUOTE);
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
                if (!(value instanceof String)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                MySQLLiterals.mysqlEscapes(this.literalEscapeMode, (String) value, sqlBuilder);
            }
            break;
            case BINARY:
            case VARBINARY:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
            case LONGBLOB: {
                if (!(value instanceof byte[])) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append("0x")
                        .append(_Literals.hexEscapes((byte[]) value));
            }
            break;
            case BIT: {
                if (!(value instanceof Long)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                // https://dev.mysql.com/doc/refman/8.0/en/bit-value-literals.html

                sqlBuilder.append("0b")
                        .append(Long.toBinaryString((Long) value));
            }
            break;
            case DOUBLE: {
                if (!(value instanceof Double || value instanceof Float)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
            }
            break;
            case FLOAT: {
                if (!(value instanceof Float)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
            }
            break;
            case TINYINT: {
                if (!(value instanceof Byte)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
            }
            break;
            case BIGINT_UNSIGNED: {
                if (!(value instanceof BigInteger)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
            }
            break;
            case TINYINT_UNSIGNED:
            case SMALLINT: {
                if (!(value instanceof Short)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
            }
            break;
            case YEAR: {
                if (value instanceof Short) {
                    sqlBuilder.append(value);
                } else if (value instanceof Year) {
                    sqlBuilder.append(((Year) value).getValue());
                } else {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
            }
            break;
            case POINT:
            case LINESTRING:
            case POLYGON:
            case MULTIPOINT:
            case MULTIPOLYGON:
            case MULTILINESTRING:
            case GEOMETRYCOLLECTION:
            case GEOMETRY: {
                if (value instanceof byte[]) {
                    sqlBuilder.append("0x")
                            .append(HexUtils.hexEscapesText(true, (byte[]) value));
                } else if (value instanceof String) {
                    MySQLLiterals.mysqlEscapes(this.literalEscapeMode, (String) value, sqlBuilder);
                } else {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
            }
            break;
            case NULL:
            case UNKNOWN:
                throw ExecutorSupport.mapMethodError(typeMeta.mappingType(), dataType);
            default:
                throw _Exceptions.unexpectedEnum((MySQLType) dataType);
        }

    }

    @Override
    protected final void standardLimitClause(final @Nullable _Expression offset, final @Nullable _Expression rowCount,
                                             _SqlContext context) {

        final StringBuilder sqlBuilder;
        if (offset != null && rowCount != null) {
            sqlBuilder = context.sqlBuilder().append(_Constant.SPACE_LIMIT);
            offset.appendSql(sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            rowCount.appendSql(sqlBuilder, context);
        } else if (rowCount != null) {
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_LIMIT);
            rowCount.appendSql(sqlBuilder, context);
        }

    }

    @Override
    protected final void standardLockClause(final SQLWords lockMode, final _SqlContext context) {
        if (!_Constant.SPACE_FOR_UPDATE.equals(lockMode.spaceRender())) {
            throw _Exceptions.castCriteriaApi();
        }
        context.sqlBuilder().append(_Constant.SPACE_FOR_UPDATE);
    }

    /**
     * @see #update(UpdateStatement, boolean, io.army.session.SessionSpec)
     */
    @Override
    protected final void parseDomainChildUpdate(final _SingleUpdate update, final _UpdateContext ctx) {
        final _MultiUpdateContext context = (_MultiUpdateContext) ctx;


        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) update.table();
        final ParentTableMeta<?> parentTable = childTable.parentMeta();
        final String safeParentTableAlias = context.saTableAliasOf(parentTable);

        // 1. UPDATE clause
        final StringBuilder sqlBuilder = context.sqlBuilder();
        if (sqlBuilder.length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
        sqlBuilder.append(_Constant.UPDATE);

        //2. child join parent
        this.appendChildJoinParent(safeParentTableAlias, sqlBuilder, context.saTableAliasOf(childTable), childTable);

        //3. set clause
        this.multiTableChildSetClause(update, context);

        //4. where clause
        this.dmlWhereClause(update.wherePredicateList(), context);


        //4.1 append discriminator for child
        this.discriminator(childTable, safeParentTableAlias, context);

        //4.2 append condition fields
        context.appendConditionFields();

        //4.3 append visible
        if (parentTable.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(parentTable, safeParentTableAlias, context, false);
        }
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/delete.html">DELETE Statement</a>
     */
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
        this.appendChildJoinParent(safeParentTableAlias, sqlBuilder, safeChildTableAlias, childTable);

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
        return BACKTICK;
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
        // MySQL 8.0 add insert row alias
        return this.dialect.compareWith(MySQLDialect.MySQL80) >= 0;
    }

    @Override
    protected final ChildUpdateMode childUpdateMode() {
        return ChildUpdateMode.MULTI_TABLE;
    }

    @Override
    protected final boolean isSupportSingleUpdateAlias() {
        //MySQL always support single update alias;
        return true;
    }

    @Override
    protected final boolean isSupportSingleDeleteAlias() {
        //as of 8.0 MySQL support single delete alias
        return this.dialect.compareWith(MySQLDialect.MySQL80) >= 0;
    }

    @Override
    protected final boolean isSupportWithClause() {
        //as of 8.0 MySQL support WITH clause
        return this.dialect.compareWith(MySQLDialect.MySQL80) >= 0;
    }

    @Override
    protected final boolean isSupportWithClauseInInsert() {
        // MySQL don't support WITH clause in INSERT statement
        return false;
    }

    @Override
    protected final boolean isSupportWindowClause() {
        //as of 8.0 MySQL support WINDOW clause
        return this.dialect.compareWith(MySQLDialect.MySQL80) >= 0;
    }

    @Override
    protected final boolean isSupportUpdateRow() {
        // MySQL don't support update row
        return false;
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/update.html">single-table UPDATE statement</a>
     */
    @Override
    protected final boolean isSupportJoinableSingleUpdate() {
        // false ,MySQL don't support single-table joinable update
        return false;
    }

    @Override
    protected final boolean isSupportUpdateDerivedField() {
        //MySQL don't support update derive field
        return false;
    }

    @Override
    protected final boolean isSupportReturningClause() {
        //MySQL don't support RETURNING clause
        return false;
    }

    @Override
    protected final boolean isValidateUnionType() {
        // MySQL 8.0 add INTERSECT and EXCEPT
        return this.dialect.compareWith(MySQLDialect.MySQL80) < 0;
    }


    @Override
    protected final void validateUnionType(final _UnionType unionType) {
        switch (unionType) {
            case UNION:
            case UNION_ALL:
            case UNION_DISTINCT:
                break;
            case EXCEPT:
            case EXCEPT_ALL:
            case EXCEPT_DISTINCT:
            case INTERSECT:
            case INTERSECT_ALL:
            case INTERSECT_DISTINCT: {
                String m = String.format("%s don't support %s", this.dialect, unionType.name());
                throw new CriteriaException(m);
            }
            default:
                //no bug,never here
                throw _Exceptions.unexpectedEnum(unionType);
        }
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/identifiers.html"> Schema Object Names</a>
     */
    @Override
    protected final IdentifierMode identifierMode(final String identifier) {
        final int length = identifier.length();
        if (length == 0) {
            return IdentifierMode.ERROR;
        }
        IdentifierMode mode = null;
        char ch;
        outerFor:
        for (int i = 0; i < length; i++) {
            ch = identifier.charAt(i);
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_') {
                continue;
            } else if (ch >= '0' && ch <= '9') {
                if (i == 0) {
                    // Identifiers may begin with a digit but unless quoted may not consist solely of digits.
                    mode = IdentifierMode.QUOTING;
                }
                continue;
            } else if (ch == '$') {
                if (i > 0 || this.asOf80) {
                    continue;
                }
                mode = IdentifierMode.QUOTING;
                continue;
            }

            switch (ch) {
                case BACKTICK: {
                    if (mode != IdentifierMode.ESCAPES) {
                        mode = IdentifierMode.ESCAPES;
                    }
                }
                break;
                case _Constant.NUL_CHAR:
                    mode = IdentifierMode.ERROR;
                    break outerFor;
                default: {
                    if (mode == null && (ch < '\u0080' || Character.isWhitespace(ch))) {
                        mode = IdentifierMode.QUOTING;
                    }
                }
            }// switch


        } // for

        if (mode == null) {
            mode = IdentifierMode.SIMPLE;
        }
        return mode;
    }

    /**
     * @see #identifierMode(String)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/identifiers.html"> Schema Object Names</a>
     */
    @Override
    protected final void escapesIdentifier(final String identifier, final StringBuilder sqlBuilder) {
        simplyEscapeIdentifier(identifier, BACKTICK, sqlBuilder);
    }


    @Override
    protected final boolean isUseObjectNameModeMethod() {
        // false,MySQL use identifierMode() method.
        return false;
    }

    @Override
    protected final IdentifierMode objectNameMode(final DatabaseObject object, final String effectiveName) {
        // no bug,never here
        throw new UnsupportedOperationException();
    }


    @Override
    protected final String qualifiedSchemaName(final ServerMeta meta) {
        final String catalog, schema, qualifiedSchema;
        catalog = meta.catalog();
        schema = meta.schema();
        if ((catalog == null) == (schema == null)) {
            throw _Exceptions.serverMetaError(meta);
        }
        if (schema == null) {
            qualifiedSchema = catalog;
        } else {
            qualifiedSchema = schema;
        }
        if (!_StringUtils.hasText(qualifiedSchema)) {
            throw _Exceptions.serverMetaError(meta);
        }
        return qualifiedSchema;
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
