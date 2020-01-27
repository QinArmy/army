package io.army;

import io.army.struct.CodeEnum;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * created  on 2018/11/19.
 */
public enum ErrorCode implements CodeEnum {

    NONE(0, "none"),

    META_ERROR(100, "meta config error", NONE),
    META_ILLEGALITY(101, "meta config illegality", META_ERROR),
    MAPPING_NOT_FOUND(102, "mapping not found", META_ERROR),

    CRITERIA_ERROR(200, "criteria error", NONE),
    ILLEGAL_PREDICATE(201, "illegal predicate", CRITERIA_ERROR),

    TYPE_ERROR(500, "date type error", NONE),
    CODE_ENUM_ERROR(501, "CedeEnum definition error", TYPE_ERROR),

    UNSUPPORT_DIALECT(600, "", NONE),

    NOT_FOUND_DOMAIN(700, "", NONE),

    NOT_FOUND_META_CLASS(701, "", NONE),
    META_CLASS_NOT_MATCH(702, "", NONE),
    SQL_TYPE_NOT_MATCH(703, "", NONE),
    PRECISION_LESS(704, "", NONE),
    NNSUPPORT_SQL_TYPE(705, "", NONE),

    ACCESS_ERROR(801, "", NONE),

    UNSUPPORTED_DIALECT(802, "", NONE);


    private final int code;

    private final String display;

    private final ErrorCode family;

    private static final Map<Integer, ErrorCode> CODE_MAP = CodeEnum.getCodeMap(ErrorCode.class);


    public static ErrorCode resolve(int code) {
        return CODE_MAP.get(code);
    }

    ErrorCode(int code, String display) {
        this(code, display, null);
    }

    ErrorCode(int code, @NonNull String display, @Nullable ErrorCode family) {
        this.code = code;
        this.display = display;
        this.family = family == null ? this : family;
    }


    @Override
    public int code() {
        return this.code;
    }

    @NonNull
    @Override
    public String display() {
        return this.display;
    }

    @NonNull
    public ErrorCode family() {
        return family;
    }
}
