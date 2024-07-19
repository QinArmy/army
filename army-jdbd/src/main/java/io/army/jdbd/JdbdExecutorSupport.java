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

import io.army.criteria.Selection;
import io.army.reactive.ReactiveMultiResultSpec;
import io.army.reactive.executor.ReactiveExecutorSupport;
import io.army.session.DataAccessException;
import io.army.session.Option;
import io.army.session.record.CurrentRecord;
import io.army.session.record.FieldType;
import io.army.session.record.KeyType;
import io.army.session.record.ResultStates;
import io.army.sqltype.DataType;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.jdbd.result.ResultRowMeta;
import reactor.core.publisher.Flux;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class JdbdExecutorSupport extends ReactiveExecutorSupport {

    JdbdExecutorSupport() {
    }


    private static abstract class JdbdRecordMeta extends ArmyResultRecordMeta {

        final JdbdExecutor executor;

        final ResultRowMeta meta;

        private Set<Option<?>> optionSet;

        private JdbdRecordMeta(int resultNo, DataType[] dataTypeArray, JdbdExecutor executor, ResultRowMeta meta) {
            super(resultNo, dataTypeArray, executor.factory.executorEnv);
            this.executor = executor;
            this.meta = meta;
        }


        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public final <T> T getOf(final int indexBasedZero, Option<T> option) throws DataAccessException {
            try {
                final io.jdbd.session.Option<T> jdbdOption;
                jdbdOption = (io.jdbd.session.Option<T>) this.executor.factory.mapToJdbdOption(option);
                if (jdbdOption == null) {
                    return null;
                }

                return this.meta.getOf(indexBasedZero, jdbdOption);
            } catch (Exception e) {
                throw this.executor.wrapExecutingError(e);
            }
        }

        @Override
        public final Set<Option<?>> optionSet() {
            Set<Option<?>> optionSet = this.optionSet;
            if (optionSet == null) {
                this.optionSet = optionSet = this.executor.factory.mapArmyOptionSet(this.meta.optionSet());
            }
            return optionSet;
        }

        @Nullable
        @Override
        public final String getCatalogName(int indexBasedZero) throws DataAccessException {
            try {
                return this.meta.getCatalogName(indexBasedZero);
            } catch (Exception e) {
                throw this.executor.wrapExecutingError(e);
            }
        }

        @Nullable
        @Override
        public final String getSchemaName(int indexBasedZero) throws DataAccessException {
            try {
                return this.meta.getSchemaName(indexBasedZero);
            } catch (Exception e) {
                throw this.executor.wrapExecutingError(e);
            }
        }

        @Nullable
        @Override
        public final String getTableName(int indexBasedZero) throws DataAccessException {
            try {
                return this.meta.getTableName(indexBasedZero);
            } catch (Exception e) {
                throw this.executor.wrapExecutingError(e);
            }
        }

        @Nullable
        @Override
        public final String getColumnName(int indexBasedZero) throws DataAccessException {
            try {
                return this.meta.getColumnName(indexBasedZero);
            } catch (Exception e) {
                throw this.executor.wrapExecutingError(e);
            }
        }

        @Override
        public final int getPrecision(int indexBasedZero) throws DataAccessException {
            try {
                return this.meta.getPrecision(indexBasedZero);
            } catch (Exception e) {
                throw this.executor.wrapExecutingError(e);
            }
        }

        @Override
        public final int getScale(int indexBasedZero) throws DataAccessException {
            try {
                return this.meta.getScale(indexBasedZero);
            } catch (Exception e) {
                throw this.executor.wrapExecutingError(e);
            }
        }

        @Override
        public final FieldType getFieldType(int indexBasedZero) throws DataAccessException {
            try {
                final FieldType type;
                switch (this.meta.getFieldType(indexBasedZero)) {
                    case FIELD:
                    case PHYSICAL_FILED:
                        type = FieldType.FIELD;
                        break;
                    case EXPRESSION:
                        type = FieldType.EXPRESSION;
                        break;
                    case UNKNOWN:
                        type = FieldType.UNKNOWN;
                        break;
                    default:
                        throw _Exceptions.unexpectedEnum(this.meta.getFieldType(indexBasedZero));
                }
                return type;
            } catch (Exception e) {
                throw this.executor.wrapExecutingError(e);
            }
        }

        @Nullable
        @Override
        public final Boolean getAutoIncrementMode(int indexBasedZero) throws DataAccessException {
            try {
                final Boolean mode;
                switch (this.meta.getAutoIncrementMode(indexBasedZero)) {
                    case TRUE:
                        mode = Boolean.TRUE;
                        break;
                    case FALSE:
                        mode = Boolean.FALSE;
                        break;
                    case UNKNOWN:
                        mode = null;
                        break;
                    default:
                        throw _Exceptions.unexpectedEnum(this.meta.getAutoIncrementMode(indexBasedZero));
                }
                return mode;
            } catch (Exception e) {
                throw this.executor.wrapExecutingError(e);
            }
        }

        @Override
        public final KeyType getKeyMode(int indexBasedZero) throws DataAccessException {
            try {
                final KeyType type;
                switch (this.meta.getKeyMode(indexBasedZero)) {
                    case NONE:
                        type = KeyType.NONE;
                        break;
                    case INDEX_KEY:
                        type = KeyType.INDEX_KEY;
                        break;
                    case UNIQUE_KEY:
                        type = KeyType.UNIQUE_KEY;
                        break;
                    case PRIMARY_KEY:
                        type = KeyType.PRIMARY_KEY;
                        break;
                    case SPATIAL_KEY:
                        type = KeyType.SPATIAL_KEY;
                        break;
                    case FULL_TEXT_KEY:
                        type = KeyType.FULL_TEXT_KEY;
                        break;
                    case UNKNOWN:
                        type = KeyType.UNKNOWN;
                        break;
                    default:
                        throw _Exceptions.unexpectedEnum(this.meta.getKeyMode(indexBasedZero));
                }
                return type;
            } catch (Exception e) {
                throw this.executor.wrapExecutingError(e);
            }
        }

        @Nullable
        @Override
        public final Boolean getNullableMode(int indexBasedZero) throws DataAccessException {
            try {
                final Boolean mode;
                switch (this.meta.getNullableMode(indexBasedZero)) {
                    case TRUE:
                        mode = Boolean.TRUE;
                        break;
                    case FALSE:
                        mode = Boolean.FALSE;
                        break;
                    case UNKNOWN:
                        mode = null;
                        break;
                    default:
                        throw _Exceptions.unexpectedEnum(this.meta.getNullableMode(indexBasedZero));
                }
                return mode;
            } catch (Exception e) {
                throw this.executor.wrapExecutingError(e);
            }
        }

        @Override
        public final Class<?> getFirstJavaType(int indexBasedZero) throws DataAccessException {
            try {
                return this.meta.getFirstJavaType(indexBasedZero);
            } catch (Exception e) {
                throw this.executor.wrapExecutingError(e);
            }
        }

        @Nullable
        @Override
        public final Class<?> getSecondJavaType(int indexBasedZero) throws DataAccessException {
            try {
                return this.meta.getSecondJavaType(indexBasedZero);
            } catch (Exception e) {
                throw this.executor.wrapExecutingError(e);
            }
        }


    } // JdbdRecordMeta


    static final class JdbdStmtRecordMeta extends JdbdRecordMeta {

        final List<? extends Selection> selectionList;

        private Map<String, Integer> aliasToIndexMap;

        private List<String> columnLabelList;

        JdbdStmtRecordMeta(int resultNo, DataType[] dataTypeArray, List<? extends Selection> selectionList,
                           JdbdExecutor executor, ResultRowMeta meta) {
            super(resultNo, dataTypeArray, executor, meta);
            this.selectionList = selectionList;
            assert selectionList.size() == dataTypeArray.length;
        }

        @Override
        public String getColumnLabel(final int indexBasedZero) throws DataAccessException {
            return this.selectionList.get(checkIndex(indexBasedZero)).label();
        }

        @Override
        public int getColumnIndex(final @Nullable String columnLabel) throws DataAccessException {
            if (columnLabel == null) {
                throw new NullPointerException("columnLabel is null");
            }
            final List<? extends Selection> selectionList = this.selectionList;

            Map<String, Integer> aliasToIndexMap = this.aliasToIndexMap;
            if (aliasToIndexMap == null && selectionList.size() > 5) {
                this.aliasToIndexMap = aliasToIndexMap = createAliasToIndexMap(selectionList);
            }

            int index = -1;
            if (aliasToIndexMap == null) {

                final int columnSize = selectionList.size();
                for (int i = columnSize - 1; i > -1; i--) {  // If alias duplication,then override.
                    if (columnLabel.equals(selectionList.get(i).label())) {
                        index = i;
                        break;
                    }
                }
            } else {
                index = aliasToIndexMap.getOrDefault(columnLabel, -1);
            }
            if (index < 0) {
                throw _Exceptions.unknownSelectionAlias(columnLabel);
            }
            return index;
        }

        @Override
        public List<String> columnLabelList() {
            List<String> list = this.columnLabelList;
            if (list != null) {
                return list;
            }
            final List<? extends Selection> selectionList = this.selectionList;
            list = _Collections.arrayList(selectionList.size());
            for (Selection selection : selectionList) {
                list.add(selection.label());
            }
            this.columnLabelList = list = _Collections.unmodifiableList(list);
            return list;
        }

        @Override
        public List<? extends Selection> selectionList() throws DataAccessException {
            return this.selectionList;
        }

        @Override
        public Selection getSelection(int indexBasedZero) throws DataAccessException {
            return this.selectionList.get(checkIndex(indexBasedZero));
        }


    } // JdbdStmtRowMeta


    protected static abstract class ArmyReactiveMultiResultSpec implements ReactiveMultiResultSpec {

        @Override
        public final <R> Flux<R> nextQuery(Class<R> resultClass) {
            return this.nextQuery(resultClass, ResultStates.IGNORE_STATES);
        }

        @Override
        public final <R> Flux<Optional<R>> nextQueryOptional(Class<R> resultClass) {
            return this.nextQueryOptional(resultClass, ResultStates.IGNORE_STATES);
        }

        @Override
        public final <R> Flux<R> nextQueryObject(Supplier<R> constructor) {
            return this.nextQueryObject(constructor, ResultStates.IGNORE_STATES);
        }

        @Override
        public final <R> Flux<R> nextQueryRecord(Function<CurrentRecord, R> function) {
            return this.nextQueryRecord(function, ResultStates.IGNORE_STATES);
        }


    }// ArmyQueryResultSpec
}
