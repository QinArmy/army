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
    }, AUTONOMOUS_REGION {
        @Override
        public int code() {
            return Constant.AUTONOMOUS_REGION;
        }
    }, SPECIAL_REGION {
        @Override
        public int code() {
            return Constant.SPECIAL_REGION;
        }
    };


    interface Constant {

        byte NONE = 0;
        byte PROVINCE = 10;
        byte CITY = 20;
        byte AUTONOMOUS_REGION = 30;
        byte SPECIAL_REGION = 40;

    }


}
