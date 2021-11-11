package io.army.env;

import io.army.util.Assert;
import io.army.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

public abstract class AbstractArmyEnvironment implements ArmyConfigurableArmyEnvironment {

    private static final LocalDateTime[] EMPTY_DATE_TIME = new LocalDateTime[0];

    @Override
    public final boolean containsValue(String key, String targetValue) {
        String[] valueArray = this.get(key, String[].class);
        boolean contains = false;
        if (valueArray != null) {

            for (String value : valueArray) {
                if (value != null && value.equals(targetValue)) {
                    contains = true;
                    break;
                }
            }

        }
        return contains;
    }


    @Override
    public final boolean isOn(String key) {
        return Boolean.parseBoolean(this.get(key));
    }

    @Override
    public final boolean isOff(String key) {
        return !Boolean.parseBoolean(this.get(key));
    }

    @Override
    public final boolean isOffDuration(String key) {
        if (!StringUtils.hasText(key)) {
            return false;
        }
        Assert.state(key.endsWith(".off.duration"), "key must end with '.off.duration'");
        return isMatchDuration(key);
    }

    @Override
    public final boolean isOnDuration(String key) {
        if (!StringUtils.hasText(key)) {
            return false;
        }
        Assert.state(key.endsWith(".on.duration"), "key must end with '.on.duration'");
        return isMatchDuration(key);
    }

    @Override
    public final <T> List<T> getPropertyList(String key, Class<T[]> targetArrayType) {
        return getPropertyList(key, targetArrayType, Collections.emptyList());
    }

    @Override
    public final <T> List<T> getPropertyList(String key, Class<T[]> targetArrayType, List<T> defaultList) {
        T[] array = this.get(key, targetArrayType);
        List<T> list;
        if (array == null || array.length == 0) {
            list = defaultList;
        } else {
            list = new ArrayList<>(array.length);
            Collections.addAll(list, array);
        }
        if (list != Collections.emptyList()) {
            list = Collections.unmodifiableList(list);
        }
        return list;
    }

    @Override
    public final <T> Set<T> getPropertySet(String key, Class<T[]> targetArrayType) {
        return getPropertySet(key, targetArrayType, Collections.emptySet());
    }

    @Override
    public final <T> Set<T> getPropertySet(String key, Class<T[]> targetArrayType, Set<T> defaultSet) {
        T[] array = this.get(key, targetArrayType);
        Set<T> set;
        if (array == null || array.length == 0) {
            set = defaultSet;
        } else {
            set = new HashSet<>((int) (array.length / 0.75f));
            Collections.addAll(set, array);
        }
        if (set != Collections.emptySet()) {
            set = Collections.unmodifiableSet(set);
        }
        return set;
    }


    @Override
    public final <T> List<T> getList(String key, Class<T> elementClass) throws IllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final <T> Set<T> getRequiredPropertySet(String key, Class<T[]> targetArrayType) throws IllegalStateException {
        Set<T> set = getPropertySet(key, targetArrayType, Collections.emptySet());
        if (set.isEmpty()) {
            throw new IllegalStateException(String.format(
                    "not found property value associated with the given key[%s]", key));
        }
        return set;
    }

    /*################################## blow ArmyConfigurableArmyEnvironment method ##################################*/


    /*################################## blow protected method ##################################*/



    /*################################## blow private method ##################################*/


    private boolean isMatchDuration(String durationKey) {

        LocalDateTime[] duration = this.get(durationKey, LocalDateTime[].class, EMPTY_DATE_TIME);
        boolean match = false;
        if (duration.length == 2) {
            LocalDateTime now = LocalDateTime.now();
            match = !now.isBefore(duration[0]) && now.isBefore(duration[1]);
        }
        return match;
    }


}
