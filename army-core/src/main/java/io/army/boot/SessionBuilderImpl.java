package io.army.boot;

import io.army.Session;
import io.army.SessionBuilder;

import java.sql.SQLException;

class SessionBuilderImpl implements SessionBuilder {

     private final InnerSessionFactory innerSessionFactory;

      SessionBuilderImpl(InnerSessionFactory innerSessionFactory) {
         this.innerSessionFactory = innerSessionFactory;
     }

     @Override
     public Session openSession()  {
         try {
             return new SessionImpl(innerSessionFactory,innerSessionFactory.getDataSource().getConnection());
         } catch (SQLException e) {
            throw new  RuntimeException(e);
         }
     }
 }
