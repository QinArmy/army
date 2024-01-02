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

package io.army.criteria.postgre;

import io.army.criteria.Item;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public interface PostgreCommand extends PostgreStatement {

    interface CursorDirection {

    }

    /*-------------------below DECLARE command syntax interfaces-------------------*/


    interface _DeclareComplexQueryCommand<I extends Item>
            extends PostgreQuery._PostgreSelectClause<_AsCommandClause<I>>
            , PostgreValues._PostgreValuesClause<_AsCommandClause<I>> {

    }


    interface _DeclareQueryWithSpec<I extends Item>
            extends _PostgreDynamicWithClause<_DeclareComplexQueryCommand<I>>,
            PostgreQuery._PostgreStaticWithClause<_DeclareComplexQueryCommand<I>>,
            _DeclareComplexQueryCommand<I> {


    }

    interface _DeclareForQueryClause<I extends Item> {

        _DeclareQueryWithSpec<I> For();

    }

    interface _DeclareWithHoldSpec<I extends Item> extends _DeclareForQueryClause<I> {

        _DeclareForQueryClause<I> withHold();

        _DeclareForQueryClause<I> withoutHold();

        _DeclareForQueryClause<I> ifWithHold(BooleanSupplier predicate);

        _DeclareForQueryClause<I> ifWithoutHold(BooleanSupplier predicate);

    }

    interface _DeclareCursorWordClause<I extends Item> {

        _DeclareWithHoldSpec<I> cursor();

    }

    interface _DeclareCursorScrollSpec<I extends Item> extends _DeclareCursorWordClause<I> {

        _DeclareCursorWordClause<I> scroll();

        _DeclareCursorWordClause<I> noScroll();

        _DeclareCursorWordClause<I> ifScroll(BooleanSupplier predicate);

        _DeclareCursorWordClause<I> ifNoScroll(BooleanSupplier predicate);
    }

    interface _DeclareCursorSensitiveSpec<I extends Item> extends _DeclareCursorScrollSpec<I> {

        _DeclareCursorScrollSpec<I> asensitive();

        _DeclareCursorScrollSpec<I> insensitive();

        _DeclareCursorScrollSpec<I> ifAsensitive(BooleanSupplier predicate);

        _DeclareCursorScrollSpec<I> ifInsensitive(BooleanSupplier predicate);


    }

    interface _DeclareCursorBinarySpec<I extends Item> extends _DeclareCursorSensitiveSpec<I> {

        _DeclareCursorSensitiveSpec<I> binary();

        _DeclareCursorSensitiveSpec<I> ifBinary(BooleanSupplier predicate);
    }

    interface _DeclareCursorNameClause<I extends Item> {

        _DeclareCursorBinarySpec<I> declare(String name);

    }


    interface _SpaceCursorNameClause<I extends Item> {

        _AsCommandClause<I> space(String cursorName);

    }

    interface _FromInCursorClause<I extends Item> extends _AsCommandClause<I> {

        _AsCommandClause<I> from(String cursorName);

        _AsCommandClause<I> in(String cursorName);

        _SpaceCursorNameClause<I> ifFrom(BooleanSupplier predicate);

        _SpaceCursorNameClause<I> ifIn(BooleanSupplier predicate);

    }


    /*-------------------below FETCH command syntax interfaces-------------------*/


    interface _FetchCursorClause<I extends Item> {

        _FromInCursorClause<I> fetch();

        _AsCommandClause<I> fetch(String cursorName);

        _FromInCursorClause<I> fetch(CursorDirection direction);

        _FromInCursorClause<I> fetchIf(Supplier<CursorDirection> supplier);

    }

    /*-------------------below MOVE syntax interfaces -------------------*/


    interface _MoveCursorClause<I extends Item> {

        _FromInCursorClause<I> move();

        _AsCommandClause<I> move(String cursorName);

        _FromInCursorClause<I> move(CursorDirection direction);

        _FromInCursorClause<I> moveIf(Supplier<CursorDirection> supplier);

    }

    /*-------------------below CLOSE syntax interfaces -------------------*/

    interface _CloseCursorClause<I extends Item> {

        _AsCommandClause<I> close(String name);

        _AsCommandClause<I> closeAll();

    }

}
