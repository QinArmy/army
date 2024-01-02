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

import io.army.criteria.standard.SQLFunction;

/**
 * <p>
 * This interface representing undone sql function,it mean that have the composite type defined by an AS clause.
 * <pre><br/>
 * For example Postgre json_to_record() function:
 * Expands the top-level JSON object to a row having the composite type defined by an AS clause.
 * (As with all functions returning record, the calling query must explicitly define the structure of the record
 * with an AS clause.) The output record is filled from fields of the JSON object, in the same way as described
 * above for json[b]_populate_record. Since there is no input record value, unmatched columns are always filled
 * with nulls.
 * create type myrowtype as (a int, b text);
 * select * from json_to_record('{"a":1,"b":[1,2,3],"c":[1,2,3],"e":"bar","r": {"a": 123, "b": "a b c"}}') as x(a int, b text, c int[], d text, r myrowtype) →
 *  a |    b    |    c    | d |       r
 * ---+---------+---------+---+---------------
 *  1 | [1,2,3] | {1,2,3} |   | (123,"a b c")
 * </pre>
 *
 * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_to_record ( jsonb ) → record<br/>
 * </a>
 * @since 0.6.0
 */
public interface UndoneFunction extends TabularItem, SQLFunction {

}
