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

package io.army.criteria.dialect;

import io.army.criteria.Selection;
import io.army.criteria.TableField;

import java.util.function.Function;

public interface Returnings {

    Returnings selection(Selection selection);

    Returnings selection(Selection selection1, Selection selection2);

    Returnings selection(Function<String, Selection> function, String alias);

    Returnings selection(Function<String, Selection> function1, String alias1,
                         Function<String, Selection> function2, String alias2);

    Returnings selection(Function<String, Selection> function, String alias, Selection selection);

    Returnings selection(Selection selection, Function<String, Selection> function, String alias);

    Returnings selection(TableField field1, TableField field2, TableField field3);

    Returnings selection(TableField field1, TableField field2, TableField field3, TableField field4);

}


