package io.army.criteria;


import io.army.domain.IDomain;
import org.springframework.lang.NonNull;

/**
 * created  on 2018/11/24.
 */
public interface SelectList extends QueryAble {


    @NonNull
    <X extends IDomain> SelectJoin<X> from(@NonNull Class<X> tableClass) throws CriteriaException;

    @NonNull
    <X> SelectJoin<X> from(@NonNull SubQuery<X> subQuery) throws CriteriaException;

}
