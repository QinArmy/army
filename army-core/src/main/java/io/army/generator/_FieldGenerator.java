package io.army.generator;

/**
 *
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
 *         &#042; &#064;paramMeta not null
 *         &#042; &#064;paramMap not null,a unmodifiable map
 *         &#042;
 *         &#042;&#047;
 *        public static MyMultiGenerator debugSQL(FieldMeta&lt;?,?&gt; paramMeta, Map&lt;String,String&gt; paramMap){
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
 *         &#042; &#064;paramMeta not null
 *         &#042; &#064;paramMap not null,a unmodifiable map
 *         &#042;
 *         &#042;&#047;
 *        public MyMultiGenerator(FieldMeta&lt;?,?&gt; paramMeta,  Map&lt;String,String&gt; paramMap){...}
 *
 *     }
 * </pre>
 * </p>
 *
 * </p>
 *
 * @see io.army.annotation.Generator
 * @see FieldGenerator
 */
public interface _FieldGenerator {

}
