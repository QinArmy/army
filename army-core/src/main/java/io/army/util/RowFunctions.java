package io.army.util;

import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.criteria.Selection;
import io.army.mapping.MappingType;
import io.army.meta.TypeMeta;
import io.army.result.DataRecord;
import io.army.stmt.SingleSqlStmt;
import io.army.stmt.TwoStmtQueryStmt;
import io.army.type.ImmutableSpec;

import io.army.lang.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public abstract class RowFunctions {

    private RowFunctions() {
        throw new UnsupportedOperationException();
    }


    public static Function<DataRecord, Map<String, Object>> hashMapRowFunc(boolean immutableMap) {
        final IntFunction<Map<String, Object>> constructor;
        constructor = _Collections::hashMapForSize;

        final SimpleRowReader<Map<String, Object>> reader;
        reader = new SimpleRowReader<>(constructor, immutableMap);
        return reader::readRow;
    }


    public static Function<DataRecord, Map<String, Object>> treeMapRowFunc(boolean immutableMap) {
        final Supplier<Map<String, Object>> constructor;
        constructor = TreeMap::new;

        final SimpleRowReader<Map<String, Object>> reader;
        reader = new SimpleRowReader<>(constructor, immutableMap);
        return reader::readRow;
    }

    public static Function<DataRecord, Map<String, Object>> linkedHashMapRowFunc(boolean immutableMap) {
        final Supplier<Map<String, Object>> constructor;
        constructor = LinkedHashMap::new;

        final SimpleRowReader<Map<String, Object>> reader;
        reader = new SimpleRowReader<>(constructor, immutableMap);
        return reader::readRow;
    }

    /**
     * @param selectionList a unmodified list
     */
    public static <R> Function<DataRecord, R> objectRowFunc(final Supplier<R> constructor,
                                                            List<? extends Selection> selectionList,
                                                            boolean immutableMap) {
        Objects.requireNonNull(constructor);
        Objects.requireNonNull(selectionList);
        final SelectionRowReader<R> reader;
        reader = new SelectionRowReader<>(constructor, null, selectionList, immutableMap);
        return reader::readRow;
    }

    /**
     * @param selectionList a unmodified list
     */
    public static <R> Function<DataRecord, R> mapRowFunc(final IntFunction<R> constructor,
                                                         List<? extends Selection> selectionList,
                                                         boolean immutableMap) {
        Objects.requireNonNull(constructor);
        Objects.requireNonNull(selectionList);
        final SelectionRowReader<R> reader;
        reader = new SelectionRowReader<>(constructor, null, selectionList, immutableMap);
        return reader::readRow;
    }

    public static <R> Function<DataRecord, R> classRowFunc(final Class<R> resultClass, final SingleSqlStmt stmt) {
        final Function<DataRecord, R> rowFunc;
        final List<? extends Selection> selectionList = stmt.selectionList();
        if ((stmt instanceof TwoStmtQueryStmt && ((TwoStmtQueryStmt) stmt).maxColumnSize() == 1)
                || selectionList.size() == 1) {
            rowFunc = record -> record.get(0, resultClass);
        } else {
            rowFunc = RowFunctions.beanRowFunc(resultClass, selectionList);
        }
        return rowFunc;
    }

    /**
     * @param selectionList a unmodified list
     */
    public static <R> Function<DataRecord, R> beanRowFunc(final Class<R> resultClass, List<? extends Selection> selectionList) {
        Objects.requireNonNull(selectionList);

        final ObjectAccessor accessor;
        accessor = ObjectAccessorFactory.forBean(resultClass);
        final SelectionRowReader<R> reader;
        reader = new SelectionRowReader<>(ObjectAccessorFactory.beanConstructor(resultClass), accessor, selectionList, false);
        return reader::readRow;
    }


    private static final class SelectionRowReader<R> {

        private final Object constructor;

        private final boolean immutableMap;

        private final List<? extends Selection> selectionList;

        private final Class<?>[] columnClassArray;

        private final String[] columnLabelArray;

        private ObjectAccessor accessor;

        private SelectionRowReader(Object constructor, @Nullable ObjectAccessor accessor,
                                   List<? extends Selection> selectionList, boolean immutableMap) {
            this.constructor = constructor;
            this.accessor = accessor;
            this.selectionList = selectionList;
            this.immutableMap = immutableMap;

            final int selectionSize = selectionList.size();
            this.columnClassArray = new Class<?>[selectionSize];

            final String[] columnLabelArray;
            this.columnLabelArray = columnLabelArray = new String[selectionSize];
            for (int i = 0; i < selectionSize; i++) {
                columnLabelArray[i] = selectionList.get(i).label();
            }
        }


        @SuppressWarnings("unchecked")
        private R readRow(final DataRecord record) {
            final int columnCount = record.getColumnCount();
            final String[] columnLabelArray = this.columnLabelArray;

            if (columnCount != columnLabelArray.length) {
                throw _Exceptions.columnCountAndSelectionCountNotMatch(columnCount, columnLabelArray.length);
            }

            final Object constructor = this.constructor;
            final R row;
            if (constructor instanceof Supplier<?>) {
                row = ((Supplier<R>) constructor).get();
            } else {
                row = ((IntFunction<R>) constructor).apply((int) (columnCount / 0.75f));
            }

            if (row == null) {
                throw _Exceptions.objectConstructorError();
            }
            ObjectAccessor accessor = this.accessor;
            if (accessor == null) {
                this.accessor = accessor = ObjectAccessorFactory.fromInstance(row);
            }


            final Class<?>[] columnClassArray = this.columnClassArray;
            final List<? extends Selection> selectionList = this.selectionList;
            TypeMeta typeMeta;

            String propertyName;
            Class<?> clumnClass;
            Object value;
            for (int i = 0; i < columnCount; i++) {
                propertyName = columnLabelArray[i];
                clumnClass = columnClassArray[i];

                if (clumnClass == null) {
                    if (row instanceof Map) {
                        typeMeta = selectionList.get(i).typeMeta();
                        if (!(typeMeta instanceof MappingType)) {
                            typeMeta = typeMeta.mappingType();
                        }
                        clumnClass = ((MappingType) typeMeta).javaType();
                    } else {
                        clumnClass = accessor.getJavaType(propertyName);
                    }
                    columnClassArray[i] = clumnClass;
                }

                value = record.get(i, clumnClass);

                accessor.set(row, propertyName, value);
            }

            final R finalRow;
            if (this.immutableMap && row instanceof Map && row instanceof ImmutableSpec) {
                finalRow = (R) _Collections.unmodifiableMap((Map<String, Object>) row);
            } else {
                finalRow = row;
            }
            return finalRow;
        }


    } // SelectionRowReader

    private static final class SimpleRowReader<R> {

        private final Object constructor;

        private final boolean immutableMap;

        private Class<?>[] columnClassArray;

        private String[] columnLabelArray;

        private ObjectAccessor accessor;

        private SimpleRowReader(Object constructor, boolean immutableMap) {
            this.constructor = constructor;
            this.immutableMap = immutableMap;
        }


        @SuppressWarnings("unchecked")
        private R readRow(final DataRecord record) {
            final int columnCount = record.getColumnCount();

            String[] columnLabelArray = this.columnLabelArray;
            Class<?>[] columnClassArray = this.columnClassArray;
            if (columnLabelArray == null) {

                this.columnLabelArray = columnLabelArray = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    columnLabelArray[i] = record.getColumnLabel(i);
                }
            } else if (columnCount != columnLabelArray.length) {
                throw _Exceptions.columnCountAndSelectionCountNotMatch(columnCount, columnLabelArray.length);
            }


            final Object constructor = this.constructor;
            final R row;
            if (constructor instanceof Supplier<?>) {
                row = ((Supplier<R>) constructor).get();
            } else {
                row = ((IntFunction<R>) constructor).apply((int) (columnCount / 0.75f));
            }

            if (row == null) {
                throw _Exceptions.objectConstructorError();
            }
            ObjectAccessor accessor = this.accessor;
            if (accessor == null) {
                this.accessor = accessor = ObjectAccessorFactory.fromInstance(row);
            }

            final boolean rowIsMap = row instanceof Map;

            String propertyName;
            Class<?> clumnClass;
            Object value;
            for (int i = 0; i < columnCount; i++) {
                propertyName = columnLabelArray[i];

                if (rowIsMap) {
                    value = record.get(i); // TODO add expression column label
                } else {
                    if (columnClassArray == null) {
                        this.columnClassArray = columnClassArray = new Class<?>[columnCount];
                    }
                    clumnClass = columnClassArray[i];
                    if (clumnClass == null) {
                        clumnClass = accessor.getJavaType(propertyName);
                        columnClassArray[i] = clumnClass;
                    }
                    value = record.get(i, clumnClass);
                }

                accessor.set(row, propertyName, value);

            }

            final R finalRow;
            if (this.immutableMap && row instanceof Map && row instanceof ImmutableSpec) {
                finalRow = (R) _Collections.unmodifiableMap((Map<String, Object>) row);
            } else {
                finalRow = row;
            }
            return finalRow;
        }


    } // SimpleRowReader


}
