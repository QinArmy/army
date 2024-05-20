/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.mysql.inner;

import io.army.criteria.SQLWords;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl._Pair;
import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Statement;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import javax.annotation.Nullable;
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
    String charset();

    @Nullable
    String columnTerminatedBy();

    boolean columnOptionallyEnclosed();

    @Nullable
    String columnEnclosedBy();

    @Nullable
    String columnEscapedBy();

    boolean linesClause();

    @Nullable
    String linesStartingBy();

    @Nullable
    String linesTerminatedBy();

    @Nullable
    Long ignoreRows();

    @Nullable
    SQLWords ignoreRowWord();

    List<_Expression> columnOrUserVarList();

    List<_Pair<FieldMeta<?>, _Expression>> columItemPairList();



    interface _ChildLoadData extends _MySQLLoadData{

        _MySQLLoadData parentLoadData();

    }







}
