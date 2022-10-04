package io.army.criteria;

@Deprecated
public interface ScalarSubQuery extends SubQuery, TypeInfer {

    default Selection selection() {
        throw new UnsupportedOperationException();
    }


}
