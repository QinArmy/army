package io.army.boot;

import io.army.SessionFactory;

import javax.sql.DataSource;

interface InnerSessionFactory extends SessionFactory {


  DataSource getDataSource();


}
