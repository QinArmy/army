package io.army.struct;


/**
 * created  on 2019-03-14.
 */
public enum Country implements CodeEnum {

    CHINA(430, "China", "CN"),

    HONG_KONG(940, "HONG KONG", "HK") {
        @Override
        public CodeEnum family() {
            return CHINA;
        }
    },

    MACAO(1230, "Macao", "MO") {
        @Override
        public CodeEnum family() {
            return CHINA;
        }
    },
    TAIWAN(2070, "TAIWAN", "TW") {
        @Override
        public CodeEnum family() {
            return CHINA;
        }
    };


    private final int code;

    private final String display;

    private final String isoCode;

    Country(int code, String display, String isoCode) {
        this.code = code;
        this.display = display;
        this.isoCode = isoCode;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String display() {
        return display;
    }

    public String isoCode() {
        return isoCode;
    }
}
