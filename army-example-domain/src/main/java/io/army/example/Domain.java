package io.army.example;

import io.army.domain.IDomain;

import java.util.Objects;

public abstract class Domain extends Criteria implements IDomain {

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
