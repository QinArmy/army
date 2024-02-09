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


import io.army.criteria.impl.inner._Statement;
import io.army.stmt.MultiStmt;

import java.util.function.BiConsumer;

interface MultiStmtContext extends _StmtContext {


    default <S extends _Statement, C extends _StmtContext> void appendStmt(BiConsumer<S, C> consumer, S statement, C context) {
        throw new UnsupportedOperationException();
    }

    default <S extends _Statement, C extends BatchSpecContext> void appendBatch(BiConsumer<S, C> consumer, S statement, C context) {
        throw new UnsupportedOperationException();
    }


    void startChildItem();

    void appendItemForChild();

    void endChildItem();

    void batchStmtStart(int batchSize);

    void addBatchItem(MultiStmt.StmtItem item);

    MultiStmtContext batchStmtEnd();


}
