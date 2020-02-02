package io.army.generator;

import io.army.dialect.SQLDialect;
import io.army.meta.FieldMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * this interface create value for the mapping property of Entity when persist a entity after persistence.
 * if you want creation before persistence ,use {@link PreMultiGenerator}
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
 *         &#042; &#064;mappingType not null
 *         &#042; &#064;paramMap not null,a unmodifiable map
 *         &#042;
 *         &#042;&#047;
 *        public static MyMultiGenerator build(FieldMeta&lt;?,?&gt; mappingType, Map&lt;String,String&gt; paramMap){
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
 *         &#042; &#064;mappingType not null
 *         &#042; &#064;paramMap not null,a unmodifiable map
 *         &#042;
 *         &#042;&#047;
 *        public MyMultiGenerator(FieldMeta&lt;?,?&gt; mappingType,  Map&lt;String,String&gt; paramMap){...}
 *
 *     }
 * </pre>
 * </p>
 *
 * @see io.army.annotation.Generator
 * @see PreMultiGenerator
 * @since Army 1.0
 */
public interface PostMultiGenerator extends MultiGenerator {


    Object apply(FieldMeta<?,?> fieldMeta,ResultSet resultSet) throws SQLException;

}
