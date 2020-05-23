package io.army.boot;

import io.army.DomainUpdateException;
import io.army.beans.AccessorFactory;
import io.army.beans.BeanWrapper;
import io.army.codec.FieldCodec;
import io.army.criteria.FieldSelection;
import io.army.criteria.Selection;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.wrapper.ParamWrapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

abstract class SQLExecutorSupport {

    final InnerSessionFactory sessionFactory;

    SQLExecutorSupport(InnerSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected final void setParams(PreparedStatement st, List<ParamWrapper> paramList) throws SQLException {
        ParamWrapper paramWrapper;
        Object value;

        final int size = paramList.size();
        for (int i = 0; i < size; i++) {
            paramWrapper = paramList.get(i);
            value = paramWrapper.value();
            if (value == null) {
                st.setNull(i + 1, ExecutorUtils.obtainVendorTypeNumber(paramWrapper.paramMeta()));
            } else {
                setNonNullValue(st, i + 1, paramWrapper.paramMeta(), value);
            }
        }

    }


    protected final void setNonNullValue(PreparedStatement st, final int index, ParamMeta paramMeta
            , final Object value)
            throws SQLException {
        Object paramValue = value;
        if (paramMeta instanceof FieldMeta) {
            FieldMeta<?, ?> fieldMeta = (FieldMeta<?, ?>) paramMeta;
            FieldCodec fieldCodec = this.sessionFactory.fieldCodec(fieldMeta);
            if (fieldCodec != null) {
                // encode param
                paramValue = fieldCodec.encode(fieldMeta, paramValue);
                if (!fieldMeta.javaType().isInstance(paramValue)) {
                    throw ExecutorUtils.createCodecReturnTypeException(fieldCodec, fieldMeta, value);
                }
            }
        }
        // set param
        paramMeta.mappingMeta().nonNullSet(st, paramValue, index);
    }


    @SuppressWarnings("unchecked")
    protected final <T> List<T> extractResult(ResultSet resultSet, List<Selection> selectionList
            , Class<T> resultClass) throws SQLException {
        List<T> resultList = new ArrayList<>();

        BeanWrapper beanWrapper;
        while (resultSet.next()) {
            beanWrapper = AccessorFactory.forBeanPropertyAccess(resultClass);
            extractRow(resultSet, selectionList, beanWrapper);
            // result add to resultList
            resultList.add((T) beanWrapper.getWrappedInstance());
        }
        return resultList;
    }


    @SuppressWarnings("unchecked")
    protected final <T> List<T> extractResultWithWrapper(ResultSet resultSet, List<Selection> selectionList
            , List<BeanWrapper> beanWrapperList) throws SQLException {
        List<T> resultList = new ArrayList<>();
        int index = 0;
        final int size = beanWrapperList.size();
        BeanWrapper beanWrapper;
        while (resultSet.next()) {
            if (index >= size) {
                throw new DomainUpdateException("");
            }
            beanWrapper = beanWrapperList.get(index);
            extractRow(resultSet, selectionList, beanWrapper);
            // result add to resultList
            resultList.add((T) beanWrapper.getWrappedInstance());
        }
        return resultList;
    }

    protected final void extractRow(ResultSet resultSet, List<Selection> selectionList, BeanWrapper beanWrapper)
            throws SQLException {

        for (Selection selection : selectionList) {
            Object value = selection.mappingMeta().nullSafeGet(resultSet, selection.alias());
            if (value == null) {
                continue;
            }
            if (selection instanceof FieldSelection) {
                FieldMeta<?, ?> fieldMeta = ((FieldSelection) selection).fieldMeta();
                FieldCodec fieldCodec = this.sessionFactory.fieldCodec(fieldMeta);
                if (fieldCodec != null) {
                    value = fieldCodec.decode(fieldMeta, value);
                }
            }
            beanWrapper.setPropertyValue(selection.alias(), value);
        }
    }


    static String[] asSelectionAliasArray(List<Selection> selectionList) {
        final int size = selectionList.size();
        String[] aliasArray = new String[size];

        for (int i = 0; i < size; i++) {
            aliasArray[i] = selectionList.get(i).alias();
        }
        return aliasArray;
    }

}
