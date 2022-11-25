package io.army.example.bank.domain.user;

import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Index;
import io.army.annotation.Table;

@Table(name = "history_china_province", indexes =
@Index(name = "uni_provincial_capital", fieldList = "provincialCapital", unique = true),
        comment = "history china province")
@DiscriminatorValue(RegionType.Constant.PROVINCE)
public class HistoryChinaProvince extends HistoryChinaRegion<HistoryChinaProvince> {


    @Column(precision = 30, nullable = false, comment = "china provincial capital")
    private String provincialCapital;

    @Column(precision = 30, nullable = false, defaultValue = "''", comment = "china provincial governor")
    private String governor;


    public String getProvincialCapital() {
        return provincialCapital;
    }

    public HistoryChinaProvince setProvincialCapital(String provincialCapital) {
        this.provincialCapital = provincialCapital;
        return this;
    }

    public String getGovernor() {
        return governor;
    }

    public HistoryChinaProvince setGovernor(String governor) {
        this.governor = governor;
        return this;
    }


}
