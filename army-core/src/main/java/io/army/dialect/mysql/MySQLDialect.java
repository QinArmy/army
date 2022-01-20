package io.army.dialect.mysql;

import io.army.DialectMode;
import io.army.criteria.GenericField;
import io.army.criteria.LockMode;
import io.army.dialect.*;
import io.army.mapping.MappingType;
import io.army.mapping.mysql.MySqlSetType;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParamMeta;
import io.army.meta.ParentTableMeta;
import io.army.modelgen._MetaBridge;
import io.army.sqltype.MySqlType;
import io.army.sqltype.SqlType;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.army.util._Exceptions;

import java.util.List;

abstract class MySQLDialect extends _AbstractDialect {

    protected static final String NO_BACKSLASH_ESCAPES = "NO_BACKSLASH_ESCAPES";

    MySQLDialect(DialectEnvironment environment) {
        super(environment);
    }


    @Override
    public final String safeTableName(String tableName) {
        return this.quoteIfNeed(tableName);
    }

    @Override
    public final String safeColumnName(String columnName) {
        return this.quoteIfNeed(columnName);
    }

    @Override
    protected final boolean supportTableOnly() {
        // MySQL don't support only before table name.
        return false;
    }

    @Override
    public void clearForDDL() {

    }


    @Override
    public final boolean supportInsertReturning() {
        // MySQL don't support insert returning.
        return false;
    }

    @Override
    public final boolean supportZone() {
        // MySQL don't support zone.
        return false;
    }

    @Override
    public final boolean supportOnlyDefault() {
        // MySQL support DEFAULT() function.
        return true;
    }


    @Override
    public final boolean tableAliasAfterAs() {
        // MySQL support table alias after 'AS' key word.
        return true;
    }

    @Override
    public boolean singleDeleteHasTableAlias() {
        return false;
    }

    @Override
    public boolean hasRowKeywords() {
        return false;
    }


    @Override
    public String showSQL(Stmt stmt) {
        return null;
    }

    @Override
    public boolean supportSavePoint() {
        return false;
    }

    @Override
    public final boolean setClauseTableAlias() {
        // MySQL support table alias in set clause.
        return true;
    }

    @Override
    public DialectMode mode() {
        return null;
    }

    @Override
    public String defaultFuncName() {
        return null;
    }

    @Override
    public final boolean multiTableUpdateChild() {
        //MySQL use multi-table update syntax update/delete child table.
        return true;
    }


    @Override
    protected final String quoteIdentifier(String identifier) {
        return '`' + identifier + '`';
    }

    @Override
    public final String literal(ParamMeta paramMeta, Object nonNull) {
        final SqlType sqlType;
        final MappingType mappingType;
        if (paramMeta instanceof MappingType) {
            mappingType = (MappingType) paramMeta;
        } else {
            mappingType = paramMeta.mappingType();
        }
        sqlType = mappingType.sqlType(this.environment.serverMeta());
        final String literal;
        switch ((MySqlType) sqlType) {
            case INT:
                literal = MySQLLiterals.integer(sqlType, nonNull);
                break;
            case BIGINT:
                literal = MySQLLiterals.bigInt(sqlType, nonNull);
                break;
            case DECIMAL:
                literal = MySQLLiterals.decimal(sqlType, nonNull);
                break;
            case BOOLEAN:
                literal = MySQLLiterals.booleanLiteral(sqlType, nonNull);
                break;
            case DATETIME:
                literal = MySQLLiterals.datetime(sqlType, paramMeta, nonNull);
                break;
            case DATE:
                literal = MySQLLiterals.date(sqlType, nonNull);
                break;
            case TIME:
                literal = MySQLLiterals.time(sqlType, paramMeta, nonNull);
                break;
            case YEAR:
                literal = MySQLLiterals.year(sqlType, nonNull);
                break;
            case CHAR:
            case VARCHAR:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
            case LONGTEXT: {
                final boolean hexEscapes;
                hexEscapes = this.environment.serverMeta().sessionVar(NO_BACKSLASH_ESCAPES) != null;
                literal = MySQLLiterals.text(sqlType, hexEscapes, nonNull);
            }
            break;
            case JSON: {
                final String json;
                json = this.environment.jsonCodec().encode(nonNull);
                final boolean hexEscapes;
                hexEscapes = this.environment.serverMeta().sessionVar(NO_BACKSLASH_ESCAPES) != null;
                literal = MySQLLiterals.text(sqlType, hexEscapes, json);
            }
            break;
            case BINARY:
            case VARBINARY:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
            case LONGBLOB:
                literal = MySQLLiterals.binary(sqlType, nonNull);
                break;
            case ENUM:
                literal = MySQLLiterals.enumLiteral(sqlType, nonNull);
                break;
            case BIT:
                literal = MySQLLiterals.bit(sqlType, nonNull);
                break;
            case DOUBLE:
                literal = MySQLLiterals.doubleLiteral(sqlType, nonNull);
                break;
            case FLOAT:
                literal = MySQLLiterals.floatLiteral(sqlType, nonNull);
                break;
            case TINYINT:
                literal = MySQLLiterals.tinyInt(sqlType, nonNull);
                break;
            case SMALLINT:
                literal = MySQLLiterals.smallInt(sqlType, nonNull);
                break;
            case MEDIUMINT:
                literal = MySQLLiterals.mediumInt(sqlType, nonNull);
                break;
            case BIGINT_UNSIGNED:
                literal = MySQLLiterals.unsignedLBigInt(sqlType, nonNull);
                break;
            case DECIMAL_UNSIGNED:
                literal = MySQLLiterals.unsignedDecimal(sqlType, nonNull);
                break;
            case INT_UNSIGNED:
                literal = MySQLLiterals.unsignedInt(sqlType, nonNull);
                break;
            case MEDIUMINT_UNSIGNED:
                literal = MySQLLiterals.unsignedMediumInt(sqlType, nonNull);
                break;
            case SMALLINT_UNSIGNED:
                literal = MySQLLiterals.unsignedSmallInt(sqlType, nonNull);
                break;
            case TINYINT_UNSIGNED:
                literal = MySQLLiterals.unsignedTinyInt(sqlType, nonNull);
                break;
            case SET: {
                try {
                    literal = ((MySqlSetType) mappingType).literal(nonNull);
                } catch (RuntimeException e) {
                    throw _Exceptions.errorLiteralType(sqlType, nonNull);
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
                throw _Exceptions.errorLiteralType(sqlType, nonNull);
            default:
                throw _Exceptions.unexpectedEnum((MySqlType) sqlType);
        }
        return literal;
    }


    /**
     * <p>
     * MySQL {@link _Dialect#multiTableUpdateChild()} always return true
     * ,so this method always use multi-table syntax update child table.
     * </p>
     *
     * @see _Dialect#multiTableUpdateChild()
     */
    @Override
    protected final SimpleStmt standardChildUpdate(final _SingleUpdateContext context) {
        assert context.multiTableUpdateChild();

        final _SetBlock childBlock = context.childBlock();
        assert childBlock != null;

        final StringBuilder sqlBuilder = context.sqlBuilder();

        // 1. UPDATE clause
        sqlBuilder.append(Constant.UPDATE_SPACE);

        //2. child join parent
        this.appendChildJoinParent(childBlock, context);

        final List<GenericField<?, ?>> childConditionFields, parentConditionFields;
        //3. set clause
        parentConditionFields = this.setClause(true, context, context);
        childConditionFields = this.setClause(false, childBlock, context);

        //4. where clause
        this.dmlWhereClause(context);

        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) childBlock.table();
        final ParentTableMeta<?> parentTable = (ParentTableMeta<?>) context.table();
        final String safeParentTableAlias = context.safeTableAlias();
        final String safeChildTableAlias = childBlock.safeTableAlias();

        //4.1 append discriminator for child
        this.discriminator(childTable, safeParentTableAlias, context);

        //4.2 append visible
        if (parentTable.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(parentTable, safeParentTableAlias, context);
        }
        //4.3 append child condition update fields
        if (childConditionFields.size() > 0) {
            this.conditionUpdate(safeChildTableAlias, childConditionFields, context);
        }
        //4.4 append parent condition update fields
        if (parentConditionFields.size() > 0) {
            this.conditionUpdate(safeParentTableAlias, parentConditionFields, context);
        }
        return context.build();
    }

    @Override
    protected final SimpleStmt standardChildDelete(final _SingleDeleteContext context) {
        assert context.multiTableUpdateChild();
        final _Block childBlock = context.childBlock();
        assert childBlock != null;

        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) childBlock.table();
        final ParentTableMeta<?> parentTable = (ParentTableMeta<?>) context.table();

        // 1. delete clause
        final StringBuilder builder = context.sqlBuilder()
                .append(Constant.DELETE_SPACE);

        builder.append(this.quoteIfNeed(childTable.tableName()))// child table name
                .append(Constant.SPACE_COMMA)
                .append(Constant.SPACE)
                .append(this.quoteIfNeed(parentTable.tableName()))// parent table name
                .append(Constant.SPACE_FROM);

        //2. child join parent
        this.appendChildJoinParent(childBlock, context);

        //3. where clause
        this.dmlWhereClause(context);

        final String safeParentTableAlias = context.safeTableAlias();

        //3.2 append discriminator for child
        this.discriminator(childTable, safeParentTableAlias, context);

        //3.3 append visible
        if (parentTable.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(parentTable, safeParentTableAlias, context);
        }
        return context.build();
    }

    @Override
    protected final void standardLockClause(final LockMode lockMode, final _SqlContext context) {
        switch (lockMode) {
            case PESSIMISTIC_READ:
                context.sqlBuilder()
                        .append(Constant.SPACE_LOCK_IN_SHARE_MODE);
                break;
            case PESSIMISTIC_WRITE:
                context.sqlBuilder()
                        .append(Constant.SPACE_FOR_UPDATE);
                break;
            default:
                throw _Exceptions.unexpectedEnum(lockMode);
        }
    }

    /*################################## blow private method ##################################*/

//    private static void re(){
//        switch ((MySqlType) sqlType) {
//            case INT:
//            case BOOLEAN:
//            case DATE:
//            case BLOB:
//            case TINYINT:
//            case SMALLINT:
//            case BIGINT:
//            case DECIMAL:
//            case FLOAT:
//            case DOUBLE:
//            case CHAR:
//            case VARCHAR:
//            case VARBINARY:
//            case BINARY:
//            case TIMESTAMP:
//            case BIT:
//            case TIME:
//            case ENUM:
//            case LONGTEXT:
//            case DATETIME:
//            case YEAR:
//            case JSON:
//            case MEDIUMTEXT:
//            case TEXT:
//            case TINYTEXT:
//            case SET:
//            case DECIMAL_UNSIGNED:
//
//            case INT_UNSIGNED:
//            case MEDIUMINT_UNSIGNED:
//            case MEDIUMINT:
//            case SMALLINT_UNSIGNED:
//            case TINYINT_UNSIGNED:
//            case POINT:
//            case POLYGON:
//            case LONGBLOB:
//            case TINYBLOB:
//            case LINESTRING:
//            case MEDIUMBLOB:
//            case MULTIPOINT:
//            case MULTIPOLYGON:
//            case BIGINT_UNSIGNED:
//            case MULTILINESTRING:
//            case GEOMETRYCOLLECTION:
//                break;
//            default:
//                throw _Exceptions.unexpectedEnum((MySqlType) sqlType);
//        }
//    }


}
