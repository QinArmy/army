package io.army.boot;

import io.army.Session;
import io.army.SessionBuilder;

class SessionBuilderImpl implements SessionBuilder {

     private final InnerSessionFactory innerSessionFactory;

      SessionBuilderImpl(InnerSessionFactory innerSessionFactory) {
         this.innerSessionFactory = innerSessionFactory;
     }

     @Override
     public Session openSession() {
         return null;
     }
 }
