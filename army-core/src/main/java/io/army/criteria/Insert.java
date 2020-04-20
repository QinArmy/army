package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public interface Insert extends SQLAble, SQLDebug, QueryAble {


    /*################################## blow interfaces  ##################################*/

    interface InsertSQLAble extends SQLAble {

    }

    interface InsertAble extends InsertSQLAble {

        Insert asInsert();
    }

    /*################################## blow  batchInsert interfaces ##################################*/

    interface InsertOptionAble<T extends IDomain, C> extends BatchInsertIntoAble<T> {

        <F> InsertOptionAble<T, C> commonValue(FieldMeta<? super T, F> fieldMeta, Expression<F> valueExp);

        <F> InsertOptionAble<T, C> commonValue(FieldMeta<? super T, F> fieldMeta, Function<C, Expression<F>> function);

        InsertOptionAble<T, C> alwaysUseCommonValue();

        InsertIntoAble<T> defaultIfNull();
    }

    interface BatchInsertIntoAble<T extends IDomain> extends InsertIntoAble<T> {

        InsertAble batchInsert(List<T> domainList);
    }

    interface InsertIntoAble<T extends IDomain> extends InsertSQLAble {

        InsertValuesAble<T> insertInto(Collection<FieldMeta<? super T, ?>> fieldMetaList);

        InsertValuesAble<T> insertInto(TableMeta<T> tableMeta);

        InsertAble insert(T domain);


    }

    interface InsertValuesAble<T extends IDomain> extends InsertSQLAble {

        InsertAble value(T domain);

        InsertAble values(List<T> domainList);
    }


    /*################################## blow subQuery batchInsert interfaces ##################################*/

    interface SubQueryTargetFieldAble<T extends IDomain, C> extends InsertSQLAble {

        SubQueryValueAble<C> insertInto(List<FieldMeta<T, ?>> fieldMetaList);

    }

    interface SubQueryValueAble<C> extends InsertSQLAble {

        InsertAble values(SubQuery subQuery);

        InsertAble values(Function<C, SubQuery> function);
    }


}
