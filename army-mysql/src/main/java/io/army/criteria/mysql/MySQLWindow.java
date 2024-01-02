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

package io.army.criteria.mysql;

import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.dialect.Window;

/**
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-named-windows.html">MySQL 8.0 Named Windows</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-usage.html">Window Function Concepts and Syntax</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html">MySQL Window Function Frame Specification</a>
 * @since 0.6.0
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

    interface _OrderBySpec extends Statement._StaticOrderByClause<_OrderByCommaSpec>,
            Statement._DynamicOrderByClause<_FrameExtentSpec>,
            _FrameExtentSpec {

    }

    interface _PartitionByCommaSpec extends _PartitionByCommaClause<_PartitionByCommaSpec>, _OrderBySpec {

    }

    interface _PartitionBySpec extends _PartitionByExpClause<_PartitionByCommaSpec>, _OrderBySpec {

    }


}
