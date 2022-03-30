package io.army.example.bank.domain.user;

import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

@Table(name = "u_bank_partner", comment = "bank partner enterprise")
@DiscriminatorValue(BankUserType.Constant.PARTNER)
public class PartnerUser extends BankUser<PartnerUser> {

    @Column(comment = "invest partner legal person user id")
    private Long legalPersonId;

    @Column(comment = "invest partner china region id")
    private Long cityId;

    public final Long getLegalPersonId() {
        return legalPersonId;
    }

    public final PartnerUser setLegalPersonId(Long legalPersonId) {
        this.legalPersonId = legalPersonId;
        return this;
    }

    public final Long getCityId() {
        return cityId;
    }

    public final PartnerUser setCityId(Long cityId) {
        this.cityId = cityId;
        return this;
    }


}
