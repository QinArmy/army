package io.army.criteria;


public interface BatchItemPairs<F extends DataField> extends UpdateStatement._ItemPairBuilder
        , UpdateStatement._StaticBatchSetClause<F, BatchItemPairs<F>> {


}
