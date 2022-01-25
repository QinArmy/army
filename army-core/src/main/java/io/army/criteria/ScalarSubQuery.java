package io.army.criteria;

/**
 * @param <E> {@link ScalarSubQuery}'s query result Java Type.
 */
public interface ScalarSubQuery extends SubQuery, TypeInfer {

    default Selection selection() {
        throw new UnsupportedOperationException();
    }


}
