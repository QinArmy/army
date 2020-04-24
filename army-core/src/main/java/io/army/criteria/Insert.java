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

    /*################################## blow insert interfaces ##################################*/

    interface InsertIntoAble<T extends IDomain> extends InsertSQLAble {

        InsertValuesAble<T> insertInto(Collection<FieldMeta<? super T, ?>> fieldMetaList);

        InsertValuesAble<T> insertInto(TableMeta<T> tableMeta);
    }

    interface InsertValuesAble<T extends IDomain> extends InsertSQLAble {

        InsertAble value(T domain);

        InsertAble values(List<T> domainList);
    }

    /*################################## blow batchInsert method ##################################*/


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

    }

    interface SubQueryValueAble<C> extends InsertSQLAble {

        InsertAble values(SubQuery subQuery);

        InsertAble values(Function<C, SubQuery> function);
    }


}
