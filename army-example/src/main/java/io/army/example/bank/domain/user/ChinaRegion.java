package io.army.example.bank.domain.user;

import io.army.annotation.*;
import io.army.example.common.BaseVersionDomain;

import java.math.BigDecimal;

@Table(name = "china_region", comment = "china region")
@Inheritance("regionType")
@SuppressWarnings("unchecked")
public class ChinaRegion<T extends ChinaRegion<T>> extends BaseVersionDomain<T> {

    @Generator(value = SNOWFLAKE, params = {@Param(name = START_TIME, value = startTime)})
    @Column
    private Long id;

    @Column
    private RegionType regionType;

    @Column(precision = 20, comment = "china region name")
    private String name;

    @Column(precision = 14, scale = 2, comment = "china region GDP")
    private BigDecimal regionGdp;


    @Column(updateMode = UpdateMode.IMMUTABLE, comment = "china region parent level id")
    private Long parentId;

    @Override
    public Long getId() {
        return id;
    }

    public T setId(Long id) {
        this.id = id;
        return (T) this;
    }

    public RegionType getRegionType() {
        return regionType;
    }

    public T setRegionType(RegionType regionType) {
        this.regionType = regionType;
        return (T) this;
    }

    public String getName() {
        return name;
    }

    public T setName(String name) {
        this.name = name;
        return (T) this;
    }

    public BigDecimal getRegionGdp() {
        return regionGdp;
    }

    public T setRegionGdp(BigDecimal regionGdp) {
        this.regionGdp = regionGdp;
        return (T) this;
    }

    public Long getParentId() {
        return parentId;
    }

    public T setParentId(Long parentId) {
        this.parentId = parentId;
        return (T) this;
    }
}
