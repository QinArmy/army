package io.army.criteria;

/**
 * @see SubStatement
 */
public interface PrimaryStatement extends Statement, Statement.StatementMockSpec, AutoCloseable {


    @Override
    void close() throws CriteriaException;

    @Override
    String toString();

}
