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

package io.army.criteria.impl.inner.postgre;

import io.army.criteria.SQLWords;
import io.army.criteria.SubStatement;
import io.army.criteria.impl.inner._Expression;

import javax.annotation.Nullable;
import java.util.List;

public interface _PostgreCteStatement extends SubStatement {

    @Nullable
    SQLWords materializedOption();

    SubStatement subStatement();


    interface _SearchOptionClauseSpec extends _PostgreCteStatement {

        @Nullable
        SQLWords searchOption();

        List<String> firstByList();

        String searchSeqColumnName();

        @Nullable
        List<String> cycleColumnList();

        String cycleMarkColumnName();

        @Nullable
        _Expression cycleMarkValue();

        @Nullable
        _Expression cycleMarkDefault();

        String cyclePathColumnName();

    }



}
