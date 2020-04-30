package io.army;

/**
 * created  on 2018/9/1.
 */
public interface GenericSession {

    SessionOptions options();

    boolean readonly();

    boolean closed();


    boolean hasTransaction();



}
