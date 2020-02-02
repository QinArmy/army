package io.army.boot;

import io.army.Session;

import java.sql.PreparedStatement;
import java.sql.SQLException;

interface InnerSession extends Session {

    PreparedStatement createStatement(String sql,boolean generatedKey)throws SQLException;
}
