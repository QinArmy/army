package io.army.criteria.impl.inner.mysql;

import io.army.criteria.SQLWords;
import io.army.criteria.impl._Pair;
import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Statement;
import io.army.criteria.mysql.MySQLWords;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;

import java.nio.file.Path;
import java.util.List;

public interface _MySQLLoadData extends _Statement, _DialectStatement {


    List<MySQLWords> modifierList();

    Path fileName();

    @Nullable
    SQLWords strategyOption();


    List<String> partitionList();

    @Nullable
    Boolean fieldsKeyWord();

    @Nullable
    String charsetName();

    @Nullable
    String columnTerminatedBy();

    boolean columnOptionallyEnclosed();

    @Nullable
    Character columnEnclosedBy();

    @Nullable
    Character columnEscapedBy();

    boolean linesClause();

    @Nullable
    String linesStartingBy();

    @Nullable
    String linesTerminatedBy();

    @Nullable
    Long ignoreRows();

    List<_Expression> columnOrUserVarList();

    List<_Pair<FieldMeta<?>, _Expression>> columItemPairList();



    interface _SingleLoadData extends _MySQLLoadData{

        SingleTableMeta<?> table();

    }


    interface _ChildLoadData extends _MySQLLoadData{

        ChildTableMeta<?> table();


        _SingleLoadData parentLoadData();

    }







}
