package io.army.generator;

import io.army.ErrorCode;

import io.army.criteria.MetaException;
import io.army.env.Environment;
import io.army.meta.FieldMeta;
import io.army.util.Assert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AutoGeneratedKeyGenerator implements PostMultiGenerator {

    private static class Holder {

        private static final AutoGeneratedKeyGenerator INSTANCE = new AutoGeneratedKeyGenerator();

        private static final Map<Class<?>, PostMultiGenerator> AUTO_GENERATOR_FUNCTIONS = createAutoGeneratorFunctions();


        private static Map<Class<?>, PostMultiGenerator> createAutoGeneratorFunctions() {

            Map<Class<?>, PostMultiGenerator> map = new HashMap<>(8);

            map.put(Long.class, AutoGeneratedKeyGenerator::longAutoGenerator);
            map.put(Integer.class, AutoGeneratedKeyGenerator::integerAutoGenerator);
            map.put(String.class, AutoGeneratedKeyGenerator::stringAutoGenerator);
            map.put(BigDecimal.class, AutoGeneratedKeyGenerator::bigDecimalAutoGenerator);

            map.put(BigInteger.class, AutoGeneratedKeyGenerator::bigIntegerAutoGenerator);
            return Collections.unmodifiableMap(map);
        }

    }

    public static PostMultiGenerator getInstance(FieldMeta<?, ?> fieldMeta, Environment env) {
        if (!fieldMeta.primary()
                || !Holder.AUTO_GENERATOR_FUNCTIONS.containsKey(fieldMeta.javaType())) {

            throw new MetaException(ErrorCode.META_ERROR, String.format(
                    "entity[%s] prop[%s] type[%s] isn't supported by %s"
                    , fieldMeta.table().javaType().getName()
                    , fieldMeta.propertyName()
                    , fieldMeta.javaType().getName()
                    , AutoGeneratedKeyGenerator.class.getName()
            ));
        }
        return Holder.INSTANCE;
    }


    private static Object longAutoGenerator(FieldMeta<?, ?> fieldMeta, ResultSet resultSet) throws SQLException {
        return resultSet.getLong(1);
    }

    private static Object integerAutoGenerator(FieldMeta<?, ?> fieldMeta, ResultSet resultSet) throws SQLException {
        return resultSet.getInt(1);
    }

    private static Object stringAutoGenerator(FieldMeta<?, ?> fieldMeta, ResultSet resultSet) throws SQLException {
        return resultSet.getString(1);
    }

    private static Object bigDecimalAutoGenerator(FieldMeta<?, ?> fieldMeta, ResultSet resultSet) throws SQLException {
        return resultSet.getBigDecimal(1);
    }

    private static Object bigIntegerAutoGenerator(FieldMeta<?, ?> fieldMeta, ResultSet resultSet) throws SQLException {
        BigDecimal decimal = resultSet.getBigDecimal(1);
        Object value;
        if (decimal == null) {
            throw new IllegalStateException("database getGeneratedKeys return null");
        } else {
            value = decimal.toBigInteger();
        }
        return value;
    }


    /*################################## blow instance method ##################################*/

    private AutoGeneratedKeyGenerator() {
    }

    @Override
    public Object apply(FieldMeta<?, ?> fieldMeta, ResultSet resultSet) throws SQLException {
        PostMultiGenerator generator = Holder.AUTO_GENERATOR_FUNCTIONS.get(fieldMeta.javaType());
        Assert.notNull(generator, "mappingType error");

        return generator.apply(fieldMeta, resultSet);
    }
}
