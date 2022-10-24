package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.SubStatement;
import io.army.criteria.Update;
import io.army.criteria.impl.inner.postgre._PostgreUpdate;
import io.army.criteria.postgre.PostgreUpdate;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.function.Function;

abstract class PostgreUpdates<I extends Item, Q extends Item, T, PS extends Update._ItemPairBuilder, SR, SD, WR, WA>
        extends SingleUpdate<I, Q, FieldMeta<T>, PS, SR, SD, WR, WA, Object, Object>
        implements PostgreUpdate, _PostgreUpdate {


    static <I extends Item> PostgreUpdate._DynamicSubMaterializedSpec<I> dynamicCteUpdate(CriteriaContext outerContext
            , Function<SubStatement, I> function) {
        throw new UnsupportedOperationException();
    }


    private PostgreUpdates(CriteriaContext context, TableMeta<?> updateTable, String tableAlias) {
        super(context, updateTable, tableAlias);
    }


}
