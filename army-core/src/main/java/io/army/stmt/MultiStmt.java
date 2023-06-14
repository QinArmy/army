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

        List<Selection> selectionList();


    }


}
