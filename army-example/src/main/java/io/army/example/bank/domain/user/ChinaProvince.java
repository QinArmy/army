package io.army.example.bank.domain.user;

import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Index;
import io.army.annotation.Table;

@Table(name = "china_province", indexes =
@Index(name = "uni_provincial_capital", fieldList = "provincialCapital", unique = true)
        , comment = "china province")
@DiscriminatorValue(RegionType.Constant.PROVINCE)
public class ChinaProvince extends ChinaRegion<ChinaProvince> {

    @Column(precision = 30, nullable = false, comment = "china provincial capital")
    private String provincialCapital;

    @Column(precision = 30, nullable = false, defaultValue = "''", comment = "china provincial governor")
    private String governor;


    public String getProvincialCapital() {
        return provincialCapital;
    }

    public ChinaProvince setProvincialCapital(String provincialCapital) {
        this.provincialCapital = provincialCapital;
        return this;
    }

    public String getGovernor() {
        return governor;
    }

    public ChinaProvince setGovernor(String governor) {
        this.governor = governor;
        return this;
    }
}
