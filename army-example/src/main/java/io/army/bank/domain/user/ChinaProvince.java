package io.army.bank.domain.user;

import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

@Table(name = "china_province", comment = "china province")
@DiscriminatorValue(RegionType.Constant.PROVINCE)
public class ChinaProvince extends ChinaRegion {

    @Column(precision = 50, comment = "china provincial capital")
    private String provincialCapital;

    @Column(precision = 50, comment = "china provincial governor")
    private String governor;


    public String getProvincialCapital() {
        return provincialCapital;
    }

    public void setProvincialCapital(String provincialCapital) {
        this.provincialCapital = provincialCapital;
    }

    public String getGovernor() {
        return governor;
    }

    public void setGovernor(String governor) {
        this.governor = governor;
    }

}
