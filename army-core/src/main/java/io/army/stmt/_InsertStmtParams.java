package io.army.stmt;

import io.army.bean.ObjectAccessor;
import io.army.lang.Nullable;
import io.army.meta.PrimaryFieldMeta;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface _InsertStmtParams extends _StmtParams {


    @Nullable
    PrimaryFieldMeta<?> idField();

    @Nullable
    String idReturnAlias();


    interface _DomainParams extends _InsertStmtParams {

        List<?> domainList();

        ObjectAccessor domainAccessor();

    }


    interface _ValueParams extends _InsertStmtParams {

        int rowSize();

        BiFunction<Integer, Object, Object> function();

    }

    interface _AssignmentParams extends _InsertStmtParams {

        Function<Object, Object> function();

    }


}