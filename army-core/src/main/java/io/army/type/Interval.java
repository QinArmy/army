package io.army.type;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;
@Deprecated
public final class Interval implements TemporalAmount {

    @Override
    public long get(TemporalUnit unit) {
        return 0;
    }

    @Override
    public List<TemporalUnit> getUnits() {
        return null;
    }

    @Override
    public Temporal addTo(Temporal temporal) {
        return null;
    }

    @Override
    public Temporal subtractFrom(Temporal temporal) {
        return null;
    }


}
