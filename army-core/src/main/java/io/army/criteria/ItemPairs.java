package io.army.criteria;

public interface ItemPairs<F extends DataField> extends UpdateStatement._ItemPairBuilder
        , UpdateStatement._StaticSetClause<F, ItemPairs<F>> {


}
