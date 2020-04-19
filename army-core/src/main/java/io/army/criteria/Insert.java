package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;

public interface Insert extends SQLAble, SQLDebug, QueryAble {


    /*################################## blow interfaces  ##################################*/

    interface InsertSQLAble extends SQLAble {

    }

    interface InsertAble extends InsertSQLAble {

        Insert asInsert();
    }

    interface InsertOptionAble<T extends IDomain, C> extends InsertIntoAble<T, C> {

        <F> InsertOptionAble<T, C> commonValue(FieldMeta<T, F> fieldMeta, Expression<F> valueExp);

        <F, S extends Expression<F>> InsertOptionAble<T, C> commonValue(FieldMeta<T, F> fieldMeta, Function<C, S> function);

        InsertIntoAble<T, C> defaultIfNull();
    }

    interface InsertIntoAble<T extends IDomain, C> extends InsertSQLAble {

        InsertValuesAble<T, C> insertInto(List<FieldMeta<T, ?>> fieldMetaList);

        InsertValuesAble<T, C> insertInto(TableMeta<T> tableMeta);

        InsertAble insertInto(T domain);

        InsertAble batchInsertInto(List<T> domainList);
    }

    interface InsertValuesAble<T extends IDomain, C> extends InsertSQLAble {

        InsertAble values(Function<C, SubQuery> function);

        InsertAble values(List<T> domainList);
    }


}
