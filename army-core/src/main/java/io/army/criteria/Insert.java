package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public interface Insert extends SQLStatement, SQLAble, SQLDebug, QueryAble {


    /*################################## blow interfaces  ##################################*/

    interface InsertSQLAble extends SQLAble {

    }

    interface InsertAble extends InsertSQLAble {

        Insert asInsert();
    }

    /*################################## blow multiInsert interfaces ##################################*/

    interface InsertOptionAble<T extends IDomain> extends InsertIntoAble<T> {

        InsertOptionAble<T> dataMigration();
    }

    interface InsertIntoAble<T extends IDomain> extends InsertSQLAble {

        InsertValuesAble<T> insertInto(Collection<FieldMeta<? super T, ?>> fieldMetaList);

        InsertValuesAble<T> insertInto(TableMeta<T> tableMeta);
    }

    interface InsertValuesAble<T extends IDomain> extends InsertSQLAble {

        InsertAble value(T domain);

        InsertAble values(List<T> domainList);
    }

    /*################################## blow batchInsert method ##################################*/

    interface BatchInsertOptionAble<T extends IDomain> extends BatchInsertIntoAble<T> {

        BatchInsertOptionAble<T> dataMigration();
    }


    interface BatchInsertIntoAble<T extends IDomain> extends InsertSQLAble {

        BatchInsertValuesAble<T> insertInto(Collection<FieldMeta<? super T, ?>> fieldMetaList);

        BatchInsertValuesAble<T> insertInto(TableMeta<T> tableMeta);
    }

    interface BatchInsertValuesAble<T extends IDomain> extends InsertSQLAble {

        InsertAble values(List<T> domainList);

    }


    /*################################## blow subQuery batchInsert interfaces ##################################*/

    interface SubQueryTargetFieldAble<T extends IDomain, C> extends InsertSQLAble {

        SubQueryValueAble<C> insertInto(List<FieldMeta<T, ?>> fieldMetaList);

        SubQueryValueAble<C> insertInto(Function<C, List<FieldMeta<T, ?>>> function);
    }

    interface SubQueryValueAble<C> extends InsertSQLAble {


        InsertAble values(Function<C, SubQuery> function);
    }

    /*################################## blow child sub query insert interfaces ##################################*/

    interface ParentSubQueryTargetFieldAble<T extends IDomain, C> extends InsertSQLAble {

        ParentSubQueryAble<T, C> parentFields(List<FieldMeta<? super T, ?>> fieldMetaList);

        ParentSubQueryAble<T, C> parentFields(Function<C, List<FieldMeta<? super T, ?>>> function);

    }

    interface ChildSubQueryTargetFieldAble<T extends IDomain, C> extends InsertSQLAble {

        ChildSubQueryAble<C> childFields(List<FieldMeta<T, ?>> fieldMetaList);

        ChildSubQueryAble<C> childFields(Function<C, List<FieldMeta<T, ?>>> function);

    }

    interface ParentSubQueryAble<T extends IDomain, C> extends InsertSQLAble {

        ChildSubQueryTargetFieldAble<T, C> parentSubQuery(Function<C, SubQuery> function);
    }

    interface ChildSubQueryAble<C> extends InsertSQLAble {

        InsertAble childSubQuery(Function<C, SubQuery> function);
    }


}
