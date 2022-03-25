package io.army.example.common;

import io.army.domain.IDomain;
import io.army.generator.FieldGenerator;
import io.army.generator.snowflake.SnowflakeGenerator;
import org.springframework.lang.Nullable;

import java.util.Objects;

public abstract class Domain extends Criteria implements IDomain {


    protected static final String SNOWFLAKE = "io.army.generator.snowflake.SnowflakeGenerator";

    protected static final String START_TIME = SnowflakeGenerator.START_TIME;

    protected static final String startTime = "1647957568404";

    protected static final String DEPEND = FieldGenerator.DEPEND_FIELD_NAME;

    protected static final String DATE = "date";

    protected static final String TRUE = "true";


    @Nullable
    public abstract Object getId();

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (getClass().isInstance(obj)) {
            final Domain v = (Domain) obj;
            final Object id;
            id = getId();
            if (id == null) {
                match = v.getId() == null;
            } else {
                match = id.equals(v.getId());
            }
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        return super.toString();
    }


}
