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
    NON_UPDATABLE(202, "", CRITERIA_ERROR),
    TABLE_ALIAS_ERROR(203, "", CRITERIA_ERROR),
    TABLE_ALIAS_DUPLICATION(204, "", CRITERIA_ERROR),
    SELECTION_DUPLICATION(205, "", CRITERIA_ERROR),
    REF_EXP_ERROR(206, "", CRITERIA_ERROR),
    NO_SELECTION(207, "", CRITERIA_ERROR),

    TYPE_ERROR(500, "date type error", NONE),
    CODE_ENUM_ERROR(501, "CedeEnum definition error", TYPE_ERROR),

    UNSUPPORT_DIALECT(600, "", NONE),

    NOT_FOUND_DOMAIN(700, "", NONE),



    NOT_FOUND_META_CLASS(701, "", NONE),
    META_CLASS_NOT_MATCH(702, "", NONE),
    SQL_TYPE_NOT_MATCH(703, "", NONE),
    PRECISION_LESS(704, "", NONE),
    NNSUPPORT_SQL_TYPE(705, "", NONE),

    ACCESS_ERROR(789, "", NONE),
    INSERT_ERROR(790, "", NONE),

    UNSUPPORTED_DIALECT(802, "", NONE),
    DDL_EXECUTE_ERROR(803, "", NONE),

    GENERATOR_ERROR(804, "", NONE),

    BEAN_ACCESS_ERROR(901, "", NONE),

    CANNOT_CREATE_SESSION(1000, "", ACCESS_ERROR),
    NO_CURRENT_SESSION(1001, "", ACCESS_ERROR),
    CLOSE_CONN_ERROR(1002, "", ACCESS_ERROR),
    TRANSACTION_ERROR(1003, "", ACCESS_ERROR),
    NO_SESSION_TRANSACTION(1004, "", TRANSACTION_ERROR),
    SESSION_FACTORY_CREATE_ERROR(1005, "", NONE),
    SESSION_CREATE_ERROR(1006, "", NONE),
    READ_ONLY_SESSION(1007, "", NONE),
    DUPLICATION_SESSION_TRANSACTION(1008, "", NONE),

    TRANSACTION_STATUS_ERROR(1009, "", TRANSACTION_ERROR),
    START_TRANSACTION_FAILURE(1010, "", TRANSACTION_ERROR),
    TRANSACTION_SYSTEM_ERROR(1011, "", TRANSACTION_ERROR),
    UNKNOWN_SAVE_POINT(1012, "", TRANSACTION_ERROR),
    TRANSACTION_TIME_OUT(1013, "", TRANSACTION_ERROR),
    CANNOT_GET_CONN(1014, "", ACCESS_ERROR),
    DUPLICATION_CURRENT_SESSION(1015, "", ACCESS_ERROR),
    DENY_BATCH_INSERT(1016, "", ACCESS_ERROR),
    INSERT_COUNT_NOT_MATCH(1017, "", ACCESS_ERROR),
    ILLEGAL_STATEMENT(1018, "", ACCESS_ERROR),
    FACTORY_NAME_DUPLICATION(1019, "", NONE),
    FIELD_CODEC_DUPLICATION(1020, "", NONE);


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
