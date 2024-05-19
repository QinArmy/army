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

package io.army.dialect.impl;

import io.army.criteria.NullMode;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.Database;
import io.army.meta.PrimaryFieldMeta;
import io.army.session.SessionSpec;
import io.army.util._Exceptions;

import javax.annotation.Nullable;

abstract class ValuesSyntaxInsertContext extends InsertContext implements _ValueSyntaxInsertContext {


    final NullMode nullMode;


    ValuesSyntaxInsertContext(@Nullable StatementContext outerContext, final _Insert._ValuesSyntaxInsert stmt,
                              ArmyParser parser, SessionSpec sessionSpec) {
        super(outerContext, stmt, parser, sessionSpec);

        final _Insert._ValuesSyntaxInsert targetStmt;
        if (stmt instanceof _Insert._ChildInsert) {
            targetStmt = (_Insert._ValuesSyntaxInsert) ((_Insert._ChildInsert) stmt).parentStmt();
        } else {
            targetStmt = stmt;
        }

        this.nullMode = handleNullMode(targetStmt.nullHandle(), parser);
    }


    ValuesSyntaxInsertContext(@Nullable StatementContext outerContext, _Insert._ChildInsert stmt,
                              ValuesSyntaxInsertContext parentContext) {
        super(outerContext, stmt, parentContext);

        this.nullMode = handleNullMode(((_Insert._ValuesSyntaxInsert) stmt).nullHandle(), this.parser);

        assert this.nullMode == parentContext.nullMode;

    }


    static IllegalStateException parentStmtDontExecute(PrimaryFieldMeta<?> filed) {
        String m = String.format("parent stmt don't execute so %s parameter value is null", filed);
        return new IllegalStateException(m);
    }


    private static NullMode handleNullMode(final NullMode userMode, final ArmyParser parser) {
        final NullMode mode;
        switch (userMode) {
            case DEFAULT: {
                if (parser.serverDatabase == Database.SQLite) {
                    mode = NullMode.INSERT_NULL;
                } else {
                    mode = NullMode.INSERT_DEFAULT;
                }
            }
            break;
            case INSERT_DEFAULT:
            case INSERT_NULL:
                mode = userMode;
                break;
            default:
                throw _Exceptions.unexpectedEnum(userMode);
        }

        if (mode == NullMode.INSERT_DEFAULT && parser.serverDatabase == Database.SQLite) {
            throw _Exceptions.dontSupportNullMode(userMode, parser.dialect);
        }
        return mode;
    }


}
