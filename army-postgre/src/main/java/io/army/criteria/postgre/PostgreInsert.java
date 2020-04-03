package io.army.criteria.postgre;

import io.army.criteria.Insert;
import io.army.criteria.SelectPart;
import io.army.criteria.SubQuery;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;

public interface PostgreInsert extends Insert {

    /*################################## blow interfaces  ##################################*/

    interface PostgreInsertAble extends InsertAble {

        @Override
        PostgreInsert asInsert();

    }

    interface PostgreWithAble<T extends IDomain, C> extends PostgreInsertOptionAble<T, C> {

        PostgreInsertOptionAble<T, C> with(Function<C, List<PostgreWithQuery>> function);

        PostgreInsertOptionAble<T, C> withRecursive(Function<C, List<PostgreWithQuery>> function);
    }

    interface PostgreInsertOptionAble<T extends IDomain, C> extends PostgreInsertIntoAble<T, C> {

        PostgreInsertIntoAble<T, C> defaultIfNull();
    }

    interface PostgreInsertIntoAble<T extends IDomain, C> extends InsertSQLAble {

        PostgreInsertValuesAble<T, C> insertInto(List<FieldMeta<T, ?>> fieldMetaList);

        PostgreInsertValuesAble<T, C> insertInto(TableMeta<T> tableMeta);

        PostgreInsertWithQueryAble insertInto(T domain);
    }

    interface PostgreInsertValueAble<T extends IDomain, C> extends InsertSQLAble {

        <F> PostgreInsertValueAble<T, C> pair(FieldMeta<T, F> fieldMeta, F fieldValue);

        /**
         * @see io.army.annotation.Generator
         * @see io.army.generator.MultiGenerator
         */
        <F> PostgreInsertValueAble<T, C> generate(FieldMeta<T, F> fieldMeta);

        PostgreInsertValueAble<T, C> defaultValue(FieldMeta<T, ?> fieldMeta);

        PostgreInsertGroupAble<T, C> and();

    }

    interface PostgreInsertGroupAble<T extends IDomain, C> extends PostgreInsertAble
            , PostgreInsertValueAble<T, C>, PostgreReturningAble<C> {


    }


    interface PostgreInsertValuesAble<T extends IDomain, C> extends PostgreInsertValueAble<T, C> {

        PostgreReturningAble<C> values(Function<C, SubQuery> function);

        PostgreReturningAble<C> values(List<T> domainList);
    }

    interface PostgreReturningAble<C> extends PostgreInsertAble {

        PostgreInsertWithQueryAble returning(List<SelectPart> selectPartList);

        PostgreInsertWithQueryAble returning(Function<C, List<SelectPart>> function);

    }

    interface PostgreInsertWithQueryAble extends PostgreInsertAble {

        PostgreWithQuery asWithQuery() throws PostgreWithQueryException;
    }


}