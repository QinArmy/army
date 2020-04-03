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

        InsertIntoAble<T, C> defaultIfNull();
    }

    interface InsertIntoAble<T extends IDomain, C> extends InsertSQLAble {

        InsertValuesAble<T, C> insertInto(List<FieldMeta<T, ?>> fieldMetaList);

        InsertValuesAble<T, C> insertInto(TableMeta<T> tableMeta);

        InsertAble insertInto(T domain);
    }


    interface InsertValueAble<T extends IDomain> extends InsertSQLAble {

        <F> InsertValueAble<T> pair(FieldMeta<T, F> fieldMeta, F fieldValue);

        /**
         * @see io.army.annotation.Generator
         * @see io.army.generator.MultiGenerator
         */
        <F> InsertValueAble<T> generate(FieldMeta<T, F> fieldMeta);

        InsertValueAble<T> defaultValue(FieldMeta<T, ?> fieldMeta);

        InsertValueAble<T> and();
    }


    interface InsertValuesAble<T extends IDomain, C> extends InsertValueAble<T> {

        InsertAble values(Function<C, SubQuery> function);

        InsertAble values(List<T> domainList);
    }


}
