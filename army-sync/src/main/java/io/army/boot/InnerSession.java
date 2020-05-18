package io.army.boot;

import io.army.Session;
import io.army.dialect.Dialect;

import java.sql.PreparedStatement;
import java.sql.SQLException;

interface InnerSession extends Session {

    PreparedStatement createStatement(String sql, boolean generatedKey) throws SQLException;

    PreparedStatement createStatement(String sql) throws SQLException;

    PreparedStatement createStatement(String sql, String[] columnNames) throws SQLException;


    Dialect dialect();
}
