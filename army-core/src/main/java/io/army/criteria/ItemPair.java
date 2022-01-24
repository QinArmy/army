package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.meta.FieldMeta;

import java.util.List;

/**
 * @see SQLs#itemPair(FieldMeta, Object)
 * @see Update.SimpleSetClause#setPairs(List)
 */
public interface ItemPair {

    SetLeftItem left();

    SetRightItem right();


}
