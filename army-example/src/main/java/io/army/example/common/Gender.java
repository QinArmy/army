package io.army.example.common;

public enum Gender {

    UNKNOWN,
    MALE,
    FEMALE;

    public static Gender fromCertificateNo(String certificateNo) {
        final Gender gender;
        if ((Byte.parseByte(Character.toString(certificateNo.charAt(16))) & 1) == 0) {
            gender = Gender.FEMALE;
        } else {
            gender = Gender.MALE;
        }
        return gender;
    }

}
