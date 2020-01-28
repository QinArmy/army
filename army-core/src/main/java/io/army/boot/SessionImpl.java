package io.army.boot;

import io.army.Session;
import io.army.SessionOptions;
import io.army.domain.IDomain;

import java.io.Serializable;

class SessionImpl implements Session {

      SessionImpl() {
     }


    @Override
    public SessionOptions options() {
        return null;
    }

    @Override
     public boolean readonly() {
         return false;
     }


    @Override
    public Serializable save(IDomain entity) {
        return null;
    }

    @Override
     public boolean closed() {
         return false;
     }

     @Override
     public void close() throws Exception {

     }


 }
