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

import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.mysql.MySQLs;

import java.util.List;

public interface _MySQLInsert extends _Insert, _Insert._ConflictActionClauseSpec, _Insert._SupportConflictClauseSpec {

    List<Hint> hintList();

    List<MySQLs.Modifier> modifierList();

    List<String> partitionList();



    interface _MySQLDomainInsert extends _Insert._DomainInsert, _MySQLInsert {


    }

    interface _MySQLChildDomainInsert extends _Insert._ChildDomainInsert, _MySQLDomainInsert {

        @Override
        _MySQLDomainInsert parentStmt();

    }

    interface _MySQLValueInsert extends _ValuesInsert, _MySQLInsert {

    }

    interface _MySQLChildValueInsert extends _Insert._ChildValuesInsert, _MySQLValueInsert {

        @Override
        _MySQLValueInsert parentStmt();

    }

    interface _MySQLAssignmentInsert extends _Insert._AssignmentInsert, _MySQLInsert {

    }

    interface _MySQLChildAssignmentInsert extends _Insert._ChildAssignmentInsert, _MySQLAssignmentInsert {

        @Override
        _MySQLAssignmentInsert parentStmt();

    }

    interface _MySQLQueryInsert extends _QueryInsert, _MySQLInsert {

    }

    interface _MySQLParentQueryInsert extends _MySQLQueryInsert, _ParentQueryInsert {

    }

    interface _MySQLChildQueryInsert extends _Insert._ChildQueryInsert, _MySQLQueryInsert {

        @Override
        _MySQLParentQueryInsert parentStmt();

    }


}
