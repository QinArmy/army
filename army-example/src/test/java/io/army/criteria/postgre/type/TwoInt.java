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

package io.army.criteria.postgre.type;

public final class TwoInt {

    private Integer first;

    private Integer second;

    public Integer getFirst() {
        return first;
    }

    public TwoInt setFirst(Integer first) {
        this.first = first;
        return this;
    }

    public Integer getSecond() {
        return second;
    }

    public TwoInt setSecond(Integer second) {
        this.second = second;
        return this;
    }
}
