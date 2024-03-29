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

import io.army.criteria.Selection;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.inner._Statement;

import java.util.List;

public interface _PostgreCommand extends _Statement {


    interface _SetCommand extends _PostgreCommand {

        _ParamValue paramValuePair();

    }

    interface _ParamValue {

        SQLs.VarScope scope();

        String name();

        Object word();

        List<Object> valueList();

    }


    interface _ShowCommand extends _PostgreCommand {

        List<? extends Selection> selectionList();


        Object parameter();

    }


}
