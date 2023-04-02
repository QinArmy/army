package io.army.dialect;

import io.army.ArmyException;
import io.army.annotation.GeneratorType;
import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl._JoinType;
import io.army.criteria.impl._SQLConsultant;
import io.army.criteria.impl.inner.*;
import io.army.criteria.standard.StandardDelete;
import io.army.criteria.standard.StandardInsert;
import io.army.criteria.standard.StandardQuery;
import io.army.criteria.standard.StandardUpdate;
import io.army.lang.Nullable;
import io.army.mapping.BooleanType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.schema._FieldResult;
import io.army.schema._SchemaResult;
import io.army.schema._TableResult;
import io.army.sqltype.SqlType;
import io.army.stmt.*;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
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
abstract class ArmyParser implements DialectParser {


    public final DialectEnv dialectEnv;

    protected final MappingEnv mappingEnv;

    protected final ServerMeta serverMeta;

    final boolean mockEnv;

    /**
     * a unmodified set
     */
    protected final Set<String> keyWordSet;

    protected final Dialect dialect;

    protected final char identifierQuote;

    final boolean supportSingleUpdateAlias;

    final boolean supportSingleDeleteAlias;

    final boolean supportOnlyDefault;

    final boolean supportRowAlias;

    final boolean singleDmlAliasAfterAs;

    final boolean aliasAfterAs;

    private final boolean tableOnlyModifier;

    final boolean supportZone;

    final boolean setClauseTableAlias;

    final boolean supportUpdateRow;

    final boolean supportUpdateDerivedField;

    final _ChildUpdateMode childUpdateMode;
    final FieldValueGenerator generator;

    ArmyParser(final DialectEnv dialectEnv, final Dialect dialect) {
        this.dialect = dialect; // first
        this.dialectEnv = dialectEnv;
        this.mappingEnv = dialectEnv.mappingEnv();
        this.serverMeta = this.mappingEnv.serverMeta();

        this.mockEnv = this.dialectEnv instanceof _MockDialects;

        assert dialect.database() == this.serverMeta.database();
        this.childUpdateMode = this.childUpdateMode();
        this.aliasAfterAs = this.isTableAliasAfterAs();
        this.identifierQuote = this.identifierDelimitedQuote();

        this.singleDmlAliasAfterAs = this.aliasAfterAs;
        this.supportSingleUpdateAlias = this.isSupportSingleUpdateAlias();
        this.supportSingleDeleteAlias = this.isSupportSingleDeleteAlias();
        this.supportZone = this.isSupportZone();

        this.supportOnlyDefault = this.isSupportOnlyDefault();
        this.tableOnlyModifier = this.isSupportTableOnly();
        this.setClauseTableAlias = this.isSetClauseTableAlias();
        this.supportUpdateRow = this.isSupportUpdateRow();

        this.supportUpdateDerivedField = this.isSupportUpdateDerivedField();

        this.supportRowAlias = this.isSupportRowAlias();

        this.keyWordSet = Collections.unmodifiableSet(this.createKeyWordSet());
        if (this.mockEnv) {
            this.generator = FieldValuesGenerators.mock(this.mappingEnv::databaseZoneOffset);
        } else {
            this.generator = FieldValuesGenerators.create(this.mappingEnv::databaseZoneOffset, dialectEnv.fieldGeneratorMap());
        }
    }




    /*################################## blow DML batchInsert method ##################################*/


    /**
     * {@inheritDoc}
     */
    @Override
    public final Stmt insert(final InsertStatement insert, final Visible visible) {
        return this.createInsertStmt(this.handleInsert(null, insert, visible));
    }


    /*################################## blow update method ##################################*/


    @Override
    public final Stmt update(final UpdateStatement update, final Visible visible) {
        final Stmt stmt;
        stmt = this.handleUpdate(null, update, visible, this::createUpdateStmt);
        if (!(update instanceof _SingleUpdate._ChildUpdate)) {
            assert !(update instanceof _BatchDml) || stmt instanceof BatchStmt;
        } else if (update instanceof _BatchDml) {
            assert stmt instanceof PairBatchStmt;
        } else {
            assert stmt instanceof PairStmt;
        }
        return stmt;
    }


    @Override
    public final Stmt delete(final DeleteStatement delete, final Visible visible) {
        final Stmt stmt;
        stmt = this.handleDelete(null, delete, visible, this::createDeleteStmt);
        if (!(delete instanceof _SingleDelete._ChildDelete)) {
            assert !(delete instanceof _BatchDml) || stmt instanceof BatchStmt;
        } else if (delete instanceof _BatchDml) {
            assert stmt instanceof PairBatchStmt;
        } else {
            assert stmt instanceof PairStmt;
        }
        return stmt;
    }

    @Override
    public final SimpleStmt select(final Select select, final Visible visible) {
        return this.handleSelect(null, select, visible)
                .build();
    }

    @Override
    public final SimpleStmt values(final Values values, final Visible visible) {
        return this.handleValues(null, values, visible)
                .build();
    }


    @Override
    public final void subQuery(final SubQuery query, final _SqlContext original) {
        this.handleSubQuery(query, original);
    }


    @Override
    public final Stmt dialectDml(final DmlStatement statement, final Visible visible) {
        return this.createDialectStmt(this.handleDialectDml(null, statement, visible));
    }

    @Override
    public final Stmt dialectDql(final DqlStatement statement, final Visible visible) {
        return this.createDialectStmt(this.handleDialectDql(null, statement, visible));
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
    public final Dialect dialect() {
        return this.dialect;
    }


    @Override
    public final String printStmt(final Stmt stmt, final boolean beautify) {
        return beautify ? stmt.printSql(this::beautifySql) : stmt.printSql(ArmyParser::nonBeautifySql);
    }


    @Override
    public final String toString() {
        return _StringUtils.builder()
                .append('[')
                .append(this.getClass().getName())
                .append(" dialect:")
                .append(this.dialect.name())
                .append(",hash:")
                .append(System.identityHashCode(this))
                .append(']')
                .toString();
    }


    /*################################## blow properties template method ##################################*/


    protected abstract Set<String> createKeyWordSet();


    protected abstract char identifierDelimitedQuote();

    protected abstract String defaultFuncName();

    protected abstract boolean isSupportZone();

    protected abstract boolean isSetClauseTableAlias();

    protected abstract boolean isTableAliasAfterAs();

    protected abstract boolean isSupportOnlyDefault();

    protected abstract boolean isSupportRowAlias();

    protected abstract boolean isSupportTableOnly();

    protected abstract _ChildUpdateMode childUpdateMode();

    protected abstract boolean isSupportSingleUpdateAlias();

    protected abstract boolean isSupportSingleDeleteAlias();

    protected abstract boolean isSupportUpdateRow();

    protected abstract boolean isSupportUpdateDerivedField();



    /*################################## blow dialect template method ##################################*/

    public final String safeObjectName(DatabaseObject object) {
        //TODO append Qualified for table name
        return this.doSafeObjectName(object);
    }

    public final StringBuilder safeObjectName(DatabaseObject object, StringBuilder builder) {
        //TODO append Qualified for table name
        return this.doSafeObjectName(object, builder);
    }


    protected abstract String doSafeObjectName(DatabaseObject object);

    protected abstract StringBuilder doSafeObjectName(DatabaseObject object, StringBuilder builder);

    protected abstract boolean isNeedConvert(SqlType type, Object nonNull);

    protected abstract void bindLiteral(TypeMeta typeMeta, SqlType type, Object value, StringBuilder sqlBuilder);

    protected abstract DdlDialect createDdlDialect();

    protected void assertInsert(InsertStatement insert) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    /**
     * @see #update(UpdateStatement, Visible)
     */
    protected void assertUpdate(UpdateStatement update) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    protected String beautifySql(String sql) {
        return sql;
    }

    protected void assertDelete(DeleteStatement delete) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    protected void assertRowSet(RowSet query) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    /**
     * @see #handleDomainInsert(_SqlContext, _Insert._DomainInsert, Visible)
     * @see #handleValueInsert(_SqlContext, _Insert._ValuesInsert, Visible)
     */
    protected void parseValuesInsert(_ValueInsertContext context, _Insert._ValuesSyntaxInsert insert) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    protected void parseAssignmentInsert(_AssignmentInsertContext context, _Insert._AssignmentInsert insert) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    protected void parseQueryInsert(_QueryInsertContext context, _Insert._QueryInsert insert) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    /**
     * @see #handleUpdate(_SqlContext, UpdateStatement, Visible, Function)
     */
    protected void parseSingleUpdate(_SingleUpdate update, _SingleUpdateContext context) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    /**
     * @see #handleUpdate(_SqlContext, UpdateStatement, Visible, Function)
     */
    protected void parseMultiUpdate(_MultiUpdate update, _MultiUpdateContext context) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    /**
     * @see #handleDelete(_SqlContext, DeleteStatement, Visible, Function)
     */
    protected void parseMultiDelete(final _MultiDelete delete, _MultiDeleteContext context) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    /**
     * @see #handleDelete(_SqlContext, DeleteStatement, Visible, Function)
     */
    protected void parseSingleDelete(_SingleDelete delete, _SingleDeleteContext context) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    /**
     * @see #handleQuery(Query, _SqlContext)
     * @see #handleSelect(_SqlContext, Select, Visible)
     * @see #handleValuesQuery(ValuesQuery, _SqlContext)
     * @see #handleValues(_SqlContext, Values, Visible)
     */
    protected void parseWithClause(_Statement._WithClauseSpec spec, _SqlContext context) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    /**
     * @see #handleSelect(_SqlContext, Select, Visible)
     * @see #handleQuery(Query, _SqlContext)
     */
    protected void parseSimpleQuery(_Query query, _SimpleQueryContext context) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    /**
     * @see #handleValues(_SqlContext, Values, Visible)
     * @see #handleValuesQuery(ValuesQuery, _SqlContext)
     */
    protected void parseSimpleValues(_ValuesQuery values, _ValuesContext context) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    /**
     * @see #dialectDml(DmlStatement, Visible)
     */
    protected _PrimaryContext handleDialectDml(@Nullable _SqlContext outerContext, DmlStatement statement
            , Visible visible) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    /**
     * @see #dialectDql(DqlStatement, Visible)
     */
    protected _PrimaryContext handleDialectDql(@Nullable _SqlContext outerContext, DqlStatement statement
            , Visible visible) {
        throw standardParserDontSupportDialect(this.dialect);
    }


    /**
     * @see #handleDomainUpdate(_SqlContext, _DomainUpdate, Visible)
     */
    protected void parseDomainChildUpdate(_SingleUpdate update, _UpdateContext context) {

        throw new UnsupportedOperationException();
    }

    /**
     * @see #handleDomainDelete(_SqlContext, _DomainDelete, Visible)
     */
    @Nullable
    protected void parseDomainChildDelete(_SingleDelete delete, _DeleteContext context) {
        throw new UnsupportedOperationException();
    }


    /**
     * @see #handleSelect(_SqlContext, Select, Visible)
     * @see #handleQuery(Query, _SqlContext)
     * @see #handleValues(_SqlContext, Values, Visible)
     * @see #handleValuesQuery(ValuesQuery, _SqlContext)
     */
    protected void parseClauseAfterRightParen(_ParensRowSet rowSet, _ParenRowSetContext context) {
        throw standardParserDontSupportDialect(this.dialect);
    }


    /*-------------------below final protected method -------------------*/


    /**
     * <p>
     * Append  literal
     * </p>
     */
    protected final void literal(final TypeMeta typeMeta, final Object nonNull, final StringBuilder sqlBuilder) {
        final MappingType mappingType;
        if (typeMeta instanceof MappingType) {
            mappingType = (MappingType) typeMeta;
        } else {
            mappingType = typeMeta.mappingType();
        }
        final SqlType sqlType;
        sqlType = mappingType.map(this.serverMeta);

        final Object value;
        if (this.isNeedConvert(sqlType, nonNull)) {
            value = mappingType.beforeBind(sqlType, this.mappingEnv, nonNull);
        } else {
            value = nonNull;
        }
        //TODO validate non-field codec
        this.bindLiteral(typeMeta, sqlType, value, sqlBuilder);

    }

    protected final _SingleUpdateContext createSingleUpdateContext(final @Nullable _SqlContext outerContext
            , final _SingleUpdate stmt, final Visible visible) {
        return SingleUpdateContext.create(outerContext, stmt, this, visible);
    }

    protected final _MultiUpdateContext createMultiUpdateContext(final @Nullable _SqlContext outerContext
            , final _SingleUpdate stmt, final Visible visible) {
        return MultiUpdateContext.forChild(outerContext, stmt, this, visible);
    }

    protected final _MultiDeleteContext createMultiDeleteContext(final @Nullable _SqlContext outerContext
            , final _SingleDelete stmt, final Visible visible) {
        return MultiDeleteContext.forChild(outerContext, stmt, this, visible);
    }

    protected final _OtherDmlContext createOtherDmlContext(final @Nullable _SqlContext outerContext
            , final Predicate<FieldMeta<?>> predicate, final Visible visible) {
        return OtherDmlContext.create(outerContext, predicate, this, visible);
    }

    protected final _OtherDmlContext createOtherDmlContext(final @Nullable _SqlContext outerContext
            , final Predicate<FieldMeta<?>> predicate, final _OtherDmlContext parentContext) {
        return OtherDmlContext.forChild(outerContext, predicate, (OtherDmlContext) parentContext);
    }

    /**
     * @param insert possibly be below :
     *               <ul>
     *                  <li>{@link InsertStatement}</li>
     *               </ul>
     * @see #insert(InsertStatement, Visible)
     */
    protected final _InsertContext handleInsert(final @Nullable _SqlContext outerContext, final InsertStatement insert,
                                                final Visible visible) {
        insert.prepared();
        if (insert instanceof StandardInsert) {
            _SQLConsultant.assertStandardInsert(insert);
        } else {
            this.assertInsert(insert);
        }
        final _InsertContext context;
        if (insert instanceof _Insert._DomainInsert) {
            context = handleDomainInsert(outerContext, (_Insert._DomainInsert) insert, visible);
        } else if (insert instanceof _Insert._ValuesInsert) {
            context = handleValueInsert(outerContext, (_Insert._ValuesInsert) insert, visible);
        } else if (insert instanceof _Insert._AssignmentInsert) {
            context = handleAssignmentInsert(outerContext, (_Insert._AssignmentInsert) insert, visible);
        } else if (insert instanceof _Insert._QueryInsert) {
            context = handleQueryInsert(outerContext, (_Insert._QueryInsert) insert, visible);
        } else {
            throw _Exceptions.unknownStatement(insert, this.dialect);
        }
        return context;
    }

    protected final void handleRowSet(final RowSet rowSet, final _SqlContext original) {
        //3. parse RowSet
        if (rowSet instanceof Query) {
            this.handleQuery((Query) rowSet, original);
        } else if (rowSet instanceof ValuesQuery) {
            this.handleValuesQuery((ValuesQuery) rowSet, original);
        } else {
            throw _Exceptions.unknownStatement(rowSet, this.dialect);
        }

    }

    /**
     * @see #subQuery(SubQuery, _SqlContext)
     * @see #standardTableReferences(List, _MultiTableStmtContext, boolean)
     */
    protected final void handleSubQuery(final SubQuery query, final _SqlContext original) {
        final StringBuilder sqlBuilder;
        sqlBuilder = original.sqlBuilder()
                .append(_Constant.SPACE_LEFT_PAREN);
        this.handleQuery(query, original);
        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
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

        for (int i = 0; i < pairSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            itemPairList.get(i).appendItemPair(context);
        }

        final TableMeta<?> insertTable;
        insertTable = context.insertTable();
        if (insertTable instanceof SingleTableMeta) {
            // here ,safe table alias must be null
            this.appendUpdateTimeAndVersion((SingleTableMeta<?>) insertTable, null, context, false);
        }
    }


    protected final void handleSubValues(final SubValues values, final _SqlContext original) {
        final StringBuilder sqlBuilder;
        sqlBuilder = ((StatementContext) original).sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
        this.handleValuesQuery(values, original);
        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
    }


    /**
     * @see #handleRowSet(RowSet, _SqlContext)
     */
    protected final void handleValuesQuery(final ValuesQuery values, final _SqlContext original) {
        this.assertRowSet(values);
        if (values instanceof _ValuesQuery) {
            final ValuesContext context;
            context = ValuesContext.create(original, values, this, original.visible());
            this.parseSimpleValues((_ValuesQuery) values, context);
        } else if (values instanceof _UnionRowSet) {
            final _UnionRowSet union = (_UnionRowSet) values;
            this.handleValuesQuery((ValuesQuery) union.leftRowSet(), original);
            ((StatementContext) original).sqlBuilder.append(union.unionType().render());
            this.handleRowSet(union.rightRowSet(), original);
        } else {
            assert values instanceof _ParensRowSet;
            final _ParenRowSetContext context;
            if (original instanceof _ParenRowSetContext) {
                context = (_ParenRowSetContext) original;
            } else {
                context = ParenSubRowSetContext.forSimple(original, this, original.visible());
            }
            final _ParensRowSet parensRowSet = (_ParensRowSet) values;
            final StringBuilder sqlBuilder;
            sqlBuilder = ((StatementContext) original).sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            this.handleRowSet(parensRowSet.innerRowSet(), context);
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            this.parseClauseAfterRightParen(parensRowSet, context);
        }

    }


    /**
     * @see #parseSimpleValues(_ValuesQuery, _ValuesContext)
     */
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
     * @see #handleQuery(Query, _SqlContext)
     */
    protected final void parseStandardParensQuery(final _ParensRowSet query, final _ParenRowSetContext context) {

        final List<? extends SortItem> orderByList;
        if ((orderByList = query.orderByList()).size() > 0) {
            this.orderByClause(orderByList, context);
        }
        this.standardLimitClause(query.offsetExp(), query.rowCountExp(), context);
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
            columnAliasList = cte.columnAliasList();
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
            sqlBuilder.append(_Constant.SPACE_AS)
                    .append(_Constant.SPACE_LEFT_PAREN);
            this.handleQuery(subQuery, context);
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }

    }


    /**
     * @see #handleSelect(_SqlContext, Select, Visible)
     * @see #handleRowSet(RowSet, _SqlContext)
     * @see #subQuery(SubQuery, _SqlContext)
     * @see #withSubQuery(boolean, List, _SqlContext, Consumer)
     */
    protected final void handleQuery(final Query query, final _SqlContext original) {
        query.prepared();
        if (query instanceof _Query) {
            final _SimpleQueryContext context;
            if (query instanceof Select) {
                context = SimpleSelectContext.create(original, (Select) query);
            } else if (query instanceof SubQuery) {
                context = SimpleSubQueryContext.forSimple(original, (SubQuery) query);
            } else {
                throw _Exceptions.unknownRowSetType(query);
            }
            if (query instanceof StandardQuery) {
                _SQLConsultant.assertStandardQuery(query);
                this.parseStandardQuery((_StandardQuery) query, context);
            } else {
                this.assertRowSet(query);
                this.parseSimpleQuery((_Query) query, context);
            }
        } else if (query instanceof _UnionRowSet) {
            _SQLConsultant.assertUnionRowSet(query);
            final _UnionRowSet unionRowSet = (_UnionRowSet) query;
            this.handleQuery((Query) unionRowSet.leftRowSet(), original);
            original.sqlBuilder().append(unionRowSet.unionType().render());
            this.handleRowSet(unionRowSet.rightRowSet(), original);
        } else if (query instanceof _ParensRowSet) {
            if (query instanceof StandardQuery) {
                _SQLConsultant.assertStandardQuery(query);
            } else {
                this.assertRowSet(query);
            }
            if (!(query instanceof StandardQuery)
                    && ((_Statement._WithClauseSpec) query).cteList().size() > 0) {
                this.parseWithClause((_Statement._WithClauseSpec) query, original);
            }
            final _ParenRowSetContext context;
            if (query instanceof Select) {
                context = ParensSelectContext.create(original, (Select) query, this, original.visible());
            } else if (query instanceof SubQuery) {
                context = ParenSubRowSetContext.forSimple(original);
            } else {
                throw _Exceptions.unknownRowSetType(query);
            }
            this.handleParenRowSet(context, (_ParensRowSet) query);
        } else {
            throw _Exceptions.unknownRowSetType(query);
        }

    }


    /**
     * @see #parseSingleUpdate(_SingleUpdate, _SingleUpdateContext)
     * @see #parseDomainChildUpdateWithId(_DomainUpdate, DomainUpdateContext)
     * @see #parseStandardSingleUpdate(_SingleUpdate, _SingleUpdateContext)
     */
    protected final void singleTableSetClause(final List<_ItemPair> itemPairList, final _SingleUpdateContext context) {
        final TableMeta<?> targetTable = context.targetTable();
        final int itemPairSize = itemPairList.size();
        if (itemPairSize == 0 && !(targetTable instanceof ParentTableMeta)) {
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

        if (targetTable instanceof SingleTableMeta) {
            final String safeTableAlias;
            if (this.supportSingleUpdateAlias) {
                safeTableAlias = context.safeTargetTableAlias();
            } else {
                safeTableAlias = null;
            }
            this.appendUpdateTimeAndVersion((SingleTableMeta<?>) targetTable, safeTableAlias, context, itemPairSize == 0);
        }
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
        this.appendUpdateTimeAndVersion(parent, context.saTableAliasOf(parent), context, false);
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
            this.appendUpdateTimeAndVersion(singleTable, safeTableAlias, context, false);
        }

        //clear
        aliasMap.clear();

    }


    protected final void selectListClause(final SelectItemListContext context) {
        final List<? extends _SelectItem> selectItemList;
        selectItemList = context.selectItemList();
        final int size = selectItemList.size();
        if (size == 0) {
            throw _Exceptions.selectListIsEmpty();
        }
        final StringBuilder builder = context.sqlBuilder();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            selectItemList.get(i).appendSelectItem(context);

        }//for

    }


    /**
     * @see #parseStandardQuery(_StandardQuery, _SimpleQueryContext)
     */
    protected final void standardTableReferences(final List<_TabularBock> tableBlockList,
                                                 final _MultiTableStmtContext context, final boolean nested) {
        final int blockSize = tableBlockList.size();
        assert blockSize > 0;

        final StringBuilder sqlBuilder = context.sqlBuilder();
        _TabularBock block;
        TabularItem tableItem;
        _JoinType joinType;
        List<_Predicate> predicateList;
        final boolean tableOnlyModifier = this.tableOnlyModifier;
        for (int i = 0; i < blockSize; i++) {
            block = tableBlockList.get(i);
            joinType = block.jointType();
            if (i > 0) {
                sqlBuilder.append(joinType.render());
            } else if (joinType != _JoinType.NONE) {
                throw _Exceptions.unexpectedEnum(joinType);
            }
            tableItem = block.tableItem();
            if (tableItem instanceof TableMeta) {
                if (tableOnlyModifier) {
                    sqlBuilder.append(_Constant.SPACE_ONLY);
                }
                sqlBuilder.append(_Constant.SPACE);
                this.identifier(((TableMeta<?>) tableItem).tableName(), sqlBuilder)
                        .append(_Constant.SPACE_AS_SPACE)
                        .append(context.safeTableAlias((TableMeta<?>) tableItem, block.alias()));
            } else if (tableItem instanceof SubQuery) {
                this.handleSubQuery((SubQuery) tableItem, context);
                sqlBuilder.append(_Constant.SPACE_AS_SPACE)
                        .append(context.safeTableAlias(block.alias()));
            } else if (tableItem instanceof _NestedItems) {
                _SQLConsultant.assertStandardNestedItems((_NestedItems) tableItem);
                if (_StringUtils.hasText(block.alias())) {
                    throw _Exceptions.nestedItemsAliasHasText(block.alias());
                }
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
                this.standardTableReferences(((_NestedItems) tableItem).tableBlockList(), context, true);
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
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
                    if (predicateList.size() > 0) {
                        this.onClause(predicateList, context);
                    } else if (!nested) {
                        throw _Exceptions.castCriteriaApi();
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
     * @see #parseStandardQuery(_StandardQuery, _SimpleQueryContext)
     */
    protected final void queryWhereClause(final List<_TabularBock> tableBlockList, final List<_Predicate> predicateList
            , final _MultiTableStmtContext context) {
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

        if (predicateSize == 0) {
            final int startIndex, endIndex;

            startIndex = builder.length();
            builder.append(_Constant.SPACE_WHERE);
            endIndex = builder.length();

            multiTableVisible(tableBlockList, context, true);
            if (builder.length() == endIndex) {
                builder.setLength(startIndex);
            }
        } else {
            multiTableVisible(tableBlockList, context, false);
        }

    }


    protected final void appendChildJoinParent(final _MultiTableStmtContext context, final ChildTableMeta<?> child) {
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

    /**
     * @see #parseDomainParentDeleteWithId(_Predicate, DomainDeleteContext)
     * @see #parseDomainParentUpdateWithId(_DomainUpdate, _Predicate, DomainUpdateContext)
     */
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
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder()
                .append(_Constant.SPACE_AND_SPACE);

        if (safeTableAlias == null) {
            this.safeObjectName(field.tableMeta(), sqlBuilder);
        } else {
            sqlBuilder.append(safeTableAlias);
        }
        sqlBuilder.append(_Constant.POINT);

        this.safeObjectName(field, sqlBuilder)
                .append(_Constant.SPACE_EQUAL_SPACE)
                .append(table.discriminatorValue().code());
    }


    /**
     * @see #multiTableVisible(List, _MultiTableStmtContext, boolean)
     */
    protected final void visiblePredicate(final SingleTableMeta<?> table, final @Nullable String safeTableAlias
            , final _SqlContext context, final boolean firstPredicate) {

        final FieldMeta<?> field = table.getField(_MetaBridge.VISIBLE);
        final Boolean visibleValue;
        visibleValue = context.visible().value;
        if (visibleValue == null) {
            return;
        }

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
     * @param context must be instance of {@link DomainDmlStmtContext}
     */
    protected final void childDomainCteWhereClause(final List<_Predicate> predicateList,
                                                   final _DmlContext context) {

        final DomainDmlStmtContext childContext = (DomainDmlStmtContext) context;
        assert childContext.parentContext() != null;

        final int predicateCount = predicateList.size();
        if (predicateCount == 0) {
            throw _Exceptions.noWhereClause(context);
        }

        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) childContext.targetTable;

        final StringBuilder sqlBuilder = childContext.sqlBuilder();
        sqlBuilder.append(_Constant.SPACE_WHERE);

        childContext.appendField(childTable.id());
        sqlBuilder.append(_Constant.SPACE_EQUAL);
        childContext.appendField(childTable.parentMeta().id());

        for (int i = 0; i < predicateCount; i++) {
            sqlBuilder.append(_Constant.SPACE_AND);
            predicateList.get(i).appendSql(childContext);
        }

    }

    /**
     * @return the number of appending visible
     * @see #parseStandardQuery(_StandardQuery, _SimpleQueryContext)
     * @see #queryWhereClause(List, List, _MultiTableStmtContext)
     */
    protected final int multiTableVisible(final List<_TabularBock> blockList, final _MultiTableStmtContext context
            , final boolean firstPredicate) {
        TabularItem tableItem;
        String safeTableAlias;
        SingleTableMeta<?> table;
        int count = 0;
        for (_TabularBock block : blockList) {
            tableItem = block.tableItem();
            if (tableItem instanceof _NestedItems) {
                count += multiTableVisible(((_NestedItems) tableItem).tableBlockList(), context,
                        firstPredicate && count == 0);
                continue;
            }

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
        return count;
    }

    protected final void appendParentVisible(final _SingleTableContext childContext) {
        assert this.childUpdateMode == _ChildUpdateMode.WITH_ID;

        final _DmlContext parentContext = ((NarrowDmlContext) childContext).parentContext();
        assert parentContext != null;
        // child WHERE must have predicate
        final StringBuilder sqlBuilder;
        sqlBuilder = childContext.sqlBuilder().append(_Constant.SPACE_AND);

        assert parentContext.sqlBuilder() == sqlBuilder;

        final ParentTableMeta<?> parentTable;
        parentTable = ((ChildTableMeta<?>) childContext.domainTable()).parentMeta();
        ((DomainDmlStmtContext) childContext).parentColumnFromSubQuery(parentTable.getField(_MetaBridge.VISIBLE));

        final Boolean visibleValue;
        visibleValue = ((DomainDmlStmtContext) childContext).visible.value;
        if (visibleValue != null) {
            sqlBuilder.append(_Constant.SPACE_EQUAL_SPACE)
                    .append(visibleValue ? BooleanType.TRUE : BooleanType.FALSE);
        }

    }


    /**
     * @see #singleTableSetClause(List, _SingleUpdateContext)
     * @see #multiTableSetClause(_MultiUpdate, _MultiUpdateContext)
     * @see #parseDomainParentUpdateWithId(_DomainUpdate, _Predicate, DomainUpdateContext)
     */
    protected final void appendUpdateTimeAndVersion(final SingleTableMeta<?> table
            , final @Nullable String safeTableAlias, final _PrimaryContext context, final boolean firstItem) {

        FieldMeta<?> field;
        field = table.getField(_MetaBridge.UPDATE_TIME);

        final Class<?> javaType = field.javaType();
        final Temporal updateTimeValue;
        if (javaType == LocalDateTime.class) {
            updateTimeValue = LocalDateTime.now();
        } else if (javaType == OffsetDateTime.class) {
            updateTimeValue = OffsetDateTime.now(this.mappingEnv.databaseZoneOffset());
        } else if (javaType == ZonedDateTime.class) {
            updateTimeValue = ZonedDateTime.now(this.mappingEnv.databaseZoneOffset());
        } else {
            // FieldMeta no bug,never here
            throw _Exceptions.dontSupportJavaType(field, javaType);
        }

        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();
        if (firstItem) {
            sqlBuilder.append(_Constant.SPACE_SET_SPACE);
        } else {
            sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
        }

        if (safeTableAlias != null && this.setClauseTableAlias) {
            sqlBuilder.append(safeTableAlias)
                    .append(_Constant.POINT);
        }
        this.safeObjectName(field, sqlBuilder)
                .append(_Constant.SPACE_EQUAL);

        if (context instanceof InsertContext) {
            final InsertContext insertContext = (InsertContext) context;
            insertContext.appendInsertValue(insertContext.literalMode, field, updateTimeValue);
        } else if (context.hasParam()) {
            context.appendParam(SingleParam.build(field, updateTimeValue));
        } else {
            sqlBuilder.append(_Constant.SPACE);
            this.literal(field, updateTimeValue, sqlBuilder);
        }

        if ((field = table.tryGetField(_MetaBridge.VERSION)) != null) {
            final String versionColumnName;
            versionColumnName = this.safeObjectName(field);
            sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);

            if (safeTableAlias != null && this.setClauseTableAlias) {
                sqlBuilder.append(safeTableAlias)
                        .append(_Constant.POINT);
            }
            sqlBuilder.append(versionColumnName)
                    .append(_Constant.SPACE_EQUAL_SPACE);
            if (safeTableAlias != null) {  // no setClauseTableAlias
                sqlBuilder.append(safeTableAlias)
                        .append(_Constant.POINT);
            }
            sqlBuilder.append(versionColumnName)
                    .append(" + 1");

        }

    }


    /*-------------------below package method -------------------*/



    /*################################## blow private method ##################################*/


    /**
     * @see #handleInsert(_SqlContext, InsertStatement, Visible)
     */
    private _ValueInsertContext handleDomainInsert(final @Nullable _SqlContext outerContext
            , final _Insert._DomainInsert insert, final Visible visible) {
        final boolean standardStmt = insert instanceof StandardInsert;
        final _ValueInsertContext context;
        if (insert instanceof _Insert._ChildDomainInsert) {

            final _Insert._ChildDomainInsert childStmt = (_Insert._ChildDomainInsert) insert;
            final _Insert._DomainInsert parentStmt = childStmt.parentStmt();
            checkParentStmt(parentStmt, (ChildTableMeta<?>) childStmt.table());

            final DomainInsertContext parentContext;
            parentContext = DomainInsertContext.forParent(outerContext, childStmt, this, visible);
            if (standardStmt) {
                this.parseStandardValuesInsert(parentContext);
            } else {
                this.parseValuesInsert(parentContext, parentStmt);
            }

            context = DomainInsertContext.forChild(outerContext, childStmt, parentContext);
            if (standardStmt) {
                this.parseStandardValuesInsert(context);
            } else {
                this.parseValuesInsert(context, childStmt);
            }
        } else {
            context = DomainInsertContext.forSingle(outerContext, insert, this, visible);
            if (standardStmt) {
                this.parseStandardValuesInsert(context);
            } else {
                this.parseValuesInsert(context, insert);
            }
        }
        return context;
    }


    /**
     * @see #handleInsert(_SqlContext, InsertStatement, Visible)
     */
    private _ValueInsertContext handleValueInsert(final @Nullable _SqlContext outerContext
            , final _Insert._ValuesInsert insert, final Visible visible) {
        final boolean standardStmt = insert instanceof StandardInsert;
        final _ValueInsertContext context;
        if (insert instanceof _Insert._ChildValuesInsert) {

            final _Insert._ChildValuesInsert childStmt = (_Insert._ChildValuesInsert) insert;
            final _Insert._ValuesInsert parentStmt = childStmt.parentStmt();
            checkParentStmt(parentStmt, (ChildTableMeta<?>) childStmt.table());

            final ValuesInsertContext parentContext;
            parentContext = ValuesInsertContext.forParent(outerContext, childStmt, this, visible);
            if (standardStmt) {
                this.parseStandardValuesInsert(parentContext);
            } else {
                this.parseValuesInsert(parentContext, parentStmt);
            }

            context = ValuesInsertContext.forChild(outerContext, childStmt, parentContext);
            if (standardStmt) {
                this.parseStandardValuesInsert(context);
            } else {
                this.parseValuesInsert(context, childStmt);
            }
        } else {
            context = ValuesInsertContext.forSingle(outerContext, insert, this, visible);
            if (standardStmt) {
                this.parseStandardValuesInsert(context);
            } else {
                this.parseValuesInsert(context, insert);
            }
        }
        return context;
    }

    /**
     * @see #handleInsert(_SqlContext, InsertStatement, Visible)
     */
    private _AssignmentInsertContext handleAssignmentInsert(final @Nullable _SqlContext outerContext
            , final _Insert._AssignmentInsert insert, final Visible visible) {
        final _AssignmentInsertContext context;
        if (insert instanceof _Insert._ChildAssignmentInsert) {

            final _Insert._ChildAssignmentInsert childStmt = (_Insert._ChildAssignmentInsert) insert;
            final _Insert._AssignmentInsert parentStmt = childStmt.parentStmt();
            checkParentStmt(parentStmt, (ChildTableMeta<?>) childStmt.table());

            final AssignmentInsertContext parentContext;
            parentContext = AssignmentInsertContext.forParent(outerContext, childStmt, this, visible);
            this.parseAssignmentInsert(parentContext, parentStmt);

            context = AssignmentInsertContext.forChild(outerContext, childStmt, parentContext);
            this.parseAssignmentInsert(context, childStmt);

        } else {
            context = AssignmentInsertContext.forSingle(outerContext, insert, this, visible);
            this.parseAssignmentInsert(context, insert);
        }
        return context;
    }

    /**
     * @see #handleInsert(_SqlContext, InsertStatement, Visible)
     */
    private _QueryInsertContext handleQueryInsert(final @Nullable _SqlContext outerContext
            , final _Insert._QueryInsert insert, final Visible visible) {
        final boolean standardStmt = insert instanceof StandardInsert;
        final _QueryInsertContext context;
        if (insert instanceof _Insert._ChildQueryInsert) {

            final _Insert._ChildQueryInsert childStmt = (_Insert._ChildQueryInsert) insert;

            final QueryInsertContext parentContext;
            parentContext = QueryInsertContext.forParent(outerContext, childStmt, this, visible);
            if (standardStmt) {
                this.parseStandardQueryInsert(parentContext);
            } else {
                this.parseQueryInsert(parentContext, childStmt.parentStmt());
            }

            context = QueryInsertContext.forChild(outerContext, childStmt, parentContext);
            if (standardStmt) {
                this.parseStandardQueryInsert(context);
            } else {
                this.parseQueryInsert(context, childStmt);
            }
        } else {
            context = QueryInsertContext.forSingle(outerContext, insert, this, visible);
            if (standardStmt) {
                this.parseStandardQueryInsert(context);
            } else {
                this.parseQueryInsert(context, insert);
            }
        }
        return context;
    }

    /**
     * @see #select(Select, Visible)
     * @see #handleQuery(Query, _SqlContext)
     */
    private _SelectContext handleSelect(final @Nullable _SqlContext outerContext, final Select stmt
            , final Visible visible) {
        stmt.prepared();
        final _SelectContext context;
        if (stmt instanceof _Query) {
            context = SimpleSelectContext.create(outerContext, stmt, this, visible);
            if (stmt instanceof StandardQuery) {
                _SQLConsultant.assertStandardQuery(stmt);
                this.parseStandardQuery((_StandardQuery) stmt, (_SimpleQueryContext) context);
            } else {
                this.assertRowSet(stmt);
                this.parseSimpleQuery((_Query) stmt, (_SimpleQueryContext) context);
            }
        } else if (stmt instanceof _UnionRowSet) {
            _SQLConsultant.assertUnionRowSet(stmt);
            context = ParensSelectContext.create(outerContext, stmt, this, visible);
            final _UnionRowSet union = (_UnionRowSet) stmt;
            this.handleQuery((Query) union.leftRowSet(), context);
            context.sqlBuilder().append(union.unionType().render());
            this.handleRowSet(union.rightRowSet(), context);
        } else if (stmt instanceof _ParensRowSet) {
            if (stmt instanceof StandardQuery) {
                _SQLConsultant.assertStandardQuery(stmt);
            } else {
                this.assertRowSet(stmt);
            }
            final _ParenRowSetContext parenContext;
            if (stmt instanceof _Statement._WithClauseSpec
                    && ((_Statement._WithClauseSpec) stmt).cteList().size() > 0) {
                context = ParensSelectContext.create(outerContext, stmt, this, visible);

                this.parseWithClause((_Statement._WithClauseSpec) stmt, context);
                parenContext = ParensSelectContext.create(context, stmt, this, visible);
            } else {
                parenContext = ParensSelectContext.create(outerContext, stmt, this, visible);
                context = (_SelectContext) parenContext;
            }
            this.handleParenRowSet(parenContext, (_ParensRowSet) stmt);
        } else {
            throw _Exceptions.unknownRowSetType(stmt);
        }
        return context;
    }

    /**
     * @see #handleSelect(_SqlContext, Select, Visible)
     * @see #handleQuery(Query, _SqlContext)
     */
    private void handleParenRowSet(final _ParenRowSetContext context, final _ParensRowSet parensRowSet) {
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder().append(_Constant.SPACE_LEFT_PAREN);
        final RowSet innerRowSet;
        innerRowSet = parensRowSet.innerRowSet();
        assert (parensRowSet instanceof PrimaryStatement) == (innerRowSet instanceof PrimaryStatement);
        this.handleRowSet(innerRowSet, context);
        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        if (parensRowSet instanceof StandardQuery) {
            _SQLConsultant.assertStandardQuery((StandardQuery) parensRowSet);
            this.parseStandardParensQuery(parensRowSet, context);
        } else {
            this.assertRowSet(parensRowSet);
            this.parseClauseAfterRightParen(parensRowSet, context);
        }

    }

    /**
     * @see #values(Values, Visible)
     */
    private _ValuesContext handleValues(final @Nullable _SqlContext outerContext, final Values stmt
            , final Visible visible) {
        stmt.prepared();
        assertRowSet(stmt);
        final _ValuesContext context;
        context = ValuesContext.create(outerContext, stmt, this, visible);
        if (stmt instanceof _ValuesQuery) {
            this.parseSimpleValues((_ValuesQuery) stmt, context);
        } else if (stmt instanceof _UnionRowSet) {
            final _UnionRowSet union = (_UnionRowSet) stmt;
            this.handleValuesQuery((Values) union.leftRowSet(), context);
            context.sqlBuilder().append(union.unionType().render());
            this.handleRowSet(union.rightRowSet(), context);
        } else {
            assert stmt instanceof _ParensRowSet;
            final _ParensRowSet parensRowSet = (_ParensRowSet) stmt;
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder().append(_Constant.SPACE_LEFT_PAREN);
            this.handleRowSet(parensRowSet.innerRowSet(), context);
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }
        return context;
    }


    /**
     * @see #update(UpdateStatement, Visible)
     */
    private <T> T handleUpdate(final @Nullable _SqlContext outerContext, final UpdateStatement stmt
            , final Visible visible, final Function<_UpdateContext, T> function) {
        stmt.prepared();
        final _UpdateContext context;
        if (stmt instanceof _MultiUpdate) {
            assertUpdate(stmt);
            context = MultiUpdateContext.create(outerContext, (_MultiUpdate) stmt, this, visible);
            parseMultiUpdate((_MultiUpdate) stmt, (MultiUpdateContext) context);
            if (outerContext instanceof _MultiStatementContext && stmt instanceof _BatchDml) {
                multiStmtBatch((_MultiUpdate) stmt, (MultiUpdateContext) context, this::parseMultiUpdate);
            }
        } else if (!(stmt instanceof _SingleUpdate)) {
            throw _Exceptions.unknownStatement(stmt, this.dialect);
        } else if (stmt instanceof _DomainUpdate) {
            _SQLConsultant.assertStandardUpdate(stmt);
            context = this.handleDomainUpdate(outerContext, (_DomainUpdate) stmt, visible);
        } else if (stmt instanceof StandardUpdate) {
            _SQLConsultant.assertStandardUpdate(stmt);
            context = SingleUpdateContext.create(outerContext, (_SingleUpdate) stmt, this, visible);
            this.parseStandardSingleUpdate((_SingleUpdate) stmt, (_SingleUpdateContext) context);
            if (outerContext instanceof _MultiStatementContext && stmt instanceof _BatchDml) {
                multiStmtBatch((_SingleUpdate) stmt, (_SingleUpdateContext) context, this::parseStandardSingleUpdate);
            }
        } else if (stmt instanceof _SingleUpdate._ChildUpdate) {
            assert outerContext == null; // now don't support outer context and multi-statement
            assertUpdate(stmt);
            final _SingleUpdate._ChildUpdate childStmt = (_SingleUpdate._ChildUpdate) stmt;
            if (stmt instanceof _Statement._JoinableStatement) {
                final SingleJoinableUpdateContext parentContext;
                parentContext = SingleJoinableUpdateContext.forParent(childStmt, this, visible);
                context = SingleJoinableUpdateContext.forChild(childStmt, parentContext);
            } else {
                final SingleUpdateContext parentContext;
                parentContext = SingleUpdateContext.forParent(childStmt, this, visible);
                context = SingleUpdateContext.forChild(childStmt, parentContext);
            }
            this.parseSingleUpdate((_SingleUpdate) stmt, (_SingleUpdateContext) context);
        } else if (stmt instanceof _Statement._JoinableStatement) {
            assertUpdate(stmt);
            context = SingleJoinableUpdateContext.create(outerContext, (_SingleUpdate) stmt, this, visible);
            this.parseSingleUpdate((_SingleUpdate) stmt, (_SingleUpdateContext) context);
            if (outerContext instanceof _MultiStatementContext && stmt instanceof _BatchDml) {
                multiStmtBatch((_SingleUpdate) stmt, (_SingleUpdateContext) context, this::parseSingleUpdate);
            }
        } else {
            assertUpdate(stmt);
            context = SingleUpdateContext.create(outerContext, (_SingleUpdate) stmt, this, visible);
            this.parseSingleUpdate((_SingleUpdate) stmt, (_SingleUpdateContext) context);
            if (outerContext instanceof _MultiStatementContext && stmt instanceof _BatchDml) {
                multiStmtBatch((_SingleUpdate) stmt, (_SingleUpdateContext) context, this::parseSingleUpdate);
            }
        }
        return function.apply(context);
    }


    /**
     * @see #handleUpdate(_SqlContext, UpdateStatement, Visible, Function)
     */
    private _UpdateContext handleDomainUpdate(final @Nullable _SqlContext outerContext, final _DomainUpdate stmt
            , final Visible visible) {
        final _UpdateContext context;
        final _ChildUpdateMode mode = this.childUpdateMode;
        if (!(stmt.table() instanceof ChildTableMeta) || stmt.childItemPairList().size() == 0) {
            context = DomainUpdateContext.forSingle(outerContext, stmt, this, visible);
            this.parseStandardSingleUpdate(stmt, (_SingleUpdateContext) context);
            if (outerContext instanceof _MultiStatementContext && stmt instanceof _BatchDml) {
                multiStmtBatch(stmt, (_SingleUpdateContext) context, this::parseStandardSingleUpdate);
            }
        } else if (mode == _ChildUpdateMode.MULTI_TABLE) {
            context = MultiUpdateContext.forChild(outerContext, stmt, this, visible);
            this.parseDomainChildUpdate(stmt, context);
            if (outerContext instanceof _MultiStatementContext && stmt instanceof _BatchDml) {
                multiStmtBatch(stmt, context, this::parseDomainChildUpdate);
            }
        } else if (mode == _ChildUpdateMode.CTE) {
            final DomainUpdateContext primaryContext;
            primaryContext = DomainUpdateContext.forSingle(outerContext, stmt, this, visible);
            context = DomainUpdateContext.forChild(stmt, primaryContext);
            this.parseDomainChildUpdate(stmt, context);
            if (outerContext instanceof _MultiStatementContext && stmt instanceof _BatchDml) {
                multiStmtBatch(stmt, context, this::parseDomainChildUpdate);
            }
        } else if (mode == _ChildUpdateMode.WITH_ID) {
            assert outerContext == null; //now don't support multi statement.
            final DomainUpdateContext parentContext;
            parentContext = DomainUpdateContext.forSingle(null, stmt, this, visible);
            context = DomainUpdateContext.forChild(stmt, parentContext);
            assert parentContext.domainTable == ((DomainUpdateContext) context).domainTable;
            assert parentContext.targetTable instanceof ParentTableMeta;
            assert ((DomainUpdateContext) context).targetTable == parentContext.domainTable;

            final _Predicate idPredicate;
            idPredicate = this.parseDomainChildUpdateWithId(stmt, (DomainUpdateContext) context);
            this.parseDomainParentUpdateWithId(stmt, idPredicate, parentContext);
        } else {
            throw _Exceptions.unexpectedEnum(mode);
        }
        return context;
    }


    /**
     * @see #handleDomainUpdate(_SqlContext, _DomainUpdate, Visible)
     * @see #parseDomainParentUpdateWithId(_DomainUpdate, _Predicate, DomainUpdateContext)
     * @see _ChildUpdateMode#WITH_ID
     */
    private _Predicate parseDomainChildUpdateWithId(final _DomainUpdate stmt, final DomainUpdateContext context) {
        assert this.childUpdateMode == _ChildUpdateMode.WITH_ID
                && context.parentContext != null;

        //1. append child common
        this.appendDomainUpdateCommon(context);
        //2. append child SET clause
        this.singleTableSetClause(stmt.childItemPairList(), context);
        //3. append child WHERE clause
        final List<_Predicate> whereList;
        whereList = stmt.wherePredicateList();
        //check first predicate
        final _Predicate firstPredicate;
        firstPredicate = whereList.get(0).getIdPredicate();
        if (firstPredicate == null) {
            throw _Exceptions.notFondIdPredicate(this.dialect);
        }
        this.dmlWhereClause(whereList, context);
        //3.1 append parent condition field
        context.appendConditionFields();

        //3.2 append parent visible
        final FieldMeta<?> visibleField;
        final Boolean visibleValue;
        final ParentTableMeta<?> parentTable;
        parentTable = ((ChildTableMeta<?>) context.targetTable).parentMeta();

        if ((visibleValue = context.visible.value) != null
                && (visibleField = parentTable.tryGetField(_MetaBridge.VISIBLE)) != null) {
            final StringBuilder sqlBuilder = context.sqlBuilder;
            sqlBuilder.append(_Constant.SPACE_AND);
            context.parentColumnFromSubQuery(visibleField);
            sqlBuilder.append(_Constant.SPACE_EQUAL_SPACE);
            this.literal(visibleField.mappingType(), visibleValue, sqlBuilder);
        }
        return firstPredicate;
    }

    /**
     * @see #handleDomainUpdate(_SqlContext, _DomainUpdate, Visible)
     * @see #parseDomainChildUpdateWithId(_DomainUpdate, DomainUpdateContext)
     * @see _ChildUpdateMode#WITH_ID
     */
    private void parseDomainParentUpdateWithId(final _DomainUpdate stmt, final _Predicate idPredicate
            , final DomainUpdateContext context) {

        assert idPredicate.getIdPredicate() != null
                && context.parentContext == null
                && context.domainTable instanceof ChildTableMeta;

        final ParentTableMeta<?> parentTable;
        parentTable = (ParentTableMeta<?>) context.targetTable;
        //1. append parent common
        this.appendDomainUpdateCommon(context);
        //2. append parent SET clause
        final List<_ItemPair> parentItemList;
        parentItemList = stmt.itemPairList();

        final String safeTableAlias;
        if (this.supportSingleUpdateAlias) {
            safeTableAlias = context.safeTableAlias;
        } else {
            safeTableAlias = null;
        }
        if (parentItemList.size() > 0) {
            this.singleTableSetClause(parentItemList, context);
        } else {
            this.appendUpdateTimeAndVersion(parentTable, safeTableAlias, context, true);
        }
        //3. append parent WHERE clause
        this.dmlWhereClause(Collections.singletonList(idPredicate), context);
        //3.1 append parent condition field
        context.appendConditionFields();
        //3.2 append discriminator
        this.discriminator(context.domainTable, safeTableAlias, context);
    }


    /**
     * @see #parseDomainChildUpdateWithId(_DomainUpdate, DomainUpdateContext)
     * @see #parseDomainParentUpdateWithId(_DomainUpdate, _Predicate, DomainUpdateContext)
     */
    private void appendDomainUpdateCommon(final DomainUpdateContext context) {
        //1. append  space if need
        final StringBuilder sqlBuilder;
        if ((sqlBuilder = context.sqlBuilder).length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
        // 2. append  table UPDATE key word
        sqlBuilder.append(_Constant.UPDATE_SPACE);
        this.safeObjectName(context.targetTable, sqlBuilder);

        //3. append table alias
        if (this.supportSingleUpdateAlias) {
            if (this.singleDmlAliasAfterAs) {
                sqlBuilder.append(_Constant.SPACE_AS_SPACE);
            } else {
                sqlBuilder.append(_Constant.SPACE);
            }
            sqlBuilder.append(context.safeTableAlias);
        }

    }

    /**
     * @see #delete(DeleteStatement, Visible)
     */
    private <T> T handleDelete(final @Nullable _SqlContext outerContext, final DeleteStatement stmt
            , final Visible visible, final Function<_DeleteContext, T> function) {
        stmt.prepared();
        final _DeleteContext context;
        if (stmt instanceof _MultiDelete) {
            assertDelete(stmt);
            context = MultiDeleteContext.create(outerContext, (_MultiDelete) stmt, this, visible);
            this.parseMultiDelete((_MultiDelete) stmt, (_MultiDeleteContext) context);
            if (outerContext instanceof _MultiStatementContext && stmt instanceof _BatchDml) {
                multiStmtBatch((_MultiDelete) stmt, (_MultiDeleteContext) context, this::parseMultiDelete);
            }
        } else if (!(stmt instanceof _SingleDelete)) {
            throw _Exceptions.unknownStatement(stmt, this.dialect);
        } else if (stmt instanceof _DomainDelete) {
            _SQLConsultant.assertStandardDelete(stmt);
            context = this.handleDomainDelete(outerContext, (_DomainDelete) stmt, visible);
        } else if (stmt instanceof StandardDelete) {
            _SQLConsultant.assertStandardDelete(stmt);
            context = SingleDeleteContext.create(outerContext, (_SingleDelete) stmt, this, visible);
            this.parseStandardSingleDelete((_SingleDelete) stmt, (_SingleDeleteContext) context);
            if (outerContext instanceof _MultiStatementContext && stmt instanceof _BatchDml) {
                multiStmtBatch((_SingleDelete) stmt, (_SingleDeleteContext) context, this::parseStandardSingleDelete);
            }
        } else if (stmt instanceof _SingleDelete._ChildDelete) {
            assert outerContext == null; // now don't support outer context and multi-statement
            assertDelete(stmt);
            final _SingleDelete._ChildDelete childStmt = (_SingleDelete._ChildDelete) stmt;
            if (stmt instanceof _Statement._JoinableStatement) {
                final SingleJoinableDeleteContext parentContext;
                parentContext = SingleJoinableDeleteContext.forParent(childStmt, this, visible);
                context = SingleJoinableDeleteContext.forChild(childStmt, parentContext);
            } else {
                final SingleDeleteContext parentContext;
                parentContext = SingleDeleteContext.forParent(childStmt, this, visible);
                context = SingleDeleteContext.forChild(childStmt, parentContext);
            }
            this.parseSingleDelete((_SingleDelete) stmt, (_SingleDeleteContext) context);
        } else if (stmt instanceof _Statement._JoinableStatement) {
            assertDelete(stmt);
            context = SingleJoinableDeleteContext.create(outerContext, (_SingleDelete) stmt, this, visible);
            this.parseSingleDelete((_SingleDelete) stmt, (_SingleDeleteContext) context);
            if (outerContext instanceof _MultiStatementContext && stmt instanceof _BatchDml) {
                multiStmtBatch((_SingleDelete) stmt, (_SingleDeleteContext) context, this::parseSingleDelete);
            }
        } else {
            assertDelete(stmt);
            context = SingleDeleteContext.create(outerContext, (_SingleDelete) stmt, this, visible);
            this.parseSingleDelete((_SingleDelete) stmt, (_SingleDeleteContext) context);
            if (outerContext instanceof _MultiStatementContext && stmt instanceof _BatchDml) {
                multiStmtBatch((_SingleDelete) stmt, (_SingleDeleteContext) context, this::parseSingleDelete);
            }
        }
        return function.apply(context);
    }

    /**
     * @see #handleDelete(_SqlContext, DeleteStatement, Visible, Function)
     */
    private _DeleteContext handleDomainDelete(final @Nullable _SqlContext outerContext, final _DomainDelete stmt
            , final Visible visible) {
        final _DeleteContext context;
        final _ChildUpdateMode mode = this.childUpdateMode;
        if (!(stmt.table() instanceof ChildTableMeta)) {
            context = DomainDeleteContext.forSingle(outerContext, stmt, this, visible);
            this.parseStandardSingleDelete(stmt, (_SingleDeleteContext) context);
            if (outerContext instanceof _MultiStatementContext && stmt instanceof _BatchDml) {
                multiStmtBatch(stmt, (_SingleDeleteContext) context, this::parseStandardSingleDelete);
            }
        } else if (mode == _ChildUpdateMode.MULTI_TABLE) {
            context = MultiDeleteContext.forChild(outerContext, stmt, this, visible);
            this.parseDomainChildDelete(stmt, context);
            if (outerContext instanceof _MultiStatementContext && stmt instanceof _BatchDml) {
                multiStmtBatch(stmt, context, this::parseDomainChildDelete);
            }
        } else if (mode == _ChildUpdateMode.CTE) {
            final DomainDeleteContext primaryContext;
            primaryContext = DomainDeleteContext.forSingle(outerContext, stmt, this, visible);
            context = DomainDeleteContext.forChild(stmt, primaryContext);
            this.parseDomainChildDelete(stmt, context);
            if (outerContext instanceof _MultiStatementContext && stmt instanceof _BatchDml) {
                multiStmtBatch(stmt, context, this::parseDomainChildDelete);
            }
        } else if (mode == _ChildUpdateMode.WITH_ID) {
            assert outerContext == null; //now don't support multi statement.
            final DomainDeleteContext parentContext;
            parentContext = DomainDeleteContext.forSingle(null, stmt, this, visible);
            context = DomainDeleteContext.forChild(stmt, parentContext);
            assert parentContext.domainTable == ((DomainDeleteContext) context).domainTable;
            assert parentContext.targetTable instanceof ParentTableMeta;
            assert ((DomainDeleteContext) context).targetTable == parentContext.domainTable;

            final _Predicate idPredicate;
            idPredicate = this.parseDomainChildDeleteWithId(stmt, (DomainDeleteContext) context);
            this.parseDomainParentDeleteWithId(idPredicate, parentContext);
        } else {
            throw _Exceptions.unexpectedEnum(mode);
        }
        return context;
    }

    /**
     * @see #handleDomainDelete(_SqlContext, _DomainDelete, Visible)
     */
    private _Predicate parseDomainChildDeleteWithId(final _DomainDelete stmt, final DomainDeleteContext childContext) {
        assert this.childUpdateMode == _ChildUpdateMode.WITH_ID;
        assert childContext.parentContext != null;
        final ParentTableMeta<?> parentTable;
        parentTable = ((ChildTableMeta<?>) childContext.domainTable).parentMeta();

        //1. append child space if need
        final StringBuilder childBuilder;
        if ((childBuilder = childContext.sqlBuilder).length() > 0) {
            childBuilder.append(_Constant.SPACE);
        }
        // 2. append child table DELETE FROM key word
        childBuilder.append(_Constant.DELETE_FROM_SPACE);
        this.safeObjectName(childContext.targetTable, childBuilder);
        //3. append table alias
        if (this.supportSingleDeleteAlias) {
            if (this.singleDmlAliasAfterAs) {
                childBuilder.append(_Constant.SPACE_AS_SPACE);
            } else {
                childBuilder.append(_Constant.SPACE);
            }
            childBuilder.append(childContext.safeTableAlias);
        }
        //4. append child WHERE clause
        final List<_Predicate> whereList;
        whereList = stmt.wherePredicateList();
        //check first predicate
        final _Predicate firstPredicate;
        if ((firstPredicate = whereList.get(0).getIdPredicate()) == null) {
            throw _Exceptions.notFondIdPredicate(this.dialect);
        }
        this.dmlWhereClause(whereList, childContext);
        //4.2 append parent visible
        final FieldMeta<?> visibleField;
        final Boolean visibleValue;
        if ((visibleValue = childContext.visible.value) != null
                && (visibleField = parentTable.tryGetField(_MetaBridge.VISIBLE)) != null) {
            childBuilder.append(_Constant.SPACE_AND);
            childContext.parentColumnFromSubQuery(visibleField);
            childBuilder.append(_Constant.SPACE_EQUAL_SPACE);
            this.literal(visibleField.mappingType(), visibleValue, childBuilder);
        }

        return firstPredicate;

    }

    /**
     * @see #handleDomainDelete(_SqlContext, _DomainDelete, Visible)
     */
    private void parseDomainParentDeleteWithId(final _Predicate idPredicate, final DomainDeleteContext context) {
        assert idPredicate.getIdPredicate() != null
                && context.parentContext == null
                && context.targetTable instanceof ParentTableMeta
                && context.domainTable instanceof ChildTableMeta;

        //1. append parent space if need
        final StringBuilder sqlBuilder;
        if ((sqlBuilder = context.sqlBuilder).length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
        // 2. append parent table DELETE FROM key word
        sqlBuilder.append(_Constant.DELETE_FROM_SPACE);
        this.safeObjectName(context.targetTable, sqlBuilder);
        //3. append parent table alias

        final String safeTableAlias;
        if (this.supportSingleDeleteAlias) {
            safeTableAlias = context.safeTableAlias;
        } else {
            safeTableAlias = null;
        }
        if (safeTableAlias != null) {
            if (this.singleDmlAliasAfterAs) {
                sqlBuilder.append(_Constant.SPACE_AS_SPACE);
            } else {
                sqlBuilder.append(_Constant.SPACE);
            }
            sqlBuilder.append(safeTableAlias);
        }

        //4. append parent WHERE clause
        this.dmlWhereClause(Collections.singletonList(idPredicate), context);

        //4.1 append discriminator
        this.discriminator(context.domainTable, safeTableAlias, context);
    }


    /**
     * @see #parseStandardQuery(_StandardQuery, _SimpleQueryContext)
     */
    private void standardSelectClause(final List<? extends SQLWords> modifierList, final StringBuilder sqlBuilder) {
        switch (modifierList.size()) {
            case 0:
                //no-op
                break;
            case 1: {
                final SQLWords modifier = modifierList.get(0);
                if (!(modifier == SQLs.DISTINCT || modifier == SQLs.ALL)) {
                    String m = String.format("Standard query api support only %s or %s", SQLs.DISTINCT, SQLs.ALL);
                    throw new CriteriaException(m);
                }
                sqlBuilder.append(modifier.render());
            }
            break;
            default:
                throw new CriteriaException("Standard query api support one modifier.");
        }

    }


    /**
     * @see #handleDomainInsert(_SqlContext, _Insert._DomainInsert, Visible)
     * @see #handleValueInsert(_SqlContext, _Insert._ValuesInsert, Visible)
     */
    private void parseStandardValuesInsert(final _ValueInsertContext context) {
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
     * @see #handleQueryInsert(_SqlContext, _Insert._QueryInsert, Visible)
     */
    private void parseStandardQueryInsert(final _QueryInsertContext context) {
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
     * @see #handleDelete(_SqlContext, DeleteStatement, Visible, Function)
     * @see #handleDomainDelete(_SqlContext, _DomainDelete, Visible)
     */
    private void parseStandardSingleDelete(final _SingleDelete stmt, final _SingleDeleteContext context) {
        assert stmt instanceof StandardDelete;

        final SingleTableMeta<?> targetTable;
        targetTable = (SingleTableMeta<?>) context.targetTable();

        final StringBuilder sqlBuilder;
        if ((sqlBuilder = context.sqlBuilder()).length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }

        //1. DELETE key words
        sqlBuilder.append(_Constant.DELETE_FROM_SPACE);
        this.safeObjectName(targetTable, sqlBuilder);

        //2. table name
        final String safeTableAlias;
        if (this.supportSingleDeleteAlias) {
            if (this.singleDmlAliasAfterAs) {
                sqlBuilder.append(_Constant.SPACE_AS_SPACE);
            } else {
                sqlBuilder.append(_Constant.SPACE);
            }
            safeTableAlias = context.safeTargetTableAlias();
            sqlBuilder.append(safeTableAlias);
        } else {
            safeTableAlias = null;
        }
        //3. WHERE clause
        this.dmlWhereClause(stmt.wherePredicateList(), context);
        //3.1 append discriminator
        if (targetTable instanceof ParentTableMeta) {
            this.discriminator(targetTable, safeTableAlias, context);
        }
        //3.2 append visible
        if (targetTable.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(targetTable, safeTableAlias, context, false);
        }
    }


    /**
     * @see #handleUpdate(_SqlContext, UpdateStatement, Visible, Function)
     * @see #handleDomainUpdate(_SqlContext, _DomainUpdate, Visible)
     */
    private void parseStandardSingleUpdate(final _SingleUpdate stmt, final _SingleUpdateContext context) {
        assert stmt instanceof StandardUpdate;
        assert !(stmt instanceof _DomainUpdate) || ((_DomainUpdate) stmt).childItemPairList().size() == 0;

        final SingleTableMeta<?> targetTable;
        targetTable = (SingleTableMeta<?>) context.targetTable();

        final StringBuilder sqlBuilder;
        if ((sqlBuilder = context.sqlBuilder()).length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
        // 1. UPDATE clause
        sqlBuilder.append(_Constant.UPDATE_SPACE);
        this.safeObjectName(targetTable, sqlBuilder);

        //1.2 updateTable alias
        final String safeTableAlias;
        if (this.supportSingleUpdateAlias) {
            safeTableAlias = context.safeTargetTableAlias();
        } else {
            safeTableAlias = null;
        }
        if (safeTableAlias != null) {
            if (this.singleDmlAliasAfterAs) {
                sqlBuilder.append(_Constant.SPACE_AS_SPACE);
            } else {
                sqlBuilder.append(_Constant.SPACE);
            }
            sqlBuilder.append(safeTableAlias);
        }

        //2. set clause
        this.singleTableSetClause(stmt.itemPairList(), context);
        //3. where clause
        this.dmlWhereClause(stmt.wherePredicateList(), context);

        //3.1 append condition update field
        context.appendConditionFields();

        //3.2 append discriminator predicate
        this.discriminator(context.domainTable(), safeTableAlias, context);

        //3.3 append visible
        if (targetTable.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(targetTable, safeTableAlias, context, false);
        }

    }


    /**
     * @see #handleSelect(_SqlContext, Select, Visible)
     * @see #handleSubQuery(SubQuery, _SqlContext)
     */
    private void parseStandardQuery(final _StandardQuery query, final _SimpleQueryContext context) {

        final StringBuilder builder;
        if ((builder = context.sqlBuilder()).length() > 0) {
            builder.append(_Constant.SPACE);
        }
        builder.append(_Constant.SELECT);
        //1. select clause
        this.standardSelectClause(query.modifierList(), builder);
        //2. select list clause
        this.selectListClause(context);
        //3. from clause
        final List<_TabularBock> blockList;
        blockList = query.tableBlockList();
        if (blockList.size() > 0) {
            context.sqlBuilder()
                    .append(_Constant.SPACE_FROM);
            this.standardTableReferences(blockList, context, false);
        }
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
        this.standardLimitClause(query.offsetExp(), query.rowCountExp(), context);

        //8. lock clause
        final SQLWords lock = query.lockMode();
        if (lock != null) {
            this.standardLockClause(lock, context);
        }

    }

    /**
     * @see #handleDomainInsert(_SqlContext, _Insert._DomainInsert, Visible)
     * @see #handleValueInsert(_SqlContext, _Insert._ValuesInsert, Visible)
     * @see #handleAssignmentInsert(_SqlContext, _Insert._AssignmentInsert, Visible)
     */
    private void checkParentStmt(_Insert parentStmt, ChildTableMeta<?> childTable) {
        if (parentStmt.table().id().generatorType() == GeneratorType.POST
                && parentStmt instanceof _Insert._SupportConflictClauseSpec
                && ((_Insert._SupportConflictClauseSpec) parentStmt).hasConflictAction()
                && this.childUpdateMode != _ChildUpdateMode.CTE) { // support RETURNING clause,could returning parent id
            throw _Exceptions.duplicateKeyAndPostIdInsert(childTable);
        }
    }


    /**
     * @see #insert(InsertStatement, Visible)
     */
    private Stmt createInsertStmt(final _InsertContext context) {
        final _InsertContext parentContext;
        parentContext = context.parentContext();
        final Stmt stmt;
        if (parentContext == null) {
            stmt = context.build();
        } else {
            stmt = Stmts.pair(parentContext.build(), context.build());
        }
        return stmt;
    }

    /**
     * @see #update(UpdateStatement, Visible)
     */
    private Stmt createUpdateStmt(final _UpdateContext context) {
        final _UpdateContext parentContext;
        final Stmt stmt;
        if (context instanceof _MultiUpdateContext || (parentContext = context.parentContext()) == null) {
            stmt = context.build();
        } else if (this.childUpdateMode == _ChildUpdateMode.CTE) {
            assert context instanceof DomainUpdateContext;
            assert parentContext.sqlBuilder() == context.sqlBuilder();
            stmt = context.build();
        } else {
            assert parentContext instanceof _SingleUpdateContext;
            final Stmt parentStmt, childStmt;
            parentStmt = parentContext.build();
            childStmt = context.build();
            if (childStmt instanceof SimpleStmt) {
                assert parentStmt instanceof SimpleStmt;
                stmt = Stmts.pair((SimpleStmt) childStmt, (SimpleStmt) parentStmt);
            } else {
                assert parentStmt instanceof BatchStmt && childStmt instanceof BatchStmt;
                stmt = Stmts.pairBatch((BatchStmt) childStmt, (BatchStmt) parentStmt);
            }
        }
        return stmt;
    }

    /**
     * @see #delete(DeleteStatement, Visible)
     */
    private Stmt createDeleteStmt(final _DeleteContext context) {
        final _DeleteContext parentContext;
        final Stmt stmt;
        if (context instanceof _MultiDeleteContext || (parentContext = context.parentContext()) == null) {
            stmt = context.build();
        } else if (this.childUpdateMode == _ChildUpdateMode.CTE) {
            assert context instanceof DomainDeleteContext;
            assert parentContext.sqlBuilder() == context.sqlBuilder();
            stmt = context.build();
        } else {
            assert parentContext instanceof _SingleDeleteContext;
            final Stmt parentStmt, childStmt;
            parentStmt = parentContext.build();
            childStmt = context.build();
            if (childStmt instanceof BatchStmt) {
                assert parentStmt instanceof BatchStmt;
                stmt = Stmts.pairBatch((BatchStmt) childStmt, (BatchStmt) parentStmt);
            } else {
                assert parentStmt instanceof SimpleStmt && childStmt instanceof SimpleStmt;
                stmt = Stmts.pair((SimpleStmt) childStmt, (SimpleStmt) parentStmt);
            }
        }
        return stmt;
    }

    /**
     * @see #dialectDml(DmlStatement, Visible)
     * @see #dialectDql(DqlStatement, Visible)
     */
    private Stmt createDialectStmt(_PrimaryContext context) {
        throw new UnsupportedOperationException();
    }


    protected static CriteriaException standardParserDontSupportDialect(Dialect dialect) {
        return new CriteriaException(String.format("standard parser[%s] don't support dialect api", dialect));
    }


    private static String nonBeautifySql(String sql) {
        //no-op
        return sql;
    }


    /**
     * @see #handleUpdate(_SqlContext, UpdateStatement, Visible, Function)
     * @see #handleDomainUpdate(_SqlContext, _DomainUpdate, Visible)
     * @see #handleDelete(_SqlContext, DeleteStatement, Visible, Function)
     * @see #handleDomainDelete(_SqlContext, _DomainDelete, Visible)
     */
    private static <S extends _Statement, C extends NarrowDmlContext> void multiStmtBatch(
            final S stmt, final C context, final BiConsumer<S, C> parserMethod) {
        if (context.hasParam()) {
            throw _Exceptions.multiStmtDontSupportParam();
        }
        assert stmt instanceof _BatchDml;
        final int paramSize;
        paramSize = ((_BatchDml) stmt).paramList().size();
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();
        for (int i = 1; i < paramSize; i++) { // from 1 ,not 0
            context.nextElement();
            sqlBuilder.append(_Constant.SPACE_SEMICOLON);
            parserMethod.accept(stmt, context);
        }
        assert context.currentIndex() == (paramSize - 1);
    }


}
