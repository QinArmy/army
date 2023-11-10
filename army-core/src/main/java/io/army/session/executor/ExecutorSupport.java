package io.army.session.executor;

import io.army.ArmyException;
import io.army.bean.ObjectAccessException;
import io.army.bean.ObjectAccessor;
import io.army.bean.ReadAccessor;
import io.army.criteria.CriteriaException;
import io.army.criteria.Selection;
import io.army.criteria.TypeInfer;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.meta.TypeMeta;
import io.army.session.DataAccessException;
import io.army.session.Isolation;
import io.army.session.Option;
import io.army.session.record.*;
import io.army.sqltype.ArmyType;
import io.army.sqltype.DataType;
import io.army.sqltype.SqlType;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class ExecutorSupport {


    protected static final ObjectAccessor SINGLE_COLUMN_PSEUDO_ACCESSOR = new PseudoWriterAccessor();

    protected static final ObjectAccessor RECORD_PSEUDO_ACCESSOR = new PseudoWriterAccessor();

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
        if (accessor == SINGLE_COLUMN_PSEUDO_ACCESSOR) {
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

    /**
     * This method is designed for second query,so :
     * <ul>
     *     <li>resultList should be {@link java.util.ArrayList}</li>
     *     <li>If accessor is {@link ExecutorSupport#SINGLE_COLUMN_PSEUDO_ACCESSOR} ,then resultList representing single column row</li>
     * </ul>
     */
    protected static <R> Map<Object, R> createIdToRowMap(final List<R> resultList, final String idFieldName,
                                                         final ObjectAccessor accessor) {
        final int rowSize = resultList.size();
        final Map<Object, R> map = _Collections.hashMap((int) (rowSize / 0.75f));
        final boolean singleColumnRow = accessor == SINGLE_COLUMN_PSEUDO_ACCESSOR;

        Object id;
        R row;
        for (int i = 0; i < rowSize; i++) {

            row = resultList.get(i);

            if (row == null) {
                // no bug,never here
                throw new NullPointerException(String.format("%s row is null", i + 1));
            }

            if (singleColumnRow) {
                id = row;
            } else {
                id = accessor.get(row, idFieldName);
            }

            if (id == null) {
                // no bug,never here
                throw new NullPointerException(String.format("%s row id is null", i + 1));
            }

            if (map.putIfAbsent(id, row) != null) {
                throw new CriteriaException(String.format("%s row id[%s] duplication", i + 1, id));
            }

        } // for loop


        return _Collections.unmodifiableMap(map);
    }

    protected static <T> T convertToTarget(Object source, Class<T> targetClass) {
        throw new UnsupportedOperationException();
    }



    /*-------------------below Exception  -------------------*/


    protected static NullPointerException currentRecordColumnIsNull(int indexBasedZero, String columnLabel) {
        String m = String.format("value is null of current record index[%s] column label[%s] ",
                indexBasedZero, columnLabel);
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

    protected static DataAccessException transactionExistsRejectStart(String sessionName) {
        String m = String.format("Session[%s] in transaction ,reject start a new transaction before commit or rollback.", sessionName);
        return new DataAccessException(m);
    }

    protected static DataAccessException unknownIsolation(String isolation) {
        String m = String.format("unknown isolation %s", isolation);
        return new DataAccessException(m);
    }


    public static ArmyException executorFactoryClosed(StmtExecutorFactorySpec factory) {
        String m = String.format("%s have closed.", factory);
        return new ArmyException(m);
    }


    protected static abstract class ArmyResultRecordMeta implements ResultRecordMeta {

        private final int resultNo;

        final DataType[] dataTypeArray;

        protected ArmyResultRecordMeta(int resultNo, DataType[] dataTypeArray) {
            assert resultNo > 0;
            this.resultNo = resultNo;
            this.dataTypeArray = dataTypeArray;
        }

        @Override
        public final int getResultNo() {
            return this.resultNo;
        }

        @Override
        public final int getColumnCount() {
            return this.dataTypeArray.length;
        }

        @Override
        public final DataType getDataType(int indexBasedZero) throws DataAccessException {
            return this.dataTypeArray[checkIndex(indexBasedZero)];
        }

        @Override
        public final ArmyType getArmyType(final int indexBasedZero) throws DataAccessException {
            final DataType dataType;
            dataType = this.dataTypeArray[checkIndex(indexBasedZero)];
            final ArmyType armyType;
            if (dataType instanceof SqlType) {
                armyType = ((SqlType) dataType).armyType();
            } else {
                armyType = ArmyType.UNKNOWN;
            }
            return armyType;
        }



        /*-------------------below label methods -------------------*/

        @Override
        public final Selection getSelection(String columnLabel) throws DataAccessException {
            return getSelection(getColumnIndex(columnLabel));
        }

        @Override
        public final DataType getDataType(String columnLabel) throws DataAccessException {
            return getDataType(getColumnIndex(columnLabel));
        }

        @Override
        public final ArmyType getArmyType(String columnLabel) throws DataAccessException {
            return getArmyType(getColumnIndex(columnLabel));
        }

        @Nullable
        @Override
        public final <T> T getOf(String columnLabel, Option<T> option) throws DataAccessException {
            return getOf(getColumnIndex(columnLabel), option);
        }

        @Override
        public final <T> T getNonNullOf(String columnLabel, Option<T> option) throws DataAccessException {
            return getNonNullOf(getColumnIndex(columnLabel), option);
        }

        @Nullable
        @Override
        public final String getCatalogName(String columnLabel) throws DataAccessException {
            return getCatalogName(getColumnIndex(columnLabel));
        }

        @Nullable
        @Override
        public final String getSchemaName(String columnLabel) throws DataAccessException {
            return getSchemaName(getColumnIndex(columnLabel));
        }

        @Nullable
        @Override
        public final String getTableName(String columnLabel) throws DataAccessException {
            return getTableName(getColumnIndex(columnLabel));
        }

        @Nullable
        @Override
        public final String getColumnName(String columnLabel) throws DataAccessException {
            return getColumnName(getColumnIndex(columnLabel));
        }

        @Override
        public final int getPrecision(String columnLabel) throws DataAccessException {
            return getPrecision(getColumnIndex(columnLabel));
        }

        @Override
        public final int getScale(String columnLabel) throws DataAccessException {
            return getScale(getColumnIndex(columnLabel));
        }

        @Override
        public final FieldType getFieldType(String columnLabel) throws DataAccessException {
            return getFieldType(getColumnIndex(columnLabel));
        }

        @Nullable
        @Override
        public final Boolean getAutoIncrementMode(String columnLabel) throws DataAccessException {
            return getAutoIncrementMode(getColumnIndex(columnLabel));
        }

        @Override
        public final KeyType getKeyMode(String columnLabel) throws DataAccessException {
            return getKeyMode(getColumnIndex(columnLabel));
        }

        @Nullable
        @Override
        public final Boolean getNullableMode(String columnLabel) throws DataAccessException {
            return getNullableMode(getColumnIndex(columnLabel));
        }

        @Override
        public final Class<?> getFirstJavaType(String columnLabel) throws DataAccessException {
            return getFirstJavaType(getColumnIndex(columnLabel));
        }

        @Nullable
        @Override
        public final Class<?> getSecondJavaType(String columnLabel) throws DataAccessException {
            return getSecondJavaType(getColumnIndex(columnLabel));
        }

        public final int checkIndex(final int indexBasedZero) {
            if (indexBasedZero < 0 || indexBasedZero >= this.dataTypeArray.length) {
                String m = String.format("index not in [0,%s)", this.dataTypeArray.length);
                throw new DataAccessException(m);
            }
            return indexBasedZero;
        }

        public final int checkIndexAndToBasedOne(final int indexBasedZero) {
            if (indexBasedZero < 0 || indexBasedZero >= this.dataTypeArray.length) {
                String m = String.format("index not in [0,%s)", this.dataTypeArray.length);
                throw new DataAccessException(m);
            }
            return indexBasedZero + 1;
        }

    } // ArmyResultRecordMeta


    private static abstract class ArmyDataRecord implements DataRecord {


        @Override
        public final int getResultNo() {
            return getRecordMeta().getResultNo();
        }

        @Override
        public final int getColumnCount() {
            return getRecordMeta().getColumnCount();
        }

        @Override
        public final String getColumnLabel(int indexBasedZero) throws IllegalArgumentException {
            return getRecordMeta().getColumnLabel(indexBasedZero);
        }

        @Override
        public final int getColumnIndex(String columnLabel) throws IllegalArgumentException {
            return getRecordMeta().getColumnIndex(columnLabel);
        }

        @Override
        public final Object getNonNull(final int indexBasedZero) {
            final Object value;
            value = get(indexBasedZero);
            if (value == null) {
                throw currentRecordColumnIsNull(indexBasedZero, getColumnLabel(indexBasedZero));
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
                throw currentRecordColumnIsNull(indexBasedZero, getColumnLabel(indexBasedZero));
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
        public final Object get(String columnLabel) {
            return get(getRecordMeta().getColumnIndex(columnLabel));
        }

        @Override
        public final Object getNonNull(String columnLabel) {
            return getNonNull(getRecordMeta().getColumnIndex(columnLabel));
        }

        @Override
        public final Object getOrDefault(String columnLabel, Object defaultValue) {
            return getOrDefault(getRecordMeta().getColumnIndex(columnLabel), defaultValue);
        }

        @Override
        public final Object getOrSupplier(String columnLabel, Supplier<?> supplier) {
            return getOrSupplier(getRecordMeta().getColumnIndex(columnLabel), supplier);
        }

        @Override
        public final <T> T get(String columnLabel, Class<T> columnClass) {
            return get(getRecordMeta().getColumnIndex(columnLabel), columnClass);
        }

        @Override
        public final <T> T getNonNull(String columnLabel, Class<T> columnClass) {
            return getNonNull(getRecordMeta().getColumnIndex(columnLabel), columnClass);
        }

        @Override
        public final <T> T getOrDefault(String columnLabel, Class<T> columnClass, T defaultValue) {
            return getOrDefault(getRecordMeta().getColumnIndex(columnLabel), columnClass, defaultValue);
        }

        @Override
        public final <T> T getOrSupplier(String columnLabel, Class<T> columnClass, Supplier<T> supplier) {
            return getOrSupplier(getRecordMeta().getColumnIndex(columnLabel), columnClass, supplier);
        }


    }//ArmyDataRecord


    protected static abstract class ArmyCurrentRecord extends ArmyDataRecord implements CurrentRecord {


        @Override
        public abstract ArmyResultRecordMeta getRecordMeta();

        @Override
        public final ResultRecord asResultRecord() {
            return new ArmyResultRecord(this);
        }

        protected abstract Object[] copyValueArray();


    }// ArmyCurrentRecord


    private static final class ArmyResultRecord extends ArmyDataRecord implements ResultRecord {


        private final ArmyResultRecordMeta meta;

        private final Object[] valueArray;

        public ArmyResultRecord(ArmyCurrentRecord currentRecord) {
            this.meta = currentRecord.getRecordMeta();
            this.valueArray = currentRecord.copyValueArray();
            assert this.valueArray.length == this.meta.getColumnCount();
        }

        @Override
        public ResultRecordMeta getRecordMeta() {
            return this.meta;
        }

        @Override
        public Object get(int indexBasedZero) {
            return this.valueArray[this.meta.checkIndex(indexBasedZero)];
        }

    }// ArmyResultRecord


    private static final class PseudoWriterAccessor implements ObjectAccessor {

        @Override
        public boolean isWritable(String propertyName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isWritable(String propertyName, Class<?> valueType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Class<?> getJavaType(String propertyName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(Object target, String propertyName, @Nullable Object value) throws ObjectAccessException {
            throw new UnsupportedOperationException();
        }

        @Override
        public ReadAccessor getReadAccessor() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isReadable(String propertyName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object get(Object target, String propertyName) throws ObjectAccessException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Class<?> getAccessedType() {
            throw new UnsupportedOperationException();
        }

    }// PseudoWriterAccessor


}
