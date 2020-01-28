package io.army.generator;

import io.army.dialect.SQLDialect;
import io.army.meta.FieldMeta;

/**
 * this interface create value for the mapping property of Entity when persist a entity.
 * <p>
 * there is two ways for implementation of MultiGenerator:
 * <pre>
 *    Example one:
 *
 *    public class MyMultiGenerator implements MultiGenerator {
 *
 *        private MyMultiGenerator(){}
 *
 *        &#047;&#042;&#042;
 *         &#042; &#064;fieldMeta not null
 *         &#042; &#064;paramMap not null,a unmodifiable map
 *         &#042;
 *         &#042;&#047;
 *        public static MyMultiGenerator build(FieldMeta&lt;?,?&gt; fieldMeta, Map&lt;String,String&gt; paramMap){
 *           ...
 *        }
 *
 *     }
 * </pre>
 * or
 * <pre>
 *    Example two:
 *
 *    public class MyMultiGenerator implements MultiGenerator {
 *
 *        &#047;&#042;&#042;
 *         &#042; &#064;fieldMeta not null
 *         &#042; &#064;paramMap not null,a unmodifiable map
 *         &#042;
 *         &#042;&#047;
 *        public MyMultiGenerator(FieldMeta&lt;?,?&gt; fieldMeta,  Map&lt;String,String&gt; paramMap){...}
 *
 *     }
 * </pre>
 * </p>
 *
 * </p>
 *
 * @see SingleGenerator
 * @see io.army.annotation.Generator
 * @since Army 1.0
 */
public interface MultiGenerator {


    Object next(FieldMeta<?, ?> fieldMeta, SQLDialect sqlDialect);

}
