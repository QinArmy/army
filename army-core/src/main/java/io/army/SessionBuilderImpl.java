package io.army;

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
