package io.army.example.bank.domain.user;

import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

@Table(name = "china_city", comment = "china city")
@DiscriminatorValue(RegionType.Constant.CITY)
public class ChinaCity extends ChinaRegion<ChinaCity> {


    @Column(precision = 30, defaultValue = "''", nullable = false, comment = "city mayor name")
    private String mayorName;

    public final String getMayorName() {
        return mayorName;
    }

    public final ChinaCity setMayorName(String mayorName) {
        this.mayorName = mayorName;
        return this;
    }

}
