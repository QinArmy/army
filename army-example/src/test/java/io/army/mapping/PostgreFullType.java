package io.army.mapping;

import io.army.annotation.Column;
import io.army.annotation.Generator;
import io.army.annotation.GeneratorType;
import io.army.annotation.Table;
import io.army.example.common.BaseVersionDomain;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "postgre_full_type", comment = "postgre full type table")
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

    @Column(nullable = false, defaultValue = "LOCALDATE", comment = "my date")
    private LocalDate myDate;


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
}
