package io.army.example.bank.domain.user;

import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

@Table(name = "history_china_city", comment = "china city")
@DiscriminatorValue(RegionType.Constant.CITY)
public class HistoryChinaCity extends HistoryChinaRegion<HistoryChinaCity> {

    @Column(precision = 30, defaultValue = "''", nullable = false, comment = "city mayor name")
    private String mayorName;


    public final String getMayorName() {
        return mayorName;
    }

    public final HistoryChinaCity setMayorName(String mayorName) {
        this.mayorName = mayorName;
        return this;
    }


}
