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

package io.army.modelgen;

import io.army.annotation.Index;

enum IndexMode {

    GENERIC,
    UNIQUE,
    PRIMARY,
    NONE;

    static IndexMode resolve(final Index index) {
        final IndexMode mode;
        if (index.unique()) {
            final String[] columnList = index.fieldList();
            if (columnList.length == 1) {
                mode = _MetaBridge.ID.equals(columnList[0]) ? IndexMode.PRIMARY : IndexMode.UNIQUE;
            } else {
                mode = IndexMode.GENERIC;
            }
        } else {
            mode = IndexMode.GENERIC;
        }
        return mode;
    }


}
