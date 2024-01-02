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

package io.army.stmt;

import io.army.criteria.Selection;

import java.util.List;

public interface MultiStmt extends Stmt {

    String multiSql();

    List<StmtItem> stmtItemList();


    interface StmtItem {
        boolean hasOptimistic();

    }

    interface ProcedureItem extends StmtItem {

        /**
         * @return empty or result item list
         */
        List<StmtItem> resultItemList();

        /**
         * @return always false
         */
        @Override
        boolean hasOptimistic();


    }//ProcedureItem


    final class UpdateStmt implements StmtItem {

        public static final UpdateStmt OPTIMISTIC = new UpdateStmt(true);

        public static final UpdateStmt NON_OPTIMISTIC = new UpdateStmt(false);


        private final boolean optimistic;

        /**
         * private constructor
         */
        private UpdateStmt(boolean optimistic) {
            this.optimistic = optimistic;
        }

        @Override
        public boolean hasOptimistic() {
            return this.optimistic;
        }

        @Override
        public String toString() {
            return String.format("%s[hash:%s,optimistic:%s]", UpdateStmt.class.getName(),
                    System.identityHashCode(this), this.optimistic
            );
        }


    }//UpdateStmt


    interface QueryStmt extends StmtItem {

        List<? extends Selection> selectionList();


    }


}
