package io.army.criteria;

public interface BatchRowPairs<F extends DataField> extends Update._ItemPairBuilder
        , Update._StaticRowSetClause<F, BatchRowPairs<F>>
        , Update._StaticBatchSetClause<F, BatchRowPairs<F>> {


}
