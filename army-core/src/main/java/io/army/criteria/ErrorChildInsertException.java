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

package io.army.criteria;

/**
 * <p>Throw when insert {@link io.army.meta.ChildTableMeta} that parent's id is {@link io.army.annotation.GeneratorType#POST} type
 * and with conflict clause(eg: MySQL ON DUPLICATE KEY clause,MySQL REPLACE,Postgre conflict).
 * Because database couldn't return correct parent ids.
 *
 * @since 0.6.0
 */
public final class ErrorChildInsertException extends CriteriaException {

    public ErrorChildInsertException(String message) {
        super(message);
    }

}
