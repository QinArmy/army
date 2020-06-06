package io.army.boot;

import io.army.modelgen.MetaConstant;
import io.army.wrapper.SimpleSQLWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

final class SelectSQLExecutorImpl extends SQLExecutorSupport implements SelectSQLExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(SelectSQLExecutorImpl.class);


    SelectSQLExecutorImpl(InnerSessionFactory sessionFactory) {
        super(sessionFactory);
    }


    @Override
    public final <T> List<T> select(InnerSession session, SimpleSQLWrapper sqlWrapper, Class<T> resultClass) {
        if (session.sessionFactory().showSQL()) {
            LOG.info("will execute select sql:{}", session.dialect().showSQL(sqlWrapper));
        }
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql())) {
            // 1. set params
            setParams(session.codecContext(), st, sqlWrapper.paramList());
            List<T> resultList;
            // 2. execute sql
            try (ResultSet resultSet = st.executeQuery()) {
                // 3. extract result
                if (MetaConstant.SIMPLE_JAVA_TYPE_SET.contains(resultClass)) {
                    resultList = extractSimpleResult(session.codecContext(), resultSet, sqlWrapper.selectionList()
                            , resultClass);
                } else {
                    resultList = extractResult(session.codecContext(), resultSet, sqlWrapper.selectionList()
                            , resultClass);
                }
            }
            return resultList;
        } catch (SQLException e) {
            throw ExecutorUtils.convertSQLException(e, sqlWrapper.sql());
        }
    }

    /*################################## blow private method ##################################*/


}
