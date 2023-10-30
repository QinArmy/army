package io.army.criteria.impl.inner.mysql;

import io.army.criteria.SQLWords;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl._Pair;
import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Statement;

import javax.annotation.Nullable;

import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.nio.file.Path;
import java.util.List;

public interface _MySQLLoadData extends _Statement, _DialectStatement {


    List<MySQLs.Modifier> modifierList();

    Path fileName();

    @Nullable
    SQLWords strategyOption();

    TableMeta<?> table();

    List<String> partitionList();

    @Nullable
    Boolean fieldsKeyWord();

    @Nullable
    Object charset();

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



    interface _ChildLoadData extends _MySQLLoadData{

        _MySQLLoadData parentLoadData();

    }







}
