package io.army.example.bank.ban;

import io.army.example.bank.domain.account.AccountType;
import io.army.example.bank.domain.user.BankUserType;
import io.army.example.bank.domain.user.CertificateType;

public class PersonAccountStatesBean implements BankFieldAccessBean {

    public Long partnerUserId;

    public BankUserType partnerUserType;

    public Long certificateId;

    public CertificateType certificateType;

    public String certificateNo;

    public Long userId;

    public String userNo;

    public BankUserType userType;

    public Long accountId;

    public String accountNo;

    public AccountType accountType;


}
