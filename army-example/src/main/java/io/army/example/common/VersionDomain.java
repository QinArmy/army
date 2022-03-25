package io.army.example.common;


import org.springframework.lang.Nullable;

import java.util.Objects;

public abstract class VersionDomain extends Domain {


    @Override
    public int hashCode() {
        return Objects.hash(getId(), getVersion());
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (super.equals(obj)) {
            final Integer version = this.getVersion();
            if (version == null) {
                match = ((VersionDomain) obj).getVersion() == null;
            } else {
                match = version.equals(((VersionDomain) obj).getVersion());
            }
        } else {
            match = false;
        }
        return match;
    }

    @Nullable
    public abstract Integer getVersion();


}
