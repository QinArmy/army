package io.army.boot.migratioin;

import io.army.dialect.DDLUtils;
import io.army.dialect.Database;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.schema.SchemaInfoException;
import io.army.session.DialectSessionFactory;
import io.army.util.StringUtils;
import io.army.util._Assert;

import java.util.*;
import java.util.regex.Pattern;

abstract class AbstractMetaSchemaComparator implements MetaSchemaComparator {

    private static final Pattern NOT_EMPTY_NUMBER_PATTERN = Pattern.compile(
            "[+-]?(?:\\d*)?\\.?(?:\\d*)?(?:[eE][+-]?\\d+)?");

    private static final Pattern BIT_VALUE_LITERAL_PATTERN = Pattern.compile("(?:[bB]'[01]+?')|(?:0b[01]+?)");

    private static final Pattern HEXADECIMAL_LITERAL_PATTERN = Pattern.compile(
            "(?:[xX]'[0-9a-fA-F]+?')|(?:0x[0-9a-fA-F]+?)");

    static final String YEAR_FORMAT = "[0-9]{4,8}";


    static final String DATE_FORMAT = YEAR_FORMAT + "-(?:(?:[0][1-9])|(?:[1][12]))-(?:(?:[0][1-9])?|(?:[12][0-9])|(?:3[01]))";

    static final String TIME_FORMAT = "(?:(?:[01][0-9])|(?:2[0-3])):[0-5][0-9]:[0-5][0-9](?:\\.[0-9]{1,6})?";

    static final String ZONE_FORMAT = "(?:[+-](?:(?:0[0-9])|(?:1[0-1])):[0-5][0-9])?";

    static final Pattern YEAR_FORMAT_PATTERN = Pattern.compile(YEAR_FORMAT);

    static final Pattern DATE_FORMAT_PATTERN = Pattern.compile("'" + DATE_FORMAT + "'");

    static final Pattern TIME_WITHOUT_ZONE_FORMAT_PATTERN = Pattern.compile("'" + TIME_FORMAT + "'");

    static final Pattern TIME_WITH_ZONE_FORMAT_PATTERN = Pattern.compile("'" + TIME_FORMAT + ZONE_FORMAT + "'");

    static final Pattern DATE_TIME_WITHOUT_ZONE_FORMAT_PATTERN = Pattern.compile(
            "'" + DATE_FORMAT + " " + TIME_FORMAT + "'"
    );

    static final Pattern DATE_TIME_WITH_ZONE_FORMAT_PATTERN = Pattern.compile(
            "'" + DATE_FORMAT + " " + TIME_FORMAT + ZONE_FORMAT + "'"
    );

    static boolean numericLiteral(String expression) {
        return StringUtils.hasText(expression)
                && NOT_EMPTY_NUMBER_PATTERN.matcher(expression).matches();
    }

    static boolean stringLiteral(String expression) {
        return StringUtils.hasText(expression)
                && expression.startsWith("'")
                && expression.endsWith("'");
    }

    static boolean bitValueLiteral(String expression) {
        return BIT_VALUE_LITERAL_PATTERN.matcher(expression).matches();
    }

    static boolean hexadecimalLiterals(String expression) {
        return HEXADECIMAL_LITERAL_PATTERN.matcher(expression).matches();
    }


    private StringBuilder dataTypeErrorBuilder = new StringBuilder();

    final DialectSessionFactory sessionFactory;

    final boolean compareDefaultOnMigrating;

    AbstractMetaSchemaComparator(DialectSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.compareDefaultOnMigrating = false;
    }

    @Override
    public final List<List<Migration>> compare(SchemaInfo schemaInfo)
            throws SchemaInfoException, MetaException {

//        final Map<String, TableInfo> tableInfoMap = schemaInfo.tableMap();
//
//        final int tableCount = this.sessionFactory.tableCountPerDatabase();
//        List<List<MigrationMember>> shardingList = new ArrayList<>(tableCount);
//        final Collection<TableMeta<?>> tableMetas = this.sessionFactory.tableMetaMap().values();
//        for (int i = 0; i < tableCount; i++) {
//            //1. obtain table suffix
//            final String tableSuffix = _RouteUtils.convertToSuffix(tableCount, i);
//            List<MigrationMember> migrationList = new ArrayList<>();
//
//            for (TableMeta<?> tableMeta : tableMetas) {
//                //2. obtain actual table name
//                String actualTableName = tableMeta.tableName();
//                if (tableSuffix != null) {
//                    actualTableName += tableSuffix;
//                }
//                //3. create MigrationMemberImpl
//                TableInfo tableInfo = tableInfoMap.get(StringUtils.toLowerCase(actualTableName));
//                if (tableInfo == null) {
//                    // will create table
//                    migrationList.add(new MigrationMemberImpl(tableMeta, tableSuffix, true));
//
//                } else {
//                    MigrationMemberImpl migration = doMigrateTable(tableMeta, tableSuffix, tableInfo);
//                    if (migration != null) {
//                        // will alter tableMeta
//                        migrationList.add(migration);
//                    }
//                    if (!DDLUtils.escapeQuote(tableMeta.comment()).equals(tableInfo.comment())) {
//                        if (migration == null) {
//                            migration = new MigrationMemberImpl(tableMeta, tableSuffix, false);
//                        }
//                        migration.modifyTableComment(true);
//                    }
//
//                }
//            }
//            //4. add migrationList to shardingList
//            if (!migrationList.isEmpty()) {
//                shardingList.add(Collections.unmodifiableList(migrationList));
//            }
//        }
//        if (this.dataTypeErrorBuilder.length() > 0) {
//            throw new SchemaInfoException(ErrorCode.SCHEMA_ERROR, this.dataTypeErrorBuilder.toString());
//        }
        return Collections.emptyList();
    }


    /*################################## blow abstract method ##################################*/

    protected abstract boolean needModifyPrecisionOrScale(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo)
            throws SchemaInfoException, MetaException;

    protected abstract boolean needModifyDefault(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo)
            throws SchemaInfoException, MetaException;

    /**
     * @return tue: sqlTypeName is synonym of  fieldMeta's SQLDataType
     */
    protected abstract boolean synonyms(FieldMeta<?, ?> fieldMeta, String sqlTypeName);

    protected abstract Database database();

    /*################################## blow protected final method ##################################*/

    protected final String obtainDefaultValue(FieldMeta<?, ?> fieldMeta) {
//        String defaultValue = fieldMeta.defaultValue();
//        Database database = database();
//        SqlDataType dataType;// = fieldMeta.mappingMeta().sqlDataType(database);
//        StringBuilder builder;
//        switch (defaultValue) {
//            case IDomain.NOW:
//                builder = DialectUtils.createSQLBuilder();
//                // dataType.nowValue(fieldMeta, builder, database);
//                defaultValue = builder.toString();
//                break;
//            case IDomain.ZERO_VALUE:
//                builder = DialectUtils.createSQLBuilder();
//                // dataType.zeroValue(fieldMeta, builder, database);
//                defaultValue = builder.toString();
//                break;
//            default:
//        }
        return null;
    }

    /*################################## blow private method ##################################*/

    @Nullable
    private MigrationMemberImpl doMigrateTable(TableMeta<?> tableMeta, @Nullable String tableSuffix, TableInfo tableInfo) {
        _Assert.state(tableMeta.tableName().equals(tableInfo.name()),
                () -> String.format("TableMeta[%s] then TableInfo[%s] not match",
                        tableMeta.tableName(), tableInfo.name()));

        MigrationMemberImpl migration = new MigrationMemberImpl(tableMeta, tableSuffix, false);

        // column migration
        migrateColumnIfNeed(tableMeta, tableInfo, migration);
        // index migration
        migrateIndexIfNeed(tableMeta, tableInfo, migration);

        if (migration.needAlter()) {
            migration.makeFinal();
        } else {
            migration = null;
        }
        return migration;
    }


    private void migrateColumnIfNeed(TableMeta<?> tableMeta, TableInfo tableInfo, MigrationMemberImpl migration) {
        final Map<String, ColumnInfo> columnInfoMap = tableInfo.columnMap();

        for (FieldMeta<?, ?> fieldMeta : tableMeta.fieldCollection()) {
            // make key lower case
            ColumnInfo columnInfo = columnInfoMap.get(StringUtils.toLowerCase(fieldMeta.columnName()));
            if (columnInfo == null) {
                // alter tableMeta add column
                migration.addColumnToAdd(fieldMeta);
            } else if (!synonyms(fieldMeta, columnInfo.sqlType())) {
                this.dataTypeErrorBuilder
                        .append("\n")
                        .append(fieldMeta)
                        .append(", SQLDataType[")
                        //  .append(fieldMeta.mappingMeta().sqlDataType(database()).typeName())
                        .append("] and ")
                        .append(columnInfo.sqlType())
                        .append(" not match.");
            } else if (needAlterColumn(fieldMeta, columnInfo)) {
                // alter tableMeta alter column
                migration.addColumnToModify(fieldMeta);
            } else if (!DDLUtils.escapeQuote(fieldMeta.comment()).equals(columnInfo.comment())) {
                migration.addCommentToModify(fieldMeta);
            }
        }

    }

    private void migrateIndexIfNeed(TableMeta<?> tableMeta, TableInfo tableInfo, MigrationMemberImpl migration) {
        final Map<String, IndexInfo> indexInfoMap = tableInfo.indexMap();
        // index migration
        Set<String> indexNameSet = new HashSet<>();

        for (IndexMeta<?> indexMeta : tableMeta.indexCollection()) {
            String indexName = StringUtils.toLowerCase(indexMeta.name());
            IndexInfo indexInfo = indexInfoMap.get(indexName);
            if (indexInfo == null) {
                if (!indexMeta.isPrimaryKey()) {
                    // alter tableMeta add index
                    migration.addIndexToAdd(indexMeta);
                }
            } else if (needAlterIndex(indexMeta, indexInfo)) {
                // alter tableMeta alter index
                migration.addIndexToModify(indexMeta);
            }
            indexNameSet.add(indexName);
        }

        // find indexes than not in index meta
        Set<String> indexNameFromSchema = new HashSet<>(indexInfoMap.keySet());
        indexNameFromSchema.removeAll(indexNameSet);
        for (String indexName : indexNameFromSchema) {
            if (primaryKeyIndex(indexInfoMap.get(indexName))) {
                continue;
            }
            migration.addIndexToDrop(indexName);
        }
    }

    private boolean primaryKeyIndex(IndexInfo indexInfo) {
        boolean yes = false;
        if ("PRIMARY".equalsIgnoreCase(indexInfo.name())) {
            yes = true;
        } else if (indexInfo.unique()) {
            Map<String, IndexColumnInfo> indexColumnInfoMap = indexInfo.columnMap();
            yes = indexColumnInfoMap.size() == 1
                    && indexColumnInfoMap.containsKey(_MetaBridge.ID);
        }
        return yes;
    }


    private boolean needAlterIndex(IndexMeta<?> indexMeta, IndexInfo indexInfo) throws SchemaInfoException {
        if (primaryKeyIndex(indexInfo)) {
            return false;
        }
        boolean need;
        if (indexMeta.unique() != indexInfo.unique()) {
            need = true;
        } else if (indexMeta.fieldList().size() != indexInfo.columnMap().size()) {
            need = true;
        } else {
            need = needAlterIndexColumn(indexMeta, indexInfo);
        }
        return need;
    }

    private boolean needAlterIndexColumn(IndexMeta<?> indexMeta, IndexInfo indexInfo) {
        Map<String, IndexColumnInfo> columnInfoMap = indexInfo.columnMap();
        boolean need = false;
        for (IndexFieldMeta<?, ?> indexFieldMeta : indexMeta.fieldList()) {
            IndexColumnInfo info = columnInfoMap.get(StringUtils.toLowerCase(indexFieldMeta.columnName()));

            if (info == null) {
                // index column not exists
                need = true;
                break;
            } else {
                Boolean fieldAsc = indexFieldMeta.fieldAsc();
                if (fieldAsc != null && fieldAsc != info.asc()) {
                    // index column asSort not match
                    need = true;
                    break;
                }
            }

        }
        return need;
    }


    private boolean needAlterColumn(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo) throws SchemaInfoException {
        return needModifyPrecisionOrScale(fieldMeta, columnInfo)
                || (this.compareDefaultOnMigrating && needModifyDefault(fieldMeta, columnInfo))
                || columnInfo.nullable() != fieldMeta.nullable();
    }


}
