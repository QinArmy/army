package io.army.example.bank.domain.user;

import io.army.annotation.Column;
import io.army.annotation.Inheritance;
import io.army.annotation.Table;
import io.army.annotation.UpdateMode;
import io.army.example.common.BaseVersionDomain;

import java.math.BigDecimal;

@Table(name = "china_region", comment = "china region")
@Inheritance("regionType")
public class ChinaRegion extends BaseVersionDomain<ChinaRegion> {


    @Column
    private RegionType regionType;

    @Column(precision = 20, comment = "china region name")
    private String name;

    @Column(precision = 14, scale = 2, comment = "china region GDP")
    private BigDecimal regionGdp;


    @Column(updateMode = UpdateMode.IMMUTABLE, comment = "china region parent level id")
    private Long parentId;


    public final RegionType getRegionType() {
        return regionType;
    }

    public final ChinaRegion setRegionType(RegionType regionType) {
        this.regionType = regionType;
        return this;
    }

    public final String getName() {
        return name;
    }

    public final ChinaRegion setName(String name) {
        this.name = name;
        return this;
    }

    public final BigDecimal getRegionGdp() {
        return regionGdp;
    }

    public final ChinaRegion setRegionGdp(BigDecimal regionGdp) {
        this.regionGdp = regionGdp;
        return this;
    }

    public final Long getParentId() {
        return parentId;
    }

    public final ChinaRegion setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }


}
