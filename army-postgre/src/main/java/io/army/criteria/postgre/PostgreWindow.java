package io.army.criteria.postgre;

import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.dialect.Window;


/**
 * @see <a href="https://www.postgresql.org/docs/current/functions-window.html">Window Functions</a>
 * @see <a href="https://www.postgresql.org/docs/current/tutorial-window.html">Window Functions tutorial</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Window Function Calls</a>
 * @since 1.0
 */
public interface PostgreWindow extends Window {


    interface _PostgreFrameExclusionClause extends _FrameExclusionClause<Item> {

    }


    interface _PostgreFrameBetweenClause extends _FrameBetweenClause<_PostgreFrameExclusionClause> {

    }

    interface _FrameUnitSpaceSpec extends _FrameUnitSpaceClause<_PostgreFrameExclusionClause, _PostgreFrameBetweenClause> {

    }

    interface _FrameExtentSpec extends _StaticFrameUnitRowsRangeGroupsSpec<_PostgreFrameExclusionClause, _PostgreFrameBetweenClause>,
            _DynamicFrameUnitRowsRangeGroupsClause<_FrameUnitSpaceSpec, Item> {

    }

    interface _OrderByCommaSpec extends Statement._OrderByCommaClause<_OrderByCommaSpec>,
            Statement._DynamicOrderByClause<_FrameExtentSpec>,
            _FrameExtentSpec {
        //TODO postgre USING operator
    }

    interface _OrderBySpec extends Statement._StaticOrderByClause<_OrderByCommaSpec>, _FrameExtentSpec {
        //TODO postgre USING operator
    }

    interface _PartitionByCommaSpec extends _PartitionByCommaClause<_PartitionByCommaSpec>, _OrderBySpec {

    }

    interface _PartitionBySpec extends _PartitionByExpClause<_PartitionByCommaSpec>, _OrderBySpec {

    }


}
