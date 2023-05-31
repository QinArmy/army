package io.army.criteria.mysql;

import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.dialect.Window;

/**
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-named-windows.html">MySQL 8.0 Named Windows</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-usage.html">Window Function Concepts and Syntax</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html">MySQL Window Function Frame Specification</a>
 * @since 1.0
 */
public interface MySQLWindow extends Window {


    interface _MySQLFrameBetweenClause extends _FrameBetweenClause<Item> {

    }

    interface _FrameUnitSpaceSpec extends _FrameUnitSpaceClause<Item, _MySQLFrameBetweenClause> {

    }

    interface _FrameExtentSpec extends _StaticFrameUnitRowsRangeSpec<Item, _MySQLFrameBetweenClause>,
            _DynamicFrameUnitRowsRangeClause<_FrameUnitSpaceSpec, Item> {

    }

    interface _OrderByCommaSpec extends Statement._OrderByCommaClause<_OrderByCommaSpec>, _FrameExtentSpec {

    }

    interface _OrderBySpec extends Statement._OrderByClause<_OrderByCommaSpec>, _FrameExtentSpec {

    }

    interface _PartitionByCommaSpec extends _PartitionByCommaClause<_PartitionByCommaSpec>, _OrderBySpec {

    }

    interface _PartitionBySpec extends _PartitionByExpClause<_PartitionByCommaSpec>, _OrderBySpec {

    }


}
