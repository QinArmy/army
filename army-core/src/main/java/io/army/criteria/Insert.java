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

    /*################################## blow  insert interfaces ##################################*/

    interface InsertOptionAble<T extends IDomain> extends InsertIntoAble<T> {

        <F> InsertOptionAble<T> commonValue(FieldMeta<T, F> fieldMeta, Expression<F> valueExp);

        InsertOptionAble<T> alwaysUseCommonValue();

        InsertIntoAble<T> defaultIfNull();
    }

    interface InsertIntoAble<T extends IDomain> extends InsertSQLAble {

        InsertValuesAble<T> insertInto(Collection<FieldMeta<T, ?>> fieldMetaList);

        InsertValuesAble<T> insertInto(TableMeta<T> tableMeta);

        InsertAble insert(T domain);

        InsertAble insert(List<T> domainList);

    }

    interface InsertValuesAble<T extends IDomain> extends InsertSQLAble {

        InsertAble value(T domain);

        InsertAble values(List<T> domainList);
    }


    /*################################## blow subQuery insert interfaces ##################################*/

    interface SubQueryTargetFieldAble<T extends IDomain, C> extends InsertSQLAble {

        SubQueryValueAble<C> insertInto(List<FieldMeta<T, ?>> fieldMetaList);

    }

    interface SubQueryValueAble<C> extends InsertSQLAble {

        InsertAble values(Function<C, SubQuery> function);
    }


}
