package io.army.dialect.mysql;

import io.army.annotation.*;
import io.army.bean.FieldAccessBean;
import io.army.example.domain.VersionDomain;
import io.army.example.struct.QinArmy;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.Set;

@Table(name = "mysql_army_types", comment = "mysql types for army example")
public class MySQLTypes extends VersionDomain implements FieldAccessBean {

    private static final String CHAR_TYPE = "io.army.mapping.optional.SQLCharType";

    private static final String BINARY_TYPE = "io.army.mapping.optional.BinaryType";

    private static final String BIT_TYPE = "io.army.mapping.mysql.MySQLBitType";

    @Generator(type = GeneratorType.POST)
    @Column
    public Long id;

    @Column
    public LocalDateTime createTime;

    @Column
    public LocalDateTime updateTime;

    @Column
    public Integer version;

    @Column
    public Boolean visible;

    @Column(comment = "datetime type")
    public LocalDateTime myDatetime;

    @Column(scale = 6, comment = "datetime(6) type")
    public LocalDateTime myDatetime6;

    @Column(comment = "date type")
    public LocalDate myDate;

    @Column(comment = "time type")
    public LocalTime myTime;

    @Column(scale = 1, comment = "time(1) type")
    public LocalTime myTime1;

    @Column(comment = "year type")
    public Year myYear;

    @Column(precision = 200, comment = "varchar(200) type")
    public String myVarChar200;

    @Mapping(CHAR_TYPE)
    @Column(precision = 200, comment = "char(200) type")
    public String myChar200;

    @Mapping(BINARY_TYPE)
    @Column(precision = 60, comment = "binary(60) type")
    public byte[] myBinary60;

    @Column(precision = 60, comment = "varbinary(60) type")
    public byte[] myVarBinary60;

    @Mapping(BIT_TYPE)
    @Column(precision = 64, comment = "bit(64) type")
    public Long myBit64;

    @Mapping(BIT_TYPE)
    @Column(precision = 20, comment = "bit(20) type")
    public Long myBit20;

    @Column(comment = "tinyint type")
    public Byte myTinyint;

    @Mapping("io.army.mapping.UnsignedByteType")
    @Column(comment = "tinyint unsigned type")
    public Short myTinyintUnsigned;

    @Column(comment = "smallint type")
    public Short mySmallint;

    @Mapping("io.army.mapping.UnsignedShortType")
    @Column(comment = "smallint unsigned type")
    public Integer mySmallintUnsigned;

    @Mapping("io.army.mapping.MediumIntType")
    @Column(comment = "medium type")
    public Integer myMediumint;

    @Mapping("io.army.mapping.UnsignedMediumIntType")
    @Column(comment = "medium unsigned type")
    public Integer myMediumintUnsigned;

    @Column(comment = "int type")
    public Integer myInt;

    @Mapping("io.army.mapping.UnsignedIntegerType")
    @Column(comment = "int unsigned type")
    public Long myIntUnsigned;

    @Column(comment = "bigint type")
    public Long myBigint;

    @Mapping("io.army.mapping.UnsignedLongType")
    @Column(comment = "bigint unsigned type")
    public BigInteger myBigintUnsigned;

    @Column(comment = "decimal unsigned type")
    public BigDecimal myDecimal;

    @Mapping("io.army.mapping.UnsignedBigDecimalType")
    @Column(comment = "decimal unsigned type")
    public BigDecimal myDecimalUnsigned;

    @Column(comment = "float unsigned type")
    public Float myFloat;

    @Column(comment = "double unsigned type")
    public Double myDouble;

    @Mapping(value = "io.army.mapping.mysql.MySQLNameEnumSetType", elements = Month.class)
    @Column(comment = "set of name enum")
    public Set<Month> myNameEnumSet;

    @Mapping(value = "io.army.mapping.mysql.MySQLTextEnumSetType", elements = QinArmy.class)
    @Column(comment = "set of text enum")
    public Set<QinArmy> myTextEnumSet;

    @Mapping("io.army.mapping.optional.JsonStringType")
    @Column(comment = "json type")
    public String myJson;

    @Mapping("io.army.mapping.mysql.MySQLTinyTextType")
    @Column(comment = "tiny text type")
    public String myTinyText;

    @Mapping("io.army.mapping.mysql.MySQLTextType")
    @Column(comment = "text type")
    public String myText;

    @Mapping("io.army.mapping.mysql.MySQLMediumTextType")
    @Column(comment = "medium text type")
    public String myMediumText;

    @Mapping("io.army.mapping.mysql.MySQLLongTextType")
    @Column(comment = "long text type")
    public String myLongText;

    @Mapping("io.army.mapping.mysql.MySQLTinyBlobType")
    @Column(comment = "tiny blob type")
    public byte[] myTinyBlob;

    @Mapping("io.army.mapping.mysql.MySQLTinyBlobType")
    @Column(comment = "blob type")
    public byte[] myBlob;

    @Mapping("io.army.mapping.mysql.MySQLMediumBlobType")
    @Column(comment = "medium blob type")
    public byte[] myMediumBlob;

    @Mapping("io.army.mapping.mysql.MySQLLongBlobType")
    @Column(comment = "long blob type")
    public byte[] myLongBlob;


    public Long getId() {
        return id;
    }


    @Override
    public Integer getVersion() {
        return version;
    }

}
