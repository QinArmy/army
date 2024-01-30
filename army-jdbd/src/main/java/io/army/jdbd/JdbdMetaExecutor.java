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

package io.army.jdbd;

import io.army.env.ArmyKey;
import io.army.env.SqlLogMode;
import io.army.reactive.executor.ReactiveMetaExecutor;
import io.army.schema.*;
import io.army.session.executor.ExecutorSupport;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;
import io.jdbd.meta.*;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.Option;
import io.jdbd.util.SqlLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

final class JdbdMetaExecutor extends ExecutorSupport implements ReactiveMetaExecutor {

    static ReactiveMetaExecutor create(JdbdStmtExecutorFactory factory, DatabaseSession session) {
        return new JdbdMetaExecutor(factory, session);
    }

    /**
     * non-static
     */
    private final Logger logger = LoggerFactory.getLogger(JdbdMetaExecutor.class);

    private final JdbdStmtExecutorFactory factory;

    private final DatabaseSession session;

    private final SqlLogger jdbdSqlLogger;


    private JdbdMetaExecutor(JdbdStmtExecutorFactory factory, DatabaseSession session) {
        this.factory = factory;
        this.session = session;
        if (this.factory.armyEnv.getOrDefault(ArmyKey.SQL_LOG_PRINT_META) && readSqlLogMode(factory) != SqlLogMode.OFF) {
            this.jdbdSqlLogger = this::jdbdSqlLogger;
        } else {
            this.jdbdSqlLogger = null;
        }
    }

    @Override
    public Mono<SchemaInfo> extractInfo() {
        final DatabaseMetaData meta;
        meta = this.session.databaseMetaData();

        return Mono.from(meta.currentSchema(obtainDefaultOptionFunc()))
                .flatMap(schemaMeta -> extractSchemaInfo(meta, schemaMeta));

    }


    @Override
    public Mono<Void> executeDdl(List<String> ddlList) {
        if (ddlList.size() == 0) {
            return Mono.empty();
        }
        return Flux.from(this.session.executeBatchUpdate(ddlList))
                .then();
    }


    @Override
    public boolean isClosed() {
        return this.session.isClosed();
    }

    @Override
    public <T> Mono<T> close() {
        return Mono.defer(() -> Mono.from(this.session.close()));
    }


    @Override
    public String toString() {
        return _StringUtils.builder(70)
                .append(getClass().getName())
                .append("[sessionFactoryName:")
                .append(this.factory.sessionFactoryName)
                .append(",executorHash:")
                .append(System.identityHashCode(this))
                .append(']')
                .toString();
    }





    /*-------------------below private methods -------------------*/

    private Function<io.jdbd.session.Option<?>, ?> obtainDefaultOptionFunc() {
        final Function<io.jdbd.session.Option<?>, ?> jdbdOptionFunc;
        if (this.jdbdSqlLogger == null) {
            jdbdOptionFunc = io.jdbd.session.Option.EMPTY_OPTION_FUNC;
        } else {
            jdbdOptionFunc = io.jdbd.session.Option.singleFunc(Option.SQL_LOGGER, this.jdbdSqlLogger);
        }
        return jdbdOptionFunc;
    }

    private void jdbdSqlLogger(final String sessionName, final int jdbdSessionHash, final String sql) {
        printSqlIfNeed(this.factory, this.factory.sessionFactoryName, this.logger, sql);
    }


    /**
     * @see #extractInfo()
     */
    private Mono<SchemaInfo> extractSchemaInfo(final DatabaseMetaData meta, final SchemaMeta schemaMeta) {

        final Map<io.jdbd.session.Option<?>, Object> optionMap;

        optionMap = _Collections.hashMap();
        optionMap.put(Option.TYPE_NAME, "TABLE,VIEW");

        if (this.jdbdSqlLogger != null) {
            optionMap.put(Option.SQL_LOGGER, this.jdbdSqlLogger);
        }

        return Flux.from(meta.tablesOfSchema(schemaMeta, optionMap::get))
                .flatMap(tableMeta -> flatMapToTableBuilder(meta, tableMeta))
                .collectMap(TableInfo.Builder::name, builder -> builder)
                .map(builderMap -> mapToSchemaInfo(schemaMeta, builderMap));
    }


    private Mono<TableInfo.Builder> flatMapToTableBuilder(final DatabaseMetaData meta, final TableMeta tableMeta) {
        final TableInfo.Builder tableBuilder;
        tableBuilder = TableInfo.builder(true, tableMeta.tableName())
                .comment(tableMeta.comment());

        switch (tableMeta.nonNullOf(Option.TYPE_NAME)) {
            case TableMeta.TABLE:
                tableBuilder.type(TableType.TABLE);
                break;
            case TableMeta.VIEW:
                tableBuilder.type(TableType.VIEW);
                break;
            default:
                // driver no bug,never here
                return Mono.error(ExecutorSupport.driverError());
        }// switch

        final Function<io.jdbd.session.Option<?>, ?> optionFunc;
        optionFunc = obtainDefaultOptionFunc();

        return Flux.from(meta.columnsOfTable(tableMeta, optionFunc))
                .doOnNext(columnMeta -> appendColumnInfo(tableBuilder, columnMeta))
                .thenMany(meta.indexesOfTable(tableMeta, optionFunc))
                .doOnNext(indexMeta -> appendIndexInfo(tableBuilder, indexMeta))
                .then(Mono.just(tableBuilder));
    }


    private void appendColumnInfo(final TableInfo.Builder tableBuilder, final TableColumnMeta meta) {
        final ColumnInfo.Builder builder;
        builder = ColumnInfo.builder()
                .name(meta.columnName())
                .type(meta.dataType().typeName())
                .scale(meta.scale())
                .comment(meta.comment())
                .autoincrement(meta.autoincrementMode() == BooleanMode.TRUE)
                .defaultExp(meta.defaultValue());

        switch (meta.nullableMode()) {
            case TRUE:
                builder.nullable(Boolean.TRUE);
                break;
            case FALSE:
                builder.nullable(Boolean.FALSE);
                break;
            case UNKNOWN:
            default:
                // no-op
        }

        final long precision;
        precision = meta.precision();
        if (precision > Integer.MAX_VALUE) {
            builder.precision(Integer.MAX_VALUE);
        } else {
            builder.precision((int) precision);
        }

        tableBuilder.appendColumn(builder.buildAndClear());

    }

    private void appendIndexInfo(final TableInfo.Builder tableBuilder, final TableIndexMeta meta) {

        final IndexInfo.Builder builder;
        builder = IndexInfo.builder()
                .name(meta.indexName())
                .type(meta.indexType())
                .unique(meta.isUnique());

        for (IndexColumnMeta columnMeta : meta.indexColumnList()) {
            switch (columnMeta.sorting()) {
                case NONE:
                    builder.appendColumn(columnMeta.columnName(), null);
                    break;
                case ASC:
                    builder.appendColumn(columnMeta.columnName(), Boolean.TRUE);
                    break;
                case DESC:
                    builder.appendColumn(columnMeta.columnName(), Boolean.FALSE);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(columnMeta.sorting());
            }

        }

        tableBuilder.appendIndex(builder.buildAndClear());
    }


    private SchemaInfo mapToSchemaInfo(final SchemaMeta schemaMeta, Map<String, TableInfo.Builder> builderMap) {
        final String catalog, schema;
        if (schemaMeta.isPseudoCatalog()) {
            catalog = null;
            schema = schemaMeta.schema();
        } else if (schemaMeta.isPseudoSchema()) {
            catalog = schemaMeta.catalog();
            schema = null;
        } else {
            catalog = schemaMeta.catalog();
            schema = schemaMeta.schema();
        }
        return SchemaInfo.create(catalog, schema, builderMap);
    }


}
