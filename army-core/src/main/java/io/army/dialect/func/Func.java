package io.army.dialect.func;

import io.army.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class Func {

    /**
     * @param fsp specify a fractional seconds columnSize from 0 to 6
     * @return a dml function expression object
     */
    public SQLFunc<LocalDateTime> now(int fsp) {
        Assert.isTrue(fsp >= 0 && fsp <= 6, "fsp error");
        return null;
    }

    public SQLFunc<LocalDate> currentDate() {
        return null;
    }

}
