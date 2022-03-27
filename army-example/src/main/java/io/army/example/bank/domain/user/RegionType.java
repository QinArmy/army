package io.army.example.bank.domain.user;

import io.army.struct.CodeEnum;

public enum RegionType implements CodeEnum {

    NONE {
        @Override
        public int code() {
            return Constant.NONE;
        }
    },
    PROVINCE {
        @Override
        public int code() {
            return Constant.PROVINCE;
        }
    }, CITY {
        @Override
        public int code() {
            return Constant.CITY;
        }
    };

    interface Constant {

        byte NONE = 0;
        byte PROVINCE = 10;
        byte CITY = 20;

    }


}
