package io.army.dialect.mysql;

import io.army.criteria.LockMode;
import io.army.criteria.TableField;
import io.army.dialect.*;
import io.army.mapping.MappingType;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParamMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.ServerMeta;
import io.army.modelgen._MetaBridge;
import io.army.sqltype.MySqlType;
import io.army.sqltype.SqlType;
import io.army.stmt.SimpleStmt;
import io.army.tx.Isolation;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

class MySQL extends _AbstractDialect {


    protected static final char IDENTIFIER_QUOTE = '`';


    MySQL(_DialectEnvironment environment, Dialect dialect) {
        super(environment, dialect);
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
    public final StringBuilder safeObjectName(final String objectName, final StringBuilder builder) {
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
    protected final boolean supportTableOnly() {
        // MySQL don't support only before table name.
        return false;
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
    public final boolean singleDeleteHasTableAlias() {
        //always false ,MySQL single delete don't support table alias.
        return false;
    }

    @Override
    public final boolean hasRowKeywords() {
        return false;
    }

    @Override
    public final boolean supportRowLeftItem() {
        //MySQL SET clause left item don't support ROW
        return false;
    }

    @Override
    public boolean supportSavePoint() {
        // always true,MySQL support save point.
        return true;
    }

    @Override
    public final boolean setClauseTableAlias() {
        // MySQL support table alias in set clause.
        return true;
    }

    @Override
    public final boolean setClauseSupportRow() {
        // MySQL SET clause don't support row
        return false;
    }

    @Override
    public final String defaultFuncName() {
        return "DEFAULT";
    }

    @Override
    public final boolean multiTableUpdateChild() {
        //MySQL use multi-table update syntax update/delete child table.
        return true;
    }


    @Override
    protected final boolean identifierCaseSensitivity() {
        //MySQL Identifier Case Sensitivity
        return true;
    }

    @Override
    protected final MySQLDdl createDdlDialect() {
        return MySQLDdl.create(this);
    }

    @Override
    protected final char identifierQuote() {
        return IDENTIFIER_QUOTE;
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
        sqlType = mappingType.map(this.environment.serverMeta());
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
            case LONGTEXT:
                literal = MySQLLiterals.text(sqlType, nonNull);
                break;
            case JSON:
                literal = MySQLLiterals.text(sqlType, this.environment.jsonCodec().encode(nonNull));
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
            case SET:
                literal = MySQLLiterals.setType(sqlType, nonNull);
                break;
            case POINT:
            case LINESTRING:
            case POLYGON:
            case MULTIPOINT:
            case MULTIPOLYGON:
            case MULTILINESTRING:
            case GEOMETRYCOLLECTION:
                throw _Exceptions.outRangeOfSqlType(sqlType, nonNull);
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
    protected final SimpleStmt standardChildUpdate(final _DomainUpdateContext context) {

        final _SetBlock childBlock = context.childBlock();
        assert childBlock != null;

        final StringBuilder sqlBuilder = context.sqlBuilder();

        // 1. UPDATE clause
        sqlBuilder.append(Constant.UPDATE);

        //2. child join parent
        this.appendChildJoinParent(childBlock, context);

        final List<TableField<?>> childConditionFields, parentConditionFields;
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

        final String safeParentTableAlias, safeChildTableAlias;
        safeChildTableAlias = childBlock.safeTableAlias();
        safeParentTableAlias = context.safeTableAlias();

        builder.append(safeChildTableAlias)// child table name
                .append(Constant.SPACE_COMMA_SPACE)
                .append(safeParentTableAlias)// parent table name
                .append(Constant.SPACE_FROM);

        //2. child join parent
        this.appendChildJoinParent(childBlock, context);

        //3. where clause
        this.dmlWhereClause(context);


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
            case READ:
                context.sqlBuilder()
                        .append(Constant.SPACE_LOCK_IN_SHARE_MODE);
                break;
            case WRITE:
                context.sqlBuilder()
                        .append(Constant.SPACE_FOR_UPDATE);
                break;
            default:
                throw _Exceptions.unexpectedEnum(lockMode);
        }
    }

    @Override
    protected final Set<String> createKeyWordSet(final ServerMeta meta) {
        final Set<String> keyWordSet;
        switch (meta.major()) {
            case 5:
                keyWordSet = MySQLDialectUtils.create57KeywordsSet();
                break;
            case 8:
                keyWordSet = MySQLDialectUtils.create80KeywordsSet();
                break;
            default:
                throw new IllegalArgumentException(String.format("unsupported MySQL version[%s]", meta.version()));
        }
        return keyWordSet;
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


}