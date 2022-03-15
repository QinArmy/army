package io.army.example.domain;

import io.army.annotation.Column;
import io.army.annotation.Inheritance;
import io.army.annotation.Table;
import io.army.example.VersionDomain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "china_region", comment = "china region")
@Inheritance("regionType")
public class ChinaRegion extends VersionDomain {

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
    private RegionType regionType;

    @Column(precision = 10, comment = "china region name")
    private String name;

    @Column(precision = 14, scale = 2, comment = "china region GDP")
    private BigDecimal regionGdp;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public RegionType getRegionType() {
        return regionType;
    }

    public void setRegionType(RegionType regionType) {
        this.regionType = regionType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getRegionGdp() {
        return regionGdp;
    }

    public void setRegionGdp(BigDecimal regionGdp) {
        this.regionGdp = regionGdp;
    }


}
