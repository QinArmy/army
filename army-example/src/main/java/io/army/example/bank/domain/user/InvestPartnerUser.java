package io.army.example.bank.domain.user;

import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

@Table(name = "u_invest_partner", comment = "bank invest partner enterprise")
@DiscriminatorValue(BankUserType.Constant.INVEST_PARTNER)
public class InvestPartnerUser extends BankUser<InvestPartnerUser> {

    @Column(comment = "invest partner legal person user id")
    private Long legalPersonId;

    @Column(comment = "invest partner china region id")
    private Long cityId;


    public final Long getLegalPersonId() {
        return legalPersonId;
    }

    public final InvestPartnerUser setLegalPersonId(Long legalPersonId) {
        this.legalPersonId = legalPersonId;
        return this;
    }

    public final Long getCityId() {
        return cityId;
    }

    public final InvestPartnerUser setCityId(Long cityId) {
        this.cityId = cityId;
        return this;
    }


}
