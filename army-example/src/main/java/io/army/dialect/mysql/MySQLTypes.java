package io.army.dialect.mysql;

import io.army.annotation.Column;
import io.army.annotation.Mapping;
import io.army.annotation.Table;
import io.army.example.VersionDomain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;

@Table(name = "mysql_types", comment = "mysql types")
public class MySQLTypes extends VersionDomain {

    private static final String CHAR_TYPE = "io.army.mapping.optional.SQLCharType";

    private static final String BINARY_TYPE = "io.army.mapping.optional.BinaryType";

    private static final String BIT_TYPE = "io.army.mapping.mysql.MySQLBitType";

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

    @Column(comment = "datetime type")
    private LocalDateTime myDatetime;

    @Column(scale = 6, comment = "datetime(6) type")
    private LocalDateTime myDatetime6;

    @Column(comment = "date type")
    private LocalDate myDate;

    @Column(comment = "time type")
    private LocalTime myTime;

    @Column(scale = 1, comment = "time(1) type")
    private LocalTime myTime1;

    @Column(comment = "year type")
    private Year myYear;

    @Column(precision = 200, comment = "varchar(200) type")
    private String myVarChar200;

    @Mapping(CHAR_TYPE)
    @Column(precision = 200, comment = "char(200) type")
    private String myChar200;

    @Mapping(BINARY_TYPE)
    @Column(precision = 60, comment = "binary(60) type")
    private byte[] myBinary60;

    @Column(precision = 60, comment = "varbinary(60) type")
    private byte[] myVarBinary60;

    @Mapping(BIT_TYPE)
    @Column(precision = 64, comment = "bit(64) type")
    private Long myBit64;

    @Mapping(BIT_TYPE)
    @Column(precision = 20, comment = "bit(20) type")
    private Long myBit20;

    @Column(comment = "tinyint type")
    private Byte myTinyint;

    @Mapping("io.army.mapping.UnsignedByteType")
    @Column(comment = "tinyint unsigned type")
    private Short myTinyintUnsigned;

    @Column(comment = "smallint type")
    private Short mySmallint;

    @Mapping("io.army.mapping.UnsignedShortType")
    @Column(comment = "smallint unsigned type")
    private Integer mySmallintUnsigned;

    @Mapping("io.army.mapping.MediumIntType")
    @Column(comment = "medium type")
    private Integer myMediumint;

    @Mapping("io.army.mapping.UnsignedMediumIntType")
    @Column(comment = "medium unsigned type")
    private Integer myMediumintUnsigned;

    @Column(comment = "int type")
    private Integer myInt;

    @Mapping("io.army.mapping.UnsignedIntegerType")
    @Column(comment = "int unsigned type")
    private Long myIntUnsigned;

    @Column(comment = "bigint type")
    private Long myBigint;

    @Mapping("io.army.mapping.UnsignedLongType")
    @Column(comment = "bigint unsigned type")
    private BigInteger myBigintUnsigned;

    @Column(comment = "decimal unsigned type")
    private BigDecimal myDecimal;

    @Mapping("io.army.mapping.UnsignedBigDecimalType")
    @Column(comment = "decimal unsigned type")
    private BigDecimal myDecimalUnsigned;

    @Column(comment = "float unsigned type")
    private Float myFloat;

    @Column(comment = "double unsigned type")
    private Double myDouble;

    @Mapping("io.army.mapping.mysql.MySQLTinyTextType")
    @Column(comment = "tiny text type")
    private String myTinyText;

    @Mapping("io.army.mapping.mysql.MySQLTextType")
    @Column(comment = "text type")
    private String myText;

    @Mapping("io.army.mapping.mysql.MySQLMediumTextType")
    @Column(comment = "medium text type")
    private String myMediumText;

    @Mapping("io.army.mapping.mysql.MySQLLongTextType")
    @Column(comment = "long text type")
    private String myLongText;


    @Override
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

    @Override
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public LocalDateTime getMyDatetime() {
        return myDatetime;
    }

    public void setMyDatetime(LocalDateTime myDatetime) {
        this.myDatetime = myDatetime;
    }

    public LocalDateTime getMyDatetime6() {
        return myDatetime6;
    }

    public void setMyDatetime6(LocalDateTime myDatetime6) {
        this.myDatetime6 = myDatetime6;
    }

    public LocalDate getMyDate() {
        return myDate;
    }

    public void setMyDate(LocalDate myDate) {
        this.myDate = myDate;
    }

    public LocalTime getMyTime() {
        return myTime;
    }

    public void setMyTime(LocalTime myTime) {
        this.myTime = myTime;
    }

    public LocalTime getMyTime1() {
        return myTime1;
    }

    public void setMyTime1(LocalTime myTime1) {
        this.myTime1 = myTime1;
    }

    public Year getMyYear() {
        return myYear;
    }

    public void setMyYear(Year myYear) {
        this.myYear = myYear;
    }

    public String getMyVarChar200() {
        return myVarChar200;
    }

    public void setMyVarChar200(String myVarChar200) {
        this.myVarChar200 = myVarChar200;
    }

    public String getMyChar200() {
        return myChar200;
    }

    public void setMyChar200(String myChar200) {
        this.myChar200 = myChar200;
    }

    public byte[] getMyBinary60() {
        return myBinary60;
    }

    public void setMyBinary60(byte[] myBinary60) {
        this.myBinary60 = myBinary60;
    }

    public byte[] getMyVarBinary60() {
        return myVarBinary60;
    }

    public void setMyVarBinary60(byte[] myVarBinary60) {
        this.myVarBinary60 = myVarBinary60;
    }

    public Long getMyBit64() {
        return myBit64;
    }

    public void setMyBit64(Long myBit64) {
        this.myBit64 = myBit64;
    }

    public Long getMyBit20() {
        return myBit20;
    }

    public void setMyBit20(Long myBit20) {
        this.myBit20 = myBit20;
    }

    public Byte getMyTinyint() {
        return myTinyint;
    }

    public void setMyTinyint(Byte myTinyint) {
        this.myTinyint = myTinyint;
    }

    public Short getMyTinyintUnsigned() {
        return myTinyintUnsigned;
    }

    public void setMyTinyintUnsigned(Short myTinyintUnsigned) {
        this.myTinyintUnsigned = myTinyintUnsigned;
    }

    public Short getMySmallint() {
        return mySmallint;
    }

    public void setMySmallint(Short mySmallint) {
        this.mySmallint = mySmallint;
    }

    public Integer getMySmallintUnsigned() {
        return mySmallintUnsigned;
    }

    public void setMySmallintUnsigned(Integer mySmallintUnsigned) {
        this.mySmallintUnsigned = mySmallintUnsigned;
    }

    public Integer getMyMediumint() {
        return myMediumint;
    }

    public void setMyMediumint(Integer myMediumint) {
        this.myMediumint = myMediumint;
    }

    public Integer getMyMediumintUnsigned() {
        return myMediumintUnsigned;
    }

    public void setMyMediumintUnsigned(Integer myMediumintUnsigned) {
        this.myMediumintUnsigned = myMediumintUnsigned;
    }

    public Integer getMyInt() {
        return myInt;
    }

    public void setMyInt(Integer myInt) {
        this.myInt = myInt;
    }

    public Long getMyIntUnsigned() {
        return myIntUnsigned;
    }

    public void setMyIntUnsigned(Long myIntUnsigned) {
        this.myIntUnsigned = myIntUnsigned;
    }

    public Long getMyBigint() {
        return myBigint;
    }

    public void setMyBigint(Long myBigint) {
        this.myBigint = myBigint;
    }

    public BigInteger getMyBigintUnsigned() {
        return myBigintUnsigned;
    }

    public void setMyBigintUnsigned(BigInteger myBigintUnsigned) {
        this.myBigintUnsigned = myBigintUnsigned;
    }


}
