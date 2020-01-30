package io.army.generator;

import io.army.beans.BeanWrapper;
import io.army.dialect.SQLDialect;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;

/**
 * this interface create value for the mapping property of Entity when persist a entity before persistence.
 * if you want creation before persistence ,use {@link PostMultiGenerator}
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
 *    public class MyMultiGenerator implements PreMultiGenerator {
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
 * @see io.army.annotation.Generator
 * @see PostMultiGenerator
 * @since Army 1.0
 */
public interface PreMultiGenerator extends MultiGenerator {


    Object next(FieldMeta<?, ?> fieldMeta, BeanWrapper entityWrapper) throws GeneratorException;


}
