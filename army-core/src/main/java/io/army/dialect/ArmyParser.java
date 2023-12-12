package io.army.dialect;

import io.army.ArmyException;
import io.army.annotation.GeneratorType;
import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl._JoinType;
import io.army.criteria.impl._SQLConsultant;
import io.army.criteria.impl._UnionType;
import io.army.criteria.impl.inner.*;
import io.army.criteria.standard.StandardDelete;
import io.army.criteria.standard.StandardInsert;
import io.army.criteria.standard.StandardQuery;
import io.army.criteria.standard.StandardUpdate;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.env.NameMode;
import io.army.mapping.BooleanType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.schema._FieldResult;
import io.army.schema._SchemaResult;
import io.army.schema._TableResult;
import io.army.sqltype.DataType;
import io.army.stmt.*;
import io.army.util.*;

import javax.annotation.Nullable;
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
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 *
 * @since 1.0
 */
abstract class ArmyParser implements DialectParser {


    protected final Dialect dialect;

    protected final Database database;
    public final DialectEnv dialectEnv;

    protected final MappingEnv mappingEnv;

    protected final ServerMeta serverMeta;


    final boolean mockEnv;

    /**
     * a unmodified set
     */
    protected final Set<String> keyWordSet;

    protected final Map<String, Boolean> keyWordMap;


    protected final char identifierQuote;

    protected final boolean unrecognizedTypeAllowed;

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


    final boolean supportReturningClause;


    final ChildUpdateMode childUpdateMode;
    final FieldValueGenerator generator;

    final NameMode funcNameMode;

    /**
     * @see ArmyKey#TRUNCATED_TIME_TYPE
     */
    final boolean truncatedTimeType;

    private final String qualifiedSchemaName;
    private final NameMode tableNameMode;

    private final NameMode columnNameMode;

    private final boolean validateUnionType;

    private final boolean useObjectNameModeMethod;

    private final BiConsumer<String, Consumer<String>> beautifySqlFunc = this::beautifySql;

    ArmyParser(final DialectEnv dialectEnv, final Dialect dialect) {
        this.dialect = dialect; // first
        this.database = this.dialect.database();
        this.dialectEnv = dialectEnv;
        this.mappingEnv = dialectEnv.mappingEnv();

        this.serverMeta = this.mappingEnv.serverMeta();
        this.mockEnv = this.dialectEnv instanceof _MockDialects;

        assert this.serverMeta.serverDatabase().isCompatible(dialect);
        this.keyWordMap = _DialectUtils.createKeyWordMap(this.createKeyWordSet());

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
        this.supportReturningClause = this.isSupportReturningClause();

        this.supportRowAlias = this.isSupportRowAlias();
        this.validateUnionType = this.isValidateUnionType();
        this.useObjectNameModeMethod = this.isUseObjectNameModeMethod();

        this.keyWordSet = Collections.unmodifiableSet(this.createKeyWordSet());
        if (this.mockEnv) {
            this.generator = FieldValuesGenerators.mock();
        } else {
            this.generator = FieldValuesGenerators.create(dialectEnv.fieldGeneratorMap());
        }

        final ArmyEnvironment env;
        env = dialectEnv.environment();
        this.tableNameMode = env.getOrDefault(ArmyKey.TABLE_NAME_MODE);
        this.columnNameMode = env.getOrDefault(ArmyKey.COLUMN_NAME_MODE);

        this.funcNameMode = env.getOrDefault(ArmyKey.FUNC_NAME_MODE);
        this.truncatedTimeType = env.getOrDefault(ArmyKey.TRUNCATED_TIME_TYPE);
        this.qualifiedSchemaName = this.getQualifiedSchemaName(env, this.serverMeta);

        this.unrecognizedTypeAllowed = env.getOrDefault(ArmyKey.UNRECOGNIZED_TYPE_ALLOWED);
    }


    /**
     * @see ArmyParser#ArmyParser(DialectEnv, Dialect)
     */
    @Nullable
    private String getQualifiedSchemaName(final ArmyEnvironment env, final ServerMeta meta) {
        if (!env.getOrDefault(ArmyKey.QUALIFIED_TABLE_NAME_ENABLE)) {
            return null;
        }
        final String schemaName, qualifiedSchemaName;
        schemaName = this.qualifiedSchemaName(meta);
        final NameMode mode;
        mode = env.getOrDefault(ArmyKey.DATABASE_NAME_MODE);
        switch (mode) {
            case DEFAULT:
                qualifiedSchemaName = schemaName;
                break;
            case LOWER_CASE:
                qualifiedSchemaName = schemaName.toLowerCase(Locale.ROOT);
                break;
            case UPPER_CASE:
                qualifiedSchemaName = schemaName.toUpperCase(Locale.ROOT);
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return qualifiedSchemaName;
    }




    /*################################## blow DML batchInsert method ##################################*/


    /**
     * {@inheritDoc}
     */
    @Override
    public final Stmt insert(final InsertStatement statement, final Visible visible) {
        if (statement instanceof _Insert._ChildInsert
                && _DialectUtils.isIllegalTwoStmtMode((_Insert._ChildInsert) statement)) {
            throw _Exceptions.illegalTwoStmtMode();
        }
        return this.createInsertStmt(this.handleInsert(null, statement, visible));
    }


    /*################################## blow update method ##################################*/


    @Override
    public final Stmt update(final UpdateStatement update, final boolean useMultiStmt, final Visible visible) {
        final Stmt stmt;
        if (useMultiStmt) {
            stmt = this.updateWithMultiStmt(MultiStmtBatchContext.create(this, visible), update)
                    .build();
        } else {
            stmt = this.handleUpdate(null, update, visible, null)
                    .build();
        }
        return stmt;
    }


    @Override
    public final Stmt delete(final DeleteStatement delete, boolean useMultiStmt, final Visible visible) {
        final Stmt stmt;
        if (useMultiStmt) {
            stmt = this.deleteWithMultiStmt(MultiStmtBatchContext.create(this, visible), delete)
                    .build();
        } else {
            stmt = this.handleDelete(null, delete, visible, null)
                    .build();
        }
        return stmt;
    }

    @Override
    public final Stmt select(final SelectStatement select, final boolean useMultiStmt, final Visible visible) {
        final Stmt stmt;
        if (useMultiStmt) {
            stmt = this.selectWithMultiSmt(MultiStmtBatchContext.create(this, visible), select)
                    .build();
        } else {
            stmt = this.handleSelect(null, select, visible, null)
                    .build();
        }
        return stmt;
    }


    @Override
    public final Stmt values(final Values values, final Visible visible) {
        return this.handleValues(null, values, visible)
                .build();
    }


    @Override
    public final Stmt dialectDml(final DmlStatement statement, final Visible visible) {
        return this.createDialectStmt(this.handleDialectDml(null, statement, visible));
    }

    @Override
    public final Stmt dialectDql(final DqlStatement statement, final Visible visible) {
        throw new UnsupportedOperationException();
    }


    @Override
    public final List<String> schemaDdl(final _SchemaResult schemaResult) {
        final DdlParser ddlDialect;
        ddlDialect = createDdlDialect();

        final List<String> ddlList = _Collections.arrayList();
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
        final StringBuilder builder = new StringBuilder(128);
        this.printStmt(stmt, beautify, builder::append);
        return builder.toString();
    }


    @Override
    public final void printStmt(Stmt stmt, boolean beautify, Consumer<String> appender) {
        if (beautify) {
            stmt.printSql(this.beautifySqlFunc, appender);
        } else {
            stmt.printSql(_DialectUtils.NON_BEAUTIFY_SQL_FUNC, appender);
        }
    }

    @Override
    public String sqlElement(SQLElement element) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    public final boolean isKeyWords(final String words) {
        return this.keyWordSet.contains(words.toUpperCase(Locale.ROOT));
    }

    @Override
    public final ServerMeta serverMeta() {
        return this.serverMeta;
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

    protected abstract ChildUpdateMode childUpdateMode();

    protected abstract boolean isSupportSingleUpdateAlias();

    protected abstract boolean isSupportSingleDeleteAlias();

    protected abstract boolean isSupportUpdateRow();

    protected abstract boolean isSupportUpdateDerivedField();

    protected abstract boolean isSupportReturningClause();

    protected abstract boolean isValidateUnionType();

    protected abstract void validateUnionType(_UnionType unionType);

    protected abstract String qualifiedSchemaName(ServerMeta meta);

    protected abstract IdentifierMode identifierMode(String identifier);

    protected abstract void escapesIdentifier(String identifier, StringBuilder sqlBuilder);

    /**
     * @see #objectNameMode(DatabaseObject, String)
     */
    protected abstract boolean isUseObjectNameModeMethod();

    /**
     * @see #isUseObjectNameModeMethod()
     */
    protected abstract IdentifierMode objectNameMode(DatabaseObject object, String effectiveName);




    /*################################## blow dialect template method ##################################*/

    public final String safeObjectName(final DatabaseObject object) {
        final String objectName;
        objectName = object.objectName();

        final StringBuilder schemaTableBuilder;
        final NameMode nameMode;
        if (object instanceof FieldMeta) {
            nameMode = this.columnNameMode;
            schemaTableBuilder = null;
        } else if (object instanceof TableMeta) {
            nameMode = this.tableNameMode;

            final String schemaName;
            if ((schemaName = this.qualifiedSchemaName) == null) {
                schemaTableBuilder = null;
            } else {
                schemaTableBuilder = new StringBuilder(schemaName.length() + 1 + objectName.length());
                schemaTableBuilder.append(schemaName)
                        .append(_Constant.PERIOD);
            }
        } else {
            // no bug,never here
            throw new IllegalArgumentException();
        }

        final String effectiveName, upperObjectName, safeObjectName;
        switch (nameMode) {
            case DEFAULT:
                effectiveName = objectName;
                upperObjectName = objectName.toUpperCase(Locale.ROOT);
                break;
            case LOWER_CASE:
                effectiveName = objectName.toLowerCase(Locale.ROOT);
                upperObjectName = objectName.toUpperCase(Locale.ROOT);
                break;
            case UPPER_CASE:
                effectiveName = objectName.toUpperCase(Locale.ROOT);
                upperObjectName = effectiveName;
                break;
            default:
                throw _Exceptions.unexpectedEnum(this.columnNameMode);
        }


        final IdentifierMode mode;
        if (this.keyWordMap.containsKey(upperObjectName)) {
            mode = IdentifierMode.QUOTING;
        } else if (this.useObjectNameModeMethod) {
            mode = this.objectNameMode(object, effectiveName);
        } else {
            mode = this.identifierMode(effectiveName);
        }
        switch (mode) {
            case ERROR:
                throw _Exceptions.objectNameError(object, this.dialect);
            case SIMPLE: {
                if (schemaTableBuilder == null) {
                    safeObjectName = effectiveName;
                } else {
                    safeObjectName = schemaTableBuilder.append(effectiveName)
                            .toString();
                }
            }
            break;
            case QUOTING: {
                final StringBuilder builder;
                if (schemaTableBuilder == null) {
                    builder = new StringBuilder(effectiveName.length() + 2);
                } else {
                    builder = schemaTableBuilder;
                }
                safeObjectName = builder.append(this.identifierQuote)
                        .append(effectiveName)
                        .append(this.identifierQuote)
                        .toString();
            }
            break;
            case ESCAPES: {
                final StringBuilder builder;
                if (schemaTableBuilder == null) {
                    builder = new StringBuilder(effectiveName.length() + 3);
                } else {
                    builder = schemaTableBuilder;
                }
                final int oldLength;
                oldLength = builder.length();
                this.escapesIdentifier(effectiveName, builder);
                assert builder.length() > oldLength;
                safeObjectName = builder.toString();
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return safeObjectName;
    }

    public final StringBuilder safeObjectName(final DatabaseObject object, final StringBuilder builder) {

        final NameMode nameMode;
        if (object instanceof FieldMeta) {
            nameMode = this.columnNameMode;
        } else if (object instanceof TableMeta) {
            nameMode = this.tableNameMode;

            final String schemaName;
            if ((schemaName = this.qualifiedSchemaName) != null) {
                builder.append(schemaName)
                        .append(_Constant.PERIOD);
            }
        } else {
            // no bug,never here
            throw new IllegalArgumentException();
        }

        final String effectiveName, upperObjectName;
        switch (nameMode) {
            case DEFAULT:
                effectiveName = object.objectName();
                upperObjectName = effectiveName.toUpperCase(Locale.ROOT);
                break;
            case LOWER_CASE:
                effectiveName = object.objectName().toLowerCase(Locale.ROOT);
                upperObjectName = effectiveName.toUpperCase(Locale.ROOT);
                break;
            case UPPER_CASE:
                effectiveName = object.objectName().toUpperCase(Locale.ROOT);
                upperObjectName = effectiveName;
                break;
            default:
                throw _Exceptions.unexpectedEnum(this.columnNameMode);
        }

        final IdentifierMode mode;
        if (this.keyWordMap.containsKey(upperObjectName)) {
            mode = IdentifierMode.QUOTING;
        } else if (this.useObjectNameModeMethod) {
            mode = this.objectNameMode(object, effectiveName);
        } else {
            mode = this.identifierMode(effectiveName);
        }
        switch (mode) {
            case ERROR:
                throw _Exceptions.objectNameError(object, this.dialect);
            case SIMPLE:
                builder.append(effectiveName);
                break;
            case QUOTING:
                builder.append(this.identifierQuote)
                        .append(effectiveName)
                        .append(this.identifierQuote);
                break;
            case ESCAPES: {
                final int oldLength;
                oldLength = builder.length();
                this.escapesIdentifier(effectiveName, builder);
                assert builder.length() > oldLength;
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return builder;
    }


    @Override
    public final String identifier(final String identifier) {
        final String safeIdentifier;
        final IdentifierMode mode;
        if (this.keyWordMap.containsKey(identifier.toUpperCase(Locale.ROOT))
                || (mode = this.identifierMode(identifier)) == IdentifierMode.QUOTING) {
            final StringBuilder builder = new StringBuilder(identifier.length() + 2);
            safeIdentifier = builder.append(this.identifierQuote)
                    .append(identifier)
                    .append(this.identifierQuote)
                    .toString();
        } else switch (mode) {
            case ERROR:
                throw _Exceptions.identifierError(identifier, this.dialect);
            case SIMPLE:
                safeIdentifier = identifier;
                break;
            case ESCAPES: {
                final int identifierLength;
                identifierLength = identifier.length();
                final StringBuilder builder = new StringBuilder(identifierLength + 3);
                this.escapesIdentifier(identifier, builder);

                assert builder.length() > identifierLength;
                safeIdentifier = builder.toString();
            }
            break;
            case QUOTING:
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return safeIdentifier;
    }


    @Override
    public final StringBuilder identifier(final String identifier, final StringBuilder builder) {

        final IdentifierMode mode;
        if (this.keyWordMap.containsKey(identifier.toUpperCase(Locale.ROOT))
                || (mode = this.identifierMode(identifier)) == IdentifierMode.QUOTING) {
            builder.append(this.identifierQuote)
                    .append(identifier)
                    .append(this.identifierQuote);
        } else switch (mode) {
            case ERROR:
                throw _Exceptions.identifierError(identifier, this.dialect);
            case SIMPLE:
                builder.append(identifier);
                break;
            case ESCAPES: {
                final int oldLength;
                oldLength = builder.length();
                this.escapesIdentifier(identifier, builder);
                assert builder.length() > oldLength + identifier.length();
            }
            break;
            case QUOTING:
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return builder;
    }


    /**
     * <p>
     * Append  literal
     *
     */
    public final void literal(final TypeMeta typeMeta, @Nullable Object value, final StringBuilder sqlBuilder) {
        final MappingType type;
        if (typeMeta instanceof MappingType) {
            type = (MappingType) typeMeta;
        } else {
            type = typeMeta.mappingType();
        }
        final DataType dataType;
        dataType = type.map(this.serverMeta);

        if (value == null) {
            bindLiteralNull(type, dataType, sqlBuilder);
            return;
        }

        value = type.beforeBind(dataType, this.mappingEnv, value);

        if (value instanceof Temporal && typeMeta instanceof FieldMeta && this.truncatedTimeType) {
            value = _TimeUtils.truncatedIfNeed(((FieldMeta<?>) typeMeta).scale(), (Temporal) value);
        }

        //TODO validate non-field codec
        bindLiteral(typeMeta, dataType, value, sqlBuilder);
    }


    protected void arrayTypeName(String safeTypeNme, int dimension, StringBuilder sqlBuilder) {
        String m = String.format("%s don't support array", this.database.name());
        throw new MetaException(m);
    }


    protected abstract void bindLiteralNull(MappingType type, DataType dataType, StringBuilder sqlBuilder);

    protected abstract void bindLiteral(TypeMeta typeMeta, DataType dataType, Object value, StringBuilder sqlBuilder);

    protected abstract DdlParser createDdlDialect();

    /**
     * @return true: exists ignore possibility,for example support DO NOTHING clause in CONFLICT clause.
     */
    protected abstract boolean existsIgnoreOnConflict();

    /**
     * @param childStmt not {@link StandardInsert}
     * @see #handleInsert(_SqlContext, InsertStatement, Visible)
     */
    @Deprecated
    @Nullable
    protected abstract CriteriaException supportChildInsert(_Insert._ChildInsert childStmt, Visible visible);


    protected void assertInsert(InsertStatement insert) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    /**
     * @see #update(UpdateStatement, boolean, Visible)
     */
    protected void assertUpdate(UpdateStatement update) {
        throw standardParserDontSupportDialect(this.dialect);
    }


    protected void beautifySql(String sql, Consumer<String> appender) {
        //currently, dont' support beatify sql
        appender.accept(sql);
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
    protected void parseValuesInsert(_ValueSyntaxInsertContext context, _Insert._ValuesSyntaxInsert insert) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    protected void parseAssignmentInsert(_AssignmentInsertContext context, _Insert._AssignmentInsert insert) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    protected void parseQueryInsert(_QueryInsertContext context, _Insert._QueryInsert insert) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    /**
     * @see #handleUpdate(_SqlContext, UpdateStatement, Visible, _UpdateContext)
     */
    protected void parseSingleUpdate(_SingleUpdate update, _SingleUpdateContext context) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    /**
     * @see #handleUpdate(_SqlContext, UpdateStatement, Visible, _UpdateContext)
     */
    protected void parseMultiUpdate(_MultiUpdate update, _MultiUpdateContext context) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    /**
     * @see #handleDelete(_SqlContext, DeleteStatement, Visible, _DeleteContext)
     */
    protected void parseMultiDelete(final _MultiDelete delete, _MultiDeleteContext context) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    /**
     * @see #handleDelete(_SqlContext, DeleteStatement, Visible, _DeleteContext)
     */
    protected void parseSingleDelete(_SingleDelete delete, _SingleDeleteContext context) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    /**
     * @see #handleQuery(Query, _SqlContext)
     * @see #handleSelect(_SqlContext, SelectStatement, Visible, _SelectContext)
     * @see #handleValuesQuery(ValuesQuery, _SqlContext)
     * @see #handleValues(_SqlContext, Values, Visible)
     */
    protected void parseWithClause(_Statement._WithClauseSpec spec, _SqlContext context) {
        throw standardParserDontSupportDialect(this.dialect);
    }

    /**
     * @see #handleSelect(_SqlContext, SelectStatement, Visible, _SelectContext)
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
     * @see #handleDomainUpdate(_SqlContext, _DomainUpdate, Visible, _UpdateContext)
     */
    protected void parseDomainChildUpdate(_SingleUpdate update, _UpdateContext context) {

        throw new UnsupportedOperationException();
    }

    /**
     * @see #handleDomainDelete(_SqlContext, _DomainDelete, Visible, _DeleteContext)
     */
    @Nullable
    protected void parseDomainChildDelete(_SingleDelete delete, _DeleteContext context) {
        throw new UnsupportedOperationException();
    }


    /**
     * @see #handleSelect(_SqlContext, SelectStatement, Visible, _SelectContext)
     * @see #handleQuery(Query, _SqlContext)
     * @see #handleValues(_SqlContext, Values, Visible)
     * @see #handleValuesQuery(ValuesQuery, _SqlContext)
     */
    protected void parseClauseAfterRightParen(_ParensRowSet rowSet, _ParenRowSetContext context) {
        throw standardParserDontSupportDialect(this.dialect);
    }


    /*-------------------below final protected method -------------------*/


    protected final void unrecognizedTypeName(final MappingType type, final DataType dataType,
                                              final boolean supportUserDefinedType, final StringBuilder sqlBuilder) {


        if (type instanceof MappingType.SqlUserDefinedType) {
            if (!supportUserDefinedType) {
                throw _Exceptions.notUserDefinedType(type, dataType);
            }
        } else if (!this.unrecognizedTypeAllowed) {
            throw _Exceptions.unrecognizedType(this.database, dataType);
        }

        final String typeName;
        typeName = dataType.typeName();

        if (!this.keyWordMap.containsKey(typeName.toUpperCase(Locale.ROOT))
                && _DialectUtils.isSimpleIdentifier(typeName)) {
            sqlBuilder.append(typeName);
        } else if (!dataType.isArray()) {
            if (!typeName.equals(identifier(typeName))) {
                String m = String.format("%s type name[%s] is illegal.", dataType, typeName);
                throw new MetaException(m);
            }
            sqlBuilder.append(typeName);
        } else if (!typeName.endsWith("[]")) {
            String m = String.format("%s is array but not end with []", dataType);
            throw new MetaException(m);
        } else {
            final String elementTypeName = typeName.substring(0, typeName.length() - 2);
            if (!elementTypeName.equals(identifier(elementTypeName))) {
                String m = String.format("%s is array but type name[%s] is illegal.", dataType, elementTypeName);
                throw new MetaException(m);
            }
            arrayTypeName(elementTypeName, ArrayUtils.dimensionOfType(type), sqlBuilder);
        }

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

        //below validate insert statement
        if (!(insert instanceof StandardInsert)) {
            if (_DialectUtils.isIllegalConflict((_Insert) insert, visible)) {
                throw _Exceptions.conflictClauseAndVisibleNotMatch(this.dialect, (_Insert) insert, visible);
            } else if (insert instanceof _Insert._ChildInsert) {
                final _Insert._ChildInsert childStmt = (_Insert._ChildInsert) insert;
                if (_DialectUtils.isIllegalChildPostInsert(childStmt)) {
                    throw _Exceptions.forbidChildInsertSyntaxError(childStmt);
                }
            } else if (insert instanceof _Insert._DomainInsert
                    && _DialectUtils.isCannotReturnId((_Insert._DomainInsert) insert)) {
                throw _Exceptions.cannotReturnPostId((_Insert._DomainInsert) insert);
            }
        }

        return this.handleInsertStmt(outerContext, (_Insert) insert, visible);

    }

    protected final _InsertContext handleInsertStmt(final @Nullable _SqlContext outerContext, final _Insert insert,
                                                    final Visible visible) {
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
            throw _Exceptions.unknownStatement(insert, this.dialect); // possibly sub statement
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
            itemPairList.get(i).appendItemPair(sqlBuilder, context);
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

            final _UnionType unionType;
            unionType = union.unionType();
            if (this.validateUnionType) {
                this.validateUnionType(unionType);
            }
            ((StatementContext) original).sqlBuilder.append(unionType.spaceRender());

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
                columnList.get(columnIndex).appendSql(sqlBuilder, context);
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
            predicateList.get(i).appendSql(sqlBuilder, context);
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
        for (int i = 0, columnAliasSize; i < cteSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            cte = cteList.get(i);
            assetConsumer.accept(cte);
            columnAliasList = cte.columnAliasList();
            subQuery = (SubQuery) cte.subStatement();

            sqlBuilder.append(_Constant.SPACE);
            this.identifier(cte.name(), sqlBuilder);// cte name

            if ((columnAliasSize = columnAliasList.size()) > 0) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
                for (int aliasIndex = 0; aliasIndex < columnAliasSize; aliasIndex++) {
                    if (aliasIndex == 0) {
                        sqlBuilder.append(_Constant.SPACE);
                    } else {
                        sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
                    }
                    this.identifier(columnAliasList.get(aliasIndex), sqlBuilder);
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
     * @see #handleSelect(_SqlContext, SelectStatement, Visible, _SelectContext)
     * @see #handleRowSet(RowSet, _SqlContext)
     * @see #handleSubQuery(SubQuery, _SqlContext)
     * @see #withSubQuery(boolean, List, _SqlContext, Consumer)
     */
    protected final void handleQuery(final Query query, final _SqlContext original) {
        query.prepared();
        if (query instanceof _Query) {
            final _SimpleQueryContext context;
            if (query instanceof SelectStatement) {
                context = SimpleSelectContext.create(original, (SelectStatement) query);
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

            final _UnionType unionType;
            unionType = unionRowSet.unionType();
            if (this.validateUnionType) {
                this.validateUnionType(unionType);
            }
            ((StatementContext) original).sqlBuilder.append(unionType.spaceRender());

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
            if (query instanceof SelectStatement) {
                context = ParensSelectContext.create(original, (SelectStatement) query, this, original.visible());
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
            itemPairList.get(i).appendItemPair(sqlBuilder, context);
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
            itemPairList.get(i).appendItemPair(sqlBuilder, context);
        }

        for (int i = 0; i < childSize; i++) {
            if (i > 0 || itemSize > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            childItemPairList.get(i).appendItemPair(sqlBuilder, context);
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
        final Map<String, Boolean> aliasMap = _Collections.hashMap();

        //1. append SET key word
        final StringBuilder sqlBuilder = context.sqlBuilder();
        sqlBuilder.append(_Constant.SPACE_SET);
        //2. append item pairs in SET clause
        _ItemPair pair;
        SqlField dataField;
        for (int i = 0; i < itemPairSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            pair = itemPairList.get(i);
            pair.appendItemPair(sqlBuilder, context);

            if (pair instanceof _ItemPair._FieldItemPair) {
                dataField = ((_ItemPair._FieldItemPair) pair).field();
                aliasMap.putIfAbsent(context.singleTableAliasOf(dataField), Boolean.TRUE);
            } else {
                assert pair instanceof _ItemPair._RowItemPair;
                for (SqlField field : ((_ItemPair._RowItemPair) pair).rowFieldList()) {
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

    protected final void selectModifierClause(final List<? extends SQLWords> modifierList, final _SqlContext context,
                                              final Function<SQLWords, Integer> validator) {
        final StringBuilder sqlBuilder = context.sqlBuilder();
        int level, lastLevel = -1;
        for (SQLWords modifier : modifierList) {
            level = validator.apply(modifier);
            if (level < 0 || level < lastLevel) {
                String m = String.format("SELECT modifier[%s] syntax error.", modifier);
                throw new CriteriaException(m);
            }
            lastLevel = level;
            sqlBuilder.append(modifier.spaceRender());
        }

    }


    protected final void selectionListClause(final SelectItemListContext context) {
        final List<? extends _SelectItem> selectItemList;
        selectItemList = context.selectItemList();
        final int size = selectItemList.size();
        if (size == 0) {
            throw _Exceptions.selectListIsEmpty();
        }
        final StringBuilder sqlBuilder = context.sqlBuilder();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            selectItemList.get(i).appendSelectItem(sqlBuilder, context);

        }//for

    }


    /**
     * @see #parseStandardQuery(_StandardQuery, _SimpleQueryContext)
     */
    protected final void standardTableReferences(final List<_TabularBlock> tableBlockList,
                                                 final _MultiTableStmtContext context, final boolean nested) {
        final int blockSize = tableBlockList.size();
        assert blockSize > 0;

        final StringBuilder sqlBuilder = context.sqlBuilder();
        _TabularBlock block;
        TabularItem tableItem;
        _JoinType joinType;
        List<_Predicate> predicateList;
        final boolean tableOnlyModifier = this.tableOnlyModifier;
        for (int i = 0; i < blockSize; i++) {
            block = tableBlockList.get(i);
            joinType = block.jointType();
            if (i > 0) {
                sqlBuilder.append(joinType.spaceRender());
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
    protected final void queryWhereClause(final List<_TabularBlock> tableBlockList, final List<_Predicate> predicateList
            , final _MultiTableStmtContext context) {
        final int predicateSize = predicateList.size();

        final StringBuilder sqlBuilder = context.sqlBuilder();
        if (predicateSize > 0) {
            //1. append where key word
            sqlBuilder.append(_Constant.SPACE_WHERE);
            //2. append where predicates
            for (int i = 0; i < predicateSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_AND);
                }
                predicateList.get(i).appendSql(sqlBuilder, context);
            }

        }

        if (context.visible() == Visible.BOTH) {
            return;
        }

        if (predicateSize == 0) {
            final int startIndex, endIndex;

            startIndex = sqlBuilder.length();
            sqlBuilder.append(_Constant.SPACE_WHERE);
            endIndex = sqlBuilder.length();

            multiTableVisible(tableBlockList, context, true);
            if (sqlBuilder.length() == endIndex) {
                sqlBuilder.setLength(startIndex);
            }
        } else {
            multiTableVisible(tableBlockList, context, false);
        }

    }


    protected final void derivedColumnAliasClause(final _AliasDerivedBlock block, final _MultiTableStmtContext context) {
        final List<String> columnAliasList;
        columnAliasList = block.columnAliasList();
        final int columnAliasSize = columnAliasList.size();
        if (columnAliasSize == 0) {
            return;
        }
        if (((_DerivedTable) block.tableItem()).refAllSelection().size() != columnAliasSize) {
            throw _Exceptions.derivedColumnAliasSizeNotMatch(block);
        }
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder()
                .append(_Constant.SPACE_LEFT_PAREN);
        for (int i = 0; i < columnAliasSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
            } else {
                sqlBuilder.append(_Constant.SPACE);
            }
            this.identifier(columnAliasList.get(i), sqlBuilder);
        }

        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
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
                .append(_Constant.PERIOD)
                .append(_MetaBridge.ID)
                .append(_Constant.SPACE_EQUAL_SPACE)
                .append(safeParentTableAlias)
                .append(_Constant.PERIOD)
                .append(_MetaBridge.ID);
    }

    protected final void groupByClause(final List<? extends GroupByItem> groupByList, final _SqlContext context) {
        final int size = groupByList.size();
        if (size == 0) {
            return;
        }
        final StringBuilder sqlBuilder = context.sqlBuilder()
                .append(_Constant.SPACE_GROUP_BY);
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            ((_SelfDescribed) groupByList.get(i)).appendSql(sqlBuilder, context);
        }

    }

    protected final void havingClause(final List<_Predicate> havingList, final _SqlContext context) {
        final int size = havingList.size();
        if (size == 0) {
            return;
        }
        final StringBuilder sqlBuilder = context.sqlBuilder()
                .append(_Constant.SPACE_HAVING);
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_AND);
            }
            havingList.get(i).appendSql(sqlBuilder, context);
        }

    }

    protected final void groupByAndHavingClause(final _Query stmt, final _SqlContext context) {
        final List<? extends GroupByItem> groupByList;
        groupByList = stmt.groupByList();
        if (groupByList.size() > 0) {
            this.groupByClause(groupByList, context);
            this.havingClause(stmt.havingList(), context);
        }
    }


    protected final void windowClause(final List<_Window> windowList, final _SimpleQueryContext context,
                                      final Consumer<_Window> validator) {
        final int windowSize = windowList.size();
        if (windowSize == 0) {
            return;
        }
        final StringBuilder sqlBuilder = context.sqlBuilder()
                .append(_Constant.SPACE_WINDOW);
        _Window window;
        for (int i = 0; i < windowSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            window = windowList.get(i);
            validator.accept(window);
            window.appendSql(sqlBuilder, context);
        }

    }

    protected final void orderByClause(final List<? extends SortItem> orderByList, final _SqlContext context) {
        final int size = orderByList.size();
        if (size == 0) {
            return;
        }
        final StringBuilder sqlBuilder = context.sqlBuilder()
                .append(_Constant.SPACE_ORDER_BY);

        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            //TODO 考虑是否 增加  append SortItem method以避免 visible field
            ((_SelfDescribed) orderByList.get(i)).appendSql(sqlBuilder, context);
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
        sqlBuilder.append(_Constant.PERIOD);

        this.safeObjectName(field, sqlBuilder)
                .append(_Constant.SPACE_EQUAL_SPACE)
                .append(table.discriminatorValue().code());
    }


    /**
     * @param context {@link _SqlContext#visible()} must be not {@link Visible#BOTH}.
     * @see #multiTableVisible(List, _MultiTableStmtContext, boolean)
     */
    protected final void visiblePredicate(final SingleTableMeta<?> table, final @Nullable String safeTableAlias,
                                          final _SqlContext context, final boolean firstPredicate) {

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
        if (safeTableAlias == null) {
            this.safeObjectName(table, sqlBuilder);
        } else {
            sqlBuilder.append(safeTableAlias);
        }
        sqlBuilder.append(_Constant.PERIOD);
        this.safeObjectName(field, sqlBuilder)
                .append(_Constant.SPACE_EQUAL_SPACE);
        this.literal(field.mappingType(), visibleValue, sqlBuilder);
    }

    protected final void parentVisiblePredicate(final _InsertContext childContext, final boolean firstPredicate) {
        final Boolean visibleValue;
        visibleValue = childContext.visible().value;
        if (visibleValue == null) {
            return;
        }
        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) childContext.insertTable();
        final ParentTableMeta<?> parentTable = childTable.parentMeta();

        final String childAlias, safeChildAlias, safeParentAlias;
        if ((childAlias = childContext.tableAlias()) == null) {
            safeChildAlias = this.safeObjectName(childTable);
            safeParentAlias = this.identifier(_DialectUtils.parentAlias(childTable.tableName()));
        } else {
            safeChildAlias = childContext.safeTableAlias();
            assert safeChildAlias != null;
            safeParentAlias = this.identifier(_DialectUtils.parentAlias(childAlias));
        }

        final StringBuilder sqlBuilder;
        sqlBuilder = childContext.sqlBuilder(); //below sub query left bracket
        if (!firstPredicate) {
            sqlBuilder.append(_Constant.SPACE_AND);
        }
        sqlBuilder.append(_Constant.SPACE_EXISTS)
                .append(_Constant.SPACE_LEFT_PAREN)
                .append(_Constant.SPACE_SELECT_SPACE)
                //below target parent column
                .append(safeParentAlias)
                .append(_Constant.PERIOD)
                .append(_MetaBridge.ID)
                .append(_Constant.SPACE_FROM_SPACE);

        this.safeObjectName(parentTable, sqlBuilder);

        if (this.aliasAfterAs) {
            sqlBuilder.append(_Constant.SPACE_AS_SPACE);
        } else {
            sqlBuilder.append(_Constant.SPACE);
        }
        sqlBuilder.append(safeParentAlias)
                .append(_Constant.SPACE_WHERE) //below where clause
                .append(_Constant.SPACE)

                .append(safeParentAlias)
                .append(_Constant.PERIOD)
                .append(_MetaBridge.ID)

                .append(_Constant.SPACE_EQUAL_SPACE)

                .append(safeChildAlias)
                .append(_Constant.PERIOD)
                .append(_MetaBridge.ID)

                .append(_Constant.SPACE_AND_SPACE)
                .append(safeParentAlias)
                .append(_Constant.PERIOD);

        // below discriminator predicate
        final FieldMeta<?> discriminator = parentTable.discriminator();
        this.safeObjectName(discriminator, sqlBuilder)
                .append(_Constant.SPACE_EQUAL_SPACE);

        this.literal(discriminator.typeMeta(), childTable.discriminatorValue(), sqlBuilder);

        // below visible predicate
        final FieldMeta<?> visibleField;
        visibleField = parentTable.getField(_MetaBridge.VISIBLE);

        sqlBuilder.append(_Constant.SPACE_AND_SPACE)
                .append(safeParentAlias)
                .append(_Constant.PERIOD);

        this.safeObjectName(visibleField, sqlBuilder)
                .append(_Constant.SPACE_EQUAL_SPACE);

        this.literal(visibleField, visibleValue, sqlBuilder);

        //below sub query right paren
        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

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
            predicateList.get(i).appendSql(sqlBuilder, context);
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
            predicateList.get(i).appendSql(sqlBuilder, childContext);
        }

    }

    /**
     * @return the number of appending visible
     * @see #parseStandardQuery(_StandardQuery, _SimpleQueryContext)
     * @see #queryWhereClause(List, List, _MultiTableStmtContext)
     */
    protected final int multiTableVisible(final List<_TabularBlock> blockList, final _MultiTableStmtContext context
            , final boolean firstPredicate) {
        TabularItem tableItem;
        String safeTableAlias;
        SingleTableMeta<?> table;
        int count = 0;
        for (_TabularBlock block : blockList) {
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
        assert this.childUpdateMode == ChildUpdateMode.WITH_ID;

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
            updateTimeValue = OffsetDateTime.now(this.mappingEnv.zoneOffset());
        } else if (javaType == ZonedDateTime.class) {
            updateTimeValue = ZonedDateTime.now(this.mappingEnv.zoneOffset());
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
                    .append(_Constant.PERIOD);
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
                        .append(_Constant.PERIOD);
            }
            sqlBuilder.append(versionColumnName)
                    .append(_Constant.SPACE_EQUAL_SPACE);
            if (safeTableAlias != null) {  // no setClauseTableAlias
                sqlBuilder.append(safeTableAlias)
                        .append(_Constant.PERIOD);
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
    private _ValueSyntaxInsertContext handleDomainInsert(final @Nullable _SqlContext outerContext
            , final _Insert._DomainInsert insert, final Visible visible) {
        final boolean standardStmt = insert instanceof StandardInsert;
        final _ValueSyntaxInsertContext context;
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
    private _ValueSyntaxInsertContext handleValueInsert(final @Nullable _SqlContext outerContext
            , final _Insert._ValuesInsert insert, final Visible visible) {
        final boolean standardStmt = insert instanceof StandardInsert;
        final _ValueSyntaxInsertContext context;
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

            final AssignmentInsertContext parentContext;
            parentContext = AssignmentInsertContext.forParent(outerContext, childStmt, this, visible);
            this.parseAssignmentInsert(parentContext, childStmt.parentStmt());

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
     * @see #select(SelectStatement, boolean, Visible)
     */
    private MultiStmtContext selectWithMultiSmt(final MultiStmtContext multiStmtContext,
                                                final SelectStatement select) {
        final int batchSize = ((_BatchStatement) select).paramList().size();
        final Visible visible = multiStmtContext.visible();


        _SelectContext context = null, tempContext;
        MultiStmt.StmtItem stmtItem = null;

        multiStmtContext.batchStmtStart(batchSize);
        for (int i = 0; i < batchSize; i++) {
            if (i == 0) {
                context = this.handleSelect(multiStmtContext, select, visible, null);
                stmtItem = Stmts.queryOrUpdateItem(context);
            } else if (context.nextGroup() != i) {
                throw new IllegalStateException("next group error");
            } else {
                tempContext = this.handleSelect(multiStmtContext, select, visible, context);
                assert tempContext == context;
            }
            multiStmtContext.addBatchItem(stmtItem);
        }

        return multiStmtContext.batchStmtEnd();
    }

    /**
     * @see #select(SelectStatement, boolean, Visible)
     * @see #selectWithMultiSmt(MultiStmtContext, SelectStatement)
     * @see #handleQuery(Query, _SqlContext)
     */
    private _SelectContext handleSelect(final @Nullable _SqlContext outerContext, final SelectStatement stmt,
                                        final Visible visible, final @Nullable _SelectContext prevContext) {
        stmt.prepared();
        final _SelectContext context;
        if (stmt instanceof _Query) {
            if (prevContext == null) {
                context = SimpleSelectContext.create(outerContext, stmt, this, visible);
            } else {
                context = prevContext;
            }
            if (stmt instanceof StandardQuery) {
                _SQLConsultant.assertStandardQuery(stmt);
                this.parseStandardQuery((_StandardQuery) stmt, (_SimpleQueryContext) context);
            } else {
                this.assertRowSet(stmt);
                this.parseSimpleQuery((_Query) stmt, (_SimpleQueryContext) context);
            }
        } else if (stmt instanceof _UnionRowSet) {
            _SQLConsultant.assertUnionRowSet(stmt);
            if (prevContext == null) {
                context = ParensSelectContext.create(outerContext, stmt, this, visible);
            } else {
                context = prevContext;
            }
            final _UnionRowSet union = (_UnionRowSet) stmt;
            this.handleQuery((Query) union.leftRowSet(), context);
            if (this.validateUnionType) {
                this.validateUnionType(union.unionType());
            }
            context.sqlBuilder().append(union.unionType().spaceWords);
            this.handleRowSet(union.rightRowSet(), context);
        } else if (stmt instanceof _ParensRowSet) {
            if (stmt instanceof StandardQuery) {
                _SQLConsultant.assertStandardQuery(stmt);
            } else {
                this.assertRowSet(stmt);
            }
            if (prevContext == null) {
                context = ParensSelectContext.create(outerContext, stmt, this, visible);
            } else {
                context = prevContext;
            }
            if (((_Statement._WithClauseSpec) stmt).cteList().size() > 0) {
                this.parseWithClause((_Statement._WithClauseSpec) stmt, context);
            }
            this.handleParenRowSet((_ParenRowSetContext) context, (_ParensRowSet) stmt);
        } else {
            throw _Exceptions.unknownRowSetType(stmt);
        }
        return context;
    }

    /**
     * @see #handleSelect(_SqlContext, SelectStatement, Visible, _SelectContext)
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
            context.sqlBuilder().append(union.unionType().spaceRender());
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
     * @see #update(UpdateStatement, boolean, Visible)
     */
    private MultiStmtContext updateWithMultiStmt(final MultiStmtContext stmtContext, final UpdateStatement update) {
        final int batchSize = ((_BatchStatement) update).paramList().size();
        final Visible visible = stmtContext.visible();
        final boolean twoStmtMode;
        twoStmtMode = (update instanceof _DomainUpdate && this.childUpdateMode == ChildUpdateMode.WITH_ID)
                || update instanceof _SingleUpdate._ChildUpdate;

        _UpdateContext context = null, tempContext;
        MultiStmt.StmtItem stmtItem = null;

        stmtContext.batchStmtStart(batchSize);
        if (twoStmtMode) {
            stmtContext.startChildItem();
        }
        for (int i = 0; i < batchSize; i++) {
            if (i == 0) {
                context = this.handleUpdate(stmtContext, update, visible, null);
                stmtItem = Stmts.queryOrUpdateItem(context);
            } else if (context.nextGroup() != i) {
                throw new IllegalStateException("next group error");
            } else {
                tempContext = this.handleUpdate(stmtContext, update, visible, context);
                assert tempContext == context;
            }
            stmtContext.addBatchItem(stmtItem);
        }
        if (twoStmtMode) {
            stmtContext.endChildItem();
        }
        return stmtContext.batchStmtEnd();
    }


    /**
     * @see #update(UpdateStatement, boolean, Visible)
     * @see #updateWithMultiStmt(MultiStmtContext, UpdateStatement)
     */
    private _UpdateContext handleUpdate(final @Nullable _SqlContext outerContext, final UpdateStatement stmt,
                                        final Visible visible, final @Nullable _UpdateContext prevContext) {
        stmt.prepared();
        final _UpdateContext context;
        if (stmt instanceof _MultiUpdate) {
            assertUpdate(stmt);
            if (prevContext == null) {
                context = MultiUpdateContext.create(outerContext, (_MultiUpdate) stmt, this, visible);
            } else {
                context = prevContext;
            }
            this.parseMultiUpdate((_MultiUpdate) stmt, (MultiUpdateContext) context);
        } else if (!(stmt instanceof _SingleUpdate)) {
            throw _Exceptions.unknownStatement(stmt, this.dialect);
        } else if (stmt instanceof _DomainUpdate) {
            _SQLConsultant.assertStandardUpdate(stmt);
            context = this.handleDomainUpdate(outerContext, (_DomainUpdate) stmt, visible, prevContext);
        } else if (stmt instanceof StandardUpdate) {
            _SQLConsultant.assertStandardUpdate(stmt);
            if (prevContext == null) {
                context = SingleUpdateContext.create(outerContext, (_SingleUpdate) stmt, this, visible);
            } else {
                context = prevContext;
            }
            this.parseStandardSingleUpdate((_SingleUpdate) stmt, (_SingleUpdateContext) context);
        } else if (stmt instanceof _SingleUpdate._ChildUpdate) { //TODO fix my's sub-class for multi-statement, for H2,firebird
            assertUpdate(stmt);

            if (prevContext != null) {
                context = prevContext;
            } else if (stmt instanceof _Statement._JoinableStatement) { // TODO add outerContext , for H2,firebird
                final _SingleUpdate._ChildUpdate childStmt = (_SingleUpdate._ChildUpdate) stmt;
                final SingleJoinableUpdateContext parentContext;
                parentContext = SingleJoinableUpdateContext.forParent(childStmt, this, visible);
                context = SingleJoinableUpdateContext.forChild(childStmt, parentContext);
            } else {
                final _SingleUpdate._ChildUpdate childStmt = (_SingleUpdate._ChildUpdate) stmt;
                final SingleUpdateContext parentContext;
                parentContext = SingleUpdateContext.forParent(childStmt, this, visible); // TODO add outerContext, for H2,firebird
                context = SingleUpdateContext.forChild(childStmt, parentContext);
            }
            this.parseSingleUpdate((_SingleUpdate) stmt, (_SingleUpdateContext) context);
        } else if (stmt instanceof _Statement._JoinableStatement) {
            assertUpdate(stmt);
            if (prevContext == null) {
                context = SingleJoinableUpdateContext.create(outerContext, (_SingleUpdate) stmt, this, visible);
            } else {
                context = prevContext;
            }
            this.parseSingleUpdate((_SingleUpdate) stmt, (_SingleUpdateContext) context);
        } else {
            assertUpdate(stmt);
            if (prevContext == null) {
                context = SingleUpdateContext.create(outerContext, (_SingleUpdate) stmt, this, visible);
            } else {
                context = prevContext;
            }
            this.parseSingleUpdate((_SingleUpdate) stmt, (_SingleUpdateContext) context);
        }

        return context;
    }


    /**
     * @see #handleUpdate(_SqlContext, UpdateStatement, Visible, _UpdateContext)
     */
    private _UpdateContext handleDomainUpdate(final @Nullable _SqlContext outerContext, final _DomainUpdate stmt,
                                              final Visible visible, final @Nullable _UpdateContext prevContext) {
        final _UpdateContext context;
        final ChildUpdateMode mode = this.childUpdateMode;
        if (!(stmt.table() instanceof ChildTableMeta) || stmt.childItemPairList().size() == 0) {
            if (prevContext == null) {
                context = DomainUpdateContext.forSingle(outerContext, stmt, this, visible);
            } else {
                context = prevContext;
            }
            this.parseStandardSingleUpdate(stmt, (_SingleUpdateContext) context);
        } else if (mode == ChildUpdateMode.MULTI_TABLE) {
            if (prevContext == null) {
                context = MultiUpdateContext.forChild(outerContext, stmt, this, visible);
            } else {
                context = prevContext;
            }
            this.parseDomainChildUpdate(stmt, context);
        } else if (mode == ChildUpdateMode.CTE) {
            if (prevContext == null) {
                final DomainUpdateContext primaryContext;
                primaryContext = DomainUpdateContext.forSingle(outerContext, stmt, this, visible);
                context = DomainUpdateContext.forChild(stmt, primaryContext);
            } else {
                context = prevContext;
            }
            this.parseDomainChildUpdate(stmt, context);
        } else if (mode == ChildUpdateMode.WITH_ID) {
            final DomainUpdateContext parentContext;
            if (prevContext == null) {
                parentContext = DomainUpdateContext.forSingle(null, stmt, this, visible);
                context = DomainUpdateContext.forChild(stmt, parentContext);
            } else {
                context = prevContext;
                parentContext = (DomainUpdateContext) context.parentContext();
                assert parentContext != null;
            }
            assert parentContext.domainTable == ((DomainUpdateContext) context).domainTable;
            assert parentContext.targetTable instanceof ParentTableMeta;
            assert ((DomainUpdateContext) context).targetTable == parentContext.domainTable;

            final _Predicate idPredicate;
            idPredicate = this.parseDomainChildUpdateWithId(stmt, (DomainUpdateContext) context);

            if (outerContext instanceof MultiStmtContext) {
                ((MultiStmtContext) outerContext).appendItemForChild();
            }
            this.parseDomainParentUpdateWithId(stmt, idPredicate, parentContext);

        } else {
            throw _Exceptions.unexpectedEnum(mode);
        }
        return context;
    }


    /**
     * @see #handleDomainUpdate(_SqlContext, _DomainUpdate, Visible, _UpdateContext)
     * @see #parseDomainParentUpdateWithId(_DomainUpdate, _Predicate, DomainUpdateContext)
     * @see ChildUpdateMode#WITH_ID
     */
    private _Predicate parseDomainChildUpdateWithId(final _DomainUpdate stmt, final DomainUpdateContext context) {
        assert this.childUpdateMode == ChildUpdateMode.WITH_ID
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
     * @see #handleDomainUpdate(_SqlContext, _DomainUpdate, Visible, _UpdateContext)
     * @see #parseDomainChildUpdateWithId(_DomainUpdate, DomainUpdateContext)
     * @see ChildUpdateMode#WITH_ID
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
     * @see #delete(DeleteStatement, boolean, Visible)
     */
    private MultiStmtContext deleteWithMultiStmt(final MultiStmtContext stmtContext, final DeleteStatement delete) {
        final int batchSize = ((_BatchStatement) delete).paramList().size();
        final Visible visible = stmtContext.visible();
        final boolean twoStmtMode;
        twoStmtMode = (delete instanceof _DomainDelete && this.childUpdateMode == ChildUpdateMode.WITH_ID)
                || delete instanceof _SingleDelete._ChildDelete;

        _DeleteContext context = null, tempContext;
        MultiStmt.StmtItem stmtItem = null;

        stmtContext.batchStmtStart(batchSize);
        if (twoStmtMode) {
            stmtContext.startChildItem();
        }
        for (int i = 0; i < batchSize; i++) {

            if (i == 0) {
                context = this.handleDelete(stmtContext, delete, visible, null);
                stmtItem = Stmts.queryOrUpdateItem(context);
            } else if (context.nextGroup() != i) {
                throw new IllegalStateException("next group error");
            } else {
                tempContext = this.handleDelete(stmtContext, delete, visible, context);
                assert tempContext == context;
            }

            stmtContext.addBatchItem(stmtItem);

        }
        if (twoStmtMode) {
            stmtContext.endChildItem();
        }
        return stmtContext.batchStmtEnd();
    }

    /**
     * @see #delete(DeleteStatement, boolean, Visible)
     * @see #deleteWithMultiStmt(MultiStmtContext, DeleteStatement)
     */
    private _DeleteContext handleDelete(final @Nullable _SqlContext outerContext, final DeleteStatement stmt,
                                        final Visible visible, final @Nullable _DeleteContext prevContext) {
        stmt.prepared();
        final _DeleteContext context;
        if (stmt instanceof _MultiDelete) {
            assertDelete(stmt);
            if (prevContext == null) {
                context = MultiDeleteContext.create(outerContext, (_MultiDelete) stmt, this, visible);
            } else {
                context = prevContext;
            }
            this.parseMultiDelete((_MultiDelete) stmt, (_MultiDeleteContext) context);
        } else if (!(stmt instanceof _SingleDelete)) {
            throw _Exceptions.unknownStatement(stmt, this.dialect);
        } else if (stmt instanceof _DomainDelete) {
            _SQLConsultant.assertStandardDelete(stmt);
            context = this.handleDomainDelete(outerContext, (_DomainDelete) stmt, visible, prevContext);
        } else if (stmt instanceof StandardDelete) {
            _SQLConsultant.assertStandardDelete(stmt);
            if (prevContext == null) {
                context = SingleDeleteContext.create(outerContext, (_SingleDelete) stmt, this, visible);
            } else {
                context = prevContext;
            }
            this.parseStandardSingleDelete((_SingleDelete) stmt, (SingleDeleteContext) context);
        } else if (stmt instanceof _SingleDelete._ChildDelete) {
            assertDelete(stmt);
            final _SingleDelete._ChildDelete childStmt = (_SingleDelete._ChildDelete) stmt;
            if (prevContext != null) {
                context = prevContext;
            } else if (stmt instanceof _Statement._JoinableStatement) {
                final SingleJoinableDeleteContext parentContext;
                parentContext = SingleJoinableDeleteContext.forParent(childStmt, this, visible);
                context = SingleJoinableDeleteContext.forChild(childStmt, parentContext);
            } else {
                final SingleDeleteContext parentContext;
                parentContext = SingleDeleteContext.forParent(childStmt, this, visible);
                context = SingleDeleteContext.forChild(childStmt, parentContext);
            }
            this.parseSingleDelete((_SingleDelete) stmt, (_SingleDeleteContext) context);
        } else {
            assertDelete(stmt);
            if (prevContext != null) {
                context = prevContext;
            } else if (stmt instanceof _Statement._JoinableStatement) {
                context = SingleJoinableDeleteContext.create(outerContext, (_SingleDelete) stmt, this, visible);
            } else {
                context = SingleDeleteContext.create(outerContext, (_SingleDelete) stmt, this, visible);
            }
            this.parseSingleDelete((_SingleDelete) stmt, (_SingleDeleteContext) context);
        }
        return context;
    }

    /**
     * @see #handleDelete(_SqlContext, DeleteStatement, Visible, _DeleteContext)
     */
    private _DeleteContext handleDomainDelete(final @Nullable _SqlContext outerContext, final _DomainDelete stmt,
                                              final Visible visible, final @Nullable _DeleteContext prevContext) {
        final _DeleteContext context;
        final ChildUpdateMode mode = this.childUpdateMode;
        if (!(stmt.table() instanceof ChildTableMeta)) {
            if (prevContext == null) {
                context = DomainDeleteContext.forSingle(outerContext, stmt, this, visible);
            } else {
                context = prevContext;
            }
            this.parseStandardSingleDelete(stmt, (_SingleDeleteContext) context);
        } else if (mode == ChildUpdateMode.MULTI_TABLE) {
            if (prevContext == null) {
                context = MultiDeleteContext.forChild(outerContext, stmt, this, visible);
            } else {
                context = prevContext;
            }
            this.parseDomainChildDelete(stmt, context);
        } else if (mode == ChildUpdateMode.CTE) {
            if (prevContext == null) {
                final DomainDeleteContext primaryContext;
                primaryContext = DomainDeleteContext.forSingle(outerContext, stmt, this, visible);
                context = DomainDeleteContext.forChild(stmt, primaryContext);
            } else {
                context = prevContext;
            }
            this.parseDomainChildDelete(stmt, context);
        } else if (mode == ChildUpdateMode.WITH_ID) {
            final DomainDeleteContext parentContext;
            if (prevContext == null) {
                parentContext = DomainDeleteContext.forSingle(null, stmt, this, visible);
                context = DomainDeleteContext.forChild(stmt, parentContext);
            } else {
                context = prevContext;
                parentContext = (DomainDeleteContext) context.parentContext();
                assert parentContext != null;
            }
            assert parentContext.domainTable == ((DomainDeleteContext) context).domainTable;
            assert parentContext.targetTable instanceof ParentTableMeta;
            assert ((DomainDeleteContext) context).targetTable == parentContext.domainTable;

            final _Predicate idPredicate;
            idPredicate = this.parseDomainChildDeleteWithId(stmt, (DomainDeleteContext) context);

            if (outerContext instanceof MultiStmtContext) {
                ((MultiStmtContext) outerContext).appendItemForChild();
            }
            this.parseDomainParentDeleteWithId(idPredicate, parentContext);

            //TODO support multi-statement for h2,firebird ?

        } else {
            throw _Exceptions.unexpectedEnum(mode);
        }
        return context;
    }

    /**
     * @see #handleDomainDelete(_SqlContext, _DomainDelete, Visible, _DeleteContext)
     */
    private _Predicate parseDomainChildDeleteWithId(final _DomainDelete stmt, final DomainDeleteContext childContext) {
        assert this.childUpdateMode == ChildUpdateMode.WITH_ID;
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
     * @see #handleDomainDelete(_SqlContext, _DomainDelete, Visible, _DeleteContext)
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
                sqlBuilder.append(modifier.spaceRender());
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
    private void parseStandardValuesInsert(final _ValueSyntaxInsertContext context) {
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
     * @see #handleDelete(_SqlContext, DeleteStatement, Visible, _DeleteContext)
     * @see #handleDomainDelete(_SqlContext, _DomainDelete, Visible, _DeleteContext)
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
     * @see #handleUpdate(_SqlContext, UpdateStatement, Visible, _UpdateContext)
     * @see #handleDomainUpdate(_SqlContext, _DomainUpdate, Visible, _UpdateContext)
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
     * @see #handleSelect(_SqlContext, SelectStatement, Visible, _SelectContext)
     * @see #handleSubQuery(SubQuery, _SqlContext)
     */
    private void parseStandardQuery(final _StandardQuery stmt, final _SimpleQueryContext context) {

        final StringBuilder builder;
        if ((builder = context.sqlBuilder()).length() > 0) {
            builder.append(_Constant.SPACE);
        }
        builder.append(_Constant.SELECT);
        //1. select clause
        this.standardSelectClause(stmt.modifierList(), builder);
        //2. select list clause
        this.selectionListClause(context);
        //3. from clause
        final List<_TabularBlock> blockList;
        blockList = stmt.tableBlockList();
        if (blockList.size() > 0) {
            context.sqlBuilder()
                    .append(_Constant.SPACE_FROM);
            this.standardTableReferences(blockList, context, false);
        }
        //4. where clause
        this.queryWhereClause(blockList, stmt.wherePredicateList(), context);

        //5. groupBy clause
        this.groupByAndHavingClause(stmt, context);
        //6. orderBy clause
        this.orderByClause(stmt.orderByList(), context);

        //7. limit clause
        this.standardLimitClause(stmt.offsetExp(), stmt.rowCountExp(), context);

        //8. lock clause
        final SQLWords lock = stmt.lockStrength();
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
                && this.childUpdateMode != ChildUpdateMode.CTE) { // support RETURNING clause,could returning parent id
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
     * @see #delete(DeleteStatement, boolean, Visible)
     */
    private Stmt createDeleteStmt(final _DeleteContext context) {
        final _DeleteContext parentContext;
        final Stmt stmt;
        if (context instanceof _MultiDeleteContext || (parentContext = context.parentContext()) == null) {
            stmt = context.build();
        } else if (this.childUpdateMode == ChildUpdateMode.CTE) {
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


    protected enum IdentifierMode {

        SIMPLE,
        QUOTING,
        ESCAPES,
        ERROR

    }


    protected enum ChildUpdateMode {

        MULTI_TABLE,
        CTE,
        WITH_ID

    }


}
