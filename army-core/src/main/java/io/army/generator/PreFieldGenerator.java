package io.army.generator;

import io.army.bean.ReadWrapper;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;

/**
 * this interface create value for the mapping property of Entity when persist a entity before persistence.
 * if you want creation before persistence ,use {@link PostFieldGenerator}
 * <p>
 * there is two ways for implementation of MultiGenerator:
 * <pre>
 *    Example one:
 *
 *    public class MyMultiGenerator implements PreMultiGenerator {
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
 *    public class MyMultiGenerator implements PreMultiGenerator {
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
 * @see io.army.annotation.Generator
 * @since Army 1.0
 */
public interface PreFieldGenerator extends _FieldGenerator {

    /**
     * @see GeneratorMeta#dependFieldName()
     */
    String DEPEND_FIELD_NAME = "dependFieldName";


    Object next(FieldMeta<?> fieldMeta, ReadWrapper domain) throws GeneratorException;


}
