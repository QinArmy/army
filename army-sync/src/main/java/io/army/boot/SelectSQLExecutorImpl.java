package io.army.boot;

import io.army.ErrorCode;
import io.army.codec.FieldCodec;
import io.army.criteria.CriteriaException;
import io.army.criteria.FieldSelection;
import io.army.criteria.Selection;
import io.army.meta.FieldMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.modelgen.MetaConstant;
import io.army.util.Pair;
import io.army.util.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

final class SelectSQLExecutorImpl extends SQLExecutorSupport implements SelectSQLExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(SelectSQLExecutorImpl.class);


    SelectSQLExecutorImpl(InnerSessionFactory sessionFactory) {
        super(sessionFactory);
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



}
