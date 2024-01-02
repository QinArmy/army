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
 * <p>
 * This interface representing single select item in select list clause.
 * * @see io.army.meta.FieldMeta
 *
 * @see QualifiedField
 * @see Expression#as(String)
 * @see io.army.meta.FieldMeta#as(String)
 * @see QualifiedField#as(String)
 * @since 0.6.0
 */
public interface Selection extends SelectItem, TypeInfer {

    String label();


}
