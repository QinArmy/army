package io.army.session.executor;

import io.army.ArmyException;
import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.criteria.Selection;
import io.army.criteria.TypeInfer;
import io.army.function.IntBiFunction;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.meta.TypeMeta;
import io.army.session.*;
import io.army.sqltype.SqlType;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public abstract class ExecutorSupport {

    protected ExecutorSupport() {

    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    protected final ArmyException unsupportedIsolation(Isolation isolation) {
        return new ArmyException(String.format("%s don't support %s", this, isolation));
    }


    protected static int restSeconds(final int timeout, final long startTime) {
        if (timeout == 0) {
            return 0;
        }
        if (timeout < 0) {
            //no bug,never here
            throw new IllegalArgumentException();
        }
        int restSec;
        final long restMills;
        restMills = (timeout * 1000L) - (System.currentTimeMillis() - startTime);
        if (restMills < 1) {
            throw _Exceptions.timeout(timeout, restMills);
        }
        restSec = (int) (restMills / 1000L);
        if (restMills % 1000L != 0) {
            restSec++;
        }
        return restSec;
    }


    protected static MappingType compatibleTypeFrom(final TypeInfer infer, final @Nullable Class<?> resultClass,
                                                    final ObjectAccessor accessor, final String fieldName)
            throws NoMatchMappingException {
        final MappingType type;
        if (infer instanceof MappingType) {
            type = (MappingType) infer;
        } else if (infer instanceof TypeMeta) {
            type = ((TypeMeta) infer).mappingType();
        } else {
            final TypeMeta meta = infer.typeMeta();
            if (meta instanceof MappingType) {
                type = (MappingType) meta;
            } else {
                type = meta.mappingType();
            }
        }

        final MappingType compatibleType;
        if (accessor == ObjectAccessorFactory.PSEUDO_ACCESSOR) {
            assert resultClass != null;
            if (resultClass.isAssignableFrom(type.javaType())) {
                compatibleType = type;
            } else {
                compatibleType = type.compatibleFor(resultClass);
            }
        } else if (accessor.isWritable(fieldName, type.javaType())) {
            compatibleType = type;
        } else {
            compatibleType = type.compatibleFor(accessor.getJavaType(fieldName));
        }
        return compatibleType;
    }

    @SuppressWarnings("unchecked")
    protected static <R> Class<R> rowResultClass(R row) {
        final Class<?> resultClass;
        if (row instanceof Map) {
            resultClass = Map.class;
        } else {
            resultClass = row.getClass();
        }
        return (Class<R>) resultClass;
    }


    /**
     * @return a unmodified map
     */
    protected static Map<String, Integer> createAliasToIndexMap(final List<? extends Selection> selectionList) {
        final int selectionSize = selectionList.size();
        Map<String, Integer> map = _Collections.hashMap((int) (selectionSize / 0.75f));
        for (int i = 0; i < selectionSize; i++) {
            map.put(selectionList.get(i).label(), i); // If alias duplication,then override.
        }
        return _Collections.unmodifiableMap(map);
    }

    protected static <T> T convertToTarget(Object source, Class<T> targetClass) {
        throw new UnsupportedOperationException();
    }

    /*-------------------below Exception  -------------------*/


    protected static NullPointerException currentRecordColumnIsNull(int indexBasedZero, Selection selection) {
        String m = String.format("value is null of current record index[%s] selection label[%s] ",
                indexBasedZero, selection.label());
        return new NullPointerException(m);
    }

    protected static NullPointerException currentRecordDefaultValueNonNull() {
        return new NullPointerException("current record default must non-null");
    }

    protected static NullPointerException currentRecordSupplierReturnNull(Supplier<?> supplier) {
        String m = String.format("current record %s %s return null", Supplier.class.getName(), supplier);
        return new NullPointerException(m);
    }


    protected static DataAccessException secondQueryRowCountNotMatch(final int firstRowCount, final int secondRowCount) {
        String m = String.format("second query row count[%s] and first query row[%s] not match.",
                secondRowCount, firstRowCount);
        return new DataAccessException(m);
    }


    public static ArmyException executorFactoryClosed(StmtExecutorFactorySpec factory) {
        String m = String.format("%s have closed.", factory);
        return new ArmyException(m);
    }


    protected static final class ArmyResultRecordMeta implements ResultRecordMeta {

        private final int resultNo;

        private final List<? extends Selection> selectionList;

        private final IntFunction<SqlType> sqlTypeFunc;

        private final IntBiFunction<Option<?>, ?> optionFunc;

        private final int columnSize;

        private final Map<String, Integer> aliasToIndexMap;

        private List<String> columnLabelList;

        public ArmyResultRecordMeta(int resultNo, List<? extends Selection> selectionList,
                                    IntFunction<SqlType> sqlTypeFunc, IntBiFunction<Option<?>, ?> optionFunc) {
            this.resultNo = resultNo;
            this.selectionList = selectionList;
            this.sqlTypeFunc = sqlTypeFunc;
            this.optionFunc = optionFunc;

            this.columnSize = selectionList.size();
            if (this.columnSize < 6) {
                this.aliasToIndexMap = null;
            } else {
                this.aliasToIndexMap = createAliasToIndexMap(selectionList);
            }
        }


        @Override
        public int getResultNo() {
            return this.resultNo;
        }

        @Override
        public int getColumnCount() {
            return this.columnSize;
        }

        @Override
        public String getColumnLabel(int indexBasedZero) throws IllegalArgumentException {
            return this.selectionList.get(checkIndex(indexBasedZero)).label();
        }

        @Override
        public int getColumnIndex(final @Nullable String columnLabel) throws IllegalArgumentException {
            if (columnLabel == null) {
                throw new NullPointerException("columnLabel is null");
            }
            int index = -1;
            final Map<String, Integer> aliasToIndexMap = this.aliasToIndexMap;
            if (aliasToIndexMap == null) {
                final List<? extends Selection> selectionList = this.selectionList;
                for (int i = this.columnSize - 1; i > -1; i--) {  // If alias duplication,then override.
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
        public List<? extends Selection> selectionList() {
            return this.selectionList;
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
        public Selection getSelection(int indexBasedZero) {
            return this.selectionList.get(checkIndex(indexBasedZero));
        }

        @Override
        public SqlType getSqlType(int indexBasedZero) {
            return this.sqlTypeFunc.apply(checkIndex(indexBasedZero));
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getOf(final int indexBasedZero, Option<T> option) {
            final Object value;
            value = this.optionFunc.apply(checkIndex(indexBasedZero), option);
            final T finalValue;
            if (value == null || option.javaType().isInstance(value)) {
                finalValue = (T) value;
            } else {
                finalValue = null;
            }
            return finalValue;
        }

        @Override
        public <T> T getNonNullOf(int indexBasedZero, Option<T> option) {
            final T value;
            value = getOf(indexBasedZero, option);
            if (value == null) {
                String m = String.format("option value is null,index[%s]", indexBasedZero);
                throw new NullPointerException(m);
            }
            return value;
        }

        public int checkIndex(final int indexBasedZero) {
            if (indexBasedZero < 0 || indexBasedZero >= this.columnSize) {
                String m = String.format("index[%s] not in [0,)", this.columnSize);
                throw new IllegalArgumentException(m);
            }
            return indexBasedZero;
        }

    }// ArmyResultRecordMeta


    private static abstract class ArmyDataRecord implements DataRecord {

        protected final ArmyResultRecordMeta meta;

        private ArmyDataRecord(ArmyResultRecordMeta meta) {
            this.meta = meta;
        }

        private ArmyDataRecord(ArmyCurrentRecord currentRecord) {
            this.meta = currentRecord.meta;
        }

        @Override
        public final ResultRecordMeta getRecordMeta() {
            return this.meta;
        }

        @Override
        public final int getResultNo() {
            return this.meta.resultNo;
        }

        @Override
        public final int getColumnCount() {
            return this.meta.columnSize;
        }

        @Override
        public final String getColumnLabel(int indexBasedZero) throws IllegalArgumentException {
            return this.meta.getColumnLabel(indexBasedZero);
        }

        @Override
        public final int getColumnIndex(String columnLabel) throws IllegalArgumentException {
            return this.meta.getColumnIndex(columnLabel);
        }


        @Override
        public final Object getNonNull(int indexBasedZero) {
            final Object value;
            value = get(indexBasedZero);
            if (value == null) {
                throw currentRecordColumnIsNull(indexBasedZero, this.meta.selectionList.get(indexBasedZero));
            }
            return value;
        }

        @Override
        public final Object getOrDefault(int indexBasedZero, @Nullable Object defaultValue) {
            if (defaultValue == null) {
                throw currentRecordDefaultValueNonNull();
            }
            Object value;
            value = get(indexBasedZero);
            if (value == null) {
                value = defaultValue;
            }
            return value;
        }

        @Override
        public final Object getOrSupplier(int indexBasedZero, Supplier<?> supplier) {
            Object value;
            value = get(indexBasedZero);
            if (value == null) {
                if ((value = supplier.get()) == null) {
                    throw currentRecordSupplierReturnNull(supplier);
                }
            }
            return value;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final <T> T get(int indexBasedZero, Class<T> columnClass) {
            final Object value;
            value = get(indexBasedZero);
            if (value == null || columnClass.isInstance(value)) {
                return (T) value;
            }
            return convertToTarget(value, columnClass);
        }

        @Override
        public final <T> T getNonNull(int indexBasedZero, Class<T> columnClass) {
            final T value;
            value = get(indexBasedZero, columnClass);
            if (value == null) {
                throw currentRecordColumnIsNull(indexBasedZero, this.meta.selectionList.get(indexBasedZero));
            }
            return value;
        }

        @Override
        public final <T> T getOrDefault(int indexBasedZero, Class<T> columnClass, final @Nullable T defaultValue) {
            if (defaultValue == null) {
                throw currentRecordDefaultValueNonNull();
            }
            T value;
            value = get(indexBasedZero, columnClass);
            if (value == null) {
                value = defaultValue;
            }
            return value;
        }

        @Override
        public final <T> T getOrSupplier(int indexBasedZero, Class<T> columnClass, Supplier<T> supplier) {
            T value;
            value = get(indexBasedZero, columnClass);
            if (value == null) {
                if ((value = supplier.get()) == null) {
                    throw currentRecordSupplierReturnNull(supplier);
                }
            }
            return value;
        }

        /*-------------------below label methods -------------------*/

        @Override
        public final Object get(String selectionLabel) {
            return get(getRecordMeta().getColumnIndex(selectionLabel));
        }

        @Override
        public final Object getNonNull(String selectionLabel) {
            return getNonNull(getRecordMeta().getColumnIndex(selectionLabel));
        }

        @Override
        public final Object getOrDefault(String selectionLabel, Object defaultValue) {
            return getOrDefault(getRecordMeta().getColumnIndex(selectionLabel), defaultValue);
        }

        @Override
        public final Object getOrSupplier(String selectionLabel, Supplier<?> supplier) {
            return getOrSupplier(getRecordMeta().getColumnIndex(selectionLabel), supplier);
        }

        @Override
        public final <T> T get(String selectionLabel, Class<T> columnClass) {
            return get(getRecordMeta().getColumnIndex(selectionLabel), columnClass);
        }

        @Override
        public final <T> T getNonNull(String selectionLabel, Class<T> columnClass) {
            return getNonNull(getRecordMeta().getColumnIndex(selectionLabel), columnClass);
        }

        @Override
        public final <T> T getOrDefault(String selectionLabel, Class<T> columnClass, T defaultValue) {
            return getOrDefault(getRecordMeta().getColumnIndex(selectionLabel), columnClass, defaultValue);
        }

        @Override
        public final <T> T getOrSupplier(String selectionLabel, Class<T> columnClass, Supplier<T> supplier) {
            return getOrSupplier(getRecordMeta().getColumnIndex(selectionLabel), columnClass, supplier);
        }


    }//ArmyDataRecord

    protected static abstract class ArmyCurrentRecord extends ArmyDataRecord implements CurrentRecord {


        public ArmyCurrentRecord(ArmyResultRecordMeta meta) {
            super(meta);
        }


        @Override
        public final ResultRecord asResultRecord() {
            return new ArmyResultRecord(this);
        }

        protected abstract Object[] copyValueArray();


    }// ArmyCurrentRecord


    private static final class ArmyResultRecord extends ArmyDataRecord implements ResultRecord {

        private final Object[] valueArray;

        public ArmyResultRecord(ArmyCurrentRecord currentRecord) {
            super(currentRecord);
            this.valueArray = currentRecord.copyValueArray();
            assert this.valueArray.length == currentRecord.meta.columnSize;
        }


        @Override
        public Object get(int indexBasedZero) {
            return this.valueArray[this.meta.checkIndex(indexBasedZero)];
        }

    }// ArmyResultRecord


}
