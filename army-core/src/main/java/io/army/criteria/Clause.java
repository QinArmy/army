package io.army.criteria;

import javax.annotation.Nullable;

public interface Clause extends Item {


    interface _VariadicCommaClause {

        _VariadicCommaClause comma(@Nullable Object exp);

    }

    interface _VariadicSpaceClause {

        _VariadicCommaClause space(@Nullable Object exp);
    }

    interface _VariadicConsumer {

        _VariadicConsumer accept(@Nullable Object exp);

    }


    interface _PairVariadicCommaClause {

        _PairVariadicCommaClause comma(String keyName, @Nullable Object value);

        _PairVariadicCommaClause comma(Expression key, @Nullable Object value);

    }

    interface _PairVariadicSpaceClause {

        _PairVariadicCommaClause space(String keyName, @Nullable Object value);

        _PairVariadicCommaClause space(Expression key, @Nullable Object value);

    }

    interface _PairVariadicConsumerClause {

        _PairVariadicConsumerClause accept(String keyName, @Nullable Object value);

        _PairVariadicConsumerClause accept(Expression key, @Nullable Object value);

    }
}
