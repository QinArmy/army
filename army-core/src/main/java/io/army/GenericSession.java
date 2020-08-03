package io.army;

/**
 */
public interface GenericSession {


    boolean readonly();

    boolean closed();

    boolean hasTransaction();


}
