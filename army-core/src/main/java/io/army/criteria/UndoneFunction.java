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
