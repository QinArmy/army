package io.army.criteria.impl.inner;

import io.army.criteria.NullHandleMode;
import io.army.criteria.RowSet;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;

public interface _Insert extends _Statement {

    TableMeta<?> table();

    /**
     * @return a unmodifiable list , maybe empty.
     */
    List<FieldMeta<?>> fieldList();

    List<FieldMeta<?>> childFieldList();

    Map<FieldMeta<?>, Boolean> fieldMap();


    interface _CommonExpInsert extends _Insert {

        boolean isMigration();

        @Nullable
        NullHandleMode nullHandle();

        Map<FieldMeta<?>, _Expression> commonExpMap();

    }


    interface _ValueInsert extends _CommonExpInsert {


        List<Map<FieldMeta<?>, _Expression>> rowValuesList();


    }


    interface _DomainInsert extends _CommonExpInsert {


        NullHandleMode nullHandle();

        boolean isPreferLiteral();


        List<IDomain> domainList();

    }


    interface _AssignmentInsert extends _Insert {


    }


    interface _RowSetInsert extends _Insert {

        RowSet rowSet();

        @Nullable
        RowSet childRowSet();


    }


}
