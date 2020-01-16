package io.army;

import javax.sql.DataSource;

interface InnerSessionFactory extends SessionFactory {


  DataSource getDataSource();


}
