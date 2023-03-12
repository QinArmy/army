package io.army.criteria;

public interface BatchRowPairs<F extends DataField> extends UpdateStatement._ItemPairBuilder
        , UpdateStatement._StaticRowSetClause<F, BatchRowPairs<F>>
        , UpdateStatement._StaticBatchSetClause<F, BatchRowPairs<F>> {


}
