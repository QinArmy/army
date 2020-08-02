package io.army.boot.sync;

import io.army.boot.ExecutorUtils;
import io.army.codec.StatementType;
import io.army.wrapper.SimpleSQLWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

final class SelectSQLExecutorImpl extends SQLExecutorSupport implements SelectSQLExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(SelectSQLExecutorImpl.class);


    SelectSQLExecutorImpl(InnerGenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }


    @Override
    public final <T> List<T> select(InnerGenericRmSession session, SimpleSQLWrapper sqlWrapper, Class<T> resultClass) {
        if (session.sessionFactory().showSQL()) {
            LOG.info("army will execute select sql:\n{}", session.dialect().showSQL(sqlWrapper));
        }
        session.codecContextStatementType(StatementType.SELECT);
        // 1. create statement
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql())) {
            // 2. set params
            setParams(session.codecContext(), st, sqlWrapper.paramList());
            List<T> resultList;
            // 3. execute query sql
            try (ResultSet resultSet = st.executeQuery()) {
                // 4. extract result
                if (simpleJavaType(sqlWrapper.selectionList(), resultClass)) {
                    resultList = extractSimpleTypeResult(session.codecContext(), resultSet, sqlWrapper.selectionList()
                            , resultClass);
                } else {
                    resultList = extractBeanTypeResult(session.codecContext(), resultSet, sqlWrapper.selectionList()
                            , resultClass);
                }
            }
            return resultList;
        } catch (SQLException e) {
            throw ExecutorUtils.convertSQLException(e, sqlWrapper.sql());
        } finally {
            session.codecContextStatementType(null);
        }
    }

    /*################################## blow private method ##################################*/


}
