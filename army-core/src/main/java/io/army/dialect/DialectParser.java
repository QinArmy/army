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

import io.army.criteria.*;
import io.army.mapping.MappingType;
import io.army.meta.ServerMeta;
import io.army.schema.SchemaResult;
import io.army.session.SessionSpec;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;

import java.util.List;
import java.util.function.Consumer;

/**
 * A common interface to all dialect of dialect.
 */
public interface DialectParser {


    /**
     * @return one of :
     * <ul>
     *     <li>{@link SimpleStmt}</li>
     *     <li>{@link io.army.stmt.GeneratedKeyStmt}</li>
     *     <li>{@link io.army.stmt.PairStmt}</li>
     * </ul>
     */
    Stmt insert(InsertStatement insert, SessionSpec sessionSpec);

    /**
     * @return one of <ul>
     * <li>{@link SimpleStmt}</li>
     * <li>{@link io.army.stmt.BatchStmt}</li>
     * </ul>
     */
    Stmt update(UpdateStatement update, boolean useMultiStmt, SessionSpec sessionSpec);

    /**
     * @return one of <ul>
     * <li>{@link SimpleStmt}</li>
     * <li>{@link io.army.stmt.BatchStmt}</li>
     * </ul>
     */
    Stmt delete(DeleteStatement delete, boolean useMultiStmt, SessionSpec sessionSpec);

    /**
     * @return one of <ul>
     * <li>{@link SimpleStmt}</li>
     * <li>{@link io.army.stmt.BatchStmt}</li>
     * </ul>
     */
    Stmt select(SelectStatement select, boolean useMultiStmt, SessionSpec sessionSpec);

    Stmt values(Values values, SessionSpec sessionSpec);


    default Stmt dialectDml(DmlStatement statement, SessionSpec sessionSpec) {
        throw new UnsupportedOperationException();
    }

    default Stmt dialectDql(DqlStatement statement, SessionSpec sessionSpec) {
        throw new UnsupportedOperationException();
    }


    default List<String> schemaDdl(SchemaResult schemaResult) {
        throw new UnsupportedOperationException();
    }


    boolean isKeyWords(String words);

    ServerMeta serverMeta();

    StringBuilder identifier(String identifier, StringBuilder builder);


    String identifier(String identifier);

    void typeName(MappingType type, StringBuilder sqlBuilder);

    Dialect dialect();


    String printStmt(Stmt stmt, boolean beautify);

    void printStmt(Stmt stmt, boolean beautify, Consumer<String> appender);

    String sqlElement(SQLElement element);


}
