package io.army.meta.mapping;

import io.army.struct.CodeEnum;
import io.army.struct.CodeEnumException;
import io.army.util.Assert;
import io.army.util.Precision;
import io.army.util.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.sql.JDBCType;
import java.util.Map;

/**
 * @see Enum
 * @see io.army.struct.CodeEnum
 */
public class CodeEnumMapping extends AbstractMappingType {

    private static final Method GET_CODE_MAP_METHOD;

    private final Class<?> javaType;


    static {
        GET_CODE_MAP_METHOD = ReflectionUtils.findMethod(CodeEnum.class, "getCodeMap", Class.class);
    }

    public CodeEnumMapping(Class<?> javaType) {
        this.javaType = javaType;
        Assert.isTrue(javaType.isEnum(), () -> String.format("javaType[%s] isn't Enum", javaType));
        Assert.isAssignable(CodeEnum.class, javaType);
        ReflectionUtils.invokeMethod(GET_CODE_MAP_METHOD, javaType);
    }

    @Override
    public Class<?> javaType() {
        return javaType;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.INTEGER;
    }

    @Override
    public String nullSafeTextValue(Object value) {

        return null;
    }

    @Override
    public boolean isTextValue(String textValue) {
        boolean yes;
        try {
            int code = Integer.parseInt(textValue);
            @SuppressWarnings({"unchecked"})
            Map<Integer, CodeEnum> codeEnumMap = (Map<Integer, CodeEnum>) ReflectionUtils.invokeMethod(
                    GET_CODE_MAP_METHOD, javaType);
            if (codeEnumMap == null) {
                return false;
            }
            CodeEnum codeEnum = codeEnumMap.get(code);
            yes = codeEnum != null;
        } catch (NumberFormatException | CodeEnumException e) {
            yes = false;
        }
        return yes;
    }

    @Nonnull
    @Override
    public Precision precision() {
        return Precision.DEFAULT_INT_PRECISION;
    }
}
