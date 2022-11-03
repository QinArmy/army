package io.army.dialect;

import io.army.ArmyException;
import io.army.annotation.GeneratorType;
import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl._JoinType;
import io.army.criteria.impl._SQLConsultant;
import io.army.criteria.impl.inner.*;
import io.army.criteria.standard.StandardInsert;
import io.army.criteria.standard.StandardQuery;
import io.army.criteria.standard.StandardStatement;
import io.army.criteria.standard.StandardUpdate;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.schema._FieldResult;
import io.army.schema._SchemaResult;
import io.army.schema._TableResult;
import io.army.stmt.*;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;


/**
 * <p>
 * This class is base class of all implementation of {@link DialectParser}.
 * </p>
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @since 1.0
 */
public abstract class _AbstractDialectParser implements ArmyParser {


    public final DialectEnv dialectEnv;

    protected final MappingEnv mappingEnv;

    protected final ServerMeta serverMeta;

    /**
     * a unmodified set
     */
    protected final Set<String> keyWordSet;

    protected final char identifierQuote;

    protected final boolean identifierCaseSensitivity;

    protected final Dialect dialect;
    private final FieldValueGenerator generator;

    protected _AbstractDialectParser(final DialectEnv dialectEnv, final Dialect dialect) {
        assert dialect instanceof Enum;

        this.dialectEnv = dialectEnv;
        this.mappingEnv = dialectEnv.mappingEnv();
        this.serverMeta = this.mappingEnv.serverMeta();
        this.dialect = dialect;

        assert dialect.database() == this.serverMeta.database();

        this.identifierQuote = identifierQuote();
        this.identifierCaseSensitivity = this.isIdentifierCaseSensitivity();

        this.keyWordSet = Collections.unmodifiableSet(this.createKeyWordSet(this.serverMeta));
        if (dialectEnv instanceof _MockDialects) {
            this.generator = FieldValuesGenerators.mock(this.mappingEnv::zoneId);
        } else {
            this.generator = FieldValuesGenerators.create(this.mappingEnv::zoneId, dialectEnv.fieldGeneratorMap());
        }
    }

    /*################################## blow DML batchInsert method ##################################*/


    /**
     * {@inheritDoc}
     */
    @Override
    public final Stmt insert(final Insert insert, final Visible visible) {
        insert.prepared();
        if (insert instanceof StandardInsert) {
            //validate implementation class
            _SQLConsultant.assertStandardInsert(insert);
        } else {
            //validate implementation class
            assertDialectInsert((_Insert) insert);
        }
        return parseInsert(null, (_Insert) insert, visible);
    }


    /*################################## blow update method ##################################*/

    @Override
    public final Stmt update(final Update update, final Visible visible) {
        update.prepared();
        final Stmt stmt;
        if (update instanceof StandardUpdate) {
            // assert implementation is standard implementation.
            _SQLConsultant.assertStandardUpdate(update);
            final _SingleUpdate s = (_SingleUpdate) update;
            if (s.table() instanceof ChildTableMeta) {
                stmt = this.standardChildUpdate(null, s, visible);
            } else {
                stmt = this.standardSingleTableUpdate(null, s, visible);
            }
            assert !(update instanceof _BatchDml) || stmt instanceof BatchStmt;
        } else if (update instanceof _SingleUpdate) {
            // assert implementation class is legal
            assertDialectUpdate(update);
            final _SingleUpdate singleUpdate = (_SingleUpdate) update;
            final _SingleUpdateContext context;
            if (singleUpdate.table() instanceof ChildTableMeta) {
                context = SingleUpdateContext.create(null, singleUpdate, this, visible);
            } else {
                context = OnlyParentUpdateContext.create(null, singleUpdate, this, visible);
            }
            dialectSingleUpdate((_SingleUpdate) update, context);
            if (update instanceof _BatchDml) {
                stmt = context.build(((_BatchDml) update).paramList());
            } else {
                stmt = context.build();
            }
        } else if (update instanceof _MultiUpdate) {
            assertDialectUpdate(update);
            final _MultiUpdateContext context;
            context = MultiUpdateContext.create(null, (_MultiUpdate) update, this, visible);
            dialectMultiUpdate((_MultiUpdate) update, context);
            if (update instanceof _BatchDml) {
                stmt = context.build(((_BatchDml) update).paramList());
            } else {
                stmt = context.build();
            }
        } else {
            throw _Exceptions.unknownStatement(update, this.dialect);
        }

        return stmt;
    }


    @Override
    public final Stmt delete(final Delete delete, final Visible visible) {
        delete.prepared();
        final Stmt stmt;
        if (delete instanceof StandardStatement) {
            _SQLConsultant.assertStandardDelete(delete);
            final _SingleDelete s = (_SingleDelete) delete;
            if (s.table() instanceof ChildTableMeta) {
                stmt = this.standardChildDelete(s, visible);
            } else {
                stmt = this.handleStandardDelete(s, visible);
            }
            if (delete instanceof _BatchDml && !(stmt instanceof BatchStmt)) {
                //no bug,never here
                throw new IllegalStateException("create stmt error");
            }
        } else if (delete instanceof _SingleDelete) {
            this.assertDialectDelete(delete);
            final _SingleDeleteContext context;
            context = SingleDeleteContext.create((_SingleDelete) delete, this, visible);
            this.dialectSingleDelete((_SingleDelete) delete, context);
            if (delete instanceof _BatchDml) {
                stmt = context.build(((_BatchDml) delete).paramList());
            } else {
                stmt = context.build();
            }
        } else if (delete instanceof _MultiDelete) {
            this.assertDialectDelete(delete);
            final _MultiDeleteContext context;
            context = MultiDeleteContext.create((_MultiDelete) delete, this, visible);
            this.dialectMultiDelete((_MultiDelete) delete, context);
            if (delete instanceof _BatchDml) {
                stmt = context.build(((_BatchDml) delete).paramList());
            } else {
                stmt = context.build();
            }
        } else {
            throw _Exceptions.unknownStatement(delete, this.dialect);
        }
        return stmt;
    }

    @Override
    public final SimpleStmt select(final Select select, final Visible visible) {
        //1. assert prepared
        select.prepared();

        //2. assert select implementation class.
        if (select instanceof StandardQuery) {
            _SQLConsultant.assertStandardQuery(select);
        } else {
            this.assertDialectRowSet(select);
        }

        //3. parse select
        final StmtContext context;
        if (select instanceof _UnionRowSet0) {
            context = UnionSelectContext.create(select, this, visible);
            this.standardParensRowSet((_UnionRowSet0) select, context);
        } else {
            context = SimpleSelectContext.create(select, this, visible);
            if (select instanceof StandardQuery) {
                this.standardSimpleQuery((_StandardQuery) select, (_SimpleQueryContext) context);
            } else {
                this.dialectSimpleQuery((_Query) select, (_SimpleQueryContext) context);
            }
        }
        return context.build();
    }

    @Override
    public final void rowSet(final RowSet rowSet, final _SqlContext original) {
        //3. parse RowSet
        if (rowSet instanceof Query) {
            this.parseQuery((Select) rowSet, original);
        } else if (rowSet instanceof RowSet.DqlValues) {
            this.parseValues((RowSet.DqlValues) rowSet, original);
        } else {
            throw _Exceptions.unknownStatement(rowSet, this.dialect);
        }

    }


    @Override
    public final List<String> schemaDdl(final _SchemaResult schemaResult) {
        final DdlDialect ddlDialect;
        ddlDialect = createDdlDialect();

        final List<String> ddlList = new ArrayList<>();
        final List<TableMeta<?>> dropTableList = schemaResult.dropTableList();
        if (dropTableList.size() > 0) {
            ddlDialect.dropTable(dropTableList, ddlList);
        }
        for (TableMeta<?> table : schemaResult.newTableList()) {
            ddlDialect.createTable(table, ddlList);
        }

        List<FieldMeta<?>> newFieldList;
        List<_FieldResult> fieldResultList;
        List<String> indexList;
        for (_TableResult tableResult : schemaResult.changeTableList()) {
            TableMeta<?> table = tableResult.table();
            if (tableResult.comment()) {
                ddlDialect.modifyTableComment(table, ddlList);
            }
            newFieldList = tableResult.newFieldList();
            if (newFieldList.size() > 0) {
                ddlDialect.addColumn(newFieldList, ddlList);
            }
            fieldResultList = tableResult.changeFieldList();
            if (fieldResultList.size() > 0) {
                ddlDialect.modifyColumn(fieldResultList, ddlList);
            }
            indexList = tableResult.newIndexList();
            if (indexList.size() > 0) {
                ddlDialect.createIndex(table, indexList, ddlList);
            }
            indexList = tableResult.changeIndexList();
            if (indexList.size() > 0) {
                ddlDialect.dropIndex(table, indexList, ddlList);
                ddlDialect.createIndex(table, indexList, ddlList);
            }
        }

        final List<String> errorList;
        errorList = ddlDialect.errorMsgList();
        if (errorList.size() > 0) {
            final StringBuilder builder = new StringBuilder(errorList.size() * 10)
                    .append("create ddl occur error:");
            for (String msg : errorList) {
                builder.append('\n')
                        .append(msg);
            }
            throw new ArmyException(builder.toString());
        }
        return Collections.unmodifiableList(ddlList);
    }

    @Override
    public final String identifier(final String identifier) {
        final String safeIdentifier;
        if (!this.identifierCaseSensitivity || this.keyWordSet.contains(identifier)) {
            safeIdentifier = this.identifierQuote + identifier + this.identifierQuote;
        } else {
            safeIdentifier = identifier;
        }
        return safeIdentifier;
    }

    @Override
    public final StringBuilder identifier(final String identifier, final StringBuilder builder) {
        if (!this.identifierCaseSensitivity || this.keyWordSet.contains(identifier)) {
            builder.append(this.identifierQuote)
                    .append(identifier)
                    .append(this.identifierQuote);
        } else {
            builder.append(identifier);
        }
        return builder;
    }

    @Override
    public final Dialect dialectMode() {
        return this.dialect;
    }


    @Override
    public final boolean isMockEnv() {
        return this.dialectEnv instanceof _MockDialects;
    }

    @Override
    public final FieldValueGenerator getGenerator() {
        return this.generator;
    }

    @Override
    public final MappingEnv mappingEnv() {
        return this.mappingEnv;
    }

    @Override
    public final void subQueryOfQueryInsert(final _QueryInsertContext outerContext, final SubQuery subQuery) {
        if (subQuery instanceof _LateralSubQuery) {
            throw _Exceptions.queryInsertDontSupportLateralSubQuery();
        }
        //here no outer paren
        if (subQuery instanceof _UnionRowSet0) {
            final _UnionQueryContext context;
            context = UnionSubQueryContext.create(outerContext);
            this.standardParensRowSet((_UnionRowSet0) subQuery, context);
        } else {
            outerContext.sqlBuilder().append(_Constant.SPACE);
            final SimpleSubQueryContext context;
            context = SimpleSubQueryContext.create(subQuery, outerContext);//create new simple sub query context
            if (subQuery instanceof StandardQuery) {
                this.standardSimpleQuery((_StandardQuery) subQuery, context);
            } else {
                this.dialectSimpleQuery((_Query) subQuery, context);
            }
        }

    }

    @Override
    public final String printStmt(final Stmt stmt, final boolean beautify) {
        return beautify ? stmt.printSql(this::beautifySql) : stmt.printSql(_AbstractDialectParser::nonBeautifySql);
    }


    @Override
    public final String toString() {
        return String.format("[%s dialect:%s,hash:%s]"
                , this.getClass().getName(), this.dialect.name(), System.identityHashCode(this));
    }

    /*################################## blow protected template method ##################################*/

    /*################################## blow multiInsert template method ##################################*/


    protected abstract Set<String> createKeyWordSet(ServerMeta meta);


    protected abstract boolean supportTableOnly();

    protected abstract char identifierQuote();

    protected abstract boolean isIdentifierCaseSensitivity();

    protected abstract DdlDialect createDdlDialect();


    /*################################## blow update template method ##################################*/

    protected void assertDialectInsert(_Insert insert) {
        String m = String.format("%s don't support this dialect insert[%s]", this.dialect, insert.getClass().getName());
        throw new CriteriaException(m);
    }

    /**
     * @see #update(Update, Visible)
     */
    protected void assertDialectUpdate(Update update) {
        String m = String.format("%s don't support this dialect update[%s]", this.dialect, update.getClass().getName());
        throw new CriteriaException(m);
    }

    protected String beautifySql(String sql) {
        return sql;
    }

    /*################################## blow delete template method ##################################*/

    protected void assertDialectDelete(Delete delete) {
        String m = String.format("%s don't support this dialect delete[%s]", this, delete.getClass().getName());
        throw new CriteriaException(m);
    }

    protected void assertDialectRowSet(RowSet query) {
        String m = String.format("%s don't support this dialect select[%s]", this, query.getClass().getName());
        throw new CriteriaException(m);
    }




    /*################################## blow protected method ##################################*/

    /*################################## blow private batchInsert method ##################################*/







    /*################################## blow update private method ##################################*/

    protected void valueSyntaxInsert(_ValueInsertContext context, _Insert._ValuesSyntaxInsert insert) {
        throw new UnsupportedOperationException();
    }

    protected void assignmentInsert(_AssignmentInsertContext context, _Insert._AssignmentInsert insert) {
        throw new UnsupportedOperationException();
    }

    protected void queryInsert(_QueryInsertContext context, _Insert._QueryInsert insert) {
        throw new UnsupportedOperationException();
    }


    protected void dialectSingleUpdate(_SingleUpdate update, _SingleUpdateContext context) {
        throw new UnsupportedOperationException();
    }

    protected void dialectMultiUpdate(_MultiUpdate update, _MultiUpdateContext context) {
        throw new UnsupportedOperationException();
    }

    protected void dialectMultiDelete(final _MultiDelete delete, _MultiDeleteContext context) {
        throw new UnsupportedOperationException();
    }

    protected void dialectSingleDelete(_SingleDelete delete, _SingleDeleteContext context) {
        throw new UnsupportedOperationException();
    }

    protected void dialectSimpleQuery(_Query query, _SimpleQueryContext context) {
        throw new UnsupportedOperationException();
    }


    /**
     * @see #update(Update, Visible)
     */
    protected Stmt standardChildUpdate(@Nullable _SqlContext outerContext, _SingleUpdate update, Visible visible) {
        throw new UnsupportedOperationException();
    }

    protected Stmt standardChildDelete(_SingleDelete delete, Visible visible) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see #parseSelectStmt(Select, _SqlContext)
     * @see #parseSubQuery(SubQuery, _SqlContext)
     */
    protected void dialectParensQuery(_ParensRowSet query, _ParenRowSetContext context) {
        String m = String.format("%s don't support %s", this.dialect, query.getClass().getName());
        throw new CriteriaException(m);
    }


    protected final _SingleUpdateContext createSingleUpdateContext(final @Nullable _SqlContext outerContext
            , final _SingleUpdate stmt, final Visible visible) {
        return SingleUpdateContext.create(outerContext, stmt, this, visible);
    }

    protected final _MultiUpdateContext createMultiUpdateContext(final @Nullable _SqlContext outerContext
            , final _SingleUpdate stmt, final Visible visible) {
        return MultiUpdateContext.forChild(outerContext, stmt, this, visible);
    }

    protected final _MultiDeleteContext createMultiDeleteContext(final _SingleDelete stmt, final Visible visible) {
        return MultiDeleteContext.forChild(stmt, this, visible);
    }

    protected final _OtherDmlContext createOtherDmlContext(final Predicate<FieldMeta<?>> predicate
            , final Visible visible) {
        return OtherDmlContext.create(this, predicate, visible);
    }

    /**
     * @param insert possibly be below :
     *               <ul>
     *                  <li>{@link Insert}</li>
     *                  <li>{@link ReplaceInsert}</li>
     *                  <li>{@link MergeInsert}</li>
     *               </ul>
     * @see #insert(Insert, Visible)
     */
    protected final Stmt parseInsert(final @Nullable _SqlContext outerContext, final _Insert insert
            , final Visible visible) {
        final Stmt stmt;
        if (insert instanceof _Insert._DomainInsert) {
            stmt = handleDomainInsert(outerContext, (_Insert._DomainInsert) insert, visible);
        } else if (insert instanceof _Insert._ValuesInsert) {
            stmt = handleValueInsert(outerContext, (_Insert._ValuesInsert) insert, visible);
        } else if (insert instanceof _Insert._AssignmentInsert) {
            stmt = handleAssignmentInsert(outerContext, (_Insert._AssignmentInsert) insert, visible);
        } else if (insert instanceof _Insert._QueryInsert) {
            stmt = handleQueryInsert(outerContext, (_Insert._QueryInsert) insert, visible);
        } else {
            throw _Exceptions.unknownStatement((Statement) insert, this.dialect);
        }
        return stmt;
    }

    protected final void appendInsertConflictSetClause(final _InsertContext context, final String conflictWords
            , final List<_ItemPair> itemPairList) {
        final int pairSize;
        pairSize = itemPairList.size();
        if (pairSize == 0) {
            return;
        }
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder().append(conflictWords);

        final _SetClauseContext setClauseContext = (_SetClauseContext) context;
        for (int i = 0; i < pairSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            itemPairList.get(i).appendItemPair(setClauseContext);
        }

        final TableMeta<?> insertTable;
        insertTable = context.insertTable();
        if (insertTable instanceof SingleTableMeta) {
            this.appendUpdateTimeAndVersion((SingleTableMeta<?>) insertTable, null, context);
        }
    }

    protected void dialectSimpleValues(_ValuesContext context, _Values values) {
        throw new UnsupportedOperationException();
    }

    protected final SimpleStmt parseValues(final Values values, final Visible visible) {
        final StmtContext context;
        if (values instanceof _UnionRowSet0) {
            context = UnionValuesContext.create((_UnionRowSet0) values, this, visible);
            this.standardParensRowSet((_UnionRowSet0) values, context);
        } else {
            context = ValuesContext.create((_Values) values, this, visible);
            this.dialectSimpleValues((_ValuesContext) context, (_Values) values);
        }
        return context.build();
    }


    protected final void valuesClauseOfValues(final _ValuesContext context, @Nullable String rowKeyword
            , final List<List<_Expression>> rowList) {

        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();
        final int rowSize = rowList.size();
        assert rowSize > 0;

        List<_Expression> columnList;
        for (int rowIndex = 0, columnSize; rowIndex < rowSize; rowIndex++) {
            if (rowIndex > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }

            if (rowKeyword == null) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            } else {
                sqlBuilder.append(rowKeyword)
                        .append(_Constant.LEFT_PAREN);
            }

            columnList = rowList.get(rowIndex);
            columnSize = columnList.size();
            assert columnSize > 0;
            for (int columnIndex = 0; columnIndex < columnSize; columnIndex++) {
                if (columnIndex > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                columnList.get(columnIndex).appendSql(context);
            }

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }

    }


    /**
     * @see #parseQuery(Query, _SqlContext)
     */
    protected final void standardParensRowSet(final _ParensRowSet query, final _ParenRowSetContext context) {

        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder().append(_Constant.SPACE_LEFT_PAREN);
        this.rowSet(query.innerRowSet(), context);

        final List<? extends SortItem> orderByList;
        if ((orderByList = query.orderByList()).size() > 0) {
            this.orderByClause(orderByList, context);
        }

        this.standardLimitClause(query.offset(), query.rowCount(), context);
        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

    }

    protected void standardLockClause(SQLWords lockMode, _SqlContext context) {
        throw new UnsupportedOperationException();
    }


    protected final void onClause(final List<_Predicate> predicateList, final _SqlContext context) {
        final int size = predicateList.size();
        if (size == 0) {
            throw new CriteriaException("ON clause must not empty");
        }
        final StringBuilder sqlBuilder = context.sqlBuilder()
                .append(_Constant.SPACE_ON);
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_AND);
            }
            predicateList.get(i).appendSql(context);
        }

    }


    protected final void withSubQuery(final boolean recursive, final List<_Cte> cteList
            , final _SqlContext context, final Consumer<_Cte> assetConsumer) {
        final int cteSize = cteList.size();
        if (cteSize == 0) {
            return;
        }
        final StringBuilder sqlBuilder = context.sqlBuilder();
        if (sqlBuilder.length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
        sqlBuilder.append(_Constant.WITH);
        if (recursive) {
            sqlBuilder.append(_Constant.SPACE_RECURSIVE);
        }
        _Cte cte;
        List<String> columnAliasList;
        SubQuery subQuery;
        for (int i = 0; i < cteSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            cte = cteList.get(i);
            assetConsumer.accept(cte);
            columnAliasList = cte.columnNameList();
            subQuery = (SubQuery) cte.subStatement();

            sqlBuilder.append(_Constant.SPACE);
            this.identifier(cte.name(), sqlBuilder);// cte name

            if (columnAliasList.size() > 0) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
                int aliasCount = 0;
                for (String columnAlias : columnAliasList) {
                    if (aliasCount > 0) {
                        sqlBuilder.append(_Constant.SPACE_COMMA);
                    }
                    this.identifier(columnAlias, sqlBuilder);
                    aliasCount++;
                }
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }
            sqlBuilder.append(_Constant.SPACE_AS);
            this.parseQuery(subQuery, context);

        }

    }


    /**
     * @see #rowSet(RowSet, _SqlContext)
     * @see #withSubQuery(boolean, List, _SqlContext, Consumer)
     */
    protected final void parseQuery(final Query query, final _SqlContext original) {
        query.prepared();
        if (query instanceof _Query) {
            final _SimpleQueryContext context;
            if (query instanceof Select) {
                context = SimpleSelectContext.create(original, (Select) query);
            } else {
                context = SimpleSubQueryContext.create(original, (SubQuery) query);
            }
            if (query instanceof StandardQuery) {
                this.standardSimpleQuery((_StandardQuery) query, context);
            } else {
                this.assertDialectRowSet(query);
                this.dialectSimpleQuery((_Query) query, context);
            }
        } else if (query instanceof _UnionRowSet) {
            final _UnionRowSet unionRowSet = (_UnionRowSet) query;
            this.parseQuery((Query) unionRowSet.leftRowSet(), original);
            original.sqlBuilder().append(unionRowSet.unionType().render());
            this.rowSet(unionRowSet.rightRowSet(), original);
        } else {
            assert query instanceof _ParensRowSet;
            final _ParenRowSetContext context;
            if (original instanceof _ParenRowSetContext) {
                context = (_ParenRowSetContext) original;
            } else {
                context = ParenRowSetContext.create(original);
            }
            if (query instanceof StandardQuery) {
                this.standardParensRowSet((_ParensRowSet) query, context);
            } else {
                this.assertDialectRowSet(query);
                this.dialectParensQuery((_ParensRowSet) query, context);
            }
        }

    }


    protected final void singleTableSetClause(final _SingleUpdate stmt, final _SingleUpdateContext context) {
        assert !(stmt instanceof _DomainUpdate) || ((_DomainUpdate) stmt).childItemPairList().size() == 0;

        final List<_ItemPair> itemPairList = stmt.itemPairList();
        final int itemPairSize = itemPairList.size();
        if (itemPairSize == 0) {
            throw _Exceptions.setClauseNotExists();
        }
        final StringBuilder sqlBuilder = context.sqlBuilder()
                .append(_Constant.SPACE_SET);
        for (int i = 0; i < itemPairSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            itemPairList.get(i).appendItemPair(context);
        }
        final TableMeta<?> table = stmt.table();
        final SingleTableMeta<?> singleTable;
        if (table instanceof ChildTableMeta) {
            singleTable = ((ChildTableMeta<?>) table).parentMeta();
        } else {
            singleTable = (SingleTableMeta<?>) table;
        }
        this.appendUpdateTimeAndVersion(singleTable, context.safeTableAlias(), context);
    }

    protected final void multiTableChildSetClause(final _SingleUpdate stmt, final _MultiUpdateContext context) {
        final List<_ItemPair> itemPairList, childItemPairList;
        itemPairList = stmt.itemPairList();
        if (stmt instanceof _DomainUpdate) {
            childItemPairList = ((_DomainUpdate) stmt).childItemPairList();
        } else {
            childItemPairList = Collections.emptyList();
        }

        final int itemSize, childSize;
        itemSize = itemPairList.size();
        childSize = childItemPairList.size();

        if (itemSize == 0 && childSize == 0) {
            throw _Exceptions.setClauseNotExists();
        }
        final StringBuilder sqlBuilder = context.sqlBuilder();
        sqlBuilder.append(_Constant.SPACE_SET);
        for (int i = 0; i < itemSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            itemPairList.get(i).appendItemPair(context);
        }

        for (int i = 0; i < childSize; i++) {
            if (i > 0 || itemSize > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            childItemPairList.get(i).appendItemPair(context);
        }

        final ParentTableMeta<?> parent = ((ChildTableMeta<?>) stmt.table()).parentMeta();
        this.appendUpdateTimeAndVersion(parent, context.saTableAliasOf(parent), context);
    }

    protected final void multiTableSetClause(final _MultiUpdate stmt, final _MultiUpdateContext context) {
        final List<_ItemPair> itemPairList = stmt.itemPairList();
        final int itemPairSize = itemPairList.size();
        if (itemPairSize == 0) {
            throw _Exceptions.setClauseNotExists();
        }

        //store SingleTableMeta alias
        final Map<String, Boolean> aliasMap = new HashMap<>();

        //1. append SET key word
        final StringBuilder sqlBuilder = context.sqlBuilder();
        sqlBuilder.append(_Constant.SPACE_SET);
        //2. append item pairs in SET clause
        _ItemPair pair;
        DataField dataField;
        for (int i = 0; i < itemPairSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            pair = itemPairList.get(i);
            pair.appendItemPair(context);

            if (pair instanceof _ItemPair._FieldItemPair) {
                dataField = ((_ItemPair._FieldItemPair) pair).field();
                aliasMap.putIfAbsent(context.singleTableAliasOf(dataField), Boolean.TRUE);
            } else {
                assert pair instanceof _ItemPair._RowItemPair;
                for (DataField field : ((_ItemPair._RowItemPair) pair).rowFieldList()) {
                    aliasMap.putIfAbsent(context.singleTableAliasOf(field), Boolean.TRUE);
                }
            }
        }

        assert aliasMap.size() > 0;
        //3. append updateTime and visible for multi-table update target table
        SingleTableMeta<?> singleTable;
        TabularItem tableItem;
        String safeTableAlias;
        for (String tableAlias : aliasMap.keySet()) {
            tableItem = context.tableItemOf(tableAlias);

            if (tableItem instanceof SingleTableMeta) {
                singleTable = (SingleTableMeta<?>) tableItem;
                safeTableAlias = context.safeTableAlias(singleTable, tableAlias);
            } else {
                assert tableItem instanceof DerivedTable;
                //TODO eg:oracle
                throw _Exceptions.immutableTable(tableItem);
            }
            this.appendUpdateTimeAndVersion(singleTable, safeTableAlias, context);
        }

        //clear
        aliasMap.clear();

    }


    protected final void selectListClause(final List<? extends SelectItem> selectItemList, final _SqlContext context) {

        final int size = selectItemList.size();
        if (size == 0) {
            throw _Exceptions.selectListIsEmpty();
        }
        final StringBuilder builder = context.sqlBuilder();
        SelectItem selectItem;
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            selectItem = selectItemList.get(i);
            if (selectItem instanceof Selection) {
                ((_Selection) selectItem).appendSelection(context);
            } else if (selectItem instanceof SelectionGroup) {
                ((_SelfDescribed) selectItem).appendSql(context);
            } else {
                throw _Exceptions.unknownSelectItem(selectItem);
            }

        }

    }

    /**
     * @see #standardSimpleQuery(_StandardQuery, _SimpleQueryContext)
     */
    protected final void standardTableReferences(final List<_TableBlock> tableBlockList
            , final _MultiTableContext context, final boolean nested) {
        final int blockSize = tableBlockList.size();
        if (blockSize == 0) {
            throw _Exceptions.tableBlockListIsEmpty(nested);
        }

        final StringBuilder sqlBuilder = context.sqlBuilder();
        _TableBlock block;
        TabularItem tableItem;
        _JoinType joinType;
        List<_Predicate> predicateList;
        final boolean supportTableOnly = this.supportTableOnly();
        for (int i = 0; i < blockSize; i++) {
            block = tableBlockList.get(i);
            if (block instanceof _DialectTableBlock) {
                throw _Exceptions.nonStandardTableBlock(block);
            }
            joinType = block.jointType();
            if (i > 0) {
                sqlBuilder.append(joinType.render());
            } else if (joinType != _JoinType.NONE) {
                throw _Exceptions.unexpectedEnum(joinType);
            }
            tableItem = block.tableItem();
            if (tableItem instanceof TableMeta) {
                if (supportTableOnly) {
                    sqlBuilder.append(_Constant.SPACE_ONLY);
                }
                sqlBuilder.append(_Constant.SPACE);
                this.identifier(((TableMeta<?>) tableItem).tableName(), sqlBuilder)
                        .append(_Constant.SPACE_AS_SPACE)
                        .append(context.safeTableAlias((TableMeta<?>) tableItem, block.alias()));
            } else if (tableItem instanceof SubQuery) {
                if (tableItem instanceof _LateralSubQuery) {
                    throw _Exceptions.dontSupportLateralItem(tableItem, block.alias(), null);
                }
                this.subQueryStmt((SubQuery) tableItem, context);
                sqlBuilder.append(_Constant.SPACE_AS_SPACE)
                        .append(context.safeTableAlias(block.alias()));
            } else if (tableItem instanceof NestedItems) {
                _SQLConsultant.assertStandardNestedItems((NestedItems) tableItem);
                if (_StringUtils.hasText(block.alias())) {
                    throw _Exceptions.nestedItemsAliasHasText(block.alias());
                }
                this.standardTableReferences(((_NestedItems) tableItem).tableBlockList(), context, true);
            } else {
                throw _Exceptions.dontSupportTableItem(tableItem, block.alias(), null);
            }

            // on clause
            switch (joinType) {
                case LEFT_JOIN:
                case JOIN:
                case RIGHT_JOIN:
                case FULL_JOIN: {
                    predicateList = block.onClauseList();
                    if (!nested || predicateList.size() > 0) {
                        this.onClause(predicateList, context);
                    }
                }
                break;
                case NONE:
                case CROSS_JOIN: {
                    if (block.onClauseList().size() > 0) {
                        throw _Exceptions.joinTypeNoOnClause(joinType);
                    }
                }
                break;
                case STRAIGHT_JOIN:
                    throw _Exceptions.castCriteriaApi();
                default:
                    throw _Exceptions.unexpectedEnum(joinType);
            }


        }// for


    }

    /**
     * @see #standardSimpleQuery(_StandardQuery, _SimpleQueryContext)
     */
    protected final void queryWhereClause(final List<_TableBlock> tableBlockList, final List<_Predicate> predicateList
            , final _MultiTableContext context) {
        final int predicateSize = predicateList.size();

        final StringBuilder builder = context.sqlBuilder();
        if (predicateSize > 0) {
            //1. append where key word
            builder.append(_Constant.SPACE_WHERE);
            //2. append where predicates
            for (int i = 0; i < predicateSize; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_AND);
                }
                predicateList.get(i).appendSql(context);
            }

        }

        if (context.visible() == Visible.BOTH) {
            return;
        }

        TabularItem tableItem;
        String safeTableAlias;
        SingleTableMeta<?> table;
        int count = 0;
        for (_TableBlock block : tableBlockList) {
            tableItem = block.tableItem();
            if (!(tableItem instanceof SingleTableMeta)) {
                continue;
            }
            table = (SingleTableMeta<?>) tableItem;
            if (!table.containField(_MetaBridge.VISIBLE)) {
                continue;
            }
            safeTableAlias = context.safeTableAlias(block.alias());
            if (count == 0 && predicateSize == 0) {
                builder.append(_Constant.SPACE_WHERE);
            }
            this.visiblePredicate(table, safeTableAlias, context, count == 0 && predicateSize == 0);
            count++;
        }

    }


    protected final void appendChildJoinParent(final _MultiTableContext context, final ChildTableMeta<?> child) {
        final ParentTableMeta<?> parent = child.parentMeta();

        final String safeChildTableAlias = context.saTableAliasOf(child);
        final String safeParentTableAlias = context.saTableAliasOf(parent);
        final StringBuilder builder = context.sqlBuilder();

        // 1. child table name
        builder.append(_Constant.SPACE);
        this.safeObjectName(child, builder)
                .append(_Constant.SPACE_AS_SPACE)
                .append(safeChildTableAlias);

        //2. join clause
        builder.append(_Constant.SPACE_JOIN_SPACE);
        // append parent table name
        this.safeObjectName(parent, builder)
                .append(_Constant.SPACE_AS_SPACE)
                .append(safeParentTableAlias);

        //2.1 on clause
        builder.append(_Constant.SPACE_ON_SPACE)
                .append(safeChildTableAlias)
                .append(_Constant.POINT)
                .append(_MetaBridge.ID)
                .append(_Constant.SPACE_EQUAL_SPACE)
                .append(safeParentTableAlias)
                .append(_Constant.POINT)
                .append(_MetaBridge.ID);
    }

    protected final void groupByClause(final List<? extends SortItem> groupByList, final _SqlContext context) {
        final int size = groupByList.size();
        if (size == 0) {
            return;
        }
        final StringBuilder builder = context.sqlBuilder()
                .append(_Constant.SPACE_GROUP_BY);
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            ((_SelfDescribed) groupByList.get(i)).appendSql(context);
        }

    }

    protected final void havingClause(final List<_Predicate> havingList, final _SqlContext context) {
        final int size = havingList.size();
        if (size == 0) {
            return;
        }
        final StringBuilder builder = context.sqlBuilder()
                .append(_Constant.SPACE_HAVING);
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_AND);
            }
            havingList.get(i).appendSql(context);
        }

    }

    protected final void orderByClause(final List<? extends SortItem> orderByList, final _SqlContext context) {
        final int size = orderByList.size();
        if (size == 0) {
            return;
        }
        final StringBuilder builder = context.sqlBuilder()
                .append(_Constant.SPACE_ORDER_BY);

        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            ((_SelfDescribed) orderByList.get(i)).appendSql(context);
        }

    }

    protected abstract void standardLimitClause(@Nullable _Expression offset, @Nullable _Expression rowCount
            , _SqlContext context);

    protected final void discriminator(final TableMeta<?> table, final @Nullable String safeTableAlias
            , final _SqlContext context) {
        final FieldMeta<?> field;
        if (table instanceof ChildTableMeta) {
            field = ((ChildTableMeta<?>) table).discriminator();
        } else if (table instanceof ParentTableMeta) {
            field = ((ParentTableMeta<?>) table).discriminator();
        } else {
            throw new IllegalArgumentException("table error");
        }
        final StringBuilder sqlBuilder = context.sqlBuilder();
        sqlBuilder.append(_Constant.SPACE_AND_SPACE);
        if (safeTableAlias != null) {
            sqlBuilder.append(safeTableAlias);
        } else if (table instanceof SingleTableMeta && context instanceof _SingleDeleteContext) {
            this.safeObjectName(table, sqlBuilder);
        } else {
            throw new IllegalArgumentException();
        }
        sqlBuilder.append(_Constant.POINT);

        this.safeObjectName(field, sqlBuilder)
                .append(_Constant.SPACE_EQUAL_SPACE)
                .append(table.discriminatorValue().code());
    }


    /**
     * @see #multiTableVisible(List, _MultiTableContext, boolean)
     */
    protected final void visiblePredicate(final SingleTableMeta<?> table, final @Nullable String safeTableAlias
            , final _SqlContext context, final boolean firstPredicate) {

        final FieldMeta<?> field = table.getField(_MetaBridge.VISIBLE);
        final Boolean visibleValue;
        switch (context.visible()) {
            case ONLY_VISIBLE:
                visibleValue = Boolean.TRUE;
                break;
            case ONLY_NON_VISIBLE:
                visibleValue = Boolean.FALSE;
                break;
            case BOTH:
                visibleValue = null;
                break;
            default:
                throw _Exceptions.unexpectedEnum(context.visible());
        }
        if (visibleValue != null) {
            final StringBuilder sqlBuilder = context.sqlBuilder();
            if (firstPredicate) {
                sqlBuilder.append(_Constant.SPACE);
            } else {
                sqlBuilder.append(_Constant.SPACE_AND_SPACE);
            }
            if (safeTableAlias != null) {
                sqlBuilder.append(safeTableAlias);
            } else if (context instanceof _SingleDeleteContext) {
                this.safeObjectName(table, sqlBuilder);
            } else {
                throw new IllegalArgumentException();
            }
            sqlBuilder.append(_Constant.POINT);
            this.safeObjectName(field, sqlBuilder)
                    .append(_Constant.SPACE_EQUAL_SPACE);
            this.literal(field.mappingType(), visibleValue, sqlBuilder);
        }

    }


    protected final void dmlWhereClause(final List<_Predicate> predicateList, final _SqlContext context) {
        final int predicateCount = predicateList.size();
        if (predicateCount == 0) {
            throw _Exceptions.noWhereClause(context);
        }
        final StringBuilder sqlBuilder = context.sqlBuilder();
        sqlBuilder.append(_Constant.SPACE_WHERE);
        for (int i = 0; i < predicateCount; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_AND);
            }
            predicateList.get(i).appendSql(context);
        }

    }

    /**
     * @see #standardSimpleQuery(_StandardQuery, _SimpleQueryContext)
     */
    protected final void multiTableVisible(final List<_TableBlock> blockList, final _MultiTableContext context
            , final boolean firstPredicate) {
        TabularItem tableItem;
        String safeTableAlias;
        SingleTableMeta<?> table;
        int count = 0;
        for (_TableBlock block : blockList) {
            tableItem = block.tableItem();
            if (!(tableItem instanceof SingleTableMeta)) {
                continue;
            }
            table = (SingleTableMeta<?>) tableItem;
            if (!table.containField(_MetaBridge.VISIBLE)) {
                continue;
            }
            safeTableAlias = context.safeTableAlias(block.alias());
            this.visiblePredicate(table, safeTableAlias, context, firstPredicate && count == 0);
            count++;
        }

    }


    /**
     * @see #rowSet(RowSet, _SqlContext)
     */
    @Deprecated
    protected final void subQueryStmt(final SubQuery subQuery, final _SqlContext outerContext) {
        //1. assert prepared
        subQuery.prepared();

        //2. assert sub query implementation class.
        if (subQuery instanceof _LateralSubQuery) {
            if (!(outerContext instanceof _LateralSubQueryContext && outerContext instanceof _UnionQueryContext)) {
                throw _Exceptions.lateralSubQueryErrorPosition();
            }
        } else if (subQuery instanceof StandardQuery) {
            _SQLConsultant.assertStandardQuery(subQuery);
        } else {
            this.assertDialectRowSet(subQuery);
        }
        //3. parse sub query
        this.parseSubQuery(subQuery, outerContext);
    }

    /**
     * @see #dialectSimpleQuery(_Query, _SimpleQueryContext)
     */
    @Deprecated
    protected final void lateralSubQuery(final _JoinType joinType, final SubQuery subQuery
            , final _MultiTableContext outerContext) {
        //1. assert prepared
        subQuery.prepared();
        //2. assert dialect sub query
        this.assertDialectRowSet(subQuery);
        //3. append key word LATERAL
        outerContext.sqlBuilder()
                .append(_Constant.SPACE_LATERAL);
        //4. parse sub query
        this.parseSubQuery(subQuery, outerContext);

    }


    /**
     * @see #rowSet(RowSet, _SqlContext)
     */
    protected final void parseValues(final _Values values, final _SqlContext original) {
        //1. assert prepared
        values.prepared();

        //2. assert sub query implementation class.
        this.assertDialectRowSet(values);
        final StringBuilder sqlBuilder = original.sqlBuilder();

        final boolean outerParen;
        outerParen = values instanceof SubValues
                && !(original instanceof _UnionQueryContext && original instanceof _ValuesContext);

        if (outerParen) {
            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
        }
        if (values instanceof _UnionRowSet0) {
            this.standardParensRowSet((_UnionRowSet0) values, UnionValuesContext.create(original));
        } else {
            sqlBuilder.append(_Constant.SPACE);
            this.dialectSimpleValues(ValuesContext.create(original), (_Values) values);
        }
        if (outerParen) {
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }


    }


    /**
     * @see #singleTableSetClause(_SingleUpdate, _SingleUpdateContext)
     * @see #multiTableSetClause(_MultiUpdate, _MultiUpdateContext)
     * @see #parseInsert(_SqlContext, _Insert, Visible)
     */
    protected final void appendUpdateTimeAndVersion(final SingleTableMeta<?> table
            , final @Nullable String safeTableAlias, final StmtContext context) {

        final StringBuilder sqlBuilder = context.sqlBuilder();
        FieldMeta<?> field;
        field = table.getField(_MetaBridge.UPDATE_TIME);

        final Class<?> javaType = field.javaType();
        final Temporal updateTimeValue;
        if (javaType == LocalDateTime.class) {
            updateTimeValue = LocalDateTime.now();
        } else if (javaType == OffsetDateTime.class) {
            updateTimeValue = OffsetDateTime.now(this.mappingEnv.zoneId());
        } else if (javaType == ZonedDateTime.class) {
            updateTimeValue = ZonedDateTime.now(this.mappingEnv.zoneId());
        } else {
            // FieldMeta no bug,never here
            throw _Exceptions.dontSupportJavaType(field, javaType);
        }
        final boolean supportTableAlias;
        supportTableAlias = safeTableAlias != null && this.setClauseTableAlias();

        sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
        if (supportTableAlias) {
            sqlBuilder.append(safeTableAlias)
                    .append(_Constant.POINT);
        }
        this.safeObjectName(field, sqlBuilder)
                .append(_Constant.SPACE_EQUAL);

        if (context instanceof _InsertContext) {
            ((StatementContext) context)
                    .appendInsertValue(((_InsertContext) context).literalMode(), field, updateTimeValue);
        } else if (context.hasParam()) {
            context.appendParam(SingleParam.build(field, updateTimeValue));
        } else {
            this.literal(field, updateTimeValue, sqlBuilder.append(_Constant.SPACE));
        }

        if ((field = table.tryGetField(_MetaBridge.VERSION)) != null) {
            sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
            if (supportTableAlias) {
                sqlBuilder.append(safeTableAlias)
                        .append(_Constant.POINT);
            }
            final String versionColumnName = this.safeObjectName(field);
            sqlBuilder.append(versionColumnName)
                    .append(_Constant.SPACE_EQUAL_SPACE);

            if (supportTableAlias) {
                sqlBuilder.append(safeTableAlias)
                        .append(_Constant.POINT);
            }
            sqlBuilder.append(versionColumnName)
                    .append(" + 1");

        }

    }


    /*################################## blow private method ##################################*/


    /**
     * @see #parseInsert(_SqlContext, _Insert, Visible)
     */
    private Stmt handleDomainInsert(final @Nullable _SqlContext outerContext, final _Insert._DomainInsert insert
            , final Visible visible) {
        final boolean standardStmt = insert instanceof StandardInsert;
        final Stmt stmt;
        if (insert instanceof _Insert._ChildDomainInsert) {
            assert outerContext == null || outerContext instanceof LiteralMultiStmtContext;

            final _Insert._ChildDomainInsert childStmt = (_Insert._ChildDomainInsert) insert;
            final _Insert._DomainInsert parentStmt = childStmt.parentStmt();
            checkParentStmt(parentStmt, (ChildTableMeta<?>) childStmt.table());

            final DomainInsertContext parentContext;
            parentContext = DomainInsertContext.forParent(outerContext, childStmt, this, visible);

            if (standardStmt) {
                this.standardValueSyntaxInsert(parentContext);
            } else {
                this.valueSyntaxInsert(parentContext, parentStmt);
            }

            final DomainInsertContext childContext;
            childContext = DomainInsertContext.forChild(outerContext, childStmt, parentContext);

            if (standardStmt) {
                this.standardValueSyntaxInsert(childContext);
            } else {
                this.valueSyntaxInsert(childContext, childStmt);
            }
            stmt = Stmts.pair(parentContext.build(), childContext.build());
        } else {
            final DomainInsertContext singleContext;
            singleContext = DomainInsertContext.forSingle(outerContext, insert, this, visible);
            if (standardStmt) {
                this.standardValueSyntaxInsert(singleContext);
            } else {
                this.valueSyntaxInsert(singleContext, insert);
            }
            stmt = singleContext.build();
        }
        return stmt;
    }

    /**
     * @see #parseInsert(_SqlContext, _Insert, Visible)
     */

    private Stmt handleValueInsert(final @Nullable _SqlContext outerContext, final _Insert._ValuesInsert insert
            , final Visible visible) {
        final boolean standardStmt = insert instanceof StandardInsert;
        final Stmt stmt;
        if (insert instanceof _Insert._ChildValuesInsert) {
            assert outerContext == null || outerContext instanceof LiteralMultiStmtContext;

            final _Insert._ChildValuesInsert childStmt = (_Insert._ChildValuesInsert) insert;
            final _Insert._ValuesInsert parentStmt = childStmt.parentStmt();
            checkParentStmt(parentStmt, (ChildTableMeta<?>) childStmt.table());

            final ValuesInsertContext parentContext;
            parentContext = ValuesInsertContext.forParent(outerContext, childStmt, this, visible);
            if (standardStmt) {
                this.standardValueSyntaxInsert(parentContext);
            } else {
                this.valueSyntaxInsert(parentContext, parentStmt);
            }

            final ValuesInsertContext childContext;
            childContext = ValuesInsertContext.forChild(outerContext, childStmt, parentContext);
            if (standardStmt) {
                this.standardValueSyntaxInsert(childContext);
            } else {
                this.valueSyntaxInsert(childContext, childStmt);
            }
            stmt = Stmts.pair(parentContext.build(), childContext.build());
        } else {
            final ValuesInsertContext singleContext;
            singleContext = ValuesInsertContext.forSingle(outerContext, insert, this, visible);
            if (standardStmt) {
                this.standardValueSyntaxInsert(singleContext);
            } else {
                this.valueSyntaxInsert(singleContext, insert);
            }
            stmt = singleContext.build();
        }
        return stmt;
    }

    /**
     * @see #parseInsert(_SqlContext, _Insert, Visible)
     */
    private Stmt handleAssignmentInsert(final @Nullable _SqlContext outerContext, final _Insert._AssignmentInsert insert
            , final Visible visible) {
        final Stmt stmt;
        if (insert instanceof _Insert._ChildAssignmentInsert) {
            assert outerContext == null || outerContext instanceof LiteralMultiStmtContext;

            final _Insert._ChildAssignmentInsert childStmt = (_Insert._ChildAssignmentInsert) insert;
            final _Insert._AssignmentInsert parentStmt = childStmt.parentStmt();
            checkParentStmt(parentStmt, (ChildTableMeta<?>) childStmt.table());

            final AssignmentInsertContext parentContext;
            parentContext = AssignmentInsertContext.forParent(outerContext, childStmt, this, visible);
            this.assignmentInsert(parentContext, parentStmt);

            final AssignmentInsertContext childContext;
            childContext = AssignmentInsertContext.forChild(outerContext, childStmt, parentContext);
            this.assignmentInsert(childContext, childStmt);

            stmt = Stmts.pair(parentContext.build(), childContext.build());
        } else {
            final AssignmentInsertContext singleContext;
            singleContext = AssignmentInsertContext.forSingle(outerContext, insert, this, visible);
            this.assignmentInsert(singleContext, insert);
            stmt = singleContext.build();
        }
        return stmt;
    }

    /**
     * @see #parseInsert(_SqlContext, _Insert, Visible)
     */
    private Stmt handleQueryInsert(final @Nullable _SqlContext outerContext, final _Insert._QueryInsert insert
            , final Visible visible) {
        final boolean standardStmt = insert instanceof StandardInsert;
        final Stmt stmt;
        if (insert instanceof _Insert._ChildQueryInsert) {

            final _Insert._ChildQueryInsert childStmt = (_Insert._ChildQueryInsert) insert;

            final QueryInsertContext parentContext;
            parentContext = QueryInsertContext.forParent(outerContext, childStmt, this, visible);
            if (standardStmt) {
                this.standardQueryInsert(parentContext);
            } else {
                this.queryInsert(parentContext, childStmt.parentStmt());
            }

            final QueryInsertContext childContext;
            childContext = QueryInsertContext.forChild(outerContext, childStmt, parentContext);
            if (standardStmt) {
                this.standardQueryInsert(childContext);
            } else {
                this.queryInsert(childContext, childStmt);
            }
            stmt = Stmts.pair(parentContext.build(), childContext.build());
        } else {
            final QueryInsertContext singleContext;
            singleContext = QueryInsertContext.forSingle(outerContext, insert, this, visible);
            if (standardStmt) {
                this.standardQueryInsert(singleContext);
            } else {
                this.queryInsert(singleContext, insert);
            }
            stmt = singleContext.build();
        }
        return stmt;
    }


    private void standardSelectClause(List<? extends SQLWords> modifierList, _SqlContext context) {
        final StringBuilder builder = context.sqlBuilder()
                .append(_Constant.SELECT);
        switch (modifierList.size()) {
            case 0:
                //no-op
                break;
            case 1: {
                final SQLWords modifier = modifierList.get(0);
                if (!(modifier instanceof SQLs.WordAll)) {
                    String m = String.format("Standard query api support only %s", SQLs.WordAll.class.getName());
                    throw new CriteriaException(m);
                }
                builder.append(_Constant.SPACE)
                        .append(modifier.render());
            }
            break;
            default:
                String m = String.format("Standard query api support only %s", SQLs.WordAll.class.getName());
                throw new CriteriaException(m);
        }

    }


    /**
     * @see #handleDomainInsert(_SqlContext, _Insert._DomainInsert, Visible)
     * @see #handleValueInsert(_SqlContext, _Insert._ValuesInsert, Visible)
     */
    private void standardValueSyntaxInsert(final _ValueInsertContext context) {
        final StringBuilder sqlBuilder;
        //1. INSERT INTO keywords
        sqlBuilder = context.sqlBuilder()
                .append(_Constant.INSERT_INTO_SPACE);
        //2. table name
        this.safeObjectName(context.insertTable(), sqlBuilder);
        //3. table column list
        context.appendFieldList();
        //4. values clause
        context.appendValueList();
    }


    /**
     * @see #handleQueryInsert(_Insert._QueryInsert, Visible)
     */
    private void standardQueryInsert(final _QueryInsertContext context) {
        final StringBuilder sqlBuilder = context.sqlBuilder();
        //1. INSERT INTO keywords
        sqlBuilder.append(_Constant.INSERT_INTO_SPACE);
        //2. table name
        this.safeObjectName(context.insertTable(), sqlBuilder);
        //3. table column list
        context.appendFieldList();
        //4. sub query
        context.appendSubQuery();
    }


    /**
     * @see #delete(Delete, Visible)
     */
    private Stmt handleStandardDelete(final _SingleDelete delete, final Visible visible) {

        final SingleDeleteContext context;
        context = SingleDeleteContext.create(delete, this, visible);

        final SingleTableMeta<?> table = (SingleTableMeta<?>) context.table;

        final StringBuilder sqlBuilder = context.sqlBuilder;

        //1. DELETE key words
        sqlBuilder.append(_Constant.DELETE_FROM_SPACE);

        //2. table name
        this.safeObjectName(table, sqlBuilder);

        final String safeTableAlias;
        if (context.supportAlias) {
            safeTableAlias = context.safeTableAlias;
            sqlBuilder.append(_Constant.SPACE_AS_SPACE)
                    .append(safeTableAlias);
        } else {
            safeTableAlias = null;
        }
        //3. WHERE clause
        this.dmlWhereClause(delete.predicateList(), context);
        //3.1 append discriminator
        if (table instanceof ParentTableMeta) {
            this.discriminator(table, safeTableAlias, context);
        }
        //3.2 append visible
        if (table.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(table, safeTableAlias, context, false);
        }
        final Stmt stmt;
        if (delete instanceof _BatchDml) {
            stmt = context.build(((_BatchDml) delete).paramList());
        } else {
            stmt = context.build();
        }
        return stmt;
    }


    /**
     * @see #update(Update, Visible)
     */
    private Stmt standardSingleTableUpdate(final @Nullable _SqlContext outerContext, final _SingleUpdate update
            , final Visible visible) {
        assert update instanceof StandardUpdate;
        assert !(update instanceof _DomainUpdate) || ((_DomainUpdate) update).childItemPairList().size() == 0;

        final SingleTableMeta<?> updateTable;
        updateTable = (SingleTableMeta<?>) update.table();
        final List<_ItemPair> itemPairList;
        itemPairList = update.itemPairList();
        final int itemPairSize = itemPairList.size();
        if (itemPairSize == 0) {
            throw _Exceptions.setClauseNotExists();
        }

        final SingleUpdateContext context;
        context = SingleUpdateContext.create(outerContext, update, this, visible);

        final StringBuilder sqlBuilder = context.sqlBuilder;
        // 1. UPDATE clause
        sqlBuilder.append(_Constant.UPDATE)
                .append(_Constant.SPACE);

        this.safeObjectName(updateTable, sqlBuilder);

        //1.2 updateTable alias
        if (this.tableAliasAfterAs()) {
            sqlBuilder.append(_Constant.SPACE_AS_SPACE);
        } else {
            sqlBuilder.append(_Constant.SPACE);
        }
        sqlBuilder.append(context.safeTableAlias);

        //2. set clause
        this.singleTableSetClause(update, context);
        //3. where clause
        this.dmlWhereClause(update.wherePredicateList(), context);

        //3.1 append condition update field
        context.appendConditionFields();

        //3.2 append discriminator predicate
        if (updateTable instanceof ParentTableMeta) {
            this.discriminator(updateTable, context.safeTableAlias, context);
        }

        //3.3 append visible
        if (updateTable.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(updateTable, context.safeTableAlias, context, false);
        }

        final Stmt stmt;
        if (update instanceof _BatchDml) {
            stmt = context.build(((_BatchDml) update).paramList());
        } else {
            stmt = context.build();
        }
        return stmt;
    }


    /**
     * @see #parseSelectStmt(Select, _SqlContext)
     * @see #parseSubQuery(SubQuery, _SqlContext)
     */
    private void standardSimpleQuery(final _StandardQuery query, final _SimpleQueryContext context) {
        _SQLConsultant.assertStandardQuery((Query) query);

        //1. select clause
        this.standardSelectClause(query.modifierList(), context);
        //2. select list clause
        this.selectListClause(query.selectItemList(), context);
        //3. from clause
        context.sqlBuilder()
                .append(_Constant.SPACE_FROM);
        final List<_TableBlock> blockList = query.tableBlockList();
        this.standardTableReferences(blockList, context, false);
        //4. where clause
        this.queryWhereClause(blockList, query.wherePredicateList(), context);

        //5. groupBy clause
        final List<? extends SortItem> groupByList = query.groupByList();
        if (groupByList.size() > 0) {
            this.groupByClause(groupByList, context);
            this.havingClause(query.havingList(), context);
        }
        //6. orderBy clause
        this.orderByClause(query.orderByList(), context);

        //7. limit clause
        this.standardLimitClause(query.offset(), query.rowCount(), context);

        //8. lock clause
        final SQLWords lock = query.lockMode();
        if (lock != null) {
            this.standardLockClause(lock, context);
        }

    }

    /**
     * @see #handleDomainInsert(_Insert._DomainInsert, Visible)
     * @see #handleValueInsert(_Insert._ValuesInsert, Visible)
     * @see #handleAssignmentInsert(_Insert._AssignmentInsert, Visible)
     */
    private void checkParentStmt(_Insert parentStmt, ChildTableMeta<?> childTable) {
        if (parentStmt instanceof _Insert._SupportConflictClauseSpec
                && parentStmt.table().id().generatorType() == GeneratorType.POST
                && !this.supportInsertReturning()) {
            throw _Exceptions.duplicateKeyAndPostIdInsert(childTable);
        }
    }


    private static String nonBeautifySql(String sql) {
        return sql;
    }


    protected static IllegalArgumentException illegalDialect() {
        return new IllegalArgumentException("dialect instance error");
    }


}
