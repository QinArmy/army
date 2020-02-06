package io.army.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static io.army.util.TimeUtils.*;

/**
 * created  on 2018/4/18.
 */
public abstract class JsonUtils {

    /**
     * 公用的 ObjectMapper 请不要在运行中修改 配置,若要修改配置请使用
     */
    private static final ObjectMapper OBJECT_MAPPER;


    public static final LocalDateTimeDeserializer LOCALDATETIME_DESERIALIZER = new LocalDateTimeDeserializer(DATE_TIME_FORMATTER);

    public static final LocalDateTimeSerializer LOCALDATETIME_SERIALIZER = new LocalDateTimeSerializer(DATE_TIME_FORMATTER);

    public static final LocalDateDeserializer LOCALDATE_DESERIALIZER = new LocalDateDeserializer(DATE_FORMATTER);

    public static final LocalDateSerializer LOCALDATE_SERIALIZER = new LocalDateSerializer(DATE_FORMATTER);

    public static final LocalTimeSerializer LOCAL_TIME_SERIALIZER = new LocalTimeSerializer(TIME_FORMATTER);

    public static final LocalTimeDeserializer LOCAL_TIME_DESERIALIZER = new LocalTimeDeserializer(TIME_FORMATTER);


    /**
     * 数字序列化器, 这个序列化器会将 {@link BigDecimal} {@link BigInteger} {@link Long} 输出为字符串
     */
    public static final NumberSerializer NUMBER_SERIALIZER = new NumberSerializer(Number.class) {
        private static final long serialVersionUID = 328989124431014858L;

        @Override
        public void serialize(Number value, JsonGenerator g, SerializerProvider provider) throws IOException {
            // should mostly come in asType one of these two:
            if (value instanceof BigDecimal) {
                BigDecimal amount = (BigDecimal) value;
                if (amount.scale() == 0) {
                    amount = amount.setScale(2, RoundingMode.HALF_EVEN);
                }
                g.writeString(amount.toPlainString());
            } else if (value instanceof BigInteger) {
                g.writeString(value.toString());
            } else if (value instanceof Long) {
                g.writeString(value.toString());
            } else if (value instanceof Double) {
                g.writeNumber(value.doubleValue());
            } else if (value instanceof Float) {
                g.writeNumber(value.floatValue());
            } else if (value instanceof Integer || value instanceof Byte || value instanceof Short) {
                g.writeNumber(value.intValue()); // doesn'table need to be cast to smaller numbers
            } else {
                g.writeNumber(value.toString());
            }
        }
    };


    static {
        OBJECT_MAPPER = createDefaultObjectMapper();
    }

    private static Module createCustomModule(String moduleName) {
        // 自定义模块,重写部分序列化器
        return new SimpleModule(moduleName == null ? "financeCustomModule" : moduleName)
                .addSerializer(Number.class, NUMBER_SERIALIZER)
                ;
    }


    public static ObjectMapper createDefaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setTimeZone(TimeUtils.TIME_ZONE8);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mapper.setDateFormat(dateFormat);

        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentObjectsWith(new DefaultIndenter("  ", "\n"));
        mapper.setDefaultPrettyPrinter(prettyPrinter);

        mapper.registerModule(createCustomModule(null));
        mapper.registerModule(createJavaTimeModule());

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        mapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);

        return mapper;
    }


    private static Module createJavaTimeModule() {
        return new JavaTimeModule()
                .addDeserializer(LocalDateTime.class, LOCALDATETIME_DESERIALIZER)
                .addSerializer(LocalDateTime.class, LOCALDATETIME_SERIALIZER)

                .addDeserializer(LocalDate.class, LOCALDATE_DESERIALIZER)
                .addSerializer(LocalDate.class, LOCALDATE_SERIALIZER)

                .addDeserializer(LocalTime.class, LOCAL_TIME_DESERIALIZER)
                .addSerializer(LocalTime.class, LOCAL_TIME_SERIALIZER)
                ;
    }


    /**
     * @throws RuntimeException - 当 json 不能转换为目标对象时抛出
     */
    public static <T> T readValue(String json, Class<T> clazz) throws RuntimeException {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param in 当读取完成后,将自动关闭
     * @throws RuntimeException - 当 json 不能转换为目标对象时抛出
     */
    public static <T> T readValue(InputStream in, Class<T> clazz) throws IOException {
        try (InputStream i = in) {
            return OBJECT_MAPPER.readValue(i, clazz);
        }
    }


    public static <T> T readValue(InputStream in, TypeReference<T> typeReference) throws IOException {
        try (InputStream i = in) {
            return OBJECT_MAPPER.readValue(i, typeReference);
        }
    }

    /**
     * @param reader 当读取完成后,将自动关闭
     * @throws RuntimeException - 当 json 不能转换为目标对象时抛出
     */
    public static <T> T readValue(Reader reader, Class<T> clazz) throws IOException {
        try (Reader r = reader) {
            return OBJECT_MAPPER.readValue(r, clazz);
        }
    }


    /**
     * @throws RuntimeException - 当 value 不能转换为 json 时抛出,如 : value 回环
     */
    public static String writeValue(Object value) throws RuntimeException {
        return writeValue(value, false);
    }

    /**
     * @throws RuntimeException - 当 value 不能转换为 json 时抛出,如 : value 回环
     */
    public static String writeValue(Object value, boolean pretty) throws RuntimeException {
        try {
            ObjectWriter objectWriter;
            if (pretty) {
                objectWriter = OBJECT_MAPPER.writerWithDefaultPrettyPrinter();
            } else {
                objectWriter = OBJECT_MAPPER.writer();
            }
            return objectWriter.writeValueAsString(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param writer 当写出完成后,将自动关闭
     * @throws IOException - 1.当 value 不能转换为 json 时抛出,如 : value 回环,2. 输出异常
     */
    public static void writeValue(Object value, Writer writer) throws IOException {
        writeValue(value, writer, false);
    }

    /**
     * @param writer 当写出完成后,将自动关闭
     * @throws IOException - 1.当 value 不能转换为 json 时抛出,如 : value 回环,2. 输出异常
     */
    public static void writeValue(Object value, Writer writer, boolean pretty) throws IOException {
        try (Writer w = writer) {

            if (pretty) {
                JsonGenerator generator = OBJECT_MAPPER.getFactory().createGenerator(w);
                OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(generator, value);
            } else {
                JsonGenerator generator = OBJECT_MAPPER.getFactory().createGenerator(w);
                OBJECT_MAPPER.writeValue(generator, value);
            }
        }
    }

    /**
     * @param out 当写出完成后,将自动关闭
     * @throws IOException - 1.当 value 不能转换为 json 时抛出,如 : value 回环,2. 输出异常
     */
    public static void writeValue(Object value, OutputStream out) throws IOException {
        writeValue(value, out, false);
    }

    /**
     * @param out 当写出完成后,将自动关闭
     * @throws IOException - 1.当 value 不能转换为 json 时抛出,如 : value 回环,2. 输出异常
     */
    public static void writeValue(Object value, OutputStream out, boolean pretty) throws IOException {
        try (OutputStream o = out) {

            if (pretty) {
                JsonGenerator generator = OBJECT_MAPPER.getFactory().createGenerator(o, JsonEncoding.UTF8);
                OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(generator, value);
            } else {
                JsonGenerator generator = OBJECT_MAPPER.getFactory().createGenerator(o, JsonEncoding.UTF8);
                OBJECT_MAPPER.writeValue(generator, value);
            }
        }
    }

    public static JsonNode parseFromByte(byte[] content) throws IOException {
        return OBJECT_MAPPER.readTree(content);
    }

    public static JsonNode parseTree(String json) throws IOException {
        return OBJECT_MAPPER.readTree(json);
    }


    public static <T> T readValue(JsonNode node, Class<T> clazz) throws IOException {
        return OBJECT_MAPPER.readValue(node.traverse(), clazz);
    }


    public static <T> T readValue(JsonNode node, TypeReference<T> typeReference) throws IOException {
        return OBJECT_MAPPER.readValue(node.traverse(), typeReference);
    }

    public static <T> T readValue(String json, TypeReference<T> typeReference) throws IOException {
        return OBJECT_MAPPER.readValue(json, typeReference);
    }


}
