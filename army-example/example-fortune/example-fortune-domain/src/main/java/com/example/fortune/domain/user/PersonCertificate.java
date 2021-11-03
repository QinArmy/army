package com.example.fortune.domain.user;

import io.army.annotation.Codec;
import io.army.annotation.Column;


//@Table(name = "cer_person", comment = "certificate of person")
//@DiscriminatorValue(CertificateType.Constant.PERSON_ID)
public class PersonCertificate extends Certificate<PersonCertificate> {


    @Codec
    @Column(updatable = false, precision = 32, comment = "cipher text of first name")
    private String firstName;

    @Codec
    @Column(updatable = false, precision = 32, comment = "cipher text of last name")
    private String lastName;


    public String getFirstName() {
        return firstName;
    }

    public PersonCertificate setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public PersonCertificate setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
}
