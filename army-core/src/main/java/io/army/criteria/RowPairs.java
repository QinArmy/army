package io.army.criteria;

public interface RowPairs<F extends DataField> extends UpdateStatement._ItemPairBuilder
        , UpdateStatement._StaticRowSetClause<F, RowPairs<F>> {


}
