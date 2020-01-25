package io.army.dialect.mysql;

import io.army.dialect.AbstractFunc;
import io.army.dialect.Func;

class MySQL5757FuncImpl extends AbstractFunc implements Func {




    @Override
    public String now() {
        return "NOW()";
    }

    @Override
    public String now(int precision) {
        if (precision < 0 || precision > 6) {
            throw new IllegalArgumentException(String.format("NOW() columnSize must in [0,6],but %s", precision));
        }
        if (precision == 0) {
            return now();
        }
        return "NOW(" + precision + ")";
    }

    @Override
    public String currentDate() {
        return null;
    }

    @Override
    public String currentTime() {
        return null;
    }

    @Override
    public String currentTime(int precision) {
        return null;
    }
}
