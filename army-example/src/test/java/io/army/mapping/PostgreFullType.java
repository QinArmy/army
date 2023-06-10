package io.army.mapping;

import io.army.annotation.*;
import io.army.example.common.BaseVersionDomain;

import java.math.BigDecimal;
import java.time.*;

@Table(name = "postgre_full_type", indexes = {
        @Index(name = "postgre_full_type_my_big_int_uni", unique = true, fieldList = {"myBigInt desc"}, type = "btree"),
        @Index(name = "postgre_full_type_my_short_my_date_idx", fieldList = {"myShort", "myDate asc"}, type = "btree")},
        comment = "postgre full type table")
public class PostgreFullType extends BaseVersionDomain<PostgreFullType> {

    @Generator(type = GeneratorType.POST)
    @Column
    private Long id;

    @Column(defaultValue = "0", comment = "my short")
    private Short myShort;

    @Column(defaultValue = "0", comment = "my integer")
    private Integer myInteger;

    @Column(defaultValue = "0", comment = "my bigint")
    private Long myBigInt;

    @Column(nullable = false, defaultValue = "0.0", precision = 14, scale = 2, comment = "my decimal")
    private BigDecimal myDecimal;

    @Column(nullable = false, defaultValue = "CURRENT_DATE", comment = "my date")
    private LocalDate myDate;

    @Column(nullable = false, defaultValue = "LOCALTIME", comment = "my time")
    private LocalTime myTime;

    @Column(nullable = false, defaultValue = "LOCALTIME(6)", scale = 6, comment = "my time6")
    private LocalTime myTime6;

    @Column(nullable = false, defaultValue = "LOCALTIMESTAMP", comment = "my date time")
    private LocalDateTime myDateTime;

    @Column(nullable = false, defaultValue = "LOCALTIMESTAMP(6)", scale = 6, comment = "my date time")
    private LocalDateTime myDateTime6;

    @Column(nullable = false, defaultValue = "CURRENT_TIME", comment = "my offset time")
    private OffsetTime myOffsetTime;

    @Column(nullable = false, defaultValue = "CURRENT_TIME(6)", scale = 6, comment = "my offset time")
    private OffsetTime myOffsetTime6;

    @Column(nullable = false, defaultValue = "CURRENT_TIMESTAMP", comment = "my offset date time")
    private OffsetDateTime myOffsetDateTime;

    @Column(nullable = false, defaultValue = "CURRENT_TIMESTAMP(6)", scale = 6, comment = "my offset date time")
    private OffsetDateTime myOffsetDateTime6;



    /*-------------------below array-------------------*/

    @Column(defaultValue = "'{}'", comment = "my integer array")
    private Integer[] myIntegerArray;

    @Override
    public Long getId() {
        return id;
    }

    public PostgreFullType setId(Long id) {
        this.id = id;
        return this;
    }


    public Short getMyShort() {
        return myShort;
    }

    public PostgreFullType setMyShort(Short myShort) {
        this.myShort = myShort;
        return this;
    }

    public Integer getMyInteger() {
        return myInteger;
    }

    public PostgreFullType setMyInteger(Integer myInteger) {
        this.myInteger = myInteger;
        return this;
    }

    public Long getMyBigInt() {
        return myBigInt;
    }

    public PostgreFullType setMyBigInt(Long myBigInt) {
        this.myBigInt = myBigInt;
        return this;
    }

    public BigDecimal getMyDecimal() {
        return myDecimal;
    }

    public PostgreFullType setMyDecimal(BigDecimal myDecimal) {
        this.myDecimal = myDecimal;
        return this;
    }

    public LocalDate getMyDate() {
        return myDate;
    }

    public PostgreFullType setMyDate(LocalDate myDate) {
        this.myDate = myDate;
        return this;
    }

    public LocalTime getMyTime() {
        return myTime;
    }

    public PostgreFullType setMyTime(LocalTime myTime) {
        this.myTime = myTime;
        return this;
    }

    public LocalTime getMyTime6() {
        return myTime6;
    }

    public PostgreFullType setMyTime6(LocalTime myTime6) {
        this.myTime6 = myTime6;
        return this;
    }

    public LocalDateTime getMyDateTime() {
        return myDateTime;
    }

    public PostgreFullType setMyDateTime(LocalDateTime myDateTime) {
        this.myDateTime = myDateTime;
        return this;
    }

    public LocalDateTime getMyDateTime6() {
        return myDateTime6;
    }

    public PostgreFullType setMyDateTime6(LocalDateTime myDateTime6) {
        this.myDateTime6 = myDateTime6;
        return this;
    }

    public OffsetTime getMyOffsetTime() {
        return myOffsetTime;
    }

    public PostgreFullType setMyOffsetTime(OffsetTime myOffsetTime) {
        this.myOffsetTime = myOffsetTime;
        return this;
    }

    public OffsetTime getMyOffsetTime6() {
        return myOffsetTime6;
    }

    public PostgreFullType setMyOffsetTime6(OffsetTime myOffsetTime6) {
        this.myOffsetTime6 = myOffsetTime6;
        return this;
    }

    public OffsetDateTime getMyOffsetDateTime() {
        return myOffsetDateTime;
    }

    public PostgreFullType setMyOffsetDateTime(OffsetDateTime myOffsetDateTime) {
        this.myOffsetDateTime = myOffsetDateTime;
        return this;
    }

    public OffsetDateTime getMyOffsetDateTime6() {
        return myOffsetDateTime6;
    }

    public PostgreFullType setMyOffsetDateTime6(OffsetDateTime myOffsetDateTime6) {
        this.myOffsetDateTime6 = myOffsetDateTime6;
        return this;
    }


    public Integer[] getMyIntegerArray() {
        return myIntegerArray;
    }

    public PostgreFullType setMyIntegerArray(Integer[] myIntegerArray) {
        this.myIntegerArray = myIntegerArray;
        return this;
    }
}
