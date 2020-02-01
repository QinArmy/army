package com.example.generator;

import io.army.annotation.*;
import io.army.domain.IDomain;
import io.army.generator.PreMultiGenerator;
import io.army.util.JsonUtils;

import java.time.LocalDateTime;
import java.util.StringJoiner;

@Table(name = "being", comment = "生物表")
@Inheritance("being_type")
@DiscriminatorValue(0)
public class Being implements IDomain {

    @Generator(value = "io.army.generator.snowflake.SnowflakeGenerator"
            ,params = {@Params(name = PreMultiGenerator.DEPEND_PROP_NAME,value = "")})
    @Column
    private Long id;

    @Column
    private LocalDateTime createTime;

    @Column
    private LocalDateTime updateTime;

    @Column
    private Integer version;

    @Column
    private Boolean visible;

    @Column
    private BeingType beingType;

    @Column(comment = "生物名")
    private String beingName;

    @Column(comment = "学名")
    private String scientificName;

    @Generator(value = "io.army.generator.snowflake.SnowflakeGenerator"
            ,params = {@Params(name = PreMultiGenerator.DEPEND_PROP_NAME,value = "id")})
    @Column(comment = "生物编号")
    private String beingNumber;

    @Generator(value = "io.army.generator.snowflake.SnowflakeGenerator"
            ,params = {@Params(name = PreMultiGenerator.DEPEND_PROP_NAME,value = "beingNumber")})
    @Column(comment = "物体编号")
    private String thingNumber;

    @Generator(value = "io.army.generator.snowflake.SnowflakeGenerator"
            ,params = {@Params(name = PreMultiGenerator.DEPEND_PROP_NAME,value = "thingNumber")})
    @Column(comment = "1编号")
    private String oneNumber;


    @Generator(value = "io.army.generator.snowflake.SnowflakeGenerator"
            ,params = {@Params(name = PreMultiGenerator.DEPEND_PROP_NAME,value = "oneNumber")})
    @Column(comment = "2编号")
    private String twoNumber;

    @Generator(value = "io.army.generator.snowflake.SnowflakeGenerator"
            ,params = {@Params(name = PreMultiGenerator.DEPEND_PROP_NAME,value = "twoNumber")})
    @Column(comment = "3编号")
    private String threeNumber;


    @Generator(value = "io.army.generator.snowflake.SnowflakeGenerator"
            ,params = {@Params(name = PreMultiGenerator.DEPEND_PROP_NAME,value = "threeNumber")})
    @Column(comment = "4编号")
    private String fourNumber;


    @Generator(value = "io.army.generator.snowflake.SnowflakeGenerator"
            ,params = {@Params(name = PreMultiGenerator.DEPEND_PROP_NAME,value = "fourNumber")})
    @Column(comment = "5编号")
    private String fiveNumber;

    @Generator(value = "io.army.generator.snowflake.SnowflakeGenerator"
            ,params = {@Params(name = PreMultiGenerator.DEPEND_PROP_NAME,value = "fiveNumber")})
    @Column(comment = "6编号")
    private String sixNumber;

    @Generator(value = "io.army.generator.snowflake.SnowflakeGenerator"
            ,params = {@Params(name = PreMultiGenerator.DEPEND_PROP_NAME,value = "sixNumber")})
    @Column(comment = "7编号")
    private String sevenNumber;

    @Generator(value = "io.army.generator.snowflake.SnowflakeGenerator"
            ,params = {@Params(name = PreMultiGenerator.DEPEND_PROP_NAME,value = "sevenNumber")})
    @Column(comment = "8编号")
    private String eightNumber;


    @Generator(value = "io.army.generator.snowflake.SnowflakeGenerator"
            ,params = {@Params(name = PreMultiGenerator.DEPEND_PROP_NAME,value = "eightNumber")})
    @Column(comment = "9编号")
    private String nineNumber;

    @Generator(value = "io.army.generator.snowflake.SnowflakeGenerator"
            ,params = {@Params(name = PreMultiGenerator.DEPEND_PROP_NAME,value = "nineNumber")})
    @Column(comment = "10编号")
    private String tenNumber;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public BeingType getBeingType() {
        return beingType;
    }

    public void setBeingType(BeingType beingType) {
        this.beingType = beingType;
    }


    public String getBeingName() {
        return beingName;
    }

    public void setBeingName(String beingName) {
        this.beingName = beingName;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getBeingNumber() {
        return beingNumber;
    }

    public void setBeingNumber(String beingNumber) {
        this.beingNumber = beingNumber;
    }

    public String getThingNumber() {
        return thingNumber;
    }

    public void setThingNumber(String thingNumber) {
        this.thingNumber = thingNumber;
    }

    public String getOneNumber() {
        return oneNumber;
    }

    public void setOneNumber(String oneNumber) {
        this.oneNumber = oneNumber;
    }

    public String getTwoNumber() {
        return twoNumber;
    }

    public void setTwoNumber(String twoNumber) {
        this.twoNumber = twoNumber;
    }

    public String getThreeNumber() {
        return threeNumber;
    }

    public void setThreeNumber(String threeNumber) {
        this.threeNumber = threeNumber;
    }

    public String getFourNumber() {
        return fourNumber;
    }

    public void setFourNumber(String fourNumber) {
        this.fourNumber = fourNumber;
    }

    public String getFiveNumber() {
        return fiveNumber;
    }

    public void setFiveNumber(String fiveNumber) {
        this.fiveNumber = fiveNumber;
    }

    public String getSixNumber() {
        return sixNumber;
    }

    public void setSixNumber(String sixNumber) {
        this.sixNumber = sixNumber;
    }

    public String getSevenNumber() {
        return sevenNumber;
    }

    public void setSevenNumber(String sevenNumber) {
        this.sevenNumber = sevenNumber;
    }

    public String getEightNumber() {
        return eightNumber;
    }

    public void setEightNumber(String eightNumber) {
        this.eightNumber = eightNumber;
    }

    public String getNineNumber() {
        return nineNumber;
    }

    public void setNineNumber(String nineNumber) {
        this.nineNumber = nineNumber;
    }

    public String getTenNumber() {
        return tenNumber;
    }

    public void setTenNumber(String tenNumber) {
        this.tenNumber = tenNumber;
    }

    @Override
    public String toString() {
        return JsonUtils.writeValue(this,true);
    }
}