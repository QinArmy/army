package io.army.generator;

import io.army.meta.FieldMeta;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * this interface create value for the mapping property of Entity when persist a entity after persistence.
 * if you want creation before persistence ,use {@link PreFieldGenerator}
 * <p>
 * there is two ways for implementation of MultiGenerator:
 * <pre>
 *    Example one:
 *
 *    public class MyMultiGenerator implements PostMultiGenerator {
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
 *    public class MyMultiGenerator implements PostMultiGenerator {
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
 * @see PreFieldGenerator
 * @since Army 1.0
 */
public interface PostFieldGenerator extends FieldGenerator {


    Object apply(FieldMeta<?, ?> fieldMeta, ResultSet resultSet) throws SQLException;

}
