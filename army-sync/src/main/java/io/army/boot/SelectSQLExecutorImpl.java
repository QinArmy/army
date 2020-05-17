package io.army.boot;

import io.army.ErrorCode;
import io.army.SessionFactory;
import io.army.UnKnownTypeException;
import io.army.beans.ObjectWrapper;
import io.army.beans.PropertyAccessorFactory;
import io.army.codec.FieldCodec;
import io.army.criteria.CriteriaException;
import io.army.criteria.FieldSelection;
import io.army.criteria.Selection;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.modelgen.MetaConstant;
import io.army.util.Pair;
import io.army.util.Triple;
import io.army.wrapper.ParamWrapper;
import io.army.wrapper.SelectSQLWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class SelectSQLExecutorImpl implements SelectSQLExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(SelectSQLExecutorImpl.class);

    private final SessionFactory sessionFactory;

    SelectSQLExecutorImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public <T> List<T> select(InnerSession session, SelectSQLWrapper wrapper, Class<T> resultClass) {
        assertSelectList(wrapper.selectionList(), resultClass);
        if (session.sessionFactory().showSQL()) {
            LOG.info("will execute select sql:{}", session.dialect().showSQL(wrapper));
        }
        try (PreparedStatement st = session.createStatement(wrapper.sql())) {
            // 1. set params
            setParams(st, wrapper.paramList());
            List<T> resultList;
            // 2. execute sql
            try (ResultSet resultSet = st.executeQuery()) {
                // 3. extract result
                if (MetaConstant.SIMPLE_JAVA_TYPE_SET.contains(resultClass)) {
                    resultList = extractSimpleResult(resultSet, wrapper.selectionList().get(0));
                } else {
                    resultList = extractResult(resultSet, wrapper.selectionList(), resultClass);
                }


            }
            return resultList;
        } catch (SQLException e) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, e, "execute sql [%s] occur error.", wrapper.sql());
        }
    }


    /*################################## blow private method ##################################*/

    private void assertSelectList(List<Selection> selectionList, Class<?> resultClass) {
        if (MetaConstant.SIMPLE_JAVA_TYPE_SET.contains(resultClass)) {
            if (selectionList.size() != 1) {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "selection size[%s] error,for resultClass[%s]", selectionList.size(), resultClass.getName());
            } else if (selectionList.get(0).mappingMeta().javaType() != resultClass) {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "selection java type[%s] and  resultClass[%s] not match."
                        , selectionList.get(0).mappingMeta().javaType().getName(), resultClass.getName());
            }
        } else if (resultClass == Pair.class) {
            if (selectionList.size() != 2) {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "selection size[%s] error,for resultClass[%s]", selectionList.size(), resultClass.getName());
            }
        } else if (resultClass == Triple.class) {
            if (selectionList.size() != 3) {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "selection size[%s] error,for resultClass[%s]", selectionList.size(), resultClass.getName());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> extractSimpleResult(ResultSet resultSet, Selection selection)
            throws SQLException {
        List<T> resultList = new ArrayList<>();

        FieldMeta<?, ?> fieldMeta = null;
        FieldCodec fieldCodec = null;

        if (selection instanceof FieldSelection) {
            fieldMeta = ((FieldSelection) selection).fieldMeta();
            fieldCodec = this.sessionFactory.fieldCodec(fieldMeta);
        }
        final MappingMeta mappingMeta = selection.mappingMeta();
        while (resultSet.next()) {
            Object value = mappingMeta.nullSafeGet(resultSet, selection.alias());

            if (value != null && fieldCodec != null) {
                value = fieldCodec.decode(fieldMeta, value);
            }
            resultList.add((T) value);
        }
        return resultList;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> extractResult(ResultSet resultSet, List<Selection> selectionList
            , Class<T> resultClass) throws SQLException {
        List<T> resultList = new ArrayList<>();

        final Map<FieldMeta<?, ?>, FieldCodec> codecMap = ExecutorUtils.createCodecMap(
                selectionList, this.sessionFactory);

        ObjectWrapper objectWrapper;
        while (resultSet.next()) {
            objectWrapper = PropertyAccessorFactory.forBeanPropertyAccess(resultClass);

            for (Selection selection : selectionList) {
                Object value = selection.mappingMeta().nullSafeGet(resultSet, selection.alias());
                if (value == null) {
                    continue;
                }
                if (selection instanceof FieldSelection) {
                    FieldMeta<?, ?> fieldMeta = ((FieldSelection) selection).fieldMeta();
                    FieldCodec fieldCodec = codecMap.get(fieldMeta);
                    if (fieldCodec != null) {
                        value = fieldCodec.decode(fieldMeta, value);
                    }
                }
                objectWrapper.setPropertyValue(selection.alias(), value);
            }

            // result add to resultList
            resultList.add((T) objectWrapper.getWrappedInstance());
        }
        return resultList;
    }

    private void setParams(PreparedStatement st, List<ParamWrapper> paramList) throws SQLException {
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


    private void setNonNullValue(PreparedStatement st, final int index, ParamMeta paramMeta, final Object value)
            throws SQLException {
        Object paramValue = value;
        MappingMeta mappingMeta;
        if (paramMeta instanceof FieldMeta) {
            FieldMeta<?, ?> fieldMeta = (FieldMeta<?, ?>) paramMeta;
            FieldCodec fieldCodec = this.sessionFactory.fieldCodec(fieldMeta);
            if (fieldCodec != null) {
                paramValue = fieldCodec.encode(fieldMeta, paramValue);
            }
            mappingMeta = fieldMeta.mappingMeta();
        } else if (paramMeta instanceof MappingMeta) {
            mappingMeta = (MappingMeta) paramMeta;
        } else {
            throw new UnKnownTypeException(paramMeta);
        }
        // set param
        mappingMeta.nonNullSet(st, paramValue, index);
    }


}
