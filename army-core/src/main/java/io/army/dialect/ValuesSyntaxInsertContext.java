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

package io.army.dialect;

import io.army.criteria.NullMode;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Insert;
import io.army.meta.PrimaryFieldMeta;

import javax.annotation.Nullable;

abstract class ValuesSyntaxInsertContext extends InsertContext implements _ValueSyntaxInsertContext {


    final NullMode nullMode;


    ValuesSyntaxInsertContext(@Nullable StatementContext outerContext, final _Insert._ValuesSyntaxInsert stmt,
                              ArmyParser parser, Visible visible) {
        super(outerContext, stmt, parser, visible);

        final _Insert._ValuesSyntaxInsert targetStmt;
        if (stmt instanceof _Insert._ChildInsert) {
            targetStmt = (_Insert._ValuesSyntaxInsert) ((_Insert._ChildInsert) stmt).parentStmt();
        } else {
            targetStmt = stmt;
        }
        this.nullMode = targetStmt.nullHandle();

    }


    ValuesSyntaxInsertContext(@Nullable StatementContext outerContext, _Insert._ChildInsert stmt
            , ValuesSyntaxInsertContext parentContext) {
        super(outerContext, stmt, parentContext);
        this.nullMode = ((_Insert._ValuesSyntaxInsert) stmt).nullHandle();
        assert this.nullMode == parentContext.nullMode;

    }


    static IllegalStateException parentStmtDontExecute(PrimaryFieldMeta<?> filed) {
        String m = String.format("parent stmt don't execute so %s parameter value is null", filed);
        return new IllegalStateException(m);
    }


}
