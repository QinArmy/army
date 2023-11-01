package io.army.session.record;

import io.army.criteria.Selection;
import io.army.session.DataAccessException;
import io.army.session.Option;

import javax.annotation.Nullable;

public abstract class ArmyResultResultMeta implements ResultRecordMeta {

    private final int resultNo;

    protected ArmyResultResultMeta(int resultNo) {
        assert resultNo > 0;
        this.resultNo = resultNo;
    }

    @Override
    public final int getResultNo() {
        return this.resultNo;
    }

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


}
