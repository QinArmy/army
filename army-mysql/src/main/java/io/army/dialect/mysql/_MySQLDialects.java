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

package io.army.dialect.mysql;

import io.army.dialect.Database;
import io.army.dialect.DialectEnv;
import io.army.dialect.DialectParserFactory;
import io.army.util._Exceptions;

@SuppressWarnings("unused")
public abstract class _MySQLDialects extends DialectParserFactory {

    private _MySQLDialects() {
        throw new UnsupportedOperationException();
    }

    public static MySQLParser create(final DialectEnv env) {
        final MySQLDialect targetDialect;
        targetDialect = (MySQLDialect) targetDialect(env, Database.MySQL);
        final MySQLParser mySQL;
        switch (targetDialect) {
            case MySQL55:
            case MySQL56:
            case MySQL57:
            case MySQL80:
                mySQL = MySQLDialectParser.create(env, targetDialect);
                break;
            default:
                throw _Exceptions.unexpectedEnum(targetDialect);
        }
        return mySQL;
    }


}
